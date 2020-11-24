//
// Created by Philip Boger on 22.11.20.
//

#ifndef YLN_IMAGING_H
#define YLN_IMAGING_H

#include "types.h"

#define MAX_IMAGE_PIXELS (64 * 1024 * 1024)
#define R(color) ((color >> 16u) & 0xffu)
#define G(color) ((color >> 8u) & 0xffu)
#define B(color) (color & 0xffu)
#define A(color) ((color >> 24u) & 0xffu)
#define RGBA(r, g, b, a) _Generic((r)+(g)+(b)+(a), default: makeRgba, double: makeRgba_d, float: makeRgba_f)(r, g, b, a)

rgba makeRgba(byte r, byte g, byte b, byte a);
rgba makeRgba_f(float r, float g, float b, float a);
rgba makeRgba_d(double r, double g, double b, double a);

typedef struct imageRgba {
    i32 width;
    i32 height;
    rgba *pixels;
} ImageRgba;

typedef struct kernel {
    i32 width;
    i32 height;
    float *values;
} Kernel;

void initImage(ImageRgba *pImage, i32 width, i32 height);
void freeImage(ImageRgba *pImage);
void invertImage(ImageRgba *pImage);
void cloneImage(ImageRgba *pDest, const ImageRgba *pOrig);
void convolveImage(ImageRgba *pDest, const ImageRgba *pOrig, const Kernel *pKernel);
void initKernel(Kernel *pKernel, i32 width, i32 height, float value);
void freeKernel(Kernel *pKernel);
float getKernelSum(const Kernel *pKernel);
i32 getPixelCount(const ImageRgba *pImage);

#endif //YLN_IMAGING_H
