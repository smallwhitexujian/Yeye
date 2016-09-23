package com.angelatech.yeyelive.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;


/**
 * Created by Shanli_pc on 2015/9/9.
 */
public class ViewHolder {
    private final SparseArray<View> mViews;
    private int mPosition;
    private View mConvertView;

    public ViewHolder(Context context, ViewGroup parent, int layoutId, int position) {
        this.mPosition = position;
        this.mViews = new SparseArray<View>();
        mConvertView = LayoutInflater.from(context).inflate(layoutId, parent,
                false);
        // setTag
        mConvertView.setTag(this);
    }

    /**
     * 拿到一个ViewHolder对象
     *
     * @param context
     * @param convertView
     * @param parent
     * @param layoutId
     * @param position
     * @return
     */
    public static ViewHolder get(Context context, View convertView, ViewGroup parent, int layoutId, int position) {
        if (convertView == null) {
            return new ViewHolder(context, parent, layoutId, position);
        }
        return (ViewHolder) convertView.getTag();
    }

    public View getConvertView() {
        return mConvertView;
    }

    /**
     * 通过控件的Id获取对于的控件，如果没有则加入views
     *
     * @param viewId
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    /**
     * 为TextView设置字符串
     *
     * @param viewId
     * @param text
     * @return
     */
    public ViewHolder setText(int viewId, CharSequence text) {
        TextView view = getView(viewId);
        view.setText(text);
        return this;
    }

    public ViewHolder setAppendText(int viewId, CharSequence text) {
        TextView view = getView(viewId);
        view.append(text);
        return this;
    }

    public ViewHolder setButtonText(int viewId, CharSequence text) {
        Button view = getView(viewId);
        view.setText(text);
        return this;
    }

    /**
     * 为ImageView设置图片
     *
     * @param viewId
     * @param drawableId
     * @return
     */
    public ViewHolder setImageResource(int viewId, int drawableId) {
        ImageView view = getView(viewId);
        view.setImageResource(drawableId);
        return this;
    }

    /**
     * 为ImageView设置图片
     *
     * @param viewId
     * @param bm
     * @return
     */
    public ViewHolder setImageBitmap(int viewId, Bitmap bm) {
        ImageView view = getView(viewId);
        view.setImageBitmap(bm);
        return this;
    }

    public void hideView(int viewId) {
        View view = getView(viewId);
        view.setVisibility(View.GONE);
    }

    public void showView(int viewId) {
        View view = getView(viewId);
        view.setVisibility(View.VISIBLE);
    }

    public void setLayoutParams(int viewId, ViewGroup.LayoutParams lp) {
        View view = getView(viewId);
        view.setLayoutParams(lp);
    }

    public void setAlpha(int viewId, float alpha) {
        View view = getView(viewId);
        view.setAlpha(alpha);
    }

    public void setBackgroundColor(int viewId, int color) {
        View view = getView(viewId);
        view.setBackgroundColor(color);
    }

    public void setBackground(int viewId, int rsId) {
        View view = getView(viewId);
        view.setBackgroundResource(rsId);
    }

    public void setOnClick(int viewId, View.OnClickListener clickListener) {
        View view = getView(viewId);
        view.setOnClickListener(clickListener);
    }

    public void setTextColor(int viewId, int color) {
        TextView view = getView(viewId);
        view.setTextColor(color);
    }

    public void setBtnListener(int viewId, View.OnClickListener listener) {
        Button btn = getView(viewId);
        btn.setOnClickListener(listener);
    }

    public int getPosition() {
        return mPosition;
    }



    //加载网络允许动画(默认的图片占位和重新加载图片在xml中配置）
    public void setImageViewByImageLoader(int viewId, String filePath) {
        final SimpleDraweeView view = getView(viewId);
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(Uri.parse(filePath))
                .setTapToRetryEnabled(true)
                .setAutoPlayAnimations(true)
                .build();
        view.setController(controller);
    }
    //加载图片
    public void setImageViewByImageLoader1(int viewId, String filePath) {
        final SimpleDraweeView view = getView(viewId);
        view.setImageURI(Uri.parse(filePath));
    }

    public void setImageViewByImageLoader2(int viewId, String filePath) {
        final SimpleDraweeView view = getView(viewId);
        view.setImageURI(Uri.fromFile(new File(filePath)));
    }


    //加载本地(默认的图片占位和重新加载图片在xml中配置）
    public void setImageViewByImageLoader3(int viewId, String filePath) {
        final SimpleDraweeView view = getView(viewId);
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(Uri.fromFile(new File(filePath)))
                .setTapToRetryEnabled(true)
                .setAutoPlayAnimations(true)
                .build();
        view.setController(controller);
    }

    public void postRunnable(int viewId, Runnable action) {
        final View view = getView(viewId);
        view.post(action);
    }

    public void setEnable(int viewId, boolean enable) {
        final View view = getView(viewId);
        view.setEnabled(enable);
    }

    public void startAnimationList(int viewId){
        final View view = getView(viewId);
        ((AnimationDrawable)((ImageView)view).getDrawable()).start();
    }

    public void stopAnimationList(int viewId){
        final View view = getView(viewId);
        ((AnimationDrawable)((ImageView)view).getDrawable()).stop();
    }

}
