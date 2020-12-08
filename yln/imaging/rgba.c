//
// Created by Philip Boger on 29.11.20.
//

#include "rgba.h"

inline byte red(rgba color) {
    return (color >> 16u) & 0xffu;
}

inline byte green(rgba color) {
    return (color >> 8u) & 0xffu;
}

inline byte blue(rgba color) {
    return color & 0xffu;
}

inline byte alpha(rgba color) {
    return (color >> 24u) & 0xffu;
}

inline byte clamp_i(int i) {
    if (i < 0) {
        return 0;
    }
    if (i > 255) {
        return 255;
    }
    return (byte) (((unsigned int)i) & 0xffu);
}

inline byte clamp_f(float f) {
    if (f < 0.0) {
        return 0;
    }
    if (f > 255.0) {
        return 255;
    }
    return (byte) (((unsigned int) f) & 0xffu);
}

inline byte clamp_d(double d) {
    if (d < 0.0) {
        return 0;
    }
    if (d > 255.0) {
        return 255;
    }
    return (byte) (((unsigned int) d) & 0xffu);
}

rgba make_rgba_i(int r, int g, int b, int a) {
    return (((rgba)clamp(a) << 24u) | ((rgba)clamp(r) << 16u) | ((rgba)clamp(g) << 8u) | (rgba)clamp(b));
}

inline rgba make_rgba_f(float r, float g, float b, float a) {
    return make_rgba_i(clamp(r), clamp(g), clamp(b), clamp(a));
}

inline rgba make_rgba_d(double r, double g, double b, double a) {
    return make_rgba_i(clamp(r), clamp(g), clamp(b), clamp(a));
}

inline rgba invert_rgba(rgba color) {
    return make_rgba(255 - red(color), 255 - green(color), 255 - blue(color), alpha(color));
}

rgba rgba_over(rgba foreground, rgba background) {
    double foregroundA = alpha(foreground) / 255.0;
    double backgroundA = alpha(background) / 255.0;
    double multipliedA = (1.0 - foregroundA) * backgroundA;
    double a = backgroundA + (1.0 - backgroundA) * foregroundA;

    return make_rgba((red(foreground) * foregroundA + red(background) * multipliedA) / a,
            (green(foreground) * foregroundA + green(background) * multipliedA) / a,
            (blue(foreground) * foregroundA + blue(background) * multipliedA) / a,
            255.0 * a);
}
