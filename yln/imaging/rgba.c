//
// Created by Philip Boger on 29.11.20.
//

#include "rgba.h"

inline rgba make_rgba(byte r, byte g, byte b, byte a) {
    return (((a & 0xffu) << 24u) | ((r & 0xffu) << 16u) | ((g & 0xffu) << 8u) | (b & 0xffu));
}

inline rgba make_rgba_f(float r, float g, float b, float a) {
    return make_rgba((byte) (r + 0.5), (byte) (g + 0.5), (byte) (b + 0.5), (byte) (a + 0.5));
}

inline rgba make_rgba_d(double r, double g, double b, double a) {
    return make_rgba((byte) (r + 0.5), (byte) (g + 0.5), (byte) (b + 0.5), (byte) (a + 0.5));
}
