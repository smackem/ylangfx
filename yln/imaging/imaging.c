//
// Created by Philip Boger on 22.11.20.
//

#include <stdlib.h>
#include <stdio.h>
#include <assert.h>
#include "imaging.h"

inline rgba make_rgba(byte r, byte g, byte b, byte a) {
    return (((a & 0xffu) << 24u) | ((r & 0xffu) << 16u) | ((g & 0xffu) << 8u) | (b & 0xffu));
}

inline rgba make_rgba_f(float r, float g, float b, float a) {
    return make_rgba((byte) (r + 0.5), (byte) (g + 0.5), (byte) (b + 0.5), (byte) (a + 0.5));
}

inline rgba make_rgba_d(double r, double g, double b, double a) {
    return make_rgba((byte) (r + 0.5), (byte) (g + 0.5), (byte) (b + 0.5), (byte) (a + 0.5));
}

void init_image(struct image_rgba *image, int width, int height) {
    assert(image != NULL);
    assert(width > 0);
    assert(height > 0);
    image->width = width;
    image->height = height;
    image->pixels = newarr(rgba, get_pixel_count(image));
}

void free_image(struct image_rgba *image) {
    assert(image != NULL);
    if (image->pixels != NULL) {
        free(image->pixels);
    }
    bzero(image, sizeof(struct image_rgba));
}

void invert_image(struct image_rgba *image) {
    assert(image != NULL);
    int size = get_pixel_count(image);
    rgba *pixel_ptr = image->pixels;
    for ( ; size > 0; size--, pixel_ptr++) {
        rgba col = *pixel_ptr;
        *pixel_ptr = RGBA(255 - R(col), 255 - G(col), 255 - B(col), A(col));
    }
}

void clone_image(struct image_rgba *dest, const struct image_rgba *orig) {
    assert(dest != NULL);
    memcpy(dest, orig, sizeof(struct image_rgba));
    size_t size = get_pixel_count(orig);
    dest->pixels = newarr(rgba, size);
    memcpy(dest->pixels, orig->pixels, size * sizeof(rgba));
}

void convolve_image(struct image_rgba *dest, const struct image_rgba *orig, const struct kernel *kernel) {
    assert(dest != NULL);
    assert(orig != NULL);
    assert(dest->width == orig->width);
    assert(dest->height == orig->height);
    assert(dest->pixels != NULL);
    assert(orig->pixels != NULL);
    assert(kernel != NULL);
    assert(kernel->width > 0);
    assert(kernel->height > 0);
    int width = dest->width;
    int height = dest->height;
    float kernelSum = get_kernel_sum(kernel);
    int kernelWidth = kernel->width;
    int kernelHeight = kernel->height;
    int halfKernelWidth = kernelWidth / 2;
    int halfKernelHeight = kernelHeight / 2;
    int targetIndex = 0;

    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
            float r = 0;
            float g = 0;
            float b = 0;
            int startY = y - halfKernelHeight;
            int endY = startY + kernelHeight;
            int startX = x - halfKernelWidth;
            int endX = startX + kernelWidth;
            int kernelIndex = 0;

            for (int imageY = startY; imageY < endY; imageY++) {
                if (imageY < 0 || imageY >= height) {
                    kernelIndex += kernelWidth;
                    continue;
                }

                int imageIndex = imageY * width + startX;
                for (int imageX = startX; imageX < endX; imageX++) {
                    if (imageX >= 0 && imageX < width) {
                        float value = kernel->values[kernelIndex];
                        rgba px = orig->pixels[imageIndex];
                        r += value * R(px);
                        g += value * G(px);
                        b += value * B(px);
                    }
                    kernelIndex++;
                    imageIndex++;
                }
            }

            float a = A(orig->pixels[y * width + x]);
            dest->pixels[targetIndex] = kernelSum == 0
                    ? RGBA(r, g, b, a)
                    : RGBA(r / kernelSum, g / kernelSum, b / kernelSum, a);
            targetIndex++;
        }
    }
}

inline int get_pixel_count(const struct image_rgba *image) {
    assert(image != NULL);
    return image->width * image->height;
}

float get_kernel_sum(const struct kernel *kernel) {
    assert(kernel != NULL);
    int size = kernel->width * kernel->height;
    const float *pValue = kernel->values;
    float sum = 0;
    for ( ; size > 0; size--, pValue++) {
        sum += *pValue;
    }
    return sum;
}

void init_kernel(struct kernel *kernel, int width, int height, float value) {
    assert(kernel != NULL);
    int size = width * height;
    kernel->width = width;
    kernel->height = height;
    kernel->values = newarr(float, size);
    for (int i = 0; i < size; i++) {
        kernel->values[i] = value;
    }
}

void free_kernel(struct kernel *kernel) {
    assert(kernel != NULL);
    if (kernel->values != NULL) {
        free(kernel->values);
    }
    bzero(kernel, sizeof(struct kernel));
}
