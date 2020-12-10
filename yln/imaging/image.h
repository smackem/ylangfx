//
// Created by Philip Boger on 29.11.20.
//

#ifndef YLN_IMAGE_H
#define YLN_IMAGE_H

#include "rgba.h"
#include "color.h"
#include "kernel.h"

#define MAX_IMAGE_PIXELS (64 * 1024 * 1024)

typedef struct image_rgba {
    int width;
    int height;
    rgba *pixels;
} ImageRgba;

typedef struct image_float {
    int width;
    int height;
    Color *pixels;
} ImageFloat;

typedef rgba (*rgba_composition_t)(rgba left, rgba right);
typedef void (*color_composition_t)(Color *dest, const Color *left, const Color *right);

void init_image_rgba(ImageRgba *image, int width, int height);
void wrap_image_rgba(ImageRgba *image, int width, int height, rgba *pixels);
void free_image_rgba(ImageRgba *image);
void invert_image_rgba(ImageRgba *image);
void clone_image_rgba(ImageRgba *dest, const ImageRgba *orig);
int get_pixel_count(const ImageRgba *image);

void init_image(ImageFloat *image, int width, int height);
void convolve_image(ImageFloat *dest, const ImageFloat *orig, const Kernel *kernel);
void convolve_image_pixel(Color *dest, const ImageFloat *orig, const Kernel *kernel, int x, int y);
void compose_images(ImageFloat *dest, const ImageFloat *left, const ImageFloat *right, color_composition_t compose);

#endif //YLN_IMAGE_H
