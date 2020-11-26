//
// Created by Philip Boger on 22.11.20.
//

#ifndef YLN_IMAGING_H
#define YLN_IMAGING_H

#include <types.h>

#define MAX_IMAGE_PIXELS (64 * 1024 * 1024)
#define R(color) ((color >> 16u) & 0xffu)
#define G(color) ((color >> 8u) & 0xffu)
#define B(color) (color & 0xffu)
#define A(color) ((color >> 24u) & 0xffu)
#define RGBA(r, g, b, a) _Generic((r)+(g)+(b)+(a), default: make_rgba, double: make_rgba_d, float: make_rgba_f)(r, g, b, a)

rgba make_rgba(byte r, byte g, byte b, byte a);
rgba make_rgba_f(float r, float g, float b, float a);
rgba make_rgba_d(double r, double g, double b, double a);

typedef struct image_rgba {
    int width;
    int height;
    rgba *pixels;
} ImageRgba;

typedef struct kernel {
    int width;
    int height;
    float *values;
} Kernel;

void init_image(ImageRgba *image, int width, int height);
void free_image(ImageRgba *image);
void invert_image(ImageRgba *image);
void clone_image(ImageRgba *dest, const ImageRgba *orig);
void convolve_image(ImageRgba *dest, const ImageRgba *orig, const Kernel *kernel);
void init_kernel(Kernel *kernel, int width, int height, float value);
void free_kernel(Kernel *kernel);
float get_kernel_sum(const Kernel *kernel);
int get_pixel_count(const ImageRgba *image);

#endif //YLN_IMAGING_H
