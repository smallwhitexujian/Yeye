package com.angelatech.yeyelive1.model;

/**
 * Created by xujian on 15/9/18.
 * 在线人列表
 */
public class OnlineListModel extends CommonModel {
    //在线列表
    public int uid;      //用户ID
    public String name;     //用户昵称
    public String headphoto;//头像地址
    public int position; //权限等级
    public int level = 1;    //等级
    public int sex = 0;
    public String isv = "0";
    public String isrobot = "0";
    public int role = 0;

    @Override
    public String toString() {
        return name;

//                "OnlineListModel{" +
//                "uid='" + uid + '\'' +
//                ", name='" + name + '\'' +
//                ", headphoto='" + headphoto + '\'' +
//                ", position='" + position + '\'' +
//                ", level='" + level + '\'' +
//                ", silenced='" + silenced + '\'' +
//                ", hasmic='" + hasmic + '\'' +
//                '}';
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    //上下线通知
    public static class OnlineNotice {
        public int kind;
        public int online;
        public OnlineListModel user;
    }

    //成员管理 成员申请表
    public static class Member {
        public String userid;       // 用户的id
        public String positionid;   // 等级
        public String nickname;     // 名字
        public String headurl;      // 头像
        public String userlevel;    // 用户等级
        public String consumption;  // 消耗
    }

    public static class MemberApplication {
        public String uid;
        public String name;
        public String headphoto;
        public String level;
    }


    //上麦
    public static class Micorder extends CommonModel {
        public String micorder;     //直播麦克风位置 0.左 1.右
        public String url;          //直播地址
        public OnlineListModel user;//用户信息
        public String userid;       //下麦用户ID
        public String reason;       //下麦状态
    }

    //抱麦
    public static class WithWheat extends CommonModel {
        public OnlineListModel from;
    }
}
