//
// Created by Philip Boger on 22.11.20.
//

#ifndef YLN_IMAGEIO_H
#define YLN_IMAGEIO_H

#include <imaging.h>

error load_image(struct image_rgba *image, const char *path);
error save_image(const struct image_rgba *image, const char *path);

#endif //YLN_IMAGEIO_H
