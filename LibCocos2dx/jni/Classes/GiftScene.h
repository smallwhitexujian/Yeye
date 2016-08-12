#ifndef __GIFTSCENE_H__
#define __GIFTSCENE_H__

#include "cocos2d.h"
#include "cocostudio/CocoStudio.h"
#include "ui/CocosGUI.h"
#include "GiftModel.h"
#include "GiftControlModel.h"

using std::string;
USING_NS_CC;

class GiftScene : public cocos2d::CCLayer
{
public:
	 static int playtag ;
    static cocos2d::Scene* createScene();
    virtual bool init();
    void play(const char * name);
    void play(const GiftModel gift,const GiftControlModel control);
    void pause();
    void stop();
    void menuCloseCallback(cocos2d::Ref* pSender);
    void movementCallback(cocostudio::Armature * armature, cocostudio::MovementEventType type, const std::string & name);
    CREATE_FUNC(GiftScene);
private:
    void loadAnimation(const GiftModel gift,const GiftControlModel control);
};

#endif
