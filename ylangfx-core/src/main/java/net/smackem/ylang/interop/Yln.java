package net.smackem.ylang.interop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// -Djava.library.path=/home/philip/java/ylangfx/yln/cmake-build-release
public class Yln {
    private Yln() {}

    public static final Yln INSTANCE;
    private static final Logger log = LoggerFactory.getLogger(Yln.class);

    static {
        boolean libLoaded = false;
        try {
            System.loadLibrary("yln");
            libLoaded = true;
            log.info("yln library available");
        } catch (UnsatisfiedLinkError e) {
            log.warn("yln (native image operations) not available", e);
        }
        INSTANCE = libLoaded ? new Yln() : null;
    }

    public native int[] convolveImage(int width, int height, int[] pixels,
                                      int kernelWidth, int kernelHeight, float[] kernelValues);

    public native float[] convolveKernel(int width, int height, float[] values,
                                         int kernelWidth, int kernelHeight, float[] kernelValues);

    public int[] composeImages(int width, int height, int[] leftPixels,
                               int[] rightPixels, MatrixComposition composition) {
        return this.composeImages(width, height, leftPixels, rightPixels, composition.nativeValue());
    }

    public float[] composeKernels(int width, int height, float[] leftValues,
                                  float[] rightValues, MatrixComposition composition) {
        return this.composeKernels(width, height, leftValues, rightValues, composition.nativeValue());
    }

    private native int[] composeImages(int width, int height, int[] leftPixels,
                                       int[] rightPixels, int composition);

    private native float[] composeKernels(int width, int height, float[] leftValues,
                                          float[] rightValues, int composition);
}
