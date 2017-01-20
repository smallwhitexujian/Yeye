package com.angelatech.yeyelive.view;

import android.app.AlertDialog;
import android.content.Context;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.activity.TabMenuActivity;
import com.angelatech.yeyelive.adapter.CommonAdapter;
import com.angelatech.yeyelive.adapter.ViewHolder;
import com.angelatech.yeyelive.model.VoucherModel;

import java.util.ArrayList;

/**
 * Created by xujian on 17-1-20.
 * 登录奖励界面
 */

public class DayDialog {
    private AlertDialog dialog = null;
    private TimeCount timeCount;

    /**
     * 通用dialog
     *
     * @param context 上下文
     */
    public void DayDialog(Context context, ArrayList<VoucherModel> model) {
        if (dialog == null) {
            dialog = new AlertDialog.Builder(context).create();
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
            dialog.show();
            Window window = dialog.getWindow();
            window.setGravity(Gravity.CENTER);
            LayoutInflater inflater = ((TabMenuActivity) context).getLayoutInflater();
            View localView = inflater.inflate(R.layout.dialog_one_day, null);
            localView.setAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_bottom_to_top));
            window.setContentView(localView);

            TextView tips = (TextView) window.findViewById(R.id.tips);
            GridView grid_day = (GridView) window.findViewById(R.id.grid_day);
            CommonAdapter<VoucherModel> adapter = new CommonAdapter<VoucherModel>(context, model, R.layout.item_day) {
                @Override
                public void convert(ViewHolder helper, VoucherModel item, int position) {
                    helper.setBackground(R.id.relativeLayout, R.drawable.item_bg_1);
                    helper.setText(R.id.dayNum, item.key);
                }
            };
            grid_day.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            Button btn_confirm = (Button) window.findViewById(R.id.btn_confirm);
            btn_confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    timeCount = new TimeCount(1500, 500);
                    timeCount.start();
                }
            });
        }
    }

    private class TimeCount extends CountDownTimer {
        private TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
            dialog.dismiss();
        }
    }
}
