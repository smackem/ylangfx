//
// Created by Philip Boger on 22.11.20.
//

#ifndef YLN_IMAGEIO_H
#define YLN_IMAGEIO_H

#include <imaging.h>

error loadImage(ImageRgba *pImage, cstr pPath);
error saveImage(const ImageRgba *pImage, cstr pPath);

#endif //YLN_IMAGEIO_H
