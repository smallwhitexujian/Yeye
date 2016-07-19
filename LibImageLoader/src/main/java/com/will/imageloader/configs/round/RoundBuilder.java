package com.will.imageloader.configs.round;

import com.facebook.drawee.generic.RoundingParams;

/**
 * Created by jjfly on 16-3-16.
 */
public class RoundBuilder {

    private RoundingParams rp = new RoundingParams();

    public RoundBuilder setRoundAsCircle(boolean roundAsCircle){
        rp.setRoundAsCircle(roundAsCircle);
        return  this;
    }

    //设置边框颜色及其宽度
    public RoundBuilder setBorder(int color,float width){
        rp.setBorder(color,width);
        return  this;
    }

    //设置叠加颜色(背景)
    public RoundBuilder setOverlayColor(int overlayColor){
        rp.setOverlayColor(overlayColor);
        return  this;
    }


    //设置圆角半径
    public RoundBuilder setCornersRadius(float radius){
        rp.setCornersRadius(radius);
        return this;
    }

    //分别设置左上角、右上角、左下角、右下角的圆角半径
    public RoundBuilder setCornersRadii(float topLeft, float topRight, float bottomRight, float bottomLeft){
        rp.setCornersRadii(topLeft,topRight,bottomRight,bottomLeft);
        return this;
    }

    //分别设置（前2个）左上角、(3、4)右上角、(5、6)左下角、(7、8)右下角的圆角半径
    public RoundBuilder setCornersRadii(float[] radii){
        rp.setCornersRadii(radii);
        return this;
    }

    /**设置圆形圆角模式
     *     OVERLAY_COLOR:
     * Draws rounded corners on top of the underlying drawable by overlaying a solid color which
     * is specified by {@code setOverlayColor}. This option should only be used when the
     * background beneath the underlying drawable is static and of the same solid color.
     *
     *    BITMAP_ONLY
     * Uses BitmapShader to draw bitmap with rounded corners. Works only with BitmapDrawables and
     * ColorDrawables.
     * IMPORTANT: Only the actual image and the placeholder image will get rounded. Other images
     * (such as retry, failure, progress bar, backgrounds, overlays, etc.) won't get rounded.
     **/
    public RoundBuilder setRoundingMethod(RoundingParams.RoundingMethod roundingMethod){
        rp.setRoundingMethod(roundingMethod);
        return this;
    }

    public RoundingParams build(){
        return rp;
    }














}
