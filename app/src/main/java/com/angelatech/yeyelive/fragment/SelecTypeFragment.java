package com.angelatech.yeyelive.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.adapter.CommonAdapter;
import com.angelatech.yeyelive.adapter.ViewHolder;
import com.angelatech.yeyelive.model.SelecTypeModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

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
 * 作者: Created by: xujian on Date: 2017/1/11.
 * 邮箱: xj626361950@163.com
 * com.angelatech.yeyelive.fragment
 */

public class SelecTypeFragment extends BaseFragment {
    @BindView(R.id.listView)
    ListView listView;
    Unbinder unbinder;
    private CommonAdapter commonAdapter;
    private List<SelecTypeModel> listDatas = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_type, container, false);
        unbinder = ButterKnife.bind(this, view);

        SelecTypeModel selec1 = new SelecTypeModel("Myclear FPX", "fpx_my");
        SelecTypeModel selec2 = new SelecTypeModel("Hong Leong", "hlb_my");
        SelecTypeModel selec3 = new SelecTypeModel("Maybank2u", "maybank2u_my");
        SelecTypeModel selec4 = new SelecTypeModel("CIMB Clicks", "cimb_my");
        SelecTypeModel selec5 = new SelecTypeModel("Affin Bank", "affinepg_my");
        SelecTypeModel selec6 = new SelecTypeModel("Am online", "amb_my");
        SelecTypeModel selec7 = new SelecTypeModel("RHB Now", "rhb_my");
        SelecTypeModel selec8 = new SelecTypeModel("MOLPay", "molpay");
        SelecTypeModel selec9 = new SelecTypeModel("Webcash", "webcash_my");
        SelecTypeModel selec10 = new SelecTypeModel("支付宝（Alipay）", "alipay_cn");
        SelecTypeModel selec11 = new SelecTypeModel("财付通/微信支付（Tenpay）", "tenpay_cn");
        SelecTypeModel selec12 = new SelecTypeModel("银联（UnionPay）", "unionpay_cn");
        listDatas.add(selec1);
        listDatas.add(selec2);
        listDatas.add(selec3);
        listDatas.add(selec4);
        listDatas.add(selec5);
        listDatas.add(selec6);
        listDatas.add(selec7);
        listDatas.add(selec8);
        listDatas.add(selec9);
        listDatas.add(selec10);
        listDatas.add(selec11);
        listDatas.add(selec12);

        commonAdapter = new CommonAdapter(getActivity(),listDatas,R.layout.item_pay_select_type) {
            @Override
            public void convert(ViewHolder helper, Object item, int position) {
                helper.setText(R.id.str,listDatas.get(position).getKey());
            }
        };
        listView.setAdapter(commonAdapter);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
