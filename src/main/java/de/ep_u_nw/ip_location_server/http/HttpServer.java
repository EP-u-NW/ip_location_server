package de.ep_u_nw.ip_location_server.http;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;

import com.google.common.net.InetAddresses;

import de.ep_u_nw.ip_location_server.database.IPv4Database;
import de.ep_u_nw.ip_location_server.database.IPv6Database;
import de.ep_u_nw.ip_location_server.http.HttpException.BadRequestException;
import de.ep_u_nw.ip_location_server.http.HttpException.ForbiddenException;
import de.ep_u_nw.ip_location_server.http.HttpException.InternalServerErrorException;
import de.ep_u_nw.ip_location_server.http.HttpException.MethodNotAllowedException;
import de.ep_u_nw.ip_location_server.http.HttpException.NotFoundException;
import de.ep_u_nw.ip_location_server.util.Startable;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.ssl.NotSslRecordException;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

public class HttpServer extends Startable {
    private final static Logger LOGGER = Logger.getLogger(HttpServer.class.getSimpleName());

    private final SslContext sslContext;
    private final RequestHandler requestHandler;
    private SocketAddress address;
    private final boolean debug;
    private final IPv4Database ip4Database;
    private final IPv6Database ip6Database;
    private final boolean exceptionDetailsOverHttp;
    private ServerBootstrap bootstrap;
    private CompletableFuture<Void> future;
    private final boolean meUseHeader;
    private final String meHeaderName;

    public HttpServer(IPv4Database ip4Database, IPv6Database ip6Database, String host, int port, boolean useSsl,
            String letsEncryptDir, boolean exceptionDetailsOverHttp, boolean meUseHeader, String meHeaderName,
            boolean debug)
            throws SSLException, CertificateException, IOException {
        this.meHeaderName = meHeaderName;
        this.meUseHeader = meUseHeader;
        this.address = new InetSocketAddress(host, port);
        this.debug = debug;
        this.ip4Database = ip4Database;
        this.ip6Database = ip6Database;
        this.exceptionDetailsOverHttp = exceptionDetailsOverHttp;
        this.requestHandler = new RequestHandler();
        this.sslContext = generateSslContext(useSsl, letsEncryptDir);
    }

    @java.lang.SuppressWarnings("resource")
    private static SslContext generateSslContext(boolean useSsl, String letsEncryptDir)
            throws SSLException, CertificateException, IOException {
        if (useSsl) {
            if (letsEncryptDir != null) {
                LOGGER.info("Creating SslContext...");
                return SslContextBuilder
                        .forServer(
                                new FileInputStream(letsEncryptDir + "/fullchain.pem"),
                                new FileInputStream(letsEncryptDir + "/privkey.pem"))
                        .build();
            } else {
                LOGGER.warning("Using self signed ssl certificate!");
                SelfSignedCertificate snaikoil = new SelfSignedCertificate();
                return SslContextBuilder.forServer(snaikoil.certificate(), snaikoil.privateKey()).build();
            }
        } else {
            LOGGER.warning("Not using SSL!");
            return null;
        }
    }

    @Override
    protected CompletableFuture<Void> startChecked() {
        future = new CompletableFuture<Void>();
        final ExceptionHandler exceptionHandler = new ExceptionHandler();
        bootstrap = new ServerBootstrap().group(new NioEventLoopGroup(), new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipline = ch.pipeline();
                        if (sslContext != null) {
                            pipline.addLast("ssl", sslContext.newHandler(ch.alloc()));
                        }
                        pipline.addLast("decoder", new HttpRequestDecoder());
                        pipline.addLast("encoder", new HttpResponseEncoder());
                        pipline.addLast("aggregator", new HttpObjectAggregator(2048));
                        pipline.addLast("request", requestHandler);
                        pipline.addLast("exceptions", exceptionHandler);
                    }
                });
        bootstrap.bind(address).syncUninterruptibly();
        LOGGER.info("Http server running on " + address);
        return future;
    }

    @Override
    protected void stopChecked() {
        LOGGER.info("Shutting down HTTP server...");
        bootstrap.config().group().shutdownGracefully().syncUninterruptibly();
        bootstrap.config().childGroup().shutdownGracefully().syncUninterruptibly();
        LOGGER.info("HTTP server done");
        future.complete(null);
    }

    @Sharable
    private class ExceptionHandler extends ChannelDuplexHandler {
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            if (cause instanceof IOException && cause.getMessage().equals("Connection reset by peer")) {
                // ignore
            } else if (cause instanceof NotSslRecordException
                    || cause instanceof SSLHandshakeException) {
                if (debug) {
                    LOGGER.info("Attempt for a no-ssl connection or malformed handshake by "
                            + ctx.channel().remoteAddress());
                }
            } else {
                super.exceptionCaught(ctx, cause);
            }
        }
    }

    @Sharable
    private class RequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
        private static final String HTTP_HEADER_CONTENT_LENGTH = "Content-Length";
        private static final String HTTP_HEADER_CONTENT_TYPE = "Content-Type";
        private static final String HTTP_HEADER_CONTENT_TYPE_VALUE = "text/plain charset=UTF-8";
        private static final String HTTP_HEADER_CONNECTION = "Connection";
        private static final String HTTP_HEADER_CONNECTION_VALUE = "close";

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, final FullHttpRequest msg) throws Exception {
            try {
                String uri = msg.uri().toLowerCase();
                if (debug) {
                    LOGGER.info("Requested " + msg.method() + " " + msg.uri());
                }
                String result;
                if (uri.equals("/me")) {
                    if (msg.method() != HttpMethod.GET) {
                        throw new MethodNotAllowedException(
                                "Invalid method " + msg.method() + " for /me");
                    }
                    result = handleMe(ctx, msg);
                } else if (uri.equals("/lookup")) {
                    if (msg.method() != HttpMethod.GET) {
                        throw new MethodNotAllowedException(
                                "Invalid method " + msg.method() + " for /lookup");
                    }
                    throw new BadRequestException("Missing parameter ip!");
                } else if (uri.startsWith("/lookup?")) {
                    if (msg.method() != HttpMethod.GET) {
                        throw new MethodNotAllowedException(
                                "Invalid method " + msg.method() + " for /lookup");
                    }
                    result = handleLookup(uri.substring("/lookup?".length()));
                } else {
                    throw new NotFoundException(msg.uri());
                }
                ByteBuf buf = Unpooled.wrappedBuffer(result.getBytes(StandardCharsets.UTF_8));
                FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                        HttpResponseStatus.OK,
                        buf);
                response.headers().add(HTTP_HEADER_CONTENT_LENGTH, buf.readableBytes());
                response.headers().add(HTTP_HEADER_CONTENT_TYPE, HTTP_HEADER_CONTENT_TYPE_VALUE);
                response.headers().add(HTTP_HEADER_CONNECTION, HTTP_HEADER_CONNECTION_VALUE);
                ctx.writeAndFlush(response);
            } catch (HttpException e) {
                handleHttpException(ctx, e);
            } catch (Exception e) {
                handleHttpException(ctx, new InternalServerErrorException(e));
            }
        }

        private String handleMe(ChannelHandlerContext ctx, FullHttpRequest msg) throws HttpException {
            if (meUseHeader) {
                String headerValue = msg.headers().get(meHeaderName);
                InetAddress address;
                try {
                    address = InetAddresses.forString(headerValue);
                } catch (IllegalArgumentException e) {
                    throw new BadRequestException("Could not parse header '" + meHeaderName + ": " + headerValue
                            + "' to ip!", e);
                }
                return getLocation(address);
            } else {
                return getLocation(ctx.channel().remoteAddress());
            }
        }

        private String handleLookup(String query) throws HttpException {
            if (query.startsWith("ip=") && !query.contains("&")) {
                InetAddress address;
                try {
                    String param = URLDecoder.decode(query.substring("ip=".length()), StandardCharsets.UTF_8);
                    address = InetAddresses.forString(param);
                } catch (IllegalArgumentException e) {
                    throw new BadRequestException("Bad parameter for ip!", e);
                }
                return getLocation(address);
            } else {
                throw new BadRequestException(
                        "Bad query! Only allowed parameter is ip=<IPv4 or IPv6>! Query was: " + query);
            }

        }

        private String getLocation(InetAddress inetAddress) throws HttpException {
            if (inetAddress instanceof Inet4Address) {
                if (ip4Database == null) {
                    throw new ForbiddenException("This server does not allow IPv4 lookup!");
                }
                return "v4," + inetAddress.toString().substring(1) + "," + ip4Database.get((Inet4Address) inetAddress);
            } else if (inetAddress instanceof Inet6Address) {
                if (ip6Database == null) {
                    throw new ForbiddenException("This server does not allow IPv6 lookup!");
                }
                return "v6," + inetAddress.toString().substring(1) + "," + ip6Database.get((Inet6Address) inetAddress);
            } else {
                throw new InternalServerErrorException(
                        "Unexpected InetAddress Type: " + inetAddress.getClass().getSimpleName());
            }
        }

        private String getLocation(SocketAddress socketAddress) throws HttpException {
            if (socketAddress instanceof InetSocketAddress) {
                InetAddress inetAddress = ((InetSocketAddress) socketAddress).getAddress();
                return getLocation(inetAddress);
            } else {
                throw new InternalServerErrorException(
                        "Unexpected SocketAddress Type: " + socketAddress.getClass().getSimpleName());
            }
        }

        private void handleHttpException(ChannelHandlerContext ctx, HttpException e) {
            int contentLength;
            FullHttpResponse response;
            if (debug) {
                e.printStackTrace();
            }
            if (exceptionDetailsOverHttp) {
                String description = captureStackTrace(e);
                ByteBuf buf = Unpooled.wrappedBuffer(description.getBytes(StandardCharsets.UTF_8));
                response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                        HttpResponseStatus.valueOf(e.responseCode), buf);
                contentLength = buf.readableBytes();
            } else {
                response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                        HttpResponseStatus.valueOf(e.responseCode));
                contentLength = 0;
            }
            response.headers().add(HTTP_HEADER_CONTENT_LENGTH, contentLength);
            response.headers().add(HTTP_HEADER_CONTENT_TYPE, HTTP_HEADER_CONTENT_TYPE_VALUE);
            response.headers().add(HTTP_HEADER_CONNECTION, HTTP_HEADER_CONNECTION_VALUE);
            ctx.writeAndFlush(response);
        }

    }

    private static String captureStackTrace(Exception e) {
        StringWriter writer = new StringWriter();
        PrintWriter capture = new PrintWriter(writer);
        e.printStackTrace(capture);
        capture.close();
        return writer.toString();
    }
}
