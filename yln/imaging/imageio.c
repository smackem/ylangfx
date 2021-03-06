//
// Created by Philip Boger on 22.11.20.
//

#include <stdlib.h>
#include <stdio.h>
#include <assert.h>
#include <netinet/in.h>
#include <png.h>
#include <pngconf.h>
#include "imageio.h"

#define IMAGE_HEADER_SIZE (16)

error load_image(ImageRgba *image, const char *path) {
    assert(image != NULL);
    FILE *file = fopen(path, "rb");
    error err = 0;
    rgba *pixels = NULL;

    do {
        byte header[IMAGE_HEADER_SIZE];
        size_t count = fread(header, 1, IMAGE_HEADER_SIZE, file);
        if (count < IMAGE_HEADER_SIZE) {
            err = 1; break;
        }
        int width = ntohl(*(uint32_t *) &header[0]);
        int height = ntohl(*(uint32_t *) &header[4]);
        int pixel_count = width * height;
        if (pixel_count < 0 || pixel_count > MAX_IMAGE_PIXELS) {
            err = 1; break;
        }
        pixels = new_arr(rgba, pixel_count);
        if (fread(pixels, sizeof(rgba), pixel_count, file) < pixel_count) {
            err = 1; break;
        }
        image->width = width;
        image->height = height;
        image->pixels = pixels;
    } ONCE;

    fclose(file);
    if (err != OK && pixels != NULL) {
        free(pixels);
    }
    return err;
}

error save_image(const ImageRgba *image, const char *path) {
    assert(image != NULL);
    byte header[IMAGE_HEADER_SIZE];
    int pixel_count = get_pixel_count(image);
    error err = 0;
    FILE *file = fopen(path, "wb");

    *(uint32_t *)&header[0] = htonl(image->width);
    *(uint32_t *)&header[4] = htonl(image->height);
    do {
        if (fwrite(header, 1, IMAGE_HEADER_SIZE, file) < IMAGE_HEADER_SIZE) {
            err = 1; break;
        }
        if (fwrite(image->pixels, sizeof(rgba), pixel_count, file) < pixel_count) {
            err = 1; break;
        }
    } ONCE;

    fclose(file);
    return err;
}

error load_png(ImageFloat *image, const char *path) {
    assert(image != NULL);
    png_image png;
    ImageRgba rgba_image;
    zero(png);
    png.version = PNG_IMAGE_VERSION;
    if (png_image_begin_read_from_file(&png, path) == 0) {
        return 1;
    }
    png.format = PNG_FORMAT_BGRA;
    size_t size = PNG_IMAGE_SIZE(png);
    byte *buffer = new_arr(byte, size);
    if (png_image_finish_read(&png, NULL, buffer, 0, NULL) == 0) {
        free(buffer);
        return 1;
    }
    wrap_image_rgba(&rgba_image, png.width, png.height, (rgba *)buffer);
    image_from_rgba(image, &rgba_image);
    free_image_rgba(&rgba_image);
    return OK;
}

error save_png(const ImageFloat *image, const char *path) {
    assert(image != NULL);
    png_image png;
    ImageRgba rgba_image;
    zero(png);
    image_to_rgba(&rgba_image, image);
    png.version = PNG_IMAGE_VERSION;
    png.format = PNG_FORMAT_BGRA;
    png.width = image->width;
    png.height = image->height;
    error err = OK;
    if (png_image_write_to_file(&png, path, false, rgba_image.pixels, 0, NULL) == 0) {
        err = 1;
    }
    free_image_rgba(&rgba_image);
    return err;
}
