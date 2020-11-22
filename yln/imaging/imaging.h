//
// Created by Philip Boger on 22.11.20.
//

#ifndef YLN_IMAGING_H
#define YLN_IMAGING_H

#include "types.h"

#define MAX_IMAGE_PIXELS (64 * 1024 * 1024)
#define ONCE while (0)
#define R(color) ((color >> 16u) & 0xff)
#define G(color) ((color >> 8u) & 0xff)
#define B(color) (color & 0xff)
#define A(color) ((color >> 24u) & 0xff)
#define RGBA(r, g, b, a) (((a & 0xff) << 24u) | ((r & 0xff) << 16u) | ((g & 0xff) << 8u) | (b & 0xff))

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

error loadImage(ImageRgba *pImage, cstr pPath);
error saveImage(const ImageRgba *pImage, cstr pPath);
void initImage(ImageRgba *pImage, i32 width, i32 height);
void freeImage(ImageRgba *pImage);
void invertImage(ImageRgba *pImage);
void cloneImage(ImageRgba *pDest, const ImageRgba *pOriginal);
void convolveImage(ImageRgba *pDest, const ImageRgba *pSource, const Kernel *pKernel);
void initKernel(Kernel *pKernel, i32 width, i32 height, float value);
void freeKernel(Kernel *pKernel);

i32 getPixelCount(const ImageRgba *pImage) {
    return pImage->width * pImage->height;
}

#endif //YLN_IMAGING_H
