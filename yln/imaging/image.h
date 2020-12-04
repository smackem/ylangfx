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

void init_image(ImageRgba *image, int width, int height);
void free_image(ImageRgba *image);
void invert_image(ImageRgba *image);
void clone_image(ImageRgba *dest, const ImageRgba *orig);
void convolve_image(ImageRgba *dest, const ImageRgba *orig, const Kernel *kernel);
rgba convolve_image_pixel(const ImageRgba *orig, const Kernel *kernel, int x, int y);
int get_pixel_count(const ImageRgba *image);

#endif //YLN_IMAGE_H
