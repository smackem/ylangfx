//
// Created by Philip Boger on 09.12.20.
//

#include <assert.h>
#include "color.h"

inline void set_color(Color *dest, float red, float green, float blue, float alpha) {
    assert(dest != NULL);
    dest->red = red;
    dest->green = green;
    dest->blue = blue;
    dest->alpha = alpha;
}

void invert_color(Color *dest, const Color *orig) {
    assert(dest != NULL);
    assert(orig != NULL);
    dest->red = 255.0f - orig->red;
    dest->green = 255.0f - orig->green;
    dest->blue = 255.0f - orig->blue;
    dest->alpha = orig->alpha;
}

void paint_color_over(Color *dest, const Color *foreground, const Color *background) {
    float foregroundA = foreground->alpha / 255.0f;
    float backgroundA = background->alpha / 255.0f;
    float multipliedA = (1.0f - foregroundA) * backgroundA;
    float a = backgroundA + (1.0f - backgroundA) * foregroundA;

    dest->red = (foreground->red * foregroundA + background->red * multipliedA) / a;
    dest->green = (foreground->green * foregroundA + background->green * multipliedA) / a;
    dest->blue = (foreground->blue * foregroundA + background->blue * multipliedA) / a;
    dest->alpha = 255.0f * a;
}

inline rgba color_to_rgba(const Color *color) {
    assert(color != NULL);
    return make_rgba(color->red, color->green, color->blue, color->alpha);
}

void color_from_rgba(Color *dest, rgba rgba_color) {
    assert(dest != NULL);
    dest->red = red(rgba_color);
    dest->green = green(rgba_color);
    dest->blue = blue(rgba_color);
    dest->alpha = alpha(rgba_color);
}
