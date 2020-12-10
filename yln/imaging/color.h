//
// Created by Philip Boger on 09.12.20.
//

#ifndef YLN_COLOR_H
#define YLN_COLOR_H

#include "rgba.h"

typedef struct color {
    float red;
    float green;
    float blue;
    float alpha;
} Color;

void set_color(Color *dest, float red, float green, float blue, float alpha);
void invert_color(Color *dest, const Color *orig);
void paint_color_over(Color *dest, const Color *foreground, const Color *background);
rgba color_to_rgba(const Color *color);
void color_from_rgba(Color *dest, rgba rgba_color);

#endif //YLN_COLOR_H
