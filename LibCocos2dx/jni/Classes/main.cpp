#include "AppDelegate.h"
#include "cocos2d.h"
#include "platform/android/jni/JniHelper.h"
#include <jni.h>
#include <android/log.h>
#include "GiftModel.h"
#include "GiftControlModel.h"

#define  LOG_TAG    "main"
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define  LOG_TAG    "main"
#define  NELEM(x) ((int) (sizeof(x) / sizeof((x)[0])))

using namespace cocos2d;

AppDelegate *pAppDelegate;

extern "C"{
	void displayFrameLayout(const char * jniClass,const char *jniMethod,int viewId){
			JniMethodInfo minfo;//定义Jni函数信息结构体
			//getStaticMethodInfo 次函数返回一个bool值表示是否找到此函数
			bool isHave = JniHelper::getStaticMethodInfo(minfo,jniClass,jniMethod,"(I)V");
			if (!isHave) {
				LOGD("jni->%s/%s:此函数不存在", jniClass,jniMethod);
			}else{
				LOGD("jni->%s/%s:此函数存在", jniClass,jniMethod);
				jint jviewId = (jint)viewId;
				//调用此函数
				minfo.env->CallStaticVoidMethod(minfo.classID, minfo.methodID,jviewId);
			}
			LOGD("jni-java函数执行完毕");
	}

    //礼物控制
    jint Java_org_cocos2dx_lib_util_Cocos2dxGift_play2(JNIEnv *env, jobject thiz,jstring aniName,jstring imagePath,jstring plistPath,jstring exportJsonPath,jfloat speedScale,jfloat scale,jint x,jint y)
     {
        char * aniNameStr = (char*)env->GetStringUTFChars(aniName, 0);
        char * imagePathStr = (char*)env->GetStringUTFChars(imagePath, 0);
        char * plistPathStr = (char*)env->GetStringUTFChars(plistPath, 0);
        char * exportJsonPathStr = (char*)env->GetStringUTFChars(exportJsonPath, 0);

        const GiftModel gift = GiftModel(aniNameStr,imagePathStr,plistPathStr,exportJsonPathStr);
        const GiftControlModel control = GiftControlModel(speedScale,scale,x,y);

        int result = pAppDelegate->play(gift,control);
        return (jint)result;
    }

    void Java_org_cocos2dx_lib_util_Cocos2dxGift_stop(JNIEnv *env, jobject thiz)
    {
        pAppDelegate->stop();
    }

    jint Java_org_cocos2dx_lib_util_Cocos2dxGift_getPlayStatus(JNIEnv *env, jobject thiz)
    {
        int result = pAppDelegate->getPlayStatus();
        return (jint)result;
    }

    void Java_org_cocos2dx_lib_util_Cocos2dxGift_destroy(JNIEnv *env, jobject thiz)
    {
       //cocos2d::Director::getInstance()->end();
       pAppDelegate->clear();
    }


}

void cocos_android_app_init (JNIEnv* env, jobject thiz) {
    LOGD("qiang cocos_android_app_init");
    pAppDelegate = new AppDelegate();
}
