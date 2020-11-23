//
// Created by Philip Boger on 22.11.20.
//

#include <stdlib.h>
#include <stdio.h>
#include <assert.h>
#include "imaging.h"

void initImage(ImageRgba *pImage, i32 width, i32 height) {
    assert(pImage != NULL);
    assert(width > 0);
    assert(height > 0);
    pImage->width = width;
    pImage->height = height;
    pImage->pixels = newarr(rgba, getPixelCount(pImage));
}

void freeImage(ImageRgba *pImage) {
    assert(pImage != NULL);
    if (pImage->pixels != NULL) {
        free(pImage->pixels);
    }
    bzero(pImage, sizeof(ImageRgba));
}

void invertImage(ImageRgba *pImage) {
    assert(pImage != NULL);
    i32 size = getPixelCount(pImage);
    rgba *pPixel = pImage->pixels;
    for ( ; size > 0; size--, pPixel++) {
        rgba col = *pPixel;
        *pPixel = RGBA(255 - R(col), 255 - G(col), 255 - B(col), A(col));
    }
}

void cloneImage(ImageRgba *pDest, const ImageRgba *pOriginal) {
    assert(pDest != NULL);
    memcpy(pDest, pOriginal, sizeof(ImageRgba));
    size_t size = getPixelCount(pOriginal);
    pDest->pixels = newarr(rgba, size);
    memcpy(pDest->pixels, pOriginal->pixels, size * sizeof(rgba));
}

void convolveImage(ImageRgba *pDest, const ImageRgba *pSource, const Kernel *pKernel) {
    assert(pDest != NULL);
    assert(pSource != NULL);
    assert(pDest->width == pSource->width);
    assert(pDest->height == pSource->height);
    assert(pDest->pixels != NULL);
    assert(pSource->pixels != NULL);
    assert(pKernel != NULL);
    assert(pKernel->width > 0);
    assert(pKernel->height > 0);

}

inline i32 getPixelCount(const ImageRgba *pImage) {
    assert(pImage != NULL);
    return pImage->width * pImage->height;
}

void initKernel(Kernel *pKernel, i32 width, i32 height, float value) {
    assert(pKernel != NULL);
    i32 size = width * height;
    pKernel->width = width;
    pKernel->height = height;
    pKernel->values = newarr(float, size);
    for (int i = 0; i < size; i++) {
        pKernel->values[i] = value;
    }
}

void freeKernel(Kernel *pKernel) {
    assert(pKernel != NULL);
    if (pKernel->values != NULL) {
        free(pKernel->values);
    }
    bzero(pKernel, sizeof(Kernel));
}
