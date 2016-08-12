#ifndef __GIFTCONTROL_MODEL_H__
#define __GIFTCONTROL_MODEL_H__

class GiftControlModel
{
public:
	float speedScale;//速度控制
	float scale;//大小控制
	int x;//坐标x
	int y;//坐标y
	GiftControlModel(float speedScale,float scale,int x,int y);
};
#endif
