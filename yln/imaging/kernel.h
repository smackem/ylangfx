//
// Created by Philip Boger on 29.11.20.
//

#ifndef YLN_KERNEL_H
#define YLN_KERNEL_H

typedef struct kernel {
    int width;
    int height;
    float *values;
} Kernel;

typedef float (*float_composition_t)(float left, float right);

void init_kernel(Kernel *kernel, int width, int height, float value);
void wrap_kernel(Kernel *kernel, int width, int height, float *values);
void free_kernel(Kernel *kernel);
float get_kernel_sum(const Kernel *kernel);
void convolve_kernel(Kernel *dest, const Kernel *orig, const Kernel *kernel);
void compose_kernel(Kernel *dest, const Kernel *left, const Kernel *right, float_composition_t compose);

#endif //YLN_KERNEL_H
