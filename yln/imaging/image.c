//
// Created by Philip Boger on 29.11.20.
//

#include <assert.h>
#include "image.h"

void init_image(ImageRgba *image, int width, int height) {
    assert(image != NULL);
    assert(width > 0);
    assert(height > 0);
    image->width = width;
    image->height = height;
    image->pixels = new_arr(rgba, get_pixel_count(image));
}

void wrap_image(ImageRgba *image, int width, int height, rgba *pixels) {
    assert(image != NULL);
    assert(width > 0);
    assert(height > 0);
    assert(pixels != NULL);
    image->width = width;
    image->height = height;
    image->pixels = pixels;
}

void free_image(ImageRgba *image) {
    assert(image != NULL);
    if (image->pixels != NULL) {
        free(image->pixels);
    }
    bzero(image, sizeof(ImageRgba));
}

void invert_image(ImageRgba *image) {
    assert(image != NULL);
    int size = get_pixel_count(image);
    rgba *pixel_ptr = image->pixels;
    for ( ; size > 0; size--, pixel_ptr++) {
        *pixel_ptr = invert_rgba(*pixel_ptr);
    }
}

void clone_image(ImageRgba *dest, const ImageRgba *orig) {
    assert(dest != NULL);
    memcpy(dest, orig, sizeof(ImageRgba));
    size_t size = get_pixel_count(orig);
    dest->pixels = new_arr(rgba, size);
    memcpy(dest->pixels, orig->pixels, size * sizeof(rgba));
}

void convolve_image(ImageRgba *dest, const ImageRgba *orig, const Kernel *kernel) {
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
                        rgba px = orig->pixels[image_index];
                        r += value * red(px);
                        g += value * green(px);
                        b += value * blue(px);
                    }
                    kernel_index++;
                    image_index++;
                }
            }

            float a = alpha(orig->pixels[y * width + x]);
            dest->pixels[target_index] = kernel_sum == 0.0
                                         ? make_rgba(r, g, b, a)
                                         : make_rgba(r / kernel_sum, g / kernel_sum, b / kernel_sum, a);
            target_index++;
        }
    }
}

rgba convolve_image_pixel(const ImageRgba *orig, const Kernel *kernel, int x, int y) {
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
                rgba px = orig->pixels[image_index];
                r += value * red(px);
                g += value * green(px);
                b += value * blue(px);
            }
            kernel_index++;
            image_index++;
        }
    }

    float a = alpha(orig->pixels[y * width + x]);
    return kernel_sum == 0.0
            ? make_rgba(r, g, b, a)
            : make_rgba(r / kernel_sum, g / kernel_sum, b / kernel_sum, a);
}

inline int get_pixel_count(const ImageRgba *image) {
    assert(image != NULL);
    return image->width * image->height;
}

void compose_images(ImageRgba *dest, const ImageRgba *left, const ImageRgba *right, composition_t compose) {
    assert(dest != NULL);
    assert(left != NULL);
    assert(right != NULL);
    assert(left->width == right->width);
    assert(left->height == right->height);
    int size = left->width * left->height;
    rgba *dest_ptr = dest->pixels;
    rgba *left_ptr = left->pixels;
    rgba *right_ptr = right->pixels;
    for ( ; size > 0; size--) {
        *dest_ptr++ = compose(*left_ptr++, *right_ptr++);
    }
}
