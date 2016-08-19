package org.cocos2dx.lib.util;

/**
 * cocos 礼物控制
 */
public class Cocos2dxGift {
    public native void stop();

    public native int getPlayStatus();

    public native int play2(String aniName, String imagePath, String plistPath, String exportJsonPath, float speedScale, float scale, int x, int y);

    public native void destroy();


    public void play(Cocos2dxView cocos2dxView, final Cocos2dxGiftModel gift) {
        cocos2dxView.updateView(new Runnable() {
            @Override
            public void run() {
                play2(gift.aniName, gift.imagePath, gift.plistPath, gift.exportJsonPath, gift.speedScale, gift.scale, gift.x, gift.y);
            }
        });
    }

    public static class Cocos2dxGiftModel {
        public int giftId; //礼物id
        public String aniName;//动画名称
        public String imagePath = "";//动画图片路径
        public String plistPath = "";//plist文件路径
        public String exportJsonPath;//exportJsonPath路径
        public float speedScale = 1;
        public float scale = 1;
        public int x;
        public int y;
    }

    public interface OnGiftPlayFinish{
        public void onGiftPlayFinish();

    }

}
