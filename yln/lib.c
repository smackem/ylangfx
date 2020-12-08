//
// Created by Philip Boger on 06.12.20.
//

#include <math.h>
#include <imaging.h>
#include <util.h>
#include "net_smackem_ylang_interop_Yln.h"

static rgba compose_rgba_add(rgba left, rgba right) {
    return make_rgba((int)red(left) + (int)red(right), (int)green(left) + (int)green(right), (int)blue(left) + (int)blue(right), alpha(left));
}

static rgba compose_rgba_sub(rgba left, rgba right) {
    return make_rgba((int)red(left) - (int)red(right), (int)green(left) - (int)green(right), (int)blue(left) - (int)blue(right), alpha(left));
}

static rgba compose_rgba_mul(rgba left, rgba right) {
    return make_rgba((double)red(left) * (double)red(right) / 255.0, (double)green(left) * (double)green(right) / 255.0, (double)blue(left) * (double)blue(right) / 255.0, alpha(left));
}

static rgba compose_rgba_div(rgba left, rgba right) {
    return make_rgba((double)red(left) * 255.0 / (double)red(right), (double)green(left) * 255.0 / (double)green(right), (double)blue(left) * 255.0 / (double)blue(right), alpha(left));
}

static rgba compose_rgba_mod(rgba left, rgba right) {
    return make_rgba((int)red(left) % (int)red(right), (int)green(left) % (int)green(right), (int)blue(left) % (int)blue(right), alpha(left));
}

static rgba compose_rgba_hypot(rgba left, rgba right) {
    return make_rgba(hypot(red(left), red(right)), hypot(green(left), green(right)), hypot(blue(left), blue(right)), alpha(left));
}

static rgba compose_rgba_over(rgba left, rgba right) {
    return rgba_over(left, right);
}

static rgba compose_rgba_min(rgba left, rgba right) {
    return make_rgba(min(red(left), red(right)), min(green(left), green(right)), min(blue(left), blue(right)), alpha(left));
}

static rgba compose_rgba_max(rgba left, rgba right) {
    return make_rgba(max(red(left), red(right)), max(green(left), green(right)), max(blue(left), blue(right)), alpha(left));
}

static const composition_t compositions[] = {
        NULL,
        compose_rgba_add,   // start at 1 ..
        compose_rgba_sub,
        compose_rgba_mul,
        compose_rgba_div,
        compose_rgba_mod,
        compose_rgba_hypot,
        compose_rgba_over,
        compose_rgba_min,
        compose_rgba_max,
};

JNIEXPORT jintArray JNICALL Java_net_smackem_ylang_interop_Yln_convolveImage(JNIEnv *env_ptr, jobject this_ptr,
         jint width, jint height, jintArray pixels,
         jint kernelWidth, jint kernelHeight, jfloatArray kernelValues) {
    JNIEnv env = *env_ptr;
    jint *origPixels = env->GetIntArrayElements(env_ptr, pixels, NULL);
    jfloat *kernelFloats = env->GetFloatArrayElements(env_ptr, kernelValues, NULL);

    ImageRgba orig;
    wrap_image(&orig, width, height, (rgba *) origPixels);
    Kernel kernel;
    wrap_kernel(&kernel, kernelWidth, kernelHeight, (float *) kernelFloats);
    ImageRgba dest;
    init_image(&dest, width, height);

    convolve_image(&dest, &orig, &kernel);

    int size = width * height;
    jintArray result = env->NewIntArray(env_ptr, size);
    env->SetIntArrayRegion(env_ptr, result, 0, size, (jint *) dest.pixels);

    env->ReleaseIntArrayElements(env_ptr, pixels, origPixels, JNI_ABORT);
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

    int size = width * height;
    jfloatArray result = env->NewFloatArray(env_ptr, size);
    env->SetFloatArrayRegion(env_ptr, result, 0, size, origValues);

    env->ReleaseFloatArrayElements(env_ptr, values, origValues, JNI_ABORT);
    env->ReleaseFloatArrayElements(env_ptr, kernelValues, kernelFloats, JNI_ABORT);
    return result;
}

JNIEXPORT jintArray JNICALL Java_net_smackem_ylang_interop_Yln_composeImages(JNIEnv *env_ptr, jobject this_ptr,
        jint width, jint height, jintArray leftPixels, jintArray rightPixels, jint composition) {
    JNIEnv env = *env_ptr;
    jint *leftBuf = env->GetIntArrayElements(env_ptr, leftPixels, NULL);
    jint *rightBuf = env->GetIntArrayElements(env_ptr, rightPixels, NULL);

    ImageRgba left;
    wrap_image(&left, width, height, (rgba *) leftBuf);
    ImageRgba right;
    wrap_image(&right, width, height, (rgba *) rightBuf);
    ImageRgba dest;
    init_image(&dest, width, height);

    compose_images(&dest, &left, &right, compositions[composition]);

    int size = width * height;
    jintArray result = env->NewIntArray(env_ptr, size);
    env->SetIntArrayRegion(env_ptr, result, 0, size, (jint *) dest.pixels);

    env->ReleaseIntArrayElements(env_ptr, leftPixels, leftBuf, JNI_ABORT);
    env->ReleaseIntArrayElements(env_ptr, rightPixels, rightBuf, JNI_ABORT);
    return result;
}

JNIEXPORT jfloatArray JNICALL Java_net_smackem_ylang_interop_Yln_composeKernels(JNIEnv *env_ptr, jobject this_ptr,
        jint width, jint height, jfloatArray leftValues, jfloatArray rightValues, jint composition) {
    return NULL;
}
