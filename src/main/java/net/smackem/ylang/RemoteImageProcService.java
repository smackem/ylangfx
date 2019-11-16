package net.smackem.ylang;

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
        final SimpleMsg result = this.stub.sayHello(SimpleMsg.newBuilder()
                .setText(text)
                .build());

        log.info(result.getText());
    }

    @Override
    public void close() throws Exception {
        this.channel.shutdownNow();
    }
}
