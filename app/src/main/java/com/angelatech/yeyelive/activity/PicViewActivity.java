package com.angelatech.yeyelive.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;

import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.activity.base.BaseActivity;
import com.angelatech.yeyelive.model.PicViewModel;
import com.angelatech.yeyelive.util.LoadBitmap;
import com.angelatech.yeyelive.util.StartActivityHelper;
import com.angelatech.yeyelive.util.UriHelper;
import com.angelatech.yeyelive.util.VerificationUtil;
import com.angelatech.yeyelive.view.PicView;
import com.facebook.datasource.DataSource;

/**
 * 图片查看
 */
public class PicViewActivity extends BaseActivity {
    private final int MSG_ON_LOAD_BITMAP = 1;
    private PicView picView;
    private PicViewModel picViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picview);
        initView();
        setView();
    }

    private void initView() {
        picView = (PicView) findViewById(R.id.picture_view);
        picViewModel = StartActivityHelper.getTransactionSerializable_1(this);
    }

    private void setView() {
        if (picViewModel.url.equals("")) {
            return;
        }
        LoadBitmap.loadBitmap(this, UriHelper.obtainUri(VerificationUtil.getImageUrl500(picViewModel.url)), new LoadBitmap.LoadBitmapCallback() {
            @Override
            public void onLoadSuc(Bitmap bitmap) {
                uiHandler.obtainMessage(MSG_ON_LOAD_BITMAP, bitmap).sendToTarget();
            }

            @Override
            public void onLoadFaild(DataSource dataSource) {
                try {
                    uiHandler.obtainMessage(MSG_ON_LOAD_BITMAP, BitmapFactory.decodeResource(getResources(), picViewModel.defaultPic)).sendToTarget();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void doHandler(Message msg) {
        switch (msg.what) {
            case MSG_ON_LOAD_BITMAP:
                picView.setImageBitmap((Bitmap) msg.obj);
                break;
        }
    }
}
