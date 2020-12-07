//
// Created by Philip Boger on 06.12.20.
//

#include <imaging.h>
#include "net_smackem_ylang_jni_Yln.h"

JNIEXPORT jintArray JNICALL Java_net_smackem_ylang_jni_Yln_convolveImage(JNIEnv *env_ptr, jobject this_ptr,
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

JNIEXPORT jfloatArray JNICALL Java_net_smackem_ylang_jni_Yln_convolveKernel(JNIEnv *env_ptr, jobject this_ptr,
        jint width, jint height, jfloatArray values,
        jint kernelWidth, jint kernelHeight, jfloatArray kernelValues) {
    return NULL;
}

JNIEXPORT jintArray JNICALL Java_net_smackem_ylang_jni_Yln_composeImages(JNIEnv *env_ptr, jobject this_ptr,
        jint width, jint height, jintArray destPixels, jintArray origPixels, jint composition) {
    return NULL;
}

JNIEXPORT jfloatArray JNICALL Java_net_smackem_ylang_jni_Yln_composeKernels(JNIEnv *env_ptr, jobject this_ptr,
        jint width, jint height, jfloatArray leftValues, jfloatArray rightValues, jint composition) {
    return NULL;
}
