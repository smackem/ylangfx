package net.smackem.ylang.runtime;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.BiFunction;

@SuppressWarnings("DuplicatedCode")
public class ImageVal_IntArgb extends MatrixVal<RgbVal> {
    private final int[] pixels;

    private ImageVal_IntArgb(int width, int height, int[] pixels) {
        super(ValueType.IMAGE, width, height);
        if (Objects.requireNonNull(pixels).length != width * height) {
            throw new IllegalArgumentException("pixel buffer size does not match width and height");
        }
        this.pixels = pixels;
    }

    public ImageVal_IntArgb(int width, int height) {
        this(width, height, getPixels(width, height, 0));
    }

    public ImageVal_IntArgb(int width, int height, RgbVal initialValue) {
        this(width, height, getPixels(width, height, toIntArgb(initialValue)));
    }

    public ImageVal_IntArgb(ImageVal_IntArgb original) {
        super(original);
        this.pixels = clonePixels(original.pixels);
    }

    public static ImageVal_IntArgb fromArgbPixels(int width, int height, int[] pixels) {
        if (width <= 0) {
            throw new IllegalArgumentException("image width must be > 0");
        }
        if (height <= 0) {
            throw new IllegalArgumentException("image height must be > 0");
        }
        if (width * height != pixels.length) {
            throw new IllegalArgumentException("width and height do not match number of pixels");
        }
        return new ImageVal_IntArgb(width, height, pixels);
    }

    public static ImageVal_IntArgb fromKernel(KernelVal kernel) {
        Objects.requireNonNull(kernel);
        final ImageVal_IntArgb image = new ImageVal_IntArgb(kernel.width(), kernel.height());
        for (int i = 0; i < image.pixels.length; i++) {
            final int value = (int) RgbVal.clamp(kernel.get(i).value());
            image.pixels[i] = 255 << 24 | value << 16 | value << 8 | value;
        }
        return image;
    }

    public int[] toArgbPixels() {
        return this.pixels;
    }

    public ImageVal_IntArgb convolve(KernelVal kernel) {
        final int width = width();
        final int height = height();
        final ImageVal_IntArgb target = new ImageVal_IntArgb(width, height);
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
                            final int px = this.pixels[imageIndex];
                            r += value * red(px);
                            g += value * green(px);
                            b += value * blue(px);
                            if (imageX == x && imageY == y) {
                                a = alpha(px);
                            }
                        }
                        kernelIndex++;
                        imageIndex++;
                    }
                }
                target.pixels[targetIndex] = kernelSum == 0
                        ? toIntArgb(r, g, b, a)
                        : toIntArgb(r / kernelSum, g / kernelSum, b / kernelSum, a);
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

    public ImageVal_IntArgb add(ImageVal_IntArgb right) {
        return composeWith(right, RgbVal::add);
    }

    public ImageVal_IntArgb subtract(ImageVal_IntArgb right) {
        return composeWith(right, RgbVal::subtract);
    }

    public ImageVal_IntArgb multiply(ImageVal_IntArgb right) {
        return composeWith(right, RgbVal::multiplyWith);
    }

    public ImageVal_IntArgb divide(ImageVal_IntArgb right) {
        return composeWith(right, RgbVal::divideBy);
    }

    public ImageVal_IntArgb composeWith(ImageVal_IntArgb that, BiFunction<RgbVal, RgbVal, RgbVal> operation) {
        if (Objects.requireNonNull(that).width() != width() || that.height() != this.height()) {
            throw new IllegalArgumentException("composed images must have the same dimensions");
        }
        final ImageVal_IntArgb result = new ImageVal_IntArgb(this.width(), this.height());
        for (int i = 0; i < this.pixels.length; i++) {
            result.pixels[i] = toIntArgb(operation.apply(toRgbVal(this.pixels[i]), toRgbVal(that.pixels[i])));
        }
        return result;
    }

    @Override
    public String toString() {
        return "image{size=%dx%d, hash=%x}".formatted(width(), height(), hashCode());
    }

    @Override
    RgbVal internalGet(int index) {
        return toRgbVal(this.pixels[index]);
    }

    @Override
    void internalSet(int index, RgbVal value) {
        this.pixels[index] = toIntArgb(value);
    }

    private static int[] getPixels(int width, int height, int initialValue) {
        if (width <= 0) {
            throw new IllegalArgumentException("image width must be > 0");
        }
        if (height <= 0) {
            throw new IllegalArgumentException("image height must be > 0");
        }
        final int[] pixels = new int[width * height];
        Arrays.fill(pixels, initialValue);
        return pixels;
    }

    private static int[] clonePixels(int[] pixels) {
        final int[] clone = new int[pixels.length];
        System.arraycopy(pixels, 0, clone, 0, pixels.length);
        return clone;
    }

    private static int toIntArgb(RgbVal rgb) {
        return toIntArgb(rgb.r(), rgb.g(), rgb.b(), rgb.a());
    }

    private static int toIntArgb(float r, float g, float b, float a) {
        return (int) RgbVal.clamp(a) << 24 |
               (int) RgbVal.clamp(r) << 16 |
               (int) RgbVal.clamp(g) << 8 |
               (int) RgbVal.clamp(b);
    }

    private static RgbVal toRgbVal(int argb) {
        return new RgbVal(red(argb), green(argb), blue(argb), alpha(argb));
    }

    private static int red(int argb) {
        return argb >> 16 & 0xff;
    }

    private static int green(int argb) {
        return argb >> 8 & 0xff;
    }

    private static int blue(int argb) {
        return argb & 0xff;
    }

    private static int alpha(int argb) {
        return argb >> 24 & 0xff;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ImageVal_IntArgb ImageVal_IntArgb = (ImageVal_IntArgb) o;
        return Arrays.equals(pixels, ImageVal_IntArgb.pixels);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(pixels);
    }
}
