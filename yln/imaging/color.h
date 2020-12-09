//
// Created by Philip Boger on 09.12.20.
//

#ifndef YLN_COLOR_H
#define YLN_COLOR_H

typedef struct color {
    float red;
    float green;
    float blue;
    float alpha;
} Color;

void init_color(Color *color, float red, float green, float blue, float alpha);
void invert_color(Color *dest, const Color *orig);
void paint_color_over(Color *dest, const Color *foreground, const Color *background);

#endif //YLN_COLOR_H
