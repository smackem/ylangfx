//
// Created by Philip Boger on 09.12.20.
//

#ifndef YLN_COLOR_H
#define YLN_COLOR_H

#include "rgba.h"

/**
 * Represents a simple RGBA color.
 */
typedef struct color {
    float red;
    float green;
    float blue;
    float alpha;
} Color;

/**
 * Initializes a color struct with component values.
 */
void set_color(Color *dest, float red, float green, float blue, float alpha);

/**
 * Inverts orig and stores the result in dest.
 */
void invert_color(Color *dest, const Color *orig);

/**
 * Executes the alpha compositing operation `over` on the two colors foreground and background.
 * Stores the result in dest.
 */
void paint_color_over(Color *dest, const Color *foreground, const Color *background);

/**
 * Clamps the given color to a 32bit rgba value.
 */
rgba color_to_rgba(const Color *color);

/**
 * Converts the given rgba color to a float-based Color, which is stored in dest.
 */
void color_from_rgba(Color *dest, rgba rgba_color);

#endif //YLN_COLOR_H
