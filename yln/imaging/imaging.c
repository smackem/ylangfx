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

void init_image(ImageRgba *image, int width, int height) {
    assert(image != NULL);
    assert(width > 0);
    assert(height > 0);
    image->width = width;
    image->height = height;
    image->pixels = NEW_ARR(rgba, get_pixel_count(image));
}

void free_image(ImageRgba *image) {
    assert(image != NULL);
    if (image->pixels != NULL) {
        free(image->pixels);
    }
    bzero(image, sizeof(ImageRgba));
}

void invert_image(ImageRgba *image) {
    assert(image != NULL);
    int size = get_pixel_count(image);
    rgba *pixel_ptr = image->pixels;
    for ( ; size > 0; size--, pixel_ptr++) {
        rgba col = *pixel_ptr;
        *pixel_ptr = RGBA(255 - R(col), 255 - G(col), 255 - B(col), A(col));
    }
}

void clone_image(ImageRgba *dest, const ImageRgba *orig) {
    assert(dest != NULL);
    memcpy(dest, orig, sizeof(ImageRgba));
    size_t size = get_pixel_count(orig);
    dest->pixels = NEW_ARR(rgba, size);
    memcpy(dest->pixels, orig->pixels, size * sizeof(rgba));
}

void convolve_image(ImageRgba *dest, const ImageRgba *orig, const Kernel *kernel) {
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
    float kernel_sum = get_kernel_sum(kernel);
    int kernel_width = kernel->width;
    int kernel_height = kernel->height;
    int half_kernel_width = kernel_width / 2;
    int half_kernel_height = kernel_height / 2;
    int target_index = 0;

    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
            float r = 0;
            float g = 0;
            float b = 0;
            int start_y = y - half_kernel_height;
            int end_y = start_y + kernel_height;
            int start_x = x - half_kernel_width;
            int end_x = start_x + kernel_width;
            int kernel_index = 0;

            for (int image_y = start_y; image_y < end_y; image_y++) {
                if (image_y < 0 || image_y >= height) {
                    kernel_index += kernel_width;
                    continue;
                }

                int image_index = image_y * width + start_x;
                for (int image_x = start_x; image_x < end_x; image_x++) {
                    if (image_x >= 0 && image_x < width) {
                        float value = kernel->values[kernel_index];
                        rgba px = orig->pixels[image_index];
                        r += value * R(px);
                        g += value * G(px);
                        b += value * B(px);
                    }
                    kernel_index++;
                    image_index++;
                }
            }

            float a = A(orig->pixels[y * width + x]);
            dest->pixels[target_index] = kernel_sum == 0
                    ? RGBA(r, g, b, a)
                    : RGBA(r / kernel_sum, g / kernel_sum, b / kernel_sum, a);
            target_index++;
        }
    }
}

inline int get_pixel_count(const ImageRgba *image) {
    assert(image != NULL);
    return image->width * image->height;
}

float get_kernel_sum(const Kernel *kernel) {
    assert(kernel != NULL);
    int size = kernel->width * kernel->height;
    const float *pValue = kernel->values;
    float sum = 0;
    for ( ; size > 0; size--, pValue++) {
        sum += *pValue;
    }
    return sum;
}

void init_kernel(Kernel *kernel, int width, int height, float value) {
    assert(kernel != NULL);
    int size = width * height;
    kernel->width = width;
    kernel->height = height;
    kernel->values = NEW_ARR(float, size);
    for (int i = 0; i < size; i++) {
        kernel->values[i] = value;
    }
}

void free_kernel(Kernel *kernel) {
    assert(kernel != NULL);
    if (kernel->values != NULL) {
        free(kernel->values);
    }
    bzero(kernel, sizeof(Kernel));
}
