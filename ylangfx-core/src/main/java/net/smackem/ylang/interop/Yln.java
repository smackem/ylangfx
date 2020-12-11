package net.smackem.ylang.interop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// -Djava.library.path=$ProjectFileDir$/yln/cmake-build-release
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

    public native float[] convolveImage(int width, int height, float[] pixels,
                                      int kernelWidth, int kernelHeight, float[] kernelValues);

    public native float[] convolveKernel(int width, int height, float[] values,
                                         int kernelWidth, int kernelHeight, float[] kernelValues);

    public float[] composeImages(int width, int height, float[] leftPixels,
                               float[] rightPixels, MatrixComposition composition) {
        return this.composeImages(width, height, leftPixels, rightPixels, composition.nativeValue());
    }

    public float[] composeKernels(int width, int height, float[] leftValues,
                                  float[] rightValues, MatrixComposition composition) {
        return this.composeKernels(width, height, leftValues, rightValues, composition.nativeValue());
    }

    private native float[] composeImages(int width, int height, float[] leftPixels,
                                       float[] rightPixels, int composition);

    private native float[] composeKernels(int width, int height, float[] leftValues,
                                          float[] rightValues, int composition);
}
