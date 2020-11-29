//
// Created by Philip Boger on 29.11.20.
//

#ifndef YLN_RGBA_H
#define YLN_RGBA_H

#include <types.h>

#define R(color) ((color >> 16u) & 0xffu)
#define G(color) ((color >> 8u) & 0xffu)
#define B(color) (color & 0xffu)
#define A(color) ((color >> 24u) & 0xffu)
#define RGBA(r, g, b, a) _Generic((r)+(g)+(b)+(a), default: make_rgba, double: make_rgba_d, float: make_rgba_f)(r, g, b, a)

typedef uint32_t rgba;

rgba make_rgba(byte r, byte g, byte b, byte a);
rgba make_rgba_f(float r, float g, float b, float a);
rgba make_rgba_d(double r, double g, double b, double a);

#endif //YLN_RGBA_H
