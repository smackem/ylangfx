package net.smackem.ylang.model;

import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import net.smackem.ylang.listener.ImageProcGrpc;
import net.smackem.ylang.listener.YLangProtos.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoteImageProcService implements AutoCloseable {
    private final ImageProcGrpc.ImageProcBlockingStub stub;
    private final ManagedChannel channel;
    private final Logger log = LoggerFactory.getLogger(RemoteImageProcService.class);

    public RemoteImageProcService(String host, int port) {
        this.channel = ManagedChannelBuilder.forAddress(host, port)
                // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
                // needing certificates.
                .usePlaintext()
                .build();
        this.stub = ImageProcGrpc.newBlockingStub(channel);
    }

    public void sayHello(String text) {
        final SimpleMsg request = SimpleMsg.newBuilder()
                .setText(text)
                .build();
        final SimpleMsg response = this.stub.sayHello(request);

        log.info(response.getText());
    }

    public ProcessImageResult processImage(String sourceCode, byte[] imageDataPng) {
        final ProcessImageRequest request = ProcessImageRequest.newBuilder()
                .setSourceCode(sourceCode)
                .setImageDataPng(ByteString.copyFrom(imageDataPng))
                .build();
        final ProcessImageResponse response = this.stub.processImage(request);
        return new ProcessImageResult() {
            @Override
            public boolean isSuccess() {
                return response.getResult() == ProcessImageResponse.CompilationResult.OK;
            }

            @Override
            public String getMessage() {
                return response.getMessage();
            }

            @Override
            public byte[] getImageDataPng() {
                return response.getImageDataPng().toByteArray();
            }
        };
    }

    @Override
    public void close() throws Exception {
        this.channel.shutdownNow();
    }
}
