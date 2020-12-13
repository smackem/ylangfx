//
// Created by Philip Boger on 22.11.20.
//

#ifndef YLN_IMAGEIO_H
#define YLN_IMAGEIO_H

#include "image.h"

/**
 * Loads an YLI image from the file at the given `path`:
 * - 16 bytes header
 *   - 4 bytes image width in network byte-order
 *   - 4 bytes image height in network byte-order
 *   - 8 bytes reserved
 * - Pixel data, 32bit per pixel in the following byte order:
 *   byte:    0  1  2  3
 *   channel: B  G  R  A
 */
error load_image(ImageRgba *image, const char *path);

/**
 * Saves a YLI image to the file at the given `path`.
 * See `load_image` for format details.
 */
error save_image(const ImageRgba *image, const char *path);

/**
 * Loads a PNG image from the file at the given path into `image`.
 */
error load_png(ImageFloat *image, const char *path);

/**
 * Saves an image to the file at the given path in PNG format.
 */
error save_png(const ImageFloat *image, const char *path);

#endif //YLN_IMAGEIO_H
