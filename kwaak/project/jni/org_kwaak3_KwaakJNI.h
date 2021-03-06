/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class org_kwaak3_KwaakJNI */

#ifndef _Included_org_kwaak3_KwaakJNI
#define _Included_org_kwaak3_KwaakJNI
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     org_kwaak3_KwaakJNI
 * Method:    enableAudio
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_org_kwaak3_KwaakJNI_enableAudio
  (JNIEnv *, jclass, jboolean);

/*
 * Class:     org_kwaak3_KwaakJNI
 * Method:    enableBenchmark
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_org_kwaak3_KwaakJNI_enableBenchmark
  (JNIEnv *, jclass, jboolean);

/*
 * Class:     org_kwaak3_KwaakJNI
 * Method:    enableLightmaps
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_org_kwaak3_KwaakJNI_enableLightmaps
  (JNIEnv *, jclass, jboolean);

/*
 * Class:     org_kwaak3_KwaakJNI
 * Method:    setAudio
 * Signature: (Lorg/kwaak3/KwaakAudio;)V
 */
JNIEXPORT void JNICALL Java_org_kwaak3_KwaakJNI_setAudio
  (JNIEnv *, jclass, jobject);

/*
 * Class:     org_kwaak3_KwaakJNI
 * Method:    showFramerate
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_org_kwaak3_KwaakJNI_showFramerate
  (JNIEnv *, jclass, jboolean);

/*
 * Class:     org_kwaak3_KwaakJNI
 * Method:    initGame
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_org_kwaak3_KwaakJNI_initGame
  (JNIEnv *, jclass, jint, jint);

/*
 * Class:     org_kwaak3_KwaakJNI
 * Method:    setLibraryDirectory
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_org_kwaak3_KwaakJNI_setLibraryDirectory
  (JNIEnv *, jclass, jstring);

/*
 * Class:     org_kwaak3_KwaakJNI
 * Method:    drawFrame
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_kwaak3_KwaakJNI_drawFrame
  (JNIEnv *, jclass);

/*
 * Class:     org_kwaak3_KwaakJNI
 * Method:    queueKeyEvent
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_org_kwaak3_KwaakJNI_queueKeyEvent
  (JNIEnv *, jclass, jint, jint);

/*
 * Class:     org_kwaak3_KwaakJNI
 * Method:    queueMotionEvent
 * Signature: (IFFF)V
 */
JNIEXPORT void JNICALL Java_org_kwaak3_KwaakJNI_queueMotionEvent
  (JNIEnv *, jclass, jint, jfloat, jfloat, jfloat);

/*
 * Class:     org_kwaak3_KwaakJNI
 * Method:    calculateMotion
 * Signature: (IIII)jbyte
 */
JNIEXPORT jbyte JNICALL Java_org_kwaak3_KwaakJNI_calculateMotion
  (JNIEnv *, jclass, jint, jint, jint, jint, jshortArray);

/*
 * Class:     org_kwaak3_KwaakJNI
 * Method:    queueTrackballEvent
 * Signature: (IFF)V
 */
JNIEXPORT void JNICALL Java_org_kwaak3_KwaakJNI_queueTrackballEvent
  (JNIEnv *, jclass, jint, jfloat, jfloat);

/*
 * Class:     org_kwaak3_KwaakJNI
 * Method:    requestAudioData
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_kwaak3_KwaakJNI_requestAudioData
  (JNIEnv *, jclass);

#ifdef __cplusplus
}
#endif
#endif
