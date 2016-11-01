package com.angelatech.yeyelive.fragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.activity.base.HeaderBaseActivity;
import com.angelatech.yeyelive.adapter.MyFragmentPagerAdapter;
import com.angelatech.yeyelive.util.MyOnPageChangeListener;

import java.util.ArrayList;

import static com.angelatech.yeyelive.R.id.headerLayout;

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
 * com.angelatech.yeyelive.activity
 * 排行榜
 */

public class TabDialogFragment extends DialogFragment {
    private ArrayList<Fragment> fragmentList;
    private ViewPager mAbSlidingTabView;
    private ImageView btn_close;
    public static TextView Tab_1;
    private Fragment fragment_Tab_1;
    private View view;
    private String userid;

    public void setUserid(String uid) {
        this.userid = uid;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
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
        btn_close = (ImageView) view.findViewById(R.id.btn_close);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mAbSlidingTabView = (ViewPager) view.findViewById(R.id.mAbSlidingTabView);

        Tab_1 = (TextView) view.findViewById(R.id.Tab_1);
        Tab_1.setText(tab1);

        mAbSlidingTabView.setAdapter(new MyFragmentPagerAdapter(getChildFragmentManager(), fragmentList));
        mAbSlidingTabView.setCurrentItem(0);
        Tab_1.setSelected(true);
    }
}
