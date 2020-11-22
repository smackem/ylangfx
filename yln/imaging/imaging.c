//
// Created by Philip Boger on 22.11.20.
//

#include <stdlib.h>
#include <assert.h>
#include "imaging.h"

#define IMAGE_HEADER_SIZE (16)

error loadImage(ImageRgba *pImage, cstr pPath) {
    assert(pImage != NULL);
    FILE *file = fopen(pPath, "rb");
    error err = 0;
    do {
        byte header[IMAGE_HEADER_SIZE];
        size_t count = fread(header, 1, IMAGE_HEADER_SIZE, file);
        if (count < IMAGE_HEADER_SIZE) {
            err = 1; break;
        }
        i32 width = ntohl(*(u32 *) &header[0]);
        i32 height = ntohl(*(i32 *) &header[4]);
        i32 pixelCount = pImage->width * pImage->height;
        if (pixelCount < 0 || pixelCount > MAX_IMAGE_PIXELS) {
            err = 1; break;
        }
        rgba *pixels = newarr(rgba, pixelCount);
        if (fread(pImage->pixels, sizeof(rgba), pixelCount, file) < pixelCount) {
            err = 1;
            free(pixels);
            break;
        }
        pImage->width = width;
        pImage->height = height;
        pImage->pixels = pixels;
    } ONCE;
    fclose(file);
#pragma clang diagnostic push
#pragma ide diagnostic ignored "DanglingPointers"
    return err;
#pragma clang diagnostic pop
}

error saveImage(const ImageRgba *pImage, cstr pPath) {
    assert(pImage != NULL);
    byte header[IMAGE_HEADER_SIZE];
    *(u32 *)&header[0] = htonl(pImage->width);
    *(u32 *)&header[4] = htonl(pImage->height);
    i32 pixelCount = getPixelCount(pImage);
    error err = 0;
    FILE *file = fopen(pPath, "wb");
    do {
        if (fwrite(header, 1, IMAGE_HEADER_SIZE, file) < IMAGE_HEADER_SIZE) {
            err = 1; break;
        }
        if (fwrite(pImage->pixels, sizeof(rgba), pixelCount, file) < pixelCount) {
            err = 1; break;
        }
    } ONCE;
    fclose(file);
    return err;
}

void initImage(ImageRgba *pImage, i32 width, i32 height) {
    assert(pImage != NULL);
    pImage->width = width;
    pImage->height = height;
    pImage->pixels = newarr(rgba, getPixelCount(pImage));
}

void freeImage(ImageRgba *pImage) {
    assert(pImage != NULL);
    if (pImage->pixels != NULL) {
        free(pImage->pixels);
    }
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
}
