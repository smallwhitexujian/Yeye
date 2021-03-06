package com.angelatech.yeyelive1.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.angelatech.yeyelive1.R;
import com.angelatech.yeyelive1.adapter.MyFragmentPagerAdapter;

import java.util.ArrayList;

/**
 * 　　┏┓　　　　┏┓
 * 　┏┛┻━━━━┛┻┓
 * 　┃　　　　　　　　┃
 * 　┃　　　━　　　　┃
 * 　┃　┳┛　┗┳　　┃
 * 　┃　　　　　　　　┃
 * 　┃　　　┻　　　　┃
 * 　┃　　　　　　　　┃
 * 　┗━━┓　　　┏━┛
 * 　　　　┃　　　┃　　　神兽保佑
 * 　　　　┃　　　┃　　　代码无BUG！
 * 　　　　┃　　　┗━━━┓
 * 　　　　┃　　　　　　　┣┓
 * 　　　　┃　　　　　　　┏┛
 * 　　　　┗┓┓┏━┳┓┏┛
 * 　　　　　┃┫┫　┃┫┫
 * <p>
 * <p>
 * 作者: Created by: xujian on Date: 16/9/21.
 * 邮箱: xj626361950@163.com
 * com.angelatech.yeyelive1.activity
 * 排行榜
 */

public class TabDialogFragment extends DialogFragment {
    private ArrayList<Fragment> fragmentList;
    private ViewPager mAbSlidingTabView;
    public static TextView Tab_1;
    private Fragment fragment_Tab_1;
    private View view,btn_close;
    private String userid;

    public void setUserid(String uid) {
        this.userid = uid;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        getDialog().getWindow().getAttributes().windowAnimations= R.style.dialogAnim;
        ;
        view = inflater.inflate(R.layout.dialog_tab, container, false);
        fragmentList = new ArrayList<>();

        if (userid != null && userid.isEmpty()) {
            fragment_Tab_1 = SevenRankFragment.newInstance(1, userid);
        } else {
            fragment_Tab_1 = SevenRankFragment.newInstance(1);
        }
        fragmentList.add(fragment_Tab_1);
        initView(getString(R.string.rank_seven_dedicated), fragmentList);
        return view;
    }

    private void initView(String tab1, ArrayList<Fragment> fragmentList) {

        mAbSlidingTabView = (ViewPager) view.findViewById(R.id.mAbSlidingTabView);
        btn_close = view.findViewById(R.id.btn_close);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        Tab_1 = (TextView) view.findViewById(R.id.Tab_1);
        Tab_1.setText(tab1);

        mAbSlidingTabView.setAdapter(new MyFragmentPagerAdapter(getChildFragmentManager(), fragmentList));
        mAbSlidingTabView.setCurrentItem(0);
        Tab_1.setSelected(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }
}
