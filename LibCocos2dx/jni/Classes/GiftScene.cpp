#include "GiftScene.h"
#include <jni.h>
#include "platform/android/jni/JniHelper.h"

#define  LOG_TAG    "GiftScene"
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
USING_NS_CC;

int GiftScene::playtag = 0;


Scene* GiftScene::createScene() {
	auto scene = Scene::create();
	auto layer = GiftScene::create();
	scene->addChild(layer);
	return scene;
}

bool GiftScene::init() {
	if (!Layer::init()) {
		return false;
	}
	return true;
}

void GiftScene::play(const GiftModel gift,const GiftControlModel control)
{
	Size vsize = Director::getInstance()->getVisibleSize();
	loadAnimation(gift,control);
}

void GiftScene::loadAnimation(const GiftModel gift,const GiftControlModel control)
{
	cocostudio::ArmatureDataManager::getInstance()->addArmatureFileInfo(gift.exportJsonPath);
	auto armature = cocostudio::Armature::create(gift.aniName);
	armature->setScale(control.scale);
	armature->setPosition(Vec2(control.x,control.y));
	armature->getAnimation()->setSpeedScale(control.speedScale);
	armature->getAnimation()->setMovementEventCallFunc(this,movementEvent_selector(GiftScene::movementCallback));
	if(strcmp(gift.aniName,"fx_jixiangwu") == 0)
        {
            LOGD("oooooo");
            ParticleSystem* m1 = ParticleSystemQuad::create("fx_jixiangwu/coin_4.plist");
            ParticleSystem* m2 = ParticleSystemQuad::create("fx_jixiangwu/jixiangwu_1.plist");
            ParticleSystem* m3 = ParticleSystemQuad::create("fx_jixiangwu/jixiangwu_2.plist");
            ParticleSystem* m4 = ParticleSystemQuad::create("fx_jixiangwu/jixiangwu_3.plist");
            ParticleSystem* m5 = ParticleSystemQuad::create("fx_jixiangwu/hongbao.plist");
            ParticleSystem* m6 = ParticleSystemQuad::create("fx_jixiangwu/coin_3.plist");
            ParticleSystem* m7 = ParticleSystemQuad::create("fx_jixiangwu/jixiangwu_4.plist");
            armature->addChild(m1);
            armature->addChild(m2);
            armature->addChild(m3);
            armature->addChild(m4);
            armature->addChild(m5);
            armature->addChild(m6);
            armature->addChild(m7);
        }
	this->addChild(armature);
	if(strcmp(gift.aniName,"fx_coin_xingyunliwu") == 0)
            {
                LOGD("oooooo");
                ParticleSystem* m1 = ParticleSystemQuad::create("fx_coin_xingyunliwu/coin2_1.plist");
                ParticleSystem* m2 = ParticleSystemQuad::create("fx_coin_xingyunliwu/coin2_2.plist");
                ParticleSystem* m3 = ParticleSystemQuad::create("fx_coin_xingyunliwu/star_2.plist");
                this->addChild(m1);
                this->addChild(m2);
                this->addChild(m3);
            }
    this->addChild(armature);
	armature->getAnimation()->playWithIndex(0);
}

void GiftScene::pause()
{
	GiftScene::playtag = 0;
	this->removeAllChildren();
}

void GiftScene::stop()
{
	GiftScene::playtag = 0;
	this->removeAllChildren();
}

void callbackJava()
{
    LOGD("qiang callbackjava ");
    JniMethodInfo t;
    const char * jniClass = "org/cocos2dx/lib/util/Cocos2dxGiftCallback";//这里写你所要调用的java代码的类名
    if(JniHelper::getStaticMethodInfo(t, jniClass, "onFinish", "()V"))
    {
        t.env->CallStaticVoidMethod(t.classID, t.methodID);
    }
}

void callbackJava_()
{
    jclass clazz = NULL;
    jobject jobj = NULL;
    jmethodID mid_construct = NULL;
    jmethodID mid_instance = NULL;
    JNIEnv *env = JniHelper::getEnv();

    if (!env) {
        return;
    }

    // 1、从classpath路径下搜索ClassMethod这个类，并返回该类的Class对象
    clazz = env->FindClass("org/cocos2dx/lib/util/Cocos2dxGiftCallback");
    if (clazz == NULL) {
        LOGD("找不到'org/cocos2dx/lib/util/Cocos2dxGiftCallback'这个类");
        return;
    }

    // 2、获取类的默认构造方法ID
    mid_construct = env->GetMethodID(clazz, "<init>","()V");
    if (mid_construct == NULL) {
        LOGD("找不到默认的构造方法");
        return;
    }

    // 3、查找实例方法的ID
    mid_instance = env->GetMethodID(clazz, "finish", "()V");
    if (mid_instance == NULL) {
        return;
    }

    // 4、创建该类的实例
    jobj = env->NewObject(clazz,mid_construct);
    if (jobj == NULL) {
        LOGD("在org/cocos2dx/lib/util/Cocos2dxGiftCallback类中找不到finish方法");
        return;
    }

    // 5、调用对象的实例方法
    env->CallVoidMethod(jobj,mid_instance);

    // 删除局部引用
    env->DeleteLocalRef(clazz);
    env->DeleteLocalRef(jobj);
}

void GiftScene::movementCallback(cocostudio::Armature * armature,cocostudio::MovementEventType type, const std::string & name)
{
	if (cocostudio::MovementEventType::COMPLETE == type)
	{
//		auto director = Director::getInstance();
//		director->pause();
		GiftScene::playtag = 0;
		this->removeChild(armature);
//		this->removeAllChildren();
        callbackJava();
	}
	else if (cocostudio::MovementEventType::LOOP_COMPLETE == type)
	{
//		LOGD("qiang HelloWorld  is LOOP_COMPLETE ");
	}
	else if (cocostudio::MovementEventType::START == type) {
		GiftScene::playtag = 1;
	}
}