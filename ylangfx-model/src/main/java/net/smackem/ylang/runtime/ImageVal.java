package net.smackem.ylang.runtime;

import java.util.Arrays;
import java.util.Objects;

public class ImageVal extends Value {
    private final int width;
    private final int height;
    private final RgbVal[] pixels;
    private IntRect clipRect;

    private ImageVal(int width, int height, RgbVal[] pixels) {
        super(ValueType.IMAGE);
        if (Objects.requireNonNull(pixels).length != width * height) {
            throw new IllegalArgumentException("pixel buffer size does not match width and height");
        }
        this.width = width;
        this.height = height;
        this.pixels = pixels;
    }

    public ImageVal(int width, int height) {
        this(width, height, emptyPixels(width, height));
    }

    public static ImageVal fromArgbPixels(int width, int height, int[] pixels) {
        if (width <= 0) {
            throw new IllegalArgumentException("image width must be > 0");
        }
        if (height <= 0) {
            throw new IllegalArgumentException("image height must be > 0");
        }
        final RgbVal[] rgbPixels = new RgbVal[pixels.length];
        for (int i = 0; i < pixels.length; i++) {
            final int pixel = pixels[i];
            rgbPixels[i] = new RgbVal(pixel >> 16 & 0xff, pixel >> 8 & 0xff, pixel & 0xff, pixel >> 24 & 0xff);
        }
        return new ImageVal(width, height, rgbPixels);
    }

    public int width() {
        return this.width;
    }

    public int height() {
        return this.height;
    }

    public IntRect getClipRect() {
        return this.clipRect;
    }

    public void setClipRect(IntRect value) {
        this.clipRect = value;
    }

    public RgbVal getPixel(int x, int y) {
        final int index = y * this.width + x;
        return this.pixels[index];
    }

    public void setPixel(int x, int y, RgbVal rgb) {
        if (this.clipRect != null && this.clipRect.contains(x, y) == false) {
            return;
        }
        final int index = y * this.width + x;
        this.pixels[index] = rgb;
    }

    public int[] toArgbPixels() {
        final int[] buffer = new int[this.width * this.height];
        final int pixelCount = buffer.length;
        for (int i = 0; i < pixelCount; i++) {
            buffer[i] = toIntArgb(this.pixels[i]);
        }
        return buffer;
    }

    public RgbVal convolute(int x, int y, KernelVal kernel) {
        final int kernelWidth = kernel.width();
        final int kernelHeight = kernel.height();
        float kernelSum = 0;
        float r = 0;
        float g = 0;
        float b = 0;
        float a = 255;
        int kernelIndex = 0;

        for (int kernelY = 0; kernelY < kernelHeight; kernelY++) {
            for (int kernelX = 0; kernelX < kernelWidth; kernelX++) {
                final int sourceY = y - (kernelHeight / 2) + kernelY;
                final int sourceX = x - (kernelWidth / 2) + kernelX;
                if (sourceX >= 0 && sourceX < this.width && sourceY >= 0 && sourceY < this.height) {
                    final float value = kernel.get(kernelIndex).value();
                    final RgbVal px = getPixel(sourceX, sourceY);
                    r += value * px.r();
                    g += value * px.g();
                    b += value * px.b();
                    kernelSum += value;

                    if (sourceX == x && sourceY == y) {
                        a = px.a();
                    }
                }
                kernelIndex++;
            }
        }
        if (kernelSum == 0) {
            return new RgbVal(r, g, b, a);
        }

        return new RgbVal(r / kernelSum, g / kernelSum, b / kernelSum, a);
    }

    private static RgbVal[] emptyPixels(int width, int height) {
        if (width <= 0) {
            throw new IllegalArgumentException("image width must be > 0");
        }
        if (height <= 0) {
            throw new IllegalArgumentException("image height must be > 0");
        }
        final RgbVal[] pixels = new RgbVal[width * height];
        Arrays.fill(pixels, RgbVal.EMPTY);
        return pixels;
    }

    private static int toIntArgb(RgbVal rgb) {
        return (int) RgbVal.clamp(rgb.a()) << 24
               | (int) RgbVal.clamp(rgb.r()) << 16
               | (int) RgbVal.clamp(rgb.g()) << 8
               | (int) RgbVal.clamp(rgb.b());
    }

    @Override
    public String toString() {
        return "ImageVal{" +
               "width=" + width +
               ", height=" + height +
               '}';
    }
}
