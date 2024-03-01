#include <jni.h>
#include <string>
extern "C"{
#include "aes.h"
#include "base64.h"
}

#define ECB 1
const char* key="0123456789abcdef";
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

#define CBC 1
#define ECB 1

extern "C" JNIEXPORT jstring

JNICALL
Java_com_xiaoeryu_native_1aes_1demo_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
jstring charToJstring(JNIEnv* envPtr,char *src) {
    JNIEnv* env = envPtr;
    jsize len = strlen(src);
    jclass clsstring = env->FindClass( "java/lang/String");
    jstring strencode = env->NewStringUTF("UTF-8");
    jmethodID mid = env->GetMethodID(clsstring, "<init>",
                                     "([BLjava/lang/String;)V");
    jbyteArray barr = env->NewByteArray(len);
    env->SetByteArrayRegion(barr, 0, len, (jbyte *) src);

    return (jstring) env->NewObject(clsstring, mid, barr, strencode);
}

//__attribute__((section (".mytext")))//隐藏字符表 并没有什么卵用 只是针对初阶hacker的一个小方案而已
//realkey:0123456789abcdef
const char *getKey() {
    return key;
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_xiaoeryu_native_1aes_1demo_MainActivity_encrypt(JNIEnv *env, jclass clazz,
                                                         jstring content) {
    uint8_t *AES_KEY = (uint8_t *) getKey();
    const char *in = (env)->GetStringUTFChars( content, 0);
    char *baseResult = AES_128_ECB_PKCS5Padding_Encrypt(in, AES_KEY);
    jstring  result = (env)->NewStringUTF( baseResult);
    return result;
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_xiaoeryu_native_1aes_1demo_MainActivity_decrypt(JNIEnv *env, jclass clazz,
                                                         jstring content) {
    uint8_t *AES_KEY = (uint8_t *) getKey();
    const char *str = (env)->GetStringUTFChars( content, 0);
    char *desResult = AES_128_ECB_PKCS5Padding_Decrypt(str, AES_KEY);
    //    return (env)->NewStringUTF( desResult);
    //不用系统自带的方法NewStringUTF是因为如果desResult是乱码,会抛出异常
    //    return charToJstring(desResult);
    jstring result = charToJstring(env,desResult);
    return result;
}