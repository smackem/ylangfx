package net.smackem.ylang.model;

import javafx.scene.image.*;
import net.smackem.ylang.runtime.ImageVal;

public class ImageConversion {
    private ImageConversion() {}

    public static ImageVal convertFromFX(Image image) {
        if (image == null) {
            return null;
        }
        final PixelReader pixelReader = image.getPixelReader();
        final int width = (int) image.getWidth();
        final int height = (int) image.getHeight();
        final int[] buffer = new int[width * height];
        pixelReader.getPixels(0, 0, width, height, PixelFormat.getIntArgbInstance(), buffer, 0, width);
        return ImageVal.fromArgbPixels(width, height, buffer);
    }

    public static Image convertToFX(ImageVal image) {
        final int width = image.width();
        final int height = image.height();
        final WritableImage wImage = new WritableImage(width, height);
        final PixelWriter pixelWriter = wImage.getPixelWriter();
        final int[] buffer = image.toArgbPixels();
        pixelWriter.setPixels(0, 0, width, height, PixelFormat.getIntArgbInstance(), buffer, 0, width);
        return wImage;
    }
}
