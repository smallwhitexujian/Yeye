package com.angelatech.yeyelive1.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.angelatech.yeyelive1.TransactionValues;
import com.angelatech.yeyelive1.activity.base.HeaderBaseActivity;
import com.angelatech.yeyelive1.adapter.CountrySelectAdapter;
import com.angelatech.yeyelive1.model.CountrySelectItemModel;
import com.angelatech.yeyelive1.util.Utility;
import com.angelatech.yeyelive1.R;
import com.angelatech.yeyelive1.view.LoadingDialog;
import com.will.view.indexview.ChineseToEnglish;
import com.will.view.indexview.PingYinUtil;
import com.will.view.indexview.PinyinComparator;
import com.will.view.indexview.SideBarView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 国家地区选择
 */
public class CountrySelectActivity extends HeaderBaseActivity implements SideBarView.LetterSelectListener {
    private final int MSG_SEARCH = 1;
    private ListView listView;
    private CountrySelectAdapter countrySelectAdapter;
    private List<CountrySelectItemModel> datas = new ArrayList<>();
    private SideBarView sideBarView;
    private TextView mTip;
    private EditText searchInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_contry_select);
        initView();
        setView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void initView() {
        LoadingDialog.showLoadingDialog(CountrySelectActivity.this,null);
        sideBarView = (SideBarView) findViewById(R.id.sidebarview);
        listView = (ListView) findViewById(R.id.country_list);
        mTip = (TextView) findViewById(R.id.tip);
        searchInput = (EditText) findViewById(R.id.search_input);
    }

    private void setView() {
        sideBarView.setOnLetterSelectListen(this);
        headerLayout.showTitle(getString(R.string.select_country));
        headerLayout.showLeftBackButton();
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    uiHandler.obtainMessage(MSG_SEARCH, s.toString()).sendToTarget();
                }
            }
        });

        String[] countryArea = getResources().getStringArray(R.array.country_area);
        String[] firstCountryArea = getResources().getStringArray(R.array.first_country_area);
        loadArray(countryArea,firstCountryArea);

        countrySelectAdapter = new CountrySelectAdapter(CountrySelectActivity.this, datas);
        listView.setAdapter(countrySelectAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CountrySelectItemModel itemModel = datas.get(position);
                Intent intent = new Intent(CountrySelectActivity.this, RegisterFindPWDActivity.class);
                intent.putExtra(TransactionValues.UI_2_UI_KEY_OBJECT, itemModel);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Utility.closeKeybord(searchInput, CountrySelectActivity.this);
                return false;
            }
        });

    }


    //获取二维数组
    private void loadArray(final String[] countryArea,final String[] firstCountryArea) {
        backgroundHandler.post(new Runnable() {
            @Override
            public void run() {
                for (String str : firstCountryArea) {
                    try {
                        String[] strAry = str.split("\\+");
                        CountrySelectItemModel data = new CountrySelectItemModel();
                        data.country = strAry[0];
                        data.num = strAry[1];
                        data.letter = "#";
                        datas.add(data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                Arrays.sort(countryArea,new PinyinComparator());
                for(String str:countryArea){
                    try{
                        String[] strAry = str.split("\\+");
                        CountrySelectItemModel data = new CountrySelectItemModel();
                        data.country = strAry[0];
                        data.num = strAry[1];
                        //                String firstSpell = ChineseToEnglish.getFirstSpell(strAry[0]);
                        String firstSpell = PingYinUtil.getPingYin(strAry[0]);
                        String substring = firstSpell.substring(0, 1).toUpperCase();
                        if (substring.matches("[A-Za-z]")) {
                            data.letter = substring;
                        } else {
                            data.letter = "#";
                        }
                        datas.add(data);
                    }catch (Exception e){
                        e.printStackTrace();

                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LoadingDialog.cancelLoadingDialog();
                        countrySelectAdapter.notifyDataSetChanged();
                    }
                });
            }
        });

    }

    @Override
    public void doHandler(Message msg) {
        switch (msg.what) {
            case MSG_SEARCH:
                String firstSpell = ChineseToEnglish.getFirstSpell((String) msg.obj);
                String substring = firstSpell.substring(0, 1).toUpperCase();
                setListViewPosition(substring);
                break;
        }

    }

    @Override
    public void onLetterSelected(String letter) {
        setListViewPosition(letter);
        mTip.setText(letter);
        mTip.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLetterChanged(String letter) {
        setListViewPosition(letter);
        mTip.setText(letter);
    }

    @Override
    public void onLetterReleased(String letter) {
        mTip.setVisibility(View.GONE);
    }

    private void setListViewPosition(String letter) {
        int firstLetterPosition = countrySelectAdapter.getFirstLetterPosition(letter);
        if (firstLetterPosition != -1) {
            listView.setSelection(firstLetterPosition);
        }
    }
}
