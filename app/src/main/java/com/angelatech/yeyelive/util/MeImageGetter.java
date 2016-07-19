package com.angelatech.yeyelive.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.text.Html;

import com.facebook.datasource.DataSource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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
    public Drawable getDrawable( String source) {
        final Drawable[] drawable = {null};
        final int w = ScreenUtils.dip2px(mcontext, 22);
        final int h = ScreenUtils.dip2px(mcontext, 22);
        Uri uri = Uri.parse(source);
        LoadBitmap.loadBitmap(mcontext, uri, new LoadBitmap.LoadBitmapCallback() {
            @Override
            public void onLoadSuc(@Nullable Bitmap bitmap) {
                drawable[0] = new BitmapDrawable(bitmap);
                drawable[0].setBounds(0,0,w,h);
            }

            @Override
            public void onLoadFaild(DataSource dataSource) {

            }
        });
//        // 封装路径
//        File file = new File(Environment.getExternalStorageDirectory(), source);
//        // 判断是否以http开头
//        if(source.startsWith("http")) {
//            // 判断路径是否存在
//            if(file.exists()) {
//                // 存在即获取drawable
//                drawable = Drawable.createFromPath(file.getAbsolutePath());
//                drawable.setBounds(0, 0, w, h);
//            } else {
//                // 不存在即开启异步任务加载网络图片
//                AsyncLoadNetworkPic networkPic = new AsyncLoadNetworkPic();
//                networkPic.execute(source);
//            }
//        }
        return drawable[0];
    }

    public final class AsyncLoadNetworkPic extends AsyncTask<String, Integer, Void> {

        @Override
        protected Void doInBackground(String... params) {
            // 加载网络图片
            loadNetPic(params);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // 当执行完成后再次为其设置一次
//            text.setText(Html.fromHtml(htmlThree, mImageGetter, null));
        }

        /**
         * 加载网络图片
         */
        private void loadNetPic(String... params) {
            String path = params[0];
            File file = new File(Environment.getExternalStorageDirectory(), path);
            file.getParentFile().mkdirs();
            InputStream in = null;
            FileOutputStream out = null;
            try {
                URL url = new URL(path);
                HttpURLConnection connUrl = (HttpURLConnection) url.openConnection();
                connUrl.setConnectTimeout(5000);
                connUrl.setRequestMethod("GET");
                if (connUrl.getResponseCode() == 200) {
                    in = connUrl.getInputStream();
                    out = new FileOutputStream(file);
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = in.read(buffer)) != -1) {
                        out.write(buffer, 0, len);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
