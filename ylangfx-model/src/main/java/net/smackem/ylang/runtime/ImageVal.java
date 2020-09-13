package net.smackem.ylang.runtime;

public class ImageVal extends Value {
    private final int width = 0;
    private final int height = 0;

    public ImageVal() {
        super(ValueType.IMAGE);
    }

    public int width() {
        return this.width;
    }

    public int height() {
        return this.height;
    }

    public RgbVal getPixel(int x, int y) {
        throw new UnsupportedOperationException();
    }

    public void setPixel(int x, int y, RgbVal rgb) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return "ImageVal{" +
               "width=" + width +
               ", height=" + height +
               '}';
    }
}
