//
// Created by Philip Boger on 22.11.20.
//

#ifndef YLN_IMAGEIO_H
#define YLN_IMAGEIO_H

#include "image.h"

error load_image(ImageRgba *image, const char *path);
error save_image(const ImageRgba *image, const char *path);
error load_png(ImageFloat *image, const char *path);
error save_png(const ImageFloat *image, const char *path);

#endif //YLN_IMAGEIO_H
