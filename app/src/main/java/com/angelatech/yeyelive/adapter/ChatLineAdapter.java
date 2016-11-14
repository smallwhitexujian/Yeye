package com.angelatech.yeyelive.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.angelatech.yeyelive.GlobalDef;
import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.activity.function.ChatRoom;
import com.angelatech.yeyelive.application.App;
import com.angelatech.yeyelive.model.BasicUserInfoModel;
import com.angelatech.yeyelive.model.ChatLineModel;
import com.angelatech.yeyelive.util.Clickable;
import com.angelatech.yeyelive.util.MeImageGetter;
import com.angelatech.yeyelive.util.VerificationUtil;
import com.will.common.string.StringFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xujian on 15/9/16.
 * bar聊天界面
 */
public class ChatLineAdapter<T> extends BaseAdapter {
    private Context mContext;
    private List<T> mData = new ArrayList<>();
    private MeImageGetter meImageGetter = null;
    private ChatRoom chatRoom;
    private IShowUser iShowUser;

    public ChatLineAdapter(Context context, IShowUser iShowUser) {
        this.mContext = context;
        this.iShowUser = iShowUser;
        meImageGetter = new MeImageGetter(context);
        chatRoom = new ChatRoom(mContext);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public T getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setDeviceList(List<T> list) {
        if (list != null) {
            mData = list;
            notifyDataSetChanged();
        }
    }

    public void clearDeviceList() {
        if (mData != null) {
            mData.clear();
        }
        notifyDataSetChanged();
    }

    @SuppressWarnings("unchecked")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ChatLineModel chatline = (ChatLineModel) mData.get(position);
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.chat_message_item, parent, false);
            holder = new ViewHolder();
            holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_content.setText("");
        holder.tv_content.setLongClickable(false);
        if (chatline.type == 0) {
            String sChatContent = chatline.message;
            if (sChatContent.contains("%/%") && chatline.giftmodel != null) {
                try {
                    String picPath = chatRoom.getGifPath(chatline.giftmodel.giftid).getImageURL();
                    picPath = VerificationUtil.getImageUrl100(picPath);
                    if (!picPath.isEmpty()) {
                        String html = "<img src='" + picPath + "'/>";
                        //处理礼物逻辑
                        String msgStr;
                        View.OnClickListener fromUserListener = new View.OnClickListener() {
                            //给发送者定义动作
                            public void onClick(View v) {
                                BasicUserInfoModel userInfoModel = new BasicUserInfoModel();
                                userInfoModel.Userid = String.valueOf(App.mChatlines.get(position).giftmodel.from.uid);
                                userInfoModel.nickname = App.mChatlines.get(position).giftmodel.from.name + " ";
                                userInfoModel.headurl = App.mChatlines.get(position).giftmodel.from.headphoto;
                                if (iShowUser != null) {
                                    iShowUser.showUser(userInfoModel);
                                }
                            }
                        };

                        View.OnClickListener toUserListener = new View.OnClickListener() {
                            //给接受者定义动作
                            public void onClick(View v) {
                                BasicUserInfoModel userInfoModel = new BasicUserInfoModel();
                                userInfoModel.Userid = String.valueOf(App.mChatlines.get(position).giftmodel.to.uid);
                                userInfoModel.nickname = " " + App.mChatlines.get(position).giftmodel.to.name;
                                userInfoModel.headurl = App.mChatlines.get(position).giftmodel.to.headphoto;
                                if (iShowUser != null) {
                                    iShowUser.showUser(userInfoModel);
                                }
                            }
                        };

                        SpannableString fromspanableInfo = new SpannableString(chatline.giftmodel.from.name + " ");
                        fromspanableInfo.setSpan(new Clickable(fromUserListener), 0, fromspanableInfo.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        SpannableString tospanableInfo = new SpannableString(" " + chatline.giftmodel.to.name);
                        tospanableInfo.setSpan(new Clickable(toUserListener), 0, tospanableInfo.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        msgStr = " x" + chatline.giftmodel.number;
                        holder.tv_content.append(fromspanableInfo);
                        holder.tv_content.append(mContext.getString(R.string.send_people));
                        holder.tv_content.append(tospanableInfo);
                        String ChatContent = html + msgStr;
                        Spanned charSequence = Html.fromHtml(ChatContent, meImageGetter, null);
                        holder.tv_content.append(" ");
                        holder.tv_content.append(charSequence);
                        holder.tv_content.setMovementMethod(LinkMovementMethod.getInstance());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {//纯文字

                View.OnClickListener fromUserListener = new View.OnClickListener() {
                    //如下定义自己的动作
                    public void onClick(View v) {
                        BasicUserInfoModel userInfoModel = new BasicUserInfoModel();
                        userInfoModel.Userid = App.mChatlines.get(position).from.uid;
                        if (chatline.isFirst) {
                            userInfoModel.nickname = App.mChatlines.get(position).from.name + " ";
                        } else {
                            userInfoModel.nickname = App.mChatlines.get(position).from.name;
                        }
                        userInfoModel.headurl = App.mChatlines.get(position).from.headphoto;
                        if (iShowUser != null) {
                            iShowUser.showUser(userInfoModel);
                        }
                    }
                };
                SpannableString spanableInfo;
                Spanned spannedText;
                if (StringFilter.check(chatline.message)) {
                    App.chatRoomApplication.finish();
                    return convertView;
                }
                if (!sChatContent.startsWith(GlobalDef.APPEND_FOLLOW) && !sChatContent.startsWith(GlobalDef.APPEND_SHARED)) {
                    if (chatline.isFirst) {
                        spanableInfo = new SpannableString(chatline.from.name + " ");
                    } else {
                        spanableInfo = new SpannableString(chatline.from.name);
                    }
                    int start = 0;
                    int end = spanableInfo.length();
                    spanableInfo.setSpan(new Clickable(fromUserListener), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    sChatContent = chatline.message;
                    //如果是首次进入房间，不显示冒号
                    if (chatline.isFirst) {
                        spannedText = Html.fromHtml(sChatContent);
                    } else {
                        spannedText = Html.fromHtml(":" + sChatContent);
                    }
                    holder.tv_content.append(spanableInfo);
                    holder.tv_content.append(spannedText);
                    holder.tv_content.setMovementMethod(LinkMovementMethod.getInstance());
                } else {//转粉和关注
                    if (sChatContent.startsWith(GlobalDef.APPEND_FOLLOW)) {
                        sChatContent = chatline.from.name + " " + mContext.getString(R.string.append_follow);
                    } else if (sChatContent.startsWith(GlobalDef.APPEND_SHARED)) {
                        sChatContent = chatline.from.name + " " + mContext.getString(R.string.append_sherad);
                    }
                    holder.tv_content.setShadowLayer(1, 2, 2, R.color.transparent);
                    holder.tv_content.append(Html.fromHtml("<font color='" + ContextCompat.getColor(mContext, R.color.color_FFEE00) + "'>" + sChatContent + "</font>"));
                }
            }
        } else if (chatline.type == 9) {
            //进入房间系统提示
            holder.tv_content.append(Html.fromHtml("<font color='" + ContextCompat.getColor(mContext, R.color.color_FFEE00) + "'>" + chatline.message + "</font>"));
            holder.tv_content.setShadowLayer(1, 2, 2, R.color.transparent);
        } else if (chatline.type == 10) {
            //系统消息类型
            holder.tv_content.setShadowLayer(1, 2, 2, R.color.transparent);
            holder.tv_content.append(Html.fromHtml("<font color='" + ContextCompat.getColor(mContext, R.color.color_FFEE00) + "'>" + chatline.message + "</font>"));
        }
        return convertView;
    }

    private class ViewHolder {
        TextView tv_content;
    }

    public interface IShowUser {
        void showUser(BasicUserInfoModel userInfoModel);
    }
}