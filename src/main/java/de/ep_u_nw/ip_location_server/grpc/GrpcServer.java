package de.ep_u_nw.ip_location_server.grpc;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.security.cert.CertificateException;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

import javax.net.ssl.SSLException;

import com.google.common.net.InetAddresses;
import com.google.protobuf.Empty;

import de.ep_u_nw.ip_location_server.database.IPv4Database;
import de.ep_u_nw.ip_location_server.database.IPv6Database;
import de.ep_u_nw.ip_location_server.grpc.generated.IP;
import de.ep_u_nw.ip_location_server.grpc.generated.IPBinary;
import de.ep_u_nw.ip_location_server.grpc.generated.IPv4Binary;
import de.ep_u_nw.ip_location_server.grpc.generated.IPv6Binary;
import de.ep_u_nw.ip_location_server.grpc.generated.LookupResult;
import de.ep_u_nw.ip_location_server.grpc.generated.IPLocationServiceGrpc.IPLocationServiceImplBase;
import de.ep_u_nw.ip_location_server.http.HttpServer;
import de.ep_u_nw.ip_location_server.util.Startable;
import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.Grpc;
import io.grpc.Metadata;
import io.grpc.Server;
import io.grpc.ServerCall;
import io.grpc.ServerCall.Listener;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import io.grpc.StatusException;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContextBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.util.SelfSignedCertificate;
import io.grpc.stub.StreamObserver;

public class GrpcServer extends Startable implements ServerInterceptor {
    private final static Logger LOGGER = Logger.getLogger(HttpServer.class.getSimpleName());

    private final static Context.Key<Object> CLIENT_IP_ADDRESS = Context.key("ClientIPAddress");

    @Override
    public <ReqT, RespT> Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {
        Object ipAddress;
        if (meHeader != null) {
            ipAddress = headers.get(meHeader);
        } else {
            ipAddress = call.getAttributes().get(Grpc.TRANSPORT_ATTR_REMOTE_ADDR);
        }
        return Contexts.interceptCall(Context.current().withValue(CLIENT_IP_ADDRESS, ipAddress), call,
                headers, next);
    }

    private final IPv4Database ip4Database;
    private final IPv6Database ip6Database;
    private final Server server;
    private final SocketAddress address;
    private CompletableFuture<Void> future;
    private final Metadata.Key<String> meHeader;

    public GrpcServer(IPv4Database ip4Database, IPv6Database ip6Database, String host, int port, boolean useSsl,
            String letsEncryptDir, boolean meUseHeader, String meHeaderName)
            throws SSLException, CertificateException, IOException, UnknownHostException {
        if (meUseHeader) {
            this.meHeader = Metadata.Key.of(meHeaderName, Metadata.ASCII_STRING_MARSHALLER);
        } else {
            this.meHeader = null;
        }
        this.ip4Database = ip4Database;
        this.ip6Database = ip6Database;
        this.address = new InetSocketAddress(InetAddress.getByName(host), port);
        this.server = NettyServerBuilder.forAddress(address).intercept(this)
                .sslContext(generateSslContext(useSsl, letsEncryptDir))
                .addService(new Endpoint()).build();
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
        try {
            server.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        LOGGER.info("Grpc server running on " + address);
        return future;
    }

    @Override
    protected void stopChecked() {
        LOGGER.info("Shutting down GRPC server...");
        server.shutdown();
        LOGGER.info("GRPC server done");
        if (!future.isDone()) {
            future.complete(null);
        }
    }

    private class Endpoint extends IPLocationServiceImplBase {
        @Override
        public void lookup(IP request, StreamObserver<LookupResult> responseObserver) {
            InetAddress address;
            if (request.hasIpString()) {
                try {
                    address = InetAddresses.forString(request.getIpString());
                } catch (IllegalArgumentException e) {
                    responseObserver.onError(Status.INVALID_ARGUMENT.withCause(e)
                            .withDescription("Could not parse ip_string to ip!").asException());
                    return;
                }
            } else if (request.hasIpBinary()) {
                IPBinary ipBinary = request.getIpBinary();
                int[] bytes;
                if (ipBinary.hasV4()) {
                    IPv4Binary v4Binary = ipBinary.getV4();
                    bytes = new int[] {
                            v4Binary.getB01(),
                            v4Binary.getB02(),
                            v4Binary.getB03(),
                            v4Binary.getB04(),
                    };
                } else if (ipBinary.hasV6()) {
                    IPv6Binary v6Binary = ipBinary.getV6();
                    bytes = new int[] {
                            v6Binary.getB01(),
                            v6Binary.getB02(),
                            v6Binary.getB03(),
                            v6Binary.getB04(),
                            v6Binary.getB05(),
                            v6Binary.getB06(),
                            v6Binary.getB07(),
                            v6Binary.getB08(),
                            v6Binary.getB09(),
                            v6Binary.getB10(),
                            v6Binary.getB11(),
                            v6Binary.getB12(),
                            v6Binary.getB13(),
                            v6Binary.getB14(),
                            v6Binary.getB15(),
                            v6Binary.getB16(),
                    };
                } else {
                    responseObserver.onError(Status.INVALID_ARGUMENT
                            .withDescription("Either v4 or v6 is required for IPBinary!").asException());
                    return;
                }
                try {
                    address = InetAddress.getByAddress(checkRange(bytes));
                } catch (UnknownHostException e) {
                    responseObserver.onError(Status.INVALID_ARGUMENT
                            .withDescription("Could not convert bytes to ip!").withCause(e)
                            .asException());
                    return;
                } catch (StatusException e) {
                    responseObserver.onError(e);
                    return;
                }
            } else {
                responseObserver.onError(Status.INVALID_ARGUMENT
                        .withDescription("Either ip_string or ip_binary is required for IP!").asException());
                return;
            }
            answer(address, responseObserver);
        }

        private byte[] checkRange(int[] input) throws StatusException {
            byte[] result = new byte[input.length];
            for (int i = 0; i < result.length; i++) {
                if (input[i] < 0) {
                    throw Status.INVALID_ARGUMENT
                            .withDescription("For all entries in a IPBinary 0 <= entry <= 255 must hold!")
                            .asException();
                }
                if (input[i] > 255) {
                    throw Status.INVALID_ARGUMENT
                            .withDescription("For all entries in a IPBinary 0 <= entry <= 255 must hold!")
                            .asException();
                }
                if (input[i] > Byte.MAX_VALUE) {
                    input[i] -= 256;
                }
                result[i] = (byte) input[i];
            }
            return result;
        }

        @Override
        public void me(Empty request, StreamObserver<LookupResult> responseObserver) {
            Object socketAddress = CLIENT_IP_ADDRESS.get();
            if (meHeader != null) {
                InetAddress inetAddress;
                try {
                    inetAddress = InetAddresses.forString((String) socketAddress);
                } catch (IllegalArgumentException e) {
                    responseObserver.onError(Status.INVALID_ARGUMENT.withCause(e)
                            .withDescription("Could not parse header '" + meHeader.originalName() + ": " + socketAddress
                                    + "' to ip!")
                            .asException());
                    return;
                }
                answer(inetAddress, responseObserver);
            } else {
                if (socketAddress instanceof InetSocketAddress) {
                    InetAddress inetAddress = ((InetSocketAddress) socketAddress).getAddress();
                    answer(inetAddress, responseObserver);
                } else {
                    responseObserver.onError(Status.INTERNAL
                            .withDescription(
                                    "Unexpected SocketAddress Type: " + socketAddress.getClass().getSimpleName())
                            .asException());
                }
            }
        }

        private void answer(InetAddress inetAddress, StreamObserver<LookupResult> responseObserver) {
            LookupResult.Builder builder = LookupResult.newBuilder().setIpString(inetAddress.toString().substring(1));
            String info;
            if (inetAddress instanceof Inet4Address) {
                if (ip4Database == null) {
                    responseObserver.onError(Status.UNIMPLEMENTED
                            .withDescription("This server does not allow IPv4 lookup!").asException());
                    return;
                }
                builder.setIsV6(false);
                info = ip4Database.get((Inet4Address) inetAddress);
            } else if (inetAddress instanceof Inet6Address) {
                if (ip6Database == null) {
                    responseObserver.onError(Status.UNIMPLEMENTED
                            .withDescription("This server does not allow IPv6 lookup!").asException());
                    return;
                }
                builder.setIsV6(true);
                info = ip6Database.get((Inet6Address) inetAddress);
            } else {
                responseObserver.onError(Status.INTERNAL
                        .withDescription("Unexpected InetAddress Type: " + inetAddress.getClass().getSimpleName())
                        .asException());
                return;
            }
            if (info != null) {
                builder.setInfo(info);
            }
            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();
        }
    }

}
