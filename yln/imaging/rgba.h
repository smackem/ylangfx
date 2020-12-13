//
// Created by Philip Boger on 29.11.20.
//

#ifndef YLN_RGBA_H
#define YLN_RGBA_H

#include <types.h>

/**
 * 32bit color type encoding one byte per channel. Byte order:
 * byte:    0  1  2  3
 * channel: B  G  R  A
 */
typedef uint32_t rgba;

/**
 * Gets the red component.
 */
byte red(rgba color);

/**
 * Gets the green component.
 */
byte green(rgba color);

/**
 * Gets the blue component.
 */
byte blue(rgba color);

/**
 * Gets the alpha component.
 */
byte alpha(rgba color);

/**
 * Clamps an image channel value to [0 .. 255
 */
#define clamp(v) _Generic((v), default: clamp_i, double: clamp_d, float: clamp_f)(v)
byte clamp_i(int i);
byte clamp_f(float f);
byte clamp_d(double d);

/**
 * Creates a new rgba value from the component values
 */
#define make_rgba(r, g, b, a) _Generic((r)+(g)+(b)+(a), default: make_rgba_i, double: make_rgba_d, float: make_rgba_f)(r, g, b, a)
rgba make_rgba_i(int r, int g, int b, int a);
rgba make_rgba_f(float r, float g, float b, float a);
rgba make_rgba_d(double r, double g, double b, double a);

/**
 * Inverts the given rgba color.
 */
rgba invert_rgba(rgba color);
#endif //YLN_RGBA_H
