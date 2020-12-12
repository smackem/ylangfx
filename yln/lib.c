//
// Created by Philip Boger on 06.12.20.
//

#include <math.h>
#include <imaging.h>
#include <util.h>
#include "net_smackem_ylang_interop_Yln.h"

static void compose_color_add(Color *dest, const Color *left, const Color *right) {
    dest->red = left->red + right->red;
    dest->green = left->green + right->green;
    dest->blue = left->blue + right->blue;
    dest->alpha = left->alpha;
}

static void compose_color_sub(Color *dest, const Color *left, const Color *right) {
    dest->red = left->red - right->red;
    dest->green = left->green - right->green;
    dest->blue = left->blue - right->blue;
    dest->alpha = left->alpha;
}

static void compose_color_mul(Color *dest, const Color *left, const Color *right) {
    dest->red = left->red * right->red / 255.0f;
    dest->green = left->green * right->green / 255.0f;
    dest->blue = left->blue * right->blue / 255.0f;
    dest->alpha = left->alpha;
}

static void compose_color_div(Color *dest, const Color *left, const Color *right) {
    dest->red = left->red * 255.0f / right->red;
    dest->green = left->green * 255.0f / right->green;
    dest->blue = left->blue * 255.0f / right->blue;
    dest->alpha = left->alpha;
}

static void compose_color_mod(Color *dest, const Color *left, const Color *right) {
    int right_red = (int) right->red;
    int right_green = (int) right->green;
    int right_blue = (int) right->blue;
    dest->red = (float)(right_red != 0 ? (int) left->red % right_red : 0);
    dest->green = (float)(right_green != 0 ? (int) left->green % (int) right->green : 0);
    dest->blue = (float)(right_blue != 0 ? (int) left->blue % (int) right->blue : 0);
    dest->alpha = left->alpha;
}

static void compose_color_hypot(Color *dest, const Color *left, const Color *right) {
    dest->red = hypotf(left->red, right->red);
    dest->green = hypotf(left->green, right->green);
    dest->blue = hypotf(left->blue, right->blue);
    dest->alpha = left->alpha;
}

static void compose_color_over(Color *dest, const Color *left, const Color *right) {
    paint_color_over(dest, left, right);
}

static void compose_color_min(Color *dest, const Color *left, const Color *right) {
    dest->red = min(left->red, right->red);
    dest->green = min(left->green, right->green);
    dest->blue = min(left->blue, right->blue);
    dest->alpha = left->alpha;
}

static void compose_color_max(Color *dest, const Color *left, const Color *right) {
    dest->red = max(left->red, right->red);
    dest->green = max(left->green, right->green);
    dest->blue = max(left->blue, right->blue);
    dest->alpha = left->alpha;
}

static const color_composition_t compositions[] = {
        NULL,
        compose_color_add,   // start at 1 ..
        compose_color_sub,
        compose_color_mul,
        compose_color_div,
        compose_color_mod,
        compose_color_hypot,
        compose_color_over,
        compose_color_min,
        compose_color_max,
};

JNIEXPORT jfloatArray JNICALL Java_net_smackem_ylang_interop_Yln_convolveImage(JNIEnv *env_ptr, jobject this_ptr,
         jint width, jint height, jfloatArray pixels,
         jint kernelWidth, jint kernelHeight, jfloatArray kernelValues) {
    JNIEnv env = *env_ptr;
    jfloat *origPixels = env->GetFloatArrayElements(env_ptr, pixels, NULL);
    jfloat *kernelFloats = env->GetFloatArrayElements(env_ptr, kernelValues, NULL);

    ImageFloat orig;
    wrap_image(&orig, width, height, (Color *) origPixels);
    Kernel kernel;
    wrap_kernel(&kernel, kernelWidth, kernelHeight, (float *) kernelFloats);
    ImageFloat dest;
    init_image(&dest, width, height);

    convolve_image(&dest, &orig, &kernel);

    int size = width * height * 4;
    jfloatArray result = env->NewFloatArray(env_ptr, size);
    env->SetFloatArrayRegion(env_ptr, result, 0, size, (jfloat *) dest.pixels);

    env->ReleaseFloatArrayElements(env_ptr, pixels, origPixels, JNI_ABORT);
    env->ReleaseFloatArrayElements(env_ptr, kernelValues, kernelFloats, JNI_ABORT);
    free_image(&dest);
    return result;
}

JNIEXPORT jfloatArray JNICALL Java_net_smackem_ylang_interop_Yln_convolveKernel(JNIEnv *env_ptr, jobject this_ptr,
        jint width, jint height, jfloatArray values,
        jint kernelWidth, jint kernelHeight, jfloatArray kernelValues) {
    JNIEnv env = *env_ptr;
    jfloat *origValues = env->GetFloatArrayElements(env_ptr, values, NULL);
    jfloat *kernelFloats = env->GetFloatArrayElements(env_ptr, kernelValues, NULL);

    int size = width * height * 4;
    jfloatArray result = env->NewFloatArray(env_ptr, size);
    env->SetFloatArrayRegion(env_ptr, result, 0, size, origValues);

    env->ReleaseFloatArrayElements(env_ptr, values, origValues, JNI_ABORT);
    env->ReleaseFloatArrayElements(env_ptr, kernelValues, kernelFloats, JNI_ABORT);
    return result;
}

JNIEXPORT jfloatArray JNICALL Java_net_smackem_ylang_interop_Yln_composeImages(JNIEnv *env_ptr, jobject this_ptr,
        jint width, jint height, jfloatArray leftPixels, jfloatArray rightPixels, jint composition) {
    JNIEnv env = *env_ptr;
    jfloat *leftBuf = env->GetFloatArrayElements(env_ptr, leftPixels, NULL);
    jfloat *rightBuf = env->GetFloatArrayElements(env_ptr, rightPixels, NULL);

    ImageFloat left;
    wrap_image(&left, width, height, (Color *) leftBuf);
    ImageFloat right;
    wrap_image(&right, width, height, (Color *) rightBuf);
    ImageFloat dest;
    init_image(&dest, width, height);

    compose_images(&dest, &left, &right, compositions[composition]);

    int size = width * height * 4;
    jfloatArray result = env->NewFloatArray(env_ptr, size);
    env->SetFloatArrayRegion(env_ptr, result, 0, size, (jfloat *) dest.pixels);

    env->ReleaseFloatArrayElements(env_ptr, leftPixels, leftBuf, JNI_ABORT);
    env->ReleaseFloatArrayElements(env_ptr, rightPixels, rightBuf, JNI_ABORT);
    return result;
}

JNIEXPORT jfloatArray JNICALL Java_net_smackem_ylang_interop_Yln_composeKernels(JNIEnv *env_ptr, jobject this_ptr,
        jint width, jint height, jfloatArray leftValues, jfloatArray rightValues, jint composition) {
    return NULL;
}
