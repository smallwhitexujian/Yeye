package com.angelatech.yeyelive1.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.angelatech.yeyelive1.R;
import com.angelatech.yeyelive1.model.RankModel;
import com.angelatech.yeyelive1.util.VerificationUtil;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

public class HorizontalUserRankListViewAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<RankModel> data;

    public HorizontalUserRankListViewAdapter(Context context, List<RankModel> data) {
        this.mContext = context;
        this.data = data;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);//LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_chatroom_gallery, parent, false);
            holder.mImage = (SimpleDraweeView) convertView.findViewById(R.id.item_chatRoom_gallery_image);
            holder.iv_vip = (ImageView) convertView.findViewById(R.id.iv_vip);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mImage.setBackgroundResource(R.drawable.default_face_icon);
        holder.mImage.setImageURI(Uri.parse(VerificationUtil.getImageUrl(data.get(position).imageurl)));

        return convertView;
    }

    private static class ViewHolder {
        private SimpleDraweeView mImage;
        private ImageView iv_vip;
    }
}