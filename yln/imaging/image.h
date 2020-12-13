//
// Created by Philip Boger on 29.11.20.
//

#ifndef YLN_IMAGE_H
#define YLN_IMAGE_H

#include "rgba.h"
#include "color.h"
#include "kernel.h"

#define MAX_IMAGE_PIXELS (64 * 1024 * 1024)

/**
 * An image based on the rgba type (32 bit with one byte per channel).
 */
typedef struct image_rgba {
    /**
     * The image width.
     */
    int width;

    /**
     * The image height.
     */
    int height;

    /**
     * The image pixels, one rgba value per pixel.
     * Length is equal to width * height.
     */
    rgba *pixels;
} ImageRgba;

/**
 * The primary yln image type, representing each pixel with four float
 * values, one for each color component.
 */
typedef struct image_float {
    /**
     * The image width.
     */
    int width;

    /**
     * The image height.
     */
    int height;

    /**
     * The image pixels, four float values per pixel.
     * Length is equal to width * height.
     */
    Color *pixels;
} ImageFloat;

/**
 * Type of function to compose two colors and store the result in dest.
 */
typedef void (*color_composition_t)(Color *dest, const Color *left, const Color *right);

/**
 * Initializes the given image, allocating memory for the pixel array.
 * Call `free_image_rgba` to free the allocated memory.
 */
void init_image_rgba(ImageRgba *image, int width, int height);

/**
 * Wraps an already allocated pixel buffer in an image.
 * The length of `pixels` must be equal to width * height.
 */
void wrap_image_rgba(ImageRgba *image, int width, int height, rgba *pixels);

/**
 * Frees all memory allocated for the given image.
 */
void free_image_rgba(ImageRgba *image);

/**
 * Inverts the given image in-place.
 */
void invert_image_rgba(ImageRgba *image);

/**
 * Clones orig to dest. Call `free_image_rgba` to free resources allocated for dest.
 */
void clone_image_rgba(ImageRgba *dest, const ImageRgba *orig);

/**
 * Gets the number of pixels in the given image (the length of the pixel buffer).
 */
int get_pixel_count(const ImageRgba *image);

/**
 * Convert the given rgba image to a float image, which is stored in `image`.
 * Call `free_image` to release `image`.
 */
void image_from_rgba(ImageFloat *image, const ImageRgba *rgba_image);

/**
 * Converts the given float image to an 32bit integer image, loosing precision.
 * Call `free_image_rgba` to release `rgba_image`.
 */
void image_to_rgba(ImageRgba *rgba_image, const ImageFloat *image);

/**
 * Initialize the given image, allocating memory for the pixel buffer.
 * Call `free_image` to release `image`.
 */
void init_image(ImageFloat *image, int width, int height);

/**
 * Wrap some pre-allocated memory with the given image. The length of the
 * pixel buffer must be equal to width * height.
 */
void wrap_image(ImageFloat *image, int width, int height, Color *pixels);

/**
 * Convolves the image `orig` with the given kernel and stores the result in `dest`.
 * `dest` must be initialized to the same size as `orig`.
 */
void convolve_image(ImageFloat *dest, const ImageFloat *orig, const Kernel *kernel);

/**
 * Applies to given convolution kernel to the area around the pixel at (x,y) in image `orig` and
 * stores the result in `dest`.
 */
void convolve_image_pixel(Color *dest, const ImageFloat *orig, const Kernel *kernel, int x, int y);

/**
 * Composes the images `left` and `right` with the given composition function `compose`, and
 * stores the resulting image in `dest`.
 * `dest` must be initialized to the same size as `left` and `right`.
 */
void compose_image(ImageFloat *dest, const ImageFloat *left, const ImageFloat *right, color_composition_t compose);

/**
 * Releases all resources allocated by the given image.
 */
void free_image(ImageFloat *image);

/**
 * Clones the image `orig` into the image `dest`. Call `free_image` on `dest` afterwards.
 */
void clone_image(ImageFloat *dest, const ImageFloat *orig);

#endif //YLN_IMAGE_H
