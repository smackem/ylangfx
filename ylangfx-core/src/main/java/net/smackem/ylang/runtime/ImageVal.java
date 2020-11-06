package net.smackem.ylang.runtime;

import java.util.Arrays;
import java.util.Objects;

public class ImageVal extends Value {
    private final int width;
    private final int height;
    private final RgbVal[] pixels;
    private IntRect clipRect;
    private RgbVal defaultPixel;

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

    @SuppressWarnings("CopyConstructorMissesField")
    public ImageVal(ImageVal original) {
        this(original.width, original.height, clonePixels(original.pixels));
        this.clipRect = original.clipRect;
        this.defaultPixel = original.defaultPixel;
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

    /**
     * @return the {@link Value} to return in {{@link #getPixel(int, int)}} for coordinates that are out of bounds.
     */
    public RgbVal getDefaultPixel() {
        return this.defaultPixel;
    }

    /**
     * sets the {@link Value} to return in {{@link #getPixel(int, int)}} for coordinates that are out of bounds.
     */
    public void setDefaultPixel(RgbVal value) {
        this.defaultPixel = value;
    }

    public RgbVal getPixel(int x, int y) {
        if (y < 0 || y >= this.height || x < 0 || x >= this.width) {
            if (this.defaultPixel != null) {
                return this.defaultPixel;
            }
            throw new IllegalArgumentException("coordinates out of range " + x + "," + y);
        }
        final int index = y * this.width + x;
        return this.pixels[index];
    }

    public void setPixel(int x, int y, RgbVal rgb) {
        if (this.clipRect != null && this.clipRect.contains(x, y) == false) {
            return;
        }
        if (y < 0 || y >= this.height || x < 0 || x >= this.width) {
            throw new IllegalArgumentException("coordinates out of range " + x + "," + y);
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

    public RgbVal convolve(int x, int y, KernelVal kernel) {
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

    public KernelVal selectKernel(int x, int y, KernelVal kernel, ToFloatFunction<RgbVal> selector) {
        final int kernelWidth = kernel.width();
        final int kernelHeight = kernel.height();
        final KernelVal result = new KernelVal(kernelWidth, kernelHeight, 0);
        int kernelIndex = 0;

        for (int kernelY = 0; kernelY < kernelHeight; kernelY++) {
            for (int kernelX = 0; kernelX < kernelWidth; kernelX++) {
                final int sourceY = y - (kernelHeight / 2) + kernelY;
                final int sourceX = x - (kernelWidth / 2) + kernelX;
                if (sourceX >= 0 && sourceX < this.width && sourceY >= 0 && sourceY < this.height) {
                    final float value = kernel.get(kernelIndex).value();
                    final RgbVal px = getPixel(sourceX, sourceY);
                    final float f = selector.apply(px);
                    result.set(kernelIndex, new NumberVal(value * f));
                }
                kernelIndex++;
            }
        }

        return result;
    }

    public void plot(GeometryVal geometry, RgbVal rgb) {
        for (final Value v : geometry) {
            final PointVal pt = (PointVal) v;
            this.setPixel((int) pt.x(), (int) pt.y(), rgb);
        }
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

    private static RgbVal[] clonePixels(RgbVal[] pixels) {
        final RgbVal[] clone = new RgbVal[pixels.length];
        System.arraycopy(pixels, 0, clone, 0, pixels.length);
        return clone;
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
