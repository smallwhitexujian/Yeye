package com.angelatech.yeyelive.util;

import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

/**
 * Created by:      xujian
 * Version          ${version}
 * Date:            16/4/21
 * Description(描述):
 * Modification  History(历史修改):
 * Date              Author          Version
 * ---------------------------------------------------------
 * 16/4/21          xujian         ${version}
 * Why & What is modified(修改原因):
 */
public class Clickable extends ClickableSpan implements View.OnClickListener {
    private final View.OnClickListener mListener;

    public Clickable(View.OnClickListener l) {
        mListener = l;
    }

    @Override
    public void onClick(View v) {
        mListener.onClick(v);
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setUnderlineText(false);
        ds.setColor(Color.parseColor("#FFFF6771"));
    }
}
