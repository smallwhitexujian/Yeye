#include "AppDelegate.h"
#include "GiftScene.h"


#define  LOG_TAG    "AppDelegate"
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)

USING_NS_CC;

AppDelegate::AppDelegate() {
}

AppDelegate::~AppDelegate() 
{
}

//cocostudio::CCArmature * armature = NULL;

GiftScene * layer = NULL;

bool AppDelegate::applicationDidFinishLaunching()
{
    // initialize director
    auto director = Director::getInstance();
    auto glview = director->getOpenGLView();
    if(!glview) {
        glview = GLView::create("My Game");
        director->setOpenGLView(glview);
    }
    // turn on display FPS
    director->setDepthTest(false);
    director->setDisplayStats(false);
    director->setProjection(Director::Projection::_2D);
    director->setAnimationInterval(1.0 / 60);
    auto scene = GiftScene::createScene();
    layer = GiftScene::create();
//    scene->setAnchorPoint(ccp(1/2,1/2));
//    scene->setPosition(ccp(100,200));
    scene->addChild(layer);

    // run
    director->runWithScene(scene);
    return true;
}


int AppDelegate::play(const GiftModel gift,GiftControlModel control)
{
	layer->play(gift,control);
	return 1;
}



void AppDelegate::stop()
{
	if(layer != nullptr)
	{
		layer->stop();
	}
}

void AppDelegate::clear()
{
    if(layer != nullptr)
    {
		layer->removeAllChildren();
	}
}



// This function will be called when the app is inactive. When comes a phone call,it's be invoked too
void AppDelegate::applicationDidEnterBackground()
 {
    Director::getInstance()->stopAnimation();
    LOGD("qiang did enter background");
}

// this function will be called when the app is active again
void AppDelegate::applicationWillEnterForeground()
 {
    Director::getInstance()->startAnimation();
    LOGD("qiang did enter WillEnterForeground");
    // if you use SimpleAudioEngine, it must resume here
    // SimpleAudioEngine::getInstance()->resumeBackgroundMusic();
}


int AppDelegate::getPlayStatus()
{
	if(GiftScene::playtag == 1)
	{
		return 1;
	}
	else{
		return 0;
	}

}

