package com.angelatech.yeyelive.fragment;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.TransactionValues;
import com.angelatech.yeyelive.model.RoomModel;
import com.angelatech.yeyelive.view.FrescoBitmapUtils;
import com.angelatech.yeyelive.view.GaussAmbiguity;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * 直播结束页面
 */
public class LiveFinishFragment extends DialogFragment implements View.OnClickListener {
    private Button btn_close;
    public RoomModel roomModel;
    private SimpleDraweeView img_head;
    private TextView txt_barname, txt_likenum, txt_live_num, txt_coin, txt_live_time;
    private LinearLayout ly_live;
    private ImageView face;
    private View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        view = inflater.inflate(R.layout.activity_live_finish, container, false);
        initView();
        setView();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        getDialog().getWindow().setLayout(dm.widthPixels, getDialog().getWindow().getAttributes().height);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void initView() {
        btn_close = (Button) view.findViewById(R.id.btn_close);
        btn_close.setOnClickListener(this);
        img_head = (SimpleDraweeView) view.findViewById(R.id.img_head);
        txt_barname = (TextView) view.findViewById(R.id.txt_barname);
        txt_likenum = (TextView) view.findViewById(R.id.txt_likenum);
        face = (ImageView) view.findViewById(R.id.face);
        ly_live = (LinearLayout) view.findViewById(R.id.ly_live);
        txt_live_num = (TextView) view.findViewById(R.id.txt_live_num);
        txt_coin = (TextView) view.findViewById(R.id.txt_coin);
        txt_live_time = (TextView) view.findViewById(R.id.txt_live_time);

    }

    public void setView() {

        roomModel = (RoomModel) getActivity().getIntent().getSerializableExtra(TransactionValues.UI_2_UI_KEY_OBJECT);
        img_head.setImageURI(Uri.parse(roomModel.getUserInfoDBModel().headurl));
        txt_barname.setText(roomModel.getUserInfoDBModel().nickname);
        txt_likenum.setText(String.valueOf(roomModel.getLikenum()));
        if (roomModel.getRoomType().equals("live")) {
            ly_live.setVisibility(View.VISIBLE);
            txt_coin.setText(String.valueOf(roomModel.getLivecoin()));
            txt_live_num.setText(String.valueOf(roomModel.getLivenum()));
            txt_live_time.setText(String.format(getString(R.string.txt_live_time), roomModel.getLivetime()));
        } else {
            ly_live.setVisibility(View.INVISIBLE);
            FrescoBitmapUtils.getImageBitmap(getActivity(), roomModel.getUserInfoDBModel().headurl, new FrescoBitmapUtils.BitCallBack() {
                @Override
                public void onNewResultImpl(Bitmap bitmap) {
                    final Drawable drawable = GaussAmbiguity.BlurImages(bitmap, getActivity());
                    face.setImageDrawable(drawable);
                    face.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }
            });
        }
    }

    public void setRoomModel(RoomModel model) {
        this.roomModel = model;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_close:
                dismiss();
                break;
        }
    }
}
