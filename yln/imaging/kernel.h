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

void init_kernel(Kernel *kernel, int width, int height, float value);
void free_kernel(Kernel *kernel);
float get_kernel_sum(const Kernel *kernel);

#endif //YLN_KERNEL_H
