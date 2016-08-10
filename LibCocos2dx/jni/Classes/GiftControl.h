#ifndef  __GIFTCONTROL_H__
#define  __GIFTCONTROL_H__



class  GiftControl
{
public:
	GiftControl();
    virtual ~GiftControl();
    //qiang
    virtual int play(const char * name);
    virtual int play(const char * aniName,const char * imagePath,const char * plistPath,const char * exportJsonPath,float scale,int x,int y);
    virtual void stop();
    virtual int getPlayStatus();

};

#endif
