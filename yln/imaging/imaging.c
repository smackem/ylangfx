//
// Created by Philip Boger on 22.11.20.
//

#include <stdlib.h>
#include <stdio.h>
#include <assert.h>
#include "imaging.h"

inline rgba makeRgba(byte r, byte g, byte b, byte a) {
    return (((a & 0xffu) << 24u) | ((r & 0xffu) << 16u) | ((g & 0xffu) << 8u) | (b & 0xffu));
}

inline rgba makeRgba_f(float r, float g, float b, float a) {
    return makeRgba((byte) (r + 0.5), (byte) (g + 0.5), (byte) (b + 0.5), (byte) (a + 0.5));
}

inline rgba makeRgba_d(double r, double g, double b, double a) {
    return makeRgba((byte) (r + 0.5), (byte) (g + 0.5), (byte) (b + 0.5), (byte) (a + 0.5));
}

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

void convolveImage(ImageRgba *pDest, const ImageRgba *pOrig, const Kernel *pKernel) {
    assert(pDest != NULL);
    assert(pOrig != NULL);
    assert(pDest->width == pOrig->width);
    assert(pDest->height == pOrig->height);
    assert(pDest->pixels != NULL);
    assert(pOrig->pixels != NULL);
    assert(pKernel != NULL);
    assert(pKernel->width > 0);
    assert(pKernel->height > 0);
    i32 width = pDest->width;
    i32 height = pDest->height;
    float kernelSum = getKernelSum(pKernel);
    i32 kernelWidth = pKernel->width;
    i32 kernelHeight = pKernel->height;
    i32 halfKernelWidth = kernelWidth / 2;
    i32 halfKernelHeight = kernelHeight / 2;
    i32 targetIndex = 0;
    for (i32 y = 0; y < height; y++) {
        for (i32 x = 0; x < width; x++) {
            float r = 0;
            float g = 0;
            float b = 0;
            float a = 255;
            i32 startY = y - halfKernelHeight;
            i32 endY = startY + kernelHeight;
            i32 startX = x - halfKernelWidth;
            i32 endX = startX + kernelWidth;
            i32 kernelIndex = 0;
            for (int imageY = startY; imageY < endY; imageY++) {
                if (imageY < 0 || imageY >= height) {
                    kernelIndex += kernelWidth;
                    continue;
                }
                int imageIndex = imageY * width + startX;
                for (int imageX = startX; imageX < endX; imageX++) {
                    if (imageX >= 0 && imageX < width) {
                        float value = pKernel->values[kernelIndex];
                        rgba px = pOrig->pixels[imageIndex];
                        r += value * R(px);
                        g += value * G(px);
                        b += value * B(px);
                        if (imageX == x && imageY == y) {
                            a = A(px);
                        }
                    }
                    kernelIndex++;
                    imageIndex++;
                }
            }
            pDest->pixels[targetIndex] = kernelSum == 0
                    ? RGBA(r, g, b, a)
                    : RGBA(r / kernelSum, g / kernelSum, b / kernelSum, a);
            targetIndex++;
        }
    }
}

inline i32 getPixelCount(const ImageRgba *pImage) {
    assert(pImage != NULL);
    return pImage->width * pImage->height;
}

float getKernelSum(const Kernel *pKernel) {
    assert(pKernel != NULL);
    i32 size = pKernel->width * pKernel->height;
    const float *pValue = pKernel->values;
    float sum = 0;
    for ( ; size > 0; size--, pValue++) {
        sum += *pValue;
    }
    return sum;
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
