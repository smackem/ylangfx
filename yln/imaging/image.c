//
// Created by Philip Boger on 29.11.20.
//

#include <assert.h>
#include "image.h"

void init_image_rgba(ImageRgba *image, int width, int height) {
    assert(image != NULL);
    assert(width > 0);
    assert(height > 0);
    image->width = width;
    image->height = height;
    image->pixels = new_arr(rgba, get_pixel_count(image));
}

void wrap_image_rgba(ImageRgba *image, int width, int height, rgba *pixels) {
    assert(image != NULL);
    assert(width > 0);
    assert(height > 0);
    assert(pixels != NULL);
    image->width = width;
    image->height = height;
    image->pixels = pixels;
}

void free_image_rgba(ImageRgba *image) {
    assert(image != NULL);
    if (image->pixels != NULL) {
        free(image->pixels);
    }
    bzero(image, sizeof(ImageRgba));
}

void invert_image_rgba(ImageRgba *image) {
    assert(image != NULL);
    int size = get_pixel_count(image);
    rgba *pixel_ptr = image->pixels;
    for ( ; size > 0; size--, pixel_ptr++) {
        *pixel_ptr = invert_rgba(*pixel_ptr);
    }
}

void clone_image_rgba(ImageRgba *dest, const ImageRgba *orig) {
    assert(dest != NULL);
    memcpy(dest, orig, sizeof(ImageRgba));
    size_t size = get_pixel_count(orig);
    dest->pixels = new_arr(rgba, size);
    memcpy(dest->pixels, orig->pixels, size * sizeof(rgba));
}

inline int get_pixel_count(const ImageRgba *image) {
    assert(image != NULL);
    return image->width * image->height;
}

void init_image(ImageFloat *image, int width, int height) {
    assert(image != NULL);
    assert(width > 0);
    assert(height > 0);
    image->width = width;
    image->height = height;
    image->pixels = new_arr(Color, width * height);
}

void convolve_image(ImageFloat *dest, const ImageFloat *orig, const Kernel *kernel) {
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
            float r = 0;
            float g = 0;
            float b = 0;
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
                        Color *px = orig->pixels + image_index;
                        r += value * px->red;
                        g += value * px->green;
                        b += value * px->blue;
                    }
                    kernel_index++;
                    image_index++;
                }
            }

            float a = (orig->pixels + y * width + x)->alpha;
            if (kernel_sum == 0.0f) {
                set_color(dest->pixels + target_index, r, g, b, a);
            } else {
                set_color(dest->pixels + target_index, r / kernel_sum, g / kernel_sum, b / kernel_sum, a);
            }
            target_index++;
        }
    }
}

void convolve_image_pixel(Color *dest, const ImageFloat *orig, const Kernel *kernel, int x, int y) {
    assert(dest != NULL);
    assert(orig != NULL);
    assert(orig->pixels != NULL);
    assert(kernel != NULL);
    assert(kernel->width > 0);
    assert(kernel->height > 0);
    int width = orig->width;
    int height = orig->height;
    float kernel_sum = get_kernel_sum(kernel);
    int kernel_width = kernel->width;
    int kernel_height = kernel->height;
    int half_kernel_width = kernel_width / 2;
    int half_kernel_height = kernel_height / 2;

    float r = 0;
    float g = 0;
    float b = 0;
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
                Color *px = orig->pixels + image_index;
                r += value * px->red;
                g += value * px->green;
                b += value * px->blue;
            }
            kernel_index++;
            image_index++;
        }
    }

    float a = (orig->pixels + y * width + x)->alpha;
    if (kernel_sum == 0.0f) {
        set_color(dest, r, g, b, a);
    } else {
        set_color(dest, r / kernel_sum, g / kernel_sum, b / kernel_sum, a);
    }
}

void compose_images(ImageFloat *dest, const ImageFloat *left, const ImageFloat *right, color_composition_t compose) {
    assert(dest != NULL);
    assert(left != NULL);
    assert(right != NULL);
    assert(left->width == right->width);
    assert(left->height == right->height);
    int size = left->width * left->height;
    Color *dest_ptr = dest->pixels;
    Color *left_ptr = left->pixels;
    Color *right_ptr = right->pixels;
    for ( ; size > 0; size--) {
        compose(dest_ptr++, left_ptr++, right_ptr++);
    }
}
