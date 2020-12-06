package net.smackem.ylang.runtime;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.BiFunction;

@SuppressWarnings("DuplicatedCode")
public class ImageVal extends MatrixVal<RgbVal> {
    private final RgbVal[] pixels;

    private ImageVal(int width, int height, RgbVal[] pixels) {
        super(ValueType.IMAGE, width, height);
        if (Objects.requireNonNull(pixels).length != width * height) {
            throw new IllegalArgumentException("pixel buffer size does not match width and height");
        }
        this.pixels = pixels;
    }

    public ImageVal(int width, int height) {
        this(width, height, getPixels(width, height, RgbVal.EMPTY));
    }

    public ImageVal(int width, int height, RgbVal initialValue) {
        this(width, height, getPixels(width, height, initialValue));
    }

    public ImageVal(ImageVal original) {
        super(original);
        this.pixels = clonePixels(original.pixels);
    }

    public static ImageVal fromArgbPixels(int width, int height, int[] pixels) {
        if (width <= 0) {
            throw new IllegalArgumentException("image width must be > 0");
        }
        if (height <= 0) {
            throw new IllegalArgumentException("image height must be > 0");
        }
        if (width * height != pixels.length) {
            throw new IllegalArgumentException("width and height do not match number of pixels");
        }
        final RgbVal[] rgbPixels = new RgbVal[pixels.length];
        for (int i = 0; i < pixels.length; i++) {
            final int pixel = pixels[i];
            rgbPixels[i] = new RgbVal(pixel >> 16 & 0xff, pixel >> 8 & 0xff, pixel & 0xff, pixel >> 24 & 0xff);
        }
        return new ImageVal(width, height, rgbPixels);
    }

    public static ImageVal fromKernel(KernelVal kernel) {
        Objects.requireNonNull(kernel);
        final ImageVal image = new ImageVal(kernel.width(), kernel.height());
        for (int i = 0; i < image.pixels.length; i++) {
            final float value = RgbVal.clamp(kernel.get(i).value());
            image.pixels[i] = new RgbVal(value, value, value, 255);
        }
        return image;
    }

    public int[] toArgbPixels() {
        final int[] buffer = new int[width() * height()];
        final int pixelCount = buffer.length;
        for (int i = 0; i < pixelCount; i++) {
            buffer[i] = toIntArgb(this.pixels[i]);
        }
        return buffer;
    }

    public ImageVal convolve(KernelVal kernel) {
        final int width = width();
        final int height = height();
        final ImageVal target = new ImageVal(width, height);
        final float kernelSum = kernel.sum().value();
        final int kernelWidth = kernel.width();
        final int kernelHeight = kernel.height();
        final int halfKernelWidth = kernelWidth / 2;
        final int halfKernelHeight = kernelHeight / 2;
        final float[] kernelValues = new float[kernel.size()];
        int targetIndex = 0;
        int i = 0;
        for (final Value n : kernel) {
            kernelValues[i++] = ((NumberVal) n).value();
        }
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                float r = 0;
                float g = 0;
                float b = 0;
                float a = 255;
                int startY = y - halfKernelHeight;
                int endY = startY + kernelHeight;
                int startX = x - halfKernelWidth;
                int endX = startX + kernelWidth;
                int kernelIndex = 0;
                for (int imageY = startY; imageY < endY; imageY++) {
                    if (imageY < 0 || imageY >= height) {
                        kernelIndex += kernelWidth;
                        continue;
                    }
                    int imageIndex = imageY * width + startX;
                    for (int imageX = startX; imageX < endX; imageX++) {
                        if (imageX >= 0 && imageX < width) {
                            final float value = kernelValues[kernelIndex];
                            final RgbVal px = this.pixels[imageIndex];
                            r += value * px.r();
                            g += value * px.g();
                            b += value * px.b();
                            if (imageX == x && imageY == y) {
                                a = px.a();
                            }
                        }
                        kernelIndex++;
                        imageIndex++;
                    }
                }
                target.pixels[targetIndex] = kernelSum == 0
                        ? new RgbVal(r, g, b, a)
                        : new RgbVal(r / kernelSum, g / kernelSum, b / kernelSum, a);
                targetIndex++;
            }
        }
        return target;
    }

    public RgbVal convolve(int x, int y, KernelVal kernel) {
        final int width = width();
        final int height = height();
        final int kernelWidth = kernel.width();
        final int kernelHeight = kernel.height();
        final int halfKernelWidth = kernelWidth / 2;
        final int halfKernelHeight = kernelHeight / 2;
        float kernelSum = 0;
        float r = 0;
        float g = 0;
        float b = 0;
        float a = 255;
        int kernelIndex = 0;

        for (int kernelY = 0; kernelY < kernelHeight; kernelY++) {
            for (int kernelX = 0; kernelX < kernelWidth; kernelX++) {
                final int sourceY = y - halfKernelHeight + kernelY;
                final int sourceX = x - halfKernelWidth + kernelX;
                if (sourceX >= 0 && sourceX < width && sourceY >= 0 && sourceY < height) {
                    final float value = kernel.get(kernelIndex).value();
                    final RgbVal px = get(sourceX, sourceY);
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

    public ImageVal add(ImageVal right) {
        return composeWith(right, RgbVal::add);
    }

    public ImageVal subtract(ImageVal right) {
        return composeWith(right, RgbVal::subtract);
    }

    public ImageVal multiply(ImageVal right) {
        return composeWith(right, RgbVal::multiplyWith);
    }

    public ImageVal divide(ImageVal right) {
        return composeWith(right, RgbVal::divideBy);
    }

    public ImageVal over(ImageVal background) {
        return composeWith(background, RgbVal::over);
    }

    public ImageVal hypot(ImageVal right) {
        return composeWith(right, RgbVal::hypot);
    }

    public static ImageVal min(ImageVal a, ImageVal b) {
        return Objects.requireNonNull(a).composeWith(b, RgbVal::min);
    }

    public static ImageVal max(ImageVal a, ImageVal b) {
        return Objects.requireNonNull(a).composeWith(b, RgbVal::max);
    }

    private ImageVal composeWith(ImageVal that, BiFunction<RgbVal, RgbVal, RgbVal> operation) {
        if (Objects.requireNonNull(that).width() != width() || that.height() != this.height()) {
            throw new IllegalArgumentException("composed images must have the same dimensions");
        }
        final ImageVal result = new ImageVal(this.width(), this.height());
        for (int i = 0; i < this.pixels.length; i++) {
            result.pixels[i] = operation.apply(this.pixels[i], that.pixels[i]);
        }
        return result;
    }

    @Override
    public String toString() {
        return "image{size=%dx%d, hash=%x}".formatted(width(), height(), hashCode());
    }

    @Override
    RgbVal internalGet(int index) {
        return this.pixels[index];
    }

    @Override
    void internalSet(int index, RgbVal value) {
        this.pixels[index] = value;
    }

    private static RgbVal[] getPixels(int width, int height, RgbVal initialValue) {
        if (width <= 0) {
            throw new IllegalArgumentException("image width must be > 0");
        }
        if (height <= 0) {
            throw new IllegalArgumentException("image height must be > 0");
        }
        Objects.requireNonNull(initialValue);
        final RgbVal[] pixels = new RgbVal[width * height];
        Arrays.fill(pixels, initialValue);
        return pixels;
    }

    private static RgbVal[] clonePixels(RgbVal[] pixels) {
        final RgbVal[] clone = new RgbVal[pixels.length];
        System.arraycopy(pixels, 0, clone, 0, pixels.length);
        return clone;
    }

    private static int toIntArgb(RgbVal rgb) {
        return (int) RgbVal.clamp(rgb.a()) << 24 |
               (int) RgbVal.clamp(rgb.r()) << 16 |
               (int) RgbVal.clamp(rgb.g()) << 8 |
               (int) RgbVal.clamp(rgb.b());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ImageVal imageVal = (ImageVal) o;
        return Arrays.equals(pixels, imageVal.pixels);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(pixels);
    }
}
