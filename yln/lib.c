//
// Created by Philip Boger on 06.12.20.
//

#include "net_smackem_ylang_jni_Yln.h"

JNIEXPORT jintArray JNICALL Java_net_smackem_ylang_jni_Yln_convolveImage(JNIEnv *env_ptr, jobject this_ptr,
         jint width, jint height, jintArray pixels,
         jint kernelWidth, jint kernelHeight, jfloatArray kernelValues) {
    JNIEnv env = *env_ptr;
    jint *argbPixels = env->GetIntArrayElements(env_ptr, pixels, NULL);
    env->ReleaseIntArrayElements(env_ptr, pixels, argbPixels, JNI_ABORT);
    return NULL;
}

/*
 * Class:     net_smackem_ylang_jni_Yln
 * Method:    convolveKernel
 * Signature: (II[FII[F)[F
 */
JNIEXPORT jfloatArray JNICALL Java_net_smackem_ylang_jni_Yln_convolveKernel
        (JNIEnv *, jobject, jint, jint, jfloatArray, jint, jint, jfloatArray);

/*
 * Class:     net_smackem_ylang_jni_Yln
 * Method:    composeImages
 * Signature: (II[I[II)[I
 */
JNIEXPORT jintArray JNICALL Java_net_smackem_ylang_jni_Yln_composeImages
        (JNIEnv *, jobject, jint, jint, jintArray, jintArray, jint);

