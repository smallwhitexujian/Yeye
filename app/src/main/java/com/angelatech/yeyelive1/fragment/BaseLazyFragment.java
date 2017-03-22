package com.angelatech.yeyelive1.fragment;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.View;

import com.angelatech.yeyelive1.handler.CommonDoHandler;
import com.angelatech.yeyelive1.handler.CommonHandler;

/**
 *  懒加载
 *  提供四个外调接口
 *  只要可见的时候调用一次{
 *      ViewPager.setOffscreenPageLimit(n);
 *      重写onFirstUserVisible
 *  }
 *  在可视时候调用{
 *      重新onUserVisible
 *  }
 *
 */
public class BaseLazyFragment extends Fragment implements View.OnClickListener,CommonDoHandler {
    private boolean isPrepared = false;
    private boolean isFirstVisible = true;
    private boolean isFirstInvisible = true;
    private boolean isFirstResume = true;

    protected CommonHandler<BaseFragment> fragmentHandler = new CommonHandler(this);

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initPrepare();
    }

    /**
     * 第一次onResume中的调用onUserVisible避免操作与onFirstUserVisible操作重复
     */
    @Override
    public void onResume() {
        super.onResume();
        if (isFirstResume) {
            isFirstResume = false;
            return;
        }
        if (getUserVisibleHint()) {
            onUserVisible();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getUserVisibleHint()) {
            onUserInvisible();
        }
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (isFirstVisible) {
                isFirstVisible = false;
                initPrepare();
            } else {
                onUserVisible();
            }
        } else {
            if (isFirstInvisible) {
                isFirstInvisible = false;
                onFirstUserInvisible();
            } else {
                onUserInvisible();
            }
        }
    }

    public synchronized void initPrepare() {
        if (isPrepared) {
            onFirstUserVisible();
        } else {
            isPrepared = true;
        }
    }

    /**
     * 第一次fragment可见（进行初始化工作）
     */
    public void onFirstUserVisible() {

    }

    /**
     * fragment可见（切换回来或者onResume）
     */
    public void onUserVisible() {

    }

    /**
     * 第一次fragment不可见（不建议在此处理事件）
     */
    public void onFirstUserInvisible() {

    }

    /**
     * fragment不可见（切换掉或者onPause）
     */
    public void onUserInvisible() {

    }



    @Override
    public void onClick(View v) {

    }

    @Override
    public void doHandler(Message msg) {

    }
}