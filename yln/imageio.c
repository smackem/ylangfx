//
// Created by Philip Boger on 22.11.20.
//

#include <stdlib.h>
#include <stdio.h>
#include <assert.h>
#include <netinet/in.h>
#include "imageio.h"

#define IMAGE_HEADER_SIZE (16)

error loadImage(ImageRgba *pImage, cstr pPath) {
    assert(pImage != NULL);
    FILE *file = fopen(pPath, "rb");
    error err = 0;
    rgba *pixels = NULL;

    do {
        byte header[IMAGE_HEADER_SIZE];
        size_t count = fread(header, 1, IMAGE_HEADER_SIZE, file);
        if (count < IMAGE_HEADER_SIZE) {
            err = 1; break;
        }
        i32 width = ntohl(*(u32 *) &header[0]);
        i32 height = ntohl(*(i32 *) &header[4]);
        i32 pixelCount = width * height;
        if (pixelCount < 0 || pixelCount > MAX_IMAGE_PIXELS) {
            err = 1; break;
        }
        pixels = newarr(rgba, pixelCount);
        if (fread(pixels, sizeof(rgba), pixelCount, file) < pixelCount) {
            err = 1; break;
        }
        pImage->width = width;
        pImage->height = height;
        pImage->pixels = pixels;
    } once;

    fclose(file);
    if (err != OK && pixels != NULL) {
        free(pixels);
    }
    return err;
}

error saveImage(const ImageRgba *pImage, cstr pPath) {
    assert(pImage != NULL);
    byte header[IMAGE_HEADER_SIZE];
    i32 pixelCount = getPixelCount(pImage);
    error err = 0;
    FILE *file = fopen(pPath, "wb");

    *(u32 *)&header[0] = htonl(pImage->width);
    *(u32 *)&header[4] = htonl(pImage->height);
    do {
        if (fwrite(header, 1, IMAGE_HEADER_SIZE, file) < IMAGE_HEADER_SIZE) {
            err = 1; break;
        }
        if (fwrite(pImage->pixels, sizeof(rgba), pixelCount, file) < pixelCount) {
            err = 1; break;
        }
    } once;

    fclose(file);
    return err;
}

