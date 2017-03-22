package com.angelatech.yeyelive1.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.angelatech.yeyelive1.R;
import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

/**
 * Created by xujian on 16/3/31.
 * 获取图片bitmap
 */
public class FrescoBitmapUtils {
    public static void getImageBitmap(final Context context, String url, final BitCallBack callBack) {
        ImageRequest imageRequest = ImageRequestBuilder
                .newBuilderWithSource(Uri.parse(url))
                .setProgressiveRenderingEnabled(true)
                .build();

        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        DataSource<CloseableReference<CloseableImage>> dataSource = imagePipeline.fetchDecodedImage(imageRequest, context);
        dataSource.subscribe(new BaseBitmapDataSubscriber() {
            @Override
            public void onNewResultImpl(@Nullable Bitmap bitmap) {
                callBack.onNewResultImpl(bitmap);
            }

            @Override
            public void onFailureImpl(DataSource dataSource) {
                // No cleanup required here.
                callBack.onNewResultImpl(  BitmapFactory.decodeResource(context.getResources(), R.drawable.default_photo));
            }
        }, CallerThreadExecutor.getInstance());
    }

    public interface BitCallBack {
        void onNewResultImpl(Bitmap bitmap);
    }
}
