//
// Created by Philip Boger on 29.11.20.
//

#ifndef YLN_IMAGE_H
#define YLN_IMAGE_H

#include "rgba.h"
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

typedef rgba (*composition_t)(rgba left, rgba right);

void init_image(ImageRgba *image, int width, int height);
void wrap_image(ImageRgba *image, int width, int height, rgba *pixels);
void free_image(ImageRgba *image);
void invert_image(ImageRgba *image);
void clone_image(ImageRgba *dest, const ImageRgba *orig);
void convolve_image(ImageRgba *dest, const ImageRgba *orig, const Kernel *kernel);
rgba convolve_image_pixel(const ImageRgba *orig, const Kernel *kernel, int x, int y);
int get_pixel_count(const ImageRgba *image);
void compose_images(ImageRgba *dest, const ImageRgba *left, const ImageRgba *right, composition_t compose);

#endif //YLN_IMAGE_H
