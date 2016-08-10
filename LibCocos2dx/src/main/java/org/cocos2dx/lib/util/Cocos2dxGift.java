package org.cocos2dx.lib.util;

/**
 * cocos 礼物控制
 */
public class Cocos2dxGift {
    public native void stop();
    public native int getPlayStatus();
    public native int play2(String aniName,String imagePath,String plistPath,String exportJsonPath,float scale,int x,int y);
    public native void destroy();
}
