package com.angelatech.yeyelive.thirdShare;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.util.LoadBitmap;
import com.angelatech.yeyelive.util.Utility;
import com.facebook.datasource.DataSource;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.will.view.ToastUtils;


public class ThirdShareDialog extends Dialog {

    public ThirdShareDialog(Context context) {
        super(context);
    }

    public ThirdShareDialog(Context context, int theme) {
        super(context, theme);
    }

    public static class Builder implements View.OnClickListener {
        private Activity mContext;
        private FragmentManager fragmentManager;
        private Handler handler;
        private ShareListener listener;

        public Builder(Activity Content, FragmentManager fragmentManager, Handler handler) {
            this.mContext = Content;
            this.fragmentManager = fragmentManager;
            this.handler = handler;
        }

        public void RegisterCallback(ShareListener shareListener) {
            this.listener = shareListener;
        }

        public Builder(Activity context, Handler handler) {
            this.mContext = context;
            this.handler = handler;
        }

        private LinearLayout ly_qq, ly_weibo, ly_wechat, ly_webchatmoments, ly_sinaweibo, ly_facebook;
        private RelativeLayout ly_body;
        private ThirdShareDialog dialog;
        private TextView tv_cancel;
        private String dialogTitle, text, url, imageUrl;
        private Bitmap img = null;

        public Builder setShareContent(String dialogTitle, String text, String url, String imageUrl) {
            this.dialogTitle = dialogTitle;
            this.text = text;
            this.url = url;
            this.imageUrl = imageUrl;

            Uri uri = Uri.parse(imageUrl);
            LoadBitmap.loadBitmap(mContext, uri, new LoadBitmap.LoadBitmapCallback() {
                @Override
                public void onLoadSuc(Bitmap bitmap) {
                    img = bitmap;
                }

                @Override
                public void onLoadFaild(DataSource dataSource) {
                    img = null;
                }
            });
            return this;
        }

        public ThirdShareDialog create() {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            dialog = new ThirdShareDialog(mContext, R.style.Dialog);
            View layout = inflater.inflate(R.layout.dialog_share_item, null);
            //dialog.addContentView(layout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            ly_body = (RelativeLayout) layout.findViewById(R.id.ly_body);
            ly_qq = (LinearLayout) layout.findViewById(R.id.ly_qq);
            ly_weibo = (LinearLayout) layout.findViewById(R.id.ly_weibo);
            ly_wechat = (LinearLayout) layout.findViewById(R.id.ly_wechat);
            ly_webchatmoments = (LinearLayout) layout.findViewById(R.id.ly_webchatmoments);
            ly_sinaweibo = (LinearLayout) layout.findViewById(R.id.ly_sinaweibo);
            ly_facebook = (LinearLayout) layout.findViewById(R.id.ly_facebook);
            tv_cancel = (TextView) layout.findViewById(R.id.tv_cancel);
            ly_body.setOnClickListener(this);
            ly_qq.setOnClickListener(this);
            ly_weibo.setOnClickListener(this);
            ly_wechat.setOnClickListener(this);
            ly_webchatmoments.setOnClickListener(this);
            ly_sinaweibo.setOnClickListener(this);
            ly_facebook.setOnClickListener(this);
            tv_cancel.setOnClickListener(this);
            Window win = dialog.getWindow();
            win.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams lp = win.getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            win.setAttributes(lp);
            dialog.setContentView(layout);
            return dialog;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ly_qq:
                    QqShare qqshare = new QqShare(mContext, mHandler);
                    qqshare.shareToFriend(dialogTitle, text, url, imageUrl);
                    break;
                case R.id.ly_weibo:
                    if (img != null) {
                        SinaShare sinaShare = new SinaShare(mContext, dialogTitle, text, url, img);
                        sinaShare.share(true, true, true, false, false, false);
                    }
                    break;
                case R.id.ly_wechat:
                    if (img != null) {
                        WxShare wxInterface = new WxShare(mContext, listener);
                        wxInterface.SceneWebPage(dialogTitle, text, url, img, SendMessageToWX.Req.WXSceneSession);
                    }
                    break;
                case R.id.ly_webchatmoments:
                    if (img != null) {
                        WxShare webchatmoment = new WxShare(mContext, listener);
                        webchatmoment.SceneWebPage(dialogTitle, text, url, img, SendMessageToWX.Req.WXSceneTimeline);
                    }
                    break;
                case R.id.ly_facebook:
                    FbShare fbshare = new FbShare(mContext, listener);
                    fbshare.postStatusUpdate(dialogTitle, text, url, imageUrl);
                    break;
                case R.id.tv_cancel:
                    Utility.copy(url, mContext);
                    ToastUtils.showToast(mContext, mContext.getString(R.string.capy_to_clip));
                    break;
            }
            dialog.dismiss();
        }

        Handler mHandler = new Handler() {
            public void handleMessage(Message msg) {
                dialog.dismiss();
            }
        };
    }
}
