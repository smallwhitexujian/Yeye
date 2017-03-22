package com.angelatech.yeyelive1.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.angelatech.yeyelive1.R;
import com.angelatech.yeyelive1.model.CountrySelectItemModel;
import com.will.view.indexview.ChineseToEnglish;

import java.util.ArrayList;
import java.util.List;

public class CountrySelectAdapter extends BaseAdapter {

    private Context mContext;
    private List<CountrySelectItemModel> datas = new ArrayList<>();


    public CountrySelectAdapter(Context context, List<CountrySelectItemModel> datas) {
        this.mContext = context;
        this.datas = datas;
    }

    public void setData(List<CountrySelectItemModel> data) {
        this.datas.clear();
        this.datas.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_country_select, null);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.title);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.name);
            viewHolder.tvAreaNum = (TextView) convertView.findViewById(R.id.area_num);
            viewHolder.tvItem = (LinearLayout) convertView.findViewById(R.id.item);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvName.setText(datas.get(position).country);
        viewHolder.tvAreaNum.setText(datas.get(position).num);
        //当前的item的title与上一个item的title不同的时候回显示title(A,B,C......)
        if (position == getFirstLetterPosition(position) && !datas.get(position).letter.equals("@")) {
            viewHolder.tvTitle.setVisibility(View.VISIBLE);
            viewHolder.tvTitle.setText(datas.get(position).letter.toUpperCase());
        } else {
            viewHolder.tvTitle.setVisibility(View.GONE);
        }

        return convertView;
    }

    /**
     * 顺序遍历所有元素．找到position对应的title是什么（A,B,C?）然后找这个title下的第一个item对应的position
     *
     * @param position
     * @return
     */
    private int getFirstLetterPosition(int position) {
        String letter = datas.get(position).letter;
        int cnAscii = ChineseToEnglish.getCnAscii(letter.toUpperCase().charAt(0));
        int size = datas.size();
        for (int i = 0; i < size; i++) {
            if (cnAscii == datas.get(i).letter.charAt(0)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 顺序遍历所有元素．找到letter下的第一个item对应的position
     *
     * @param letter
     * @return
     */
    public int getFirstLetterPosition(String letter) {
        int size = datas.size();
        for (int i = 0; i < size; i++) {
            if (letter.charAt(0) == datas.get(i).letter.charAt(0)) {
                return i;
            }
        }
        return -1;
    }

    class ViewHolder {
        TextView tvName;
        TextView tvTitle;
        TextView tvAreaNum;
        LinearLayout tvItem;
    }
}

