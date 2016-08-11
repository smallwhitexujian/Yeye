package org.cocos2dx.lib.util;

/**
 * cocos 礼物控制
 */
public class Cocos2dxGift {
    public native void stop();
    public native int getPlayStatus();
    public native int play2(String aniName,String imagePath,String plistPath,String exportJsonPath,float scale,int x,int y);
    public native void destroy();


    public void play(Cocos2dxView cocos2dxView,final String aniName,final String imagePath,final String plistPath,final String exportJsonPath,final float scale,final int x,final int y){
        cocos2dxView.updateView(new Runnable() {
            @Override
            public void run() {
                play2(aniName,imagePath,plistPath,exportJsonPath,scale,x,y);
            }
        });
    }
}
