#ifndef __GIFTMODE_H__
#define __GIFTMODE_H__

class GiftModel
{
public:
	int imageCode;
	char * aniName;
	char * imagePath;
	char * plistPath;
	char * exportJsonPath;
	GiftModel(char * aniName,char * imagePath,char * plistPath,char * exportJsonPath);
};
#endif
