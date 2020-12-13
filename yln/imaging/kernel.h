//
// Created by Philip Boger on 29.11.20.
//

#ifndef YLN_KERNEL_H
#define YLN_KERNEL_H

/**
 * Represents a two-dimensional matrix of float values, which can
 * be used for image operations like convolution or to represent
 * greyscale images.
 */
typedef struct kernel {
    /**
     * The width of the kernel.
     */
    int width;

    /**
     * The height of the kernel.
     */
    int height;

    /**
     * The kernel values. Length is equal to width * height.
     */
    float *values;
} Kernel;

/**
 * A type of function to compose float values.
 */
typedef float (*float_composition_t)(float left, float right);

/**
 * Initializes the given kernel, allocating memory for the kernel values.
 * Call `free_kernel` to dispose of the kernel memory.
 */
void init_kernel(Kernel *kernel, int width, int height, float value);

/**
 * Wraps some preallocated memory with the given kernel structure.
 * The size of the buffer at `values` must be equal to width * height.
 */
void wrap_kernel(Kernel *kernel, int width, int height, float *values);

/**
 * Releases all resources allocated by the given kernel.
 */
void free_kernel(Kernel *kernel);

/**
 * Sums up all values in the given kernel.
 */
float get_kernel_sum(const Kernel *kernel);

/**
 * Applies the image operation convolution to `orig`, which in this case represents a greyscale image,
 * using the given kernel and stores the result in `dest`.
 * `dest` must be initialized to the same size as `orig`.
 */
void convolve_kernel(Kernel *dest, const Kernel *orig, const Kernel *kernel);

/**
 * Composes two kernels `left` and `right`, using the given composition function (which is applied
 * to all pixels in `left` and `right`) and stores the result in `dest`.
 * `dest` must be initialized to the same size as `left` and `right`.
 */
void compose_kernel(Kernel *dest, const Kernel *left, const Kernel *right, float_composition_t compose);

#endif //YLN_KERNEL_H
