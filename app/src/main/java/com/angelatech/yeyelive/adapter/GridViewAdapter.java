package com.angelatech.yeyelive.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.model.GiftModel;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;


/**
 * User: cbl
 * Date: 2016/4/22
 * Time: 11:40
 * gridView adapter
 */
public class GridViewAdapter extends BaseAdapter {

    private List<GiftModel> list;
    private Context context;
    private List<GiftModel> mList = new ArrayList<>();//定义一个list对象
    private int nowPageIndex = 0;
    private int pageSize = 0;

    public GridViewAdapter(Context context, List<GiftModel> list, int pageIndex, int pageSize) {
        this.context = context;
        this.list = list;
        this.nowPageIndex = pageIndex;
        this.pageSize = pageSize;
        initList();
    }

    private void initList() {
        //根据当前页计算装载的应用，每页只装载几个
        int iStart = nowPageIndex * pageSize;//当前页的其实位置
        int iEnd = iStart + pageSize;//所有数据的结束位置
        while ((iStart < list.size()) && (iStart < iEnd)) {
            mList.add(list.get(iStart));
            iStart++;
        }
    }

    @Override
    public GiftModel getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return mList.get(i).getID();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_gift, parent, false);
            //holder.name = (TextView) convertView.findViewById(R.id.gift_name);
            holder.price = (TextView) convertView.findViewById(R.id.txt_propNum);
            holder.image = (SimpleDraweeView) convertView.findViewById(R.id.img_propimage);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //holder.name.setText(mList.get(i).getName());
        holder.price.setText(mList.get(i).getPrice());
        holder.image.setImageURI(Uri.parse(mList.get(i).getImageURL()));
        return convertView;
    }

    class ViewHolder {
        TextView name, price;
        SimpleDraweeView image;
    }
}
