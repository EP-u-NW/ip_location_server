package de.ep_u_nw.ip_location_server.grpc.generated;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 * Exceptions are with respect to https://grpc.github.io/grpc-java/javadoc/io/grpc/Status.html
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.37.1)",
    comments = "Source: ip_location_server.proto")
public final class IPLocationServiceGrpc {

  private IPLocationServiceGrpc() {}

  public static final String SERVICE_NAME = "ip_location_server.IPLocationService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.google.protobuf.Empty,
      de.ep_u_nw.ip_location_server.grpc.generated.LookupResult> getMeMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Me",
      requestType = com.google.protobuf.Empty.class,
      responseType = de.ep_u_nw.ip_location_server.grpc.generated.LookupResult.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.google.protobuf.Empty,
      de.ep_u_nw.ip_location_server.grpc.generated.LookupResult> getMeMethod() {
    io.grpc.MethodDescriptor<com.google.protobuf.Empty, de.ep_u_nw.ip_location_server.grpc.generated.LookupResult> getMeMethod;
    if ((getMeMethod = IPLocationServiceGrpc.getMeMethod) == null) {
      synchronized (IPLocationServiceGrpc.class) {
        if ((getMeMethod = IPLocationServiceGrpc.getMeMethod) == null) {
          IPLocationServiceGrpc.getMeMethod = getMeMethod =
              io.grpc.MethodDescriptor.<com.google.protobuf.Empty, de.ep_u_nw.ip_location_server.grpc.generated.LookupResult>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Me"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.protobuf.Empty.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  de.ep_u_nw.ip_location_server.grpc.generated.LookupResult.getDefaultInstance()))
              .setSchemaDescriptor(new IPLocationServiceMethodDescriptorSupplier("Me"))
              .build();
        }
      }
    }
    return getMeMethod;
  }

  private static volatile io.grpc.MethodDescriptor<de.ep_u_nw.ip_location_server.grpc.generated.IP,
      de.ep_u_nw.ip_location_server.grpc.generated.LookupResult> getLookupMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Lookup",
      requestType = de.ep_u_nw.ip_location_server.grpc.generated.IP.class,
      responseType = de.ep_u_nw.ip_location_server.grpc.generated.LookupResult.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<de.ep_u_nw.ip_location_server.grpc.generated.IP,
      de.ep_u_nw.ip_location_server.grpc.generated.LookupResult> getLookupMethod() {
    io.grpc.MethodDescriptor<de.ep_u_nw.ip_location_server.grpc.generated.IP, de.ep_u_nw.ip_location_server.grpc.generated.LookupResult> getLookupMethod;
    if ((getLookupMethod = IPLocationServiceGrpc.getLookupMethod) == null) {
      synchronized (IPLocationServiceGrpc.class) {
        if ((getLookupMethod = IPLocationServiceGrpc.getLookupMethod) == null) {
          IPLocationServiceGrpc.getLookupMethod = getLookupMethod =
              io.grpc.MethodDescriptor.<de.ep_u_nw.ip_location_server.grpc.generated.IP, de.ep_u_nw.ip_location_server.grpc.generated.LookupResult>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Lookup"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  de.ep_u_nw.ip_location_server.grpc.generated.IP.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  de.ep_u_nw.ip_location_server.grpc.generated.LookupResult.getDefaultInstance()))
              .setSchemaDescriptor(new IPLocationServiceMethodDescriptorSupplier("Lookup"))
              .build();
        }
      }
    }
    return getLookupMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static IPLocationServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<IPLocationServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<IPLocationServiceStub>() {
        @java.lang.Override
        public IPLocationServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new IPLocationServiceStub(channel, callOptions);
        }
      };
    return IPLocationServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static IPLocationServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<IPLocationServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<IPLocationServiceBlockingStub>() {
        @java.lang.Override
        public IPLocationServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new IPLocationServiceBlockingStub(channel, callOptions);
        }
      };
    return IPLocationServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static IPLocationServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<IPLocationServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<IPLocationServiceFutureStub>() {
        @java.lang.Override
        public IPLocationServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new IPLocationServiceFutureStub(channel, callOptions);
        }
      };
    return IPLocationServiceFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   * Exceptions are with respect to https://grpc.github.io/grpc-java/javadoc/io/grpc/Status.html
   * </pre>
   */
  public static abstract class IPLocationServiceImplBase implements io.grpc.BindableService {

    /**
     * <pre>
     * Looks up information about the peer that made the request.
     * Throws INVALID_ARGUMENT if GRPC_ME_USE_HEADER is enabled, but GRPC_ME_HEADER_NAME did not contain a valid IPv4 or IPv6.
     * Throws UNIMPLEMENTED if the supplied IP is IPv? but IPv? lookup is not enabled in the configuration. Here, ? is placeholder for either 4 or 6.
     * Throws INTERNAL on internal failures.
     * </pre>
     */
    public void me(com.google.protobuf.Empty request,
        io.grpc.stub.StreamObserver<de.ep_u_nw.ip_location_server.grpc.generated.LookupResult> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getMeMethod(), responseObserver);
    }

    /**
     * <pre>
     * Looks up information about the supplied IP.
     * Throws INVALID_ARGUMENT if the supplied IP was not a valid IPv4 or IPv6.
     * Throws UNIMPLEMENTED if the supplied IP is IPv? but IPv? lookup is not enabled in the configuration. Here, ? is placeholder for either 4 or 6.
     * Throws INTERNAL on internal failures.
     * </pre>
     */
    public void lookup(de.ep_u_nw.ip_location_server.grpc.generated.IP request,
        io.grpc.stub.StreamObserver<de.ep_u_nw.ip_location_server.grpc.generated.LookupResult> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getLookupMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getMeMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.google.protobuf.Empty,
                de.ep_u_nw.ip_location_server.grpc.generated.LookupResult>(
                  this, METHODID_ME)))
          .addMethod(
            getLookupMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                de.ep_u_nw.ip_location_server.grpc.generated.IP,
                de.ep_u_nw.ip_location_server.grpc.generated.LookupResult>(
                  this, METHODID_LOOKUP)))
          .build();
    }
  }

  /**
   * <pre>
   * Exceptions are with respect to https://grpc.github.io/grpc-java/javadoc/io/grpc/Status.html
   * </pre>
   */
  public static final class IPLocationServiceStub extends io.grpc.stub.AbstractAsyncStub<IPLocationServiceStub> {
    private IPLocationServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected IPLocationServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new IPLocationServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     * Looks up information about the peer that made the request.
     * Throws INVALID_ARGUMENT if GRPC_ME_USE_HEADER is enabled, but GRPC_ME_HEADER_NAME did not contain a valid IPv4 or IPv6.
     * Throws UNIMPLEMENTED if the supplied IP is IPv? but IPv? lookup is not enabled in the configuration. Here, ? is placeholder for either 4 or 6.
     * Throws INTERNAL on internal failures.
     * </pre>
     */
    public void me(com.google.protobuf.Empty request,
        io.grpc.stub.StreamObserver<de.ep_u_nw.ip_location_server.grpc.generated.LookupResult> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getMeMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Looks up information about the supplied IP.
     * Throws INVALID_ARGUMENT if the supplied IP was not a valid IPv4 or IPv6.
     * Throws UNIMPLEMENTED if the supplied IP is IPv? but IPv? lookup is not enabled in the configuration. Here, ? is placeholder for either 4 or 6.
     * Throws INTERNAL on internal failures.
     * </pre>
     */
    public void lookup(de.ep_u_nw.ip_location_server.grpc.generated.IP request,
        io.grpc.stub.StreamObserver<de.ep_u_nw.ip_location_server.grpc.generated.LookupResult> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getLookupMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * <pre>
   * Exceptions are with respect to https://grpc.github.io/grpc-java/javadoc/io/grpc/Status.html
   * </pre>
   */
  public static final class IPLocationServiceBlockingStub extends io.grpc.stub.AbstractBlockingStub<IPLocationServiceBlockingStub> {
    private IPLocationServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected IPLocationServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new IPLocationServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * Looks up information about the peer that made the request.
     * Throws INVALID_ARGUMENT if GRPC_ME_USE_HEADER is enabled, but GRPC_ME_HEADER_NAME did not contain a valid IPv4 or IPv6.
     * Throws UNIMPLEMENTED if the supplied IP is IPv? but IPv? lookup is not enabled in the configuration. Here, ? is placeholder for either 4 or 6.
     * Throws INTERNAL on internal failures.
     * </pre>
     */
    public de.ep_u_nw.ip_location_server.grpc.generated.LookupResult me(com.google.protobuf.Empty request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getMeMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Looks up information about the supplied IP.
     * Throws INVALID_ARGUMENT if the supplied IP was not a valid IPv4 or IPv6.
     * Throws UNIMPLEMENTED if the supplied IP is IPv? but IPv? lookup is not enabled in the configuration. Here, ? is placeholder for either 4 or 6.
     * Throws INTERNAL on internal failures.
     * </pre>
     */
    public de.ep_u_nw.ip_location_server.grpc.generated.LookupResult lookup(de.ep_u_nw.ip_location_server.grpc.generated.IP request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getLookupMethod(), getCallOptions(), request);
    }
  }

  /**
   * <pre>
   * Exceptions are with respect to https://grpc.github.io/grpc-java/javadoc/io/grpc/Status.html
   * </pre>
   */
  public static final class IPLocationServiceFutureStub extends io.grpc.stub.AbstractFutureStub<IPLocationServiceFutureStub> {
    private IPLocationServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected IPLocationServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new IPLocationServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * Looks up information about the peer that made the request.
     * Throws INVALID_ARGUMENT if GRPC_ME_USE_HEADER is enabled, but GRPC_ME_HEADER_NAME did not contain a valid IPv4 or IPv6.
     * Throws UNIMPLEMENTED if the supplied IP is IPv? but IPv? lookup is not enabled in the configuration. Here, ? is placeholder for either 4 or 6.
     * Throws INTERNAL on internal failures.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<de.ep_u_nw.ip_location_server.grpc.generated.LookupResult> me(
        com.google.protobuf.Empty request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getMeMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Looks up information about the supplied IP.
     * Throws INVALID_ARGUMENT if the supplied IP was not a valid IPv4 or IPv6.
     * Throws UNIMPLEMENTED if the supplied IP is IPv? but IPv? lookup is not enabled in the configuration. Here, ? is placeholder for either 4 or 6.
     * Throws INTERNAL on internal failures.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<de.ep_u_nw.ip_location_server.grpc.generated.LookupResult> lookup(
        de.ep_u_nw.ip_location_server.grpc.generated.IP request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getLookupMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_ME = 0;
  private static final int METHODID_LOOKUP = 1;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final IPLocationServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(IPLocationServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_ME:
          serviceImpl.me((com.google.protobuf.Empty) request,
              (io.grpc.stub.StreamObserver<de.ep_u_nw.ip_location_server.grpc.generated.LookupResult>) responseObserver);
          break;
        case METHODID_LOOKUP:
          serviceImpl.lookup((de.ep_u_nw.ip_location_server.grpc.generated.IP) request,
              (io.grpc.stub.StreamObserver<de.ep_u_nw.ip_location_server.grpc.generated.LookupResult>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class IPLocationServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    IPLocationServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return de.ep_u_nw.ip_location_server.grpc.generated.IpLocationServer.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("IPLocationService");
    }
  }

  private static final class IPLocationServiceFileDescriptorSupplier
      extends IPLocationServiceBaseDescriptorSupplier {
    IPLocationServiceFileDescriptorSupplier() {}
  }

  private static final class IPLocationServiceMethodDescriptorSupplier
      extends IPLocationServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    IPLocationServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (IPLocationServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new IPLocationServiceFileDescriptorSupplier())
              .addMethod(getMeMethod())
              .addMethod(getLookupMethod())
              .build();
        }
      }
    }
    return result;
  }
}
