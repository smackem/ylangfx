//
// Created by Philip Boger on 29.11.20.
//

#include "rgba.h"

inline byte clamp_i(int i) {
    if (i < 0.0) {
        return 0;
    }
    if (i > 255.0) {
        return 255;
    }
    return (byte) (i + 0.5);
}

inline byte clamp_f(float f) {
    if (f < 0.0) {
        return 0;
    }
    if (f > 255.0) {
        return 255;
    }
    return (byte) (f + 0.5);
}

inline byte clamp_d(double d) {
    if (d < 0.0) {
        return 0;
    }
    if (d > 255.0) {
        return 255;
    }
    return (byte) (d + 0.5);
}

inline rgba make_rgba_i(byte r, byte g, byte b, byte a) {
    return (((a & 0xffu) << 24u) | ((r & 0xffu) << 16u) | ((g & 0xffu) << 8u) | (b & 0xffu));
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
