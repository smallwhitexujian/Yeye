#include "GiftScene.h"

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
	cocostudio::ArmatureDataManager::getInstance()->addArmatureFileInfo(gift.imagePath,gift.plistPath,gift.exportJsonPath);
	auto armature = cocostudio::Armature::create(gift.aniName);
	armature->setScale(control.scale);
	armature->setPosition(Vec2(control.x,control.y));
	armature->getAnimation()->setSpeedScale(control.speedScale);
	armature->getAnimation()->setMovementEventCallFunc(this,movementEvent_selector(GiftScene::movementCallback));
	this->addChild(armature);
	armature->getAnimation()->playWithIndex(0);
}

void GiftScene::pause() {
	GiftScene::playtag = 0;
	this->removeAllChildren();
}

void GiftScene::stop() {
	GiftScene::playtag = 0;
	this->removeAllChildren();
}

void GiftScene::movementCallback(cocostudio::Armature * armature,
		cocostudio::MovementEventType type, const std::string & name) {
	if (cocostudio::MovementEventType::COMPLETE == type) {
//		auto director = Director::getInstance();
//		director->pause();
		GiftScene::playtag = 0;
//		this->removeAllChildren();
	} else if (cocostudio::MovementEventType::LOOP_COMPLETE == type) {
//		LOGD("qiang HelloWorld  is LOOP_COMPLETE ");
	} else if (cocostudio::MovementEventType::START == type) {
		GiftScene::playtag = 1;
	}
}

