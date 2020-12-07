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
