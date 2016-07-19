package com.angelatech.yeyelive.model;


/**
 * Created by xujian on 15/9/16.
 * 房间聊天Model
 */
public class ChatLineModel extends CommonModel {
    public from from;   //发送者
    public GiftModel.AcceptGift giftmodel; //礼物

    public static class from{
        public String uid;      //发送者的id
        public String name;     //发送者的名字
        public String headphoto;//发送者的头像
        public String level;    //发送者的等级

        @Override
        public String toString() {
            return "from{" +
                    "uid='" + uid + '\'' +
                    ", name='" + name + '\'' +
                    ", headphoto='" + headphoto + '\'' +
                    ", level='" + level + '\'' +
                    '}';
        }
    }

    public String id;
    public int type;            //消息类型
    public String nickname;     //昵称
    public String headurl;      //头像
    public boolean isFirst = false; //新进房间

}
