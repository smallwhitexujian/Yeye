package com.angelatech.yeyelive.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;

import com.angelatech.yeyelive.view.FrescoBitmapUtils;


/**
 * Created by xujian on 16/3/29.
 * ImageGetter处理网络图片
 */
public class MeImageGetter implements Html.ImageGetter {
    private Context mcontext;
    public MeImageGetter(Context c){
        this.mcontext = c;
    }

    @Override
    public Drawable getDrawable(String source) {
        final Drawable[] draw = {null};
        final int w = ScreenUtils.dip2px(mcontext, 22);
        final int h = ScreenUtils.dip2px(mcontext, 22);
        FrescoBitmapUtils.getImageBitmap(mcontext, source, new FrescoBitmapUtils.BitCallBack() {
            @Override
            public void onNewResultImpl(Bitmap bitmap) {
                Drawable drawable = new BitmapDrawable(null,bitmap);
                drawable.setBounds(0, 0, w, h);
                draw[0] = drawable;
            }
        });
        return draw[0];
    }
}
