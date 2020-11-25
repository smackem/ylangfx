//
// Created by Philip Boger on 22.11.20.
//

#ifndef YLN_IMAGING_H
#define YLN_IMAGING_H

#include "types.h"

#define MAX_IMAGE_PIXELS (64 * 1024 * 1024)
#define R(color) ((color >> 16u) & 0xffu)
#define G(color) ((color >> 8u) & 0xffu)
#define B(color) (color & 0xffu)
#define A(color) ((color >> 24u) & 0xffu)
#define RGBA(r, g, b, a) _Generic((r)+(g)+(b)+(a), default: make_rgba, double: make_rgba_d, float: make_rgba_f)(r, g, b, a)

rgba make_rgba(byte r, byte g, byte b, byte a);
rgba make_rgba_f(float r, float g, float b, float a);
rgba make_rgba_d(double r, double g, double b, double a);

struct image_rgba {
    int width;
    int height;
    rgba *pixels;
};

struct kernel {
    int width;
    int height;
    float *values;
};

void init_image(struct image_rgba *image, int width, int height);
void free_image(struct image_rgba *image);
void invert_image(struct image_rgba *image);
void clone_image(struct image_rgba *dest, const struct image_rgba *orig);
void convolve_image(struct image_rgba *dest, const struct image_rgba *orig, const struct kernel *kernel);
void init_kernel(struct kernel *kernel, int width, int height, float value);
void free_kernel(struct kernel *kernel);
float get_kernel_sum(const struct kernel *kernel);
int get_pixel_count(const struct image_rgba *image);

#endif //YLN_IMAGING_H
