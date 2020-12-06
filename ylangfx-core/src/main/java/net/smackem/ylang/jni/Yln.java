package net.smackem.ylang.jni;

public class Yln {
    public native int[] convolveImage(int width, int height, int[] pixels,
                                      int kernelWidth, int kernelHeight, float[] kernelValues);

    public native float[] convolveKernel(int width, int height, float[] values,
                                         int kernelWidth, int kernelHeight, float[] kernelValues);

    public int[] composeImages(int width, int height, int[] leftPixels,
                               int[] rightPixels, MatrixComposition composition) {
        return this.composeImages(width, height, leftPixels, rightPixels, composition.nativeValue());
    }

    private native int[] composeImages(int width, int height, int[] leftPixels,
                                       int[] rightPixels, int composition);
}
