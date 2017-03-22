package com.angelatech.yeyelive1.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.angelatech.yeyelive1.R;


/**
 * 自定义title
 */
public class HeaderLayout extends LinearLayout {
    LayoutInflater mInflater;
    RelativeLayout header,rootView;
    TextView titleView;
    LinearLayout leftContainer, rightContainer;
    Button backBtn, submit;
    View imageViewLayout;
    TextView rightButton;


    public HeaderLayout(Context context) {
        super(context);
        init();
    }

    public HeaderLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mInflater = LayoutInflater.from(getContext());
        header = (RelativeLayout) mInflater.inflate(R.layout.base_common_header, null, false);
        rootView  = (RelativeLayout)header.findViewById(R.id.rootView);
        titleView = (TextView) header.findViewById(R.id.titleView);
        leftContainer = (LinearLayout) header.findViewById(R.id.leftContainer);
        rightContainer = (LinearLayout) header.findViewById(R.id.rightContainer);
        backBtn = (Button) header.findViewById(R.id.backBtn);
        submit = (Button) header.findViewById(R.id.submit);
        addView(header);
    }

    public void showTitle(int titleId) {
        titleView.setText(titleId);
    }

    public void showTitle(String s) {
        titleView.setText(s);
    }

    public void showLeftBackButton(OnClickListener listener) {
        showLeftBackButton(R.string.empty_header_str, listener);
    }

    public void showLeftBackButton() {
        showLeftBackButton(null);
    }

    public void showLeftBackButton(int backTextId, OnClickListener listener) {
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.back, 0, 0, 0);
        backBtn.setText(backTextId);
        if (listener == null) {
            listener = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((Activity) getContext()).finish();
                }
            };
        }
        backBtn.setOnClickListener(listener);
    }

    public void showLeftBackButton(int backTextId, OnClickListener listener, boolean isback) {
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setText(backTextId);
        if (listener == null) {
            listener = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((Activity) getContext()).finish();
                }
            };
        }
        backBtn.setOnClickListener(listener);
    }

    public void showRightSubmitButton(int backTextId, OnClickListener listener) {
        submit.setVisibility(View.VISIBLE);
        submit.setText(backTextId);
        if (listener == null) {
            listener = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((Activity) getContext()).finish();
                }
            };
        }
        submit.setOnClickListener(listener);
    }


    public void showLeftImageButton(int rightResId, OnClickListener listener) {
        View imageViewLayout = mInflater.inflate(R.layout.base_common_header_right_image_btn, null, false);
        ImageButton leftButton = (ImageButton) imageViewLayout.findViewById(R.id.imageBtn);
        leftButton.setImageResource(rightResId);
        leftButton.setOnClickListener(listener);
        leftContainer.addView(imageViewLayout);
    }

    public void showLeftImageButton(int rightResId, OnClickListener listener, boolean isShow) {
        View imageViewLayout = mInflater.inflate(R.layout.base_common_header_right_image_btn, null, false);
        ImageButton leftButton = (ImageButton) imageViewLayout.findViewById(R.id.imageBtn);
//        TextView notreadnum = (TextView)imageViewLayout.findViewById(R.id.notreadnum);
//        if (isShow){
//            notreadnum.setVisibility(VISIBLE);
//        }else{
//            notreadnum.setVisibility(GONE);
//        }
        leftButton.setImageResource(rightResId);
        leftButton.setOnClickListener(listener);
        leftContainer.addView(imageViewLayout);
    }

    public void showRightImageButton(int rightResId, OnClickListener listener) {
        View imageViewLayout = mInflater.inflate(R.layout.base_common_header_right_image_btn, null, false);
        ImageButton rightButton = (ImageButton) imageViewLayout.findViewById(R.id.imageBtn);
        rightButton.setImageResource(rightResId);
        rightButton.setOnClickListener(listener);
        rightContainer.addView(imageViewLayout);
    }

    public void showRightImageButton(int rightResId, OnClickListener listener, boolean isShow) {
        imageViewLayout = mInflater.inflate(R.layout.base_common_header_right_image_btn, null, false);
        ImageButton rightButton = (ImageButton) imageViewLayout.findViewById(R.id.imageBtn);
//        TextView notreadnum = (TextView)imageViewLayout.findViewById(R.id.notreadnum);
//        if (isShow){
//            notreadnum.setVisibility(VISIBLE);
//        }else{
//            notreadnum.setVisibility(GONE);
//        }
        rightButton.setImageResource(rightResId);
        rightButton.setOnClickListener(listener);
        rightContainer.addView(imageViewLayout);
    }

    public void removeView() {
        rightContainer.removeView(imageViewLayout);
    }

    public void showRightTextButton(int color, int rightResId, OnClickListener listener) {
        View imageViewLayout = mInflater.inflate(R.layout.base_common_header_right_btn, null, false);
        rightButton = (TextView) imageViewLayout.findViewById(R.id.textBtn);
        rightButton.setText(rightResId);
        rightButton.setTextColor(color);
        rightButton.setOnClickListener(listener);
        rightContainer.addView(imageViewLayout);
    }

    public void showRightTextButton(int color, String str, OnClickListener listener) {
        View imageViewLayout = mInflater.inflate(R.layout.base_common_header_right_btn, null, false);
        rightButton = (TextView) imageViewLayout.findViewById(R.id.textBtn);
        rightButton.setText(str);
        rightButton.setTextColor(color);
        rightButton.setOnClickListener(listener);
        rightContainer.addView(imageViewLayout);
    }


    public void setRightTextButton(int color){
        rightButton.setTextColor(color);
    }

    public void setLeftTextButton(int color, int rightResId){
        backBtn.setText(rightResId);
        backBtn.setTextColor(color);
    }

    public void setBackground(Drawable drawable){
        if(rootView!=null){
            rootView.setBackground(drawable);
        }
    }
}
