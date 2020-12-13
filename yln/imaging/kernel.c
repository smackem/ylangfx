//
// Created by Philip Boger on 29.11.20.
//

#include <assert.h>
#include <types.h>
#include "kernel.h"

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
    kernel->values = new_arr(float, size);
    for (int i = 0; i < size; i++) {
        kernel->values[i] = value;
    }
}

void wrap_kernel(Kernel *kernel, int width, int height, float *values) {
    assert(kernel != NULL);
    assert(width > 0);
    assert(height > 0);
    assert(values != NULL);
    kernel->width = width;
    kernel->height = height;
    kernel->values = values;
}

void free_kernel(Kernel *kernel) {
    assert(kernel != NULL);
    if (kernel->values != NULL) {
        free(kernel->values);
    }
    bzero(kernel, sizeof(Kernel));
}

void convolve_kernel(Kernel *dest, const Kernel *orig, const Kernel *kernel) {
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
            float v = 0;
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
                        float px = orig->values[image_index];
                        v += px * value;
                    }
                    kernel_index++;
                    image_index++;
                }
            }

            dest->values[target_index] = kernel_sum == 0.0f ? v : v / kernel_sum;
            target_index++;
        }
    }
}

void compose_kernel(Kernel *dest, const Kernel *left, const Kernel *right, float_composition_t compose) {
    assert(dest != NULL);
    assert(left != NULL);
    assert(right != NULL);
    assert(compose != NULL);
    assert(left->width == right->width);
    assert(left->height == right->height);
    assert(dest->width == left->width);
    assert(dest->height == left->height);
    size_t size = left->width * left->height;
    float *dest_ptr = dest->values;
    const float *left_ptr = left->values;
    const float *right_ptr = right->values;
    for ( ; size > 0; size--) {
        *dest_ptr++ = compose(*left_ptr++, *right_ptr++);
    }
}
