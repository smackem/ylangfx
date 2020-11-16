package net.smackem.ylang.model;

import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import net.smackem.ylang.listener.ImageProcGrpc;
import net.smackem.ylang.listener.YLangProtos.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.*;

public class RemoteImageProcService implements AutoCloseable {
    private static final Logger log = LoggerFactory.getLogger(RemoteImageProcService.class);
    private final ImageProcGrpc.ImageProcStub stub;
    private final ManagedChannel channel;
    private final ExecutorService rpcExecutor;
    private final Executor callbackExecutor;

    public RemoteImageProcService(String host, int port, Executor executor) {
        this.rpcExecutor = Executors.newSingleThreadExecutor();
        this.callbackExecutor = executor;
        this.channel = ManagedChannelBuilder.forAddress(host, port)
                // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
                // needing certificates.
                .usePlaintext()
                .executor(this.rpcExecutor)
                .build();
        this.stub = ImageProcGrpc.newStub(channel);
    }

    public CompletableFuture<ProcessImageResult> processImage(String source, byte[] imageDataPng) {
        final CompletableFuture<ProcessImageResponse> future = new CompletableFuture<>();
        final StreamObserver<ProcessImageRequest> writer =
            this.stub.processImage(new ProcessImageResponseReader(future));

        this.rpcExecutor.submit(() -> {
            try {
                writeProcessImageRequest(source, imageDataPng, writer);
            } catch (Throwable e) {
                writer.onError(e);
                log.error("error writing request", e);
                throw e;
            }
            writer.onCompleted();
        });

        return future.thenApplyAsync(response -> new ProcessImageResult() {
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

            @Override
            public String getLogOutput() {
                return response.getLogOutput();
            }
        }, this.callbackExecutor);
    }

    private void writeProcessImageRequest(String source, byte[] imageDataPng, StreamObserver<ProcessImageRequest> writer) {
        final int chunkSize = 64 * 1024;
        int index = 0;
        boolean isFirstMessage = true;
        for (int remaining = imageDataPng.length; remaining > 0 || isFirstMessage; ) {
            final int toWrite = Math.min(remaining, chunkSize);
            final ProcessImageRequest.Builder request = ProcessImageRequest.newBuilder()
                    .setImageDataPng(ByteString.copyFrom(imageDataPng, index, toWrite));
            if (isFirstMessage) {
                request.setSourceCode(source);
                isFirstMessage = false;
            }
            writer.onNext(request.build());
            index += toWrite;
            remaining -= toWrite;
        }
    }

    private static final class ProcessImageResponseReader implements StreamObserver<ProcessImageResponse> {
        private final CompletableFuture<ProcessImageResponse> future;
        private final ProcessImageResponse.Builder builder = ProcessImageResponse.newBuilder();
        private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        private boolean isFirstMessage = true;

        ProcessImageResponseReader(CompletableFuture<ProcessImageResponse> future) {
            this.future = future;
        }

        @Override
        public void onNext(ProcessImageResponse response) {
            if (this.isFirstMessage) {
                this.builder
                        .setResultValue(response.getResultValue())
                        .setMessage(response.getMessage())
                        .setLogOutput(response.getLogOutput());
                this.isFirstMessage = false;
            }
            try {
                response.getImageDataPng().writeTo(buffer);
            } catch (IOException e) {
                log.error("error receiving response from server", e);
            }
        }

        @Override
        public void onError(Throwable throwable) {
            future.completeExceptionally(throwable);
        }

        @Override
        public void onCompleted() {
            this.builder.setImageDataPng(ByteString.copyFrom(buffer.toByteArray()));
            future.complete(this.builder.build());
        }
    }

    @Override
    public void close() throws Exception {
        this.channel.shutdownNow();
        this.rpcExecutor.shutdown();
        try {
            if (this.rpcExecutor.awaitTermination(60, TimeUnit.SECONDS) == false) {
                this.rpcExecutor.shutdownNow();
                if (this.rpcExecutor.awaitTermination(60, TimeUnit.SECONDS) == false) {
                    log.error("Pool did not terminate");
                }
            }
        } catch (InterruptedException ie) {
            this.rpcExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
