package com.will.imageloader.controllerlistener;

import android.graphics.drawable.Animatable;
import android.support.annotation.Nullable;

import com.facebook.common.logging.FLog;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.image.QualityInfo;

/**
 * Created by jjfly on 16-3-16.
 */
public class ListenerBuilder extends BaseControllerListener<ImageInfo> {

    @Override
    public void onFinalImageSet(String id, @Nullable ImageInfo imageInfo, @Nullable Animatable anim) {
        if (imageInfo == null) {
            return;
        }
        QualityInfo qualityInfo = imageInfo.getQualityInfo();
        FLog.d("Final image received! " + "Size %d x %d",
                "Quality level %d, good enough: %s, full quality: %s",
                imageInfo.getWidth(),
                imageInfo.getHeight(),
                qualityInfo.getQuality(),
                qualityInfo.isOfGoodEnoughQuality(),
                qualityInfo.isOfFullQuality());
    }

    @Override
    public void onIntermediateImageSet(String id, @Nullable ImageInfo imageInfo) {

    }

    @Override
    public void onFailure(String id, Throwable throwable) {
        FLog.e(getClass(), throwable, "Error loading %s", id);
    }

}
