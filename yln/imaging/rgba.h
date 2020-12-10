//
// Created by Philip Boger on 29.11.20.
//

#ifndef YLN_RGBA_H
#define YLN_RGBA_H

#include <types.h>

typedef uint32_t rgba;

byte red(rgba color);
byte green(rgba color);
byte blue(rgba color);
byte alpha(rgba color);

#define clamp(v) _Generic((v), default: clamp_i, double: clamp_d, float: clamp_f)(v)
byte clamp_i(int i);
byte clamp_f(float f);
byte clamp_d(double d);

#define make_rgba(r, g, b, a) _Generic((r)+(g)+(b)+(a), default: make_rgba_i, double: make_rgba_d, float: make_rgba_f)(r, g, b, a)
rgba make_rgba_i(int r, int g, int b, int a);
rgba make_rgba_f(float r, float g, float b, float a);
rgba make_rgba_d(double r, double g, double b, double a);

rgba invert_rgba(rgba color);
#endif //YLN_RGBA_H
