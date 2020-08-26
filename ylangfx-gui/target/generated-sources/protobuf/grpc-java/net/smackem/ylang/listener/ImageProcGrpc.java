package net.smackem.ylang.listener;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.25.0)",
    comments = "Source: listener.proto")
public final class ImageProcGrpc {

  private ImageProcGrpc() {}

  public static final String SERVICE_NAME = "listener.ImageProc";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<net.smackem.ylang.listener.YLangProtos.ProcessImageRequest,
      net.smackem.ylang.listener.YLangProtos.ProcessImageResponse> getProcessImageMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ProcessImage",
      requestType = net.smackem.ylang.listener.YLangProtos.ProcessImageRequest.class,
      responseType = net.smackem.ylang.listener.YLangProtos.ProcessImageResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
  public static io.grpc.MethodDescriptor<net.smackem.ylang.listener.YLangProtos.ProcessImageRequest,
      net.smackem.ylang.listener.YLangProtos.ProcessImageResponse> getProcessImageMethod() {
    io.grpc.MethodDescriptor<net.smackem.ylang.listener.YLangProtos.ProcessImageRequest, net.smackem.ylang.listener.YLangProtos.ProcessImageResponse> getProcessImageMethod;
    if ((getProcessImageMethod = ImageProcGrpc.getProcessImageMethod) == null) {
      synchronized (ImageProcGrpc.class) {
        if ((getProcessImageMethod = ImageProcGrpc.getProcessImageMethod) == null) {
          ImageProcGrpc.getProcessImageMethod = getProcessImageMethod =
              io.grpc.MethodDescriptor.<net.smackem.ylang.listener.YLangProtos.ProcessImageRequest, net.smackem.ylang.listener.YLangProtos.ProcessImageResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ProcessImage"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  net.smackem.ylang.listener.YLangProtos.ProcessImageRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  net.smackem.ylang.listener.YLangProtos.ProcessImageResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ImageProcMethodDescriptorSupplier("ProcessImage"))
              .build();
        }
      }
    }
    return getProcessImageMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static ImageProcStub newStub(io.grpc.Channel channel) {
    return new ImageProcStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static ImageProcBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new ImageProcBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static ImageProcFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new ImageProcFutureStub(channel);
  }

  /**
   */
  public static abstract class ImageProcImplBase implements io.grpc.BindableService {

    /**
     */
    public io.grpc.stub.StreamObserver<net.smackem.ylang.listener.YLangProtos.ProcessImageRequest> processImage(
        io.grpc.stub.StreamObserver<net.smackem.ylang.listener.YLangProtos.ProcessImageResponse> responseObserver) {
      return asyncUnimplementedStreamingCall(getProcessImageMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getProcessImageMethod(),
            asyncBidiStreamingCall(
              new MethodHandlers<
                net.smackem.ylang.listener.YLangProtos.ProcessImageRequest,
                net.smackem.ylang.listener.YLangProtos.ProcessImageResponse>(
                  this, METHODID_PROCESS_IMAGE)))
          .build();
    }
  }

  /**
   */
  public static final class ImageProcStub extends io.grpc.stub.AbstractStub<ImageProcStub> {
    private ImageProcStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ImageProcStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ImageProcStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ImageProcStub(channel, callOptions);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<net.smackem.ylang.listener.YLangProtos.ProcessImageRequest> processImage(
        io.grpc.stub.StreamObserver<net.smackem.ylang.listener.YLangProtos.ProcessImageResponse> responseObserver) {
      return asyncBidiStreamingCall(
          getChannel().newCall(getProcessImageMethod(), getCallOptions()), responseObserver);
    }
  }

  /**
   */
  public static final class ImageProcBlockingStub extends io.grpc.stub.AbstractStub<ImageProcBlockingStub> {
    private ImageProcBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ImageProcBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ImageProcBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ImageProcBlockingStub(channel, callOptions);
    }
  }

  /**
   */
  public static final class ImageProcFutureStub extends io.grpc.stub.AbstractStub<ImageProcFutureStub> {
    private ImageProcFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ImageProcFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ImageProcFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ImageProcFutureStub(channel, callOptions);
    }
  }

  private static final int METHODID_PROCESS_IMAGE = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final ImageProcImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(ImageProcImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_PROCESS_IMAGE:
          return (io.grpc.stub.StreamObserver<Req>) serviceImpl.processImage(
              (io.grpc.stub.StreamObserver<net.smackem.ylang.listener.YLangProtos.ProcessImageResponse>) responseObserver);
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class ImageProcBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    ImageProcBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return net.smackem.ylang.listener.YLangProtos.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("ImageProc");
    }
  }

  private static final class ImageProcFileDescriptorSupplier
      extends ImageProcBaseDescriptorSupplier {
    ImageProcFileDescriptorSupplier() {}
  }

  private static final class ImageProcMethodDescriptorSupplier
      extends ImageProcBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    ImageProcMethodDescriptorSupplier(String methodName) {
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
      synchronized (ImageProcGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new ImageProcFileDescriptorSupplier())
              .addMethod(getProcessImageMethod())
              .build();
        }
      }
    }
    return result;
  }
}
