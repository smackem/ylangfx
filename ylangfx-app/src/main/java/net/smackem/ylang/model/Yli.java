package net.smackem.ylang.model;

import javafx.scene.image.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class Yli {
    private Yli() {}

    public static final String FILE_EXTENSION = ".yli";

    public static void saveImage(Image image, Path path) throws IOException {
        final PixelReader pixelReader = image.getPixelReader();
        final int width = (int) image.getWidth();
        final int height = (int) image.getHeight();
        final ByteBuffer header = ByteBuffer.allocate(16);
        header.putInt(width);
        header.putInt(height);
        header.putInt(0);
        header.putInt(0);
        final byte[] buffer = new byte[width * height * 4];
        pixelReader.getPixels(0, 0, width, height, PixelFormat.getByteBgraInstance(), buffer, 0, width * 4);
        try (final OutputStream os = Files.newOutputStream(path, StandardOpenOption.CREATE)) {
            os.write(header.array(), 0, header.position());
            os.write(buffer);
        }
    }

    public static Image loadImage(Path path) throws IOException {
        try (final InputStream is = Files.newInputStream(path, StandardOpenOption.READ)) {
            final byte[] headerBuf = is.readNBytes(16);
            final ByteBuffer header = ByteBuffer.wrap(headerBuf);
            final int width = header.getInt();
            final int height = header.getInt();
            final byte[] buffer = is.readNBytes(width * height * 4);
            final WritableImage wImage = new WritableImage(width, height);
            final PixelWriter pixelWriter = wImage.getPixelWriter();
            pixelWriter.setPixels(0, 0, width, height, PixelFormat.getByteBgraInstance(), buffer, 0, width * 4);
            return wImage;
        }
    }
}
