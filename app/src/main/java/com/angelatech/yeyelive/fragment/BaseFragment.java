package com.angelatech.yeyelive.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.View;

import com.angelatech.yeyelive.handler.CommonDoHandler;
import com.angelatech.yeyelive.handler.CommonHandler;
import com.umeng.analytics.MobclickAgent;


/**
 *
 * fragment基本类。
 */
public class BaseFragment extends Fragment implements View.OnClickListener,CommonDoHandler {

    protected CommonHandler<BaseFragment> fragmentHandler = new CommonHandler(this);

    public void doHandler(Message msg) {
        fragmentHandler.handleMessage(msg);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(getActivity());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {

    }

    public CommonHandler<BaseFragment> getFragmentHandler() {
        return fragmentHandler;
    }
}
