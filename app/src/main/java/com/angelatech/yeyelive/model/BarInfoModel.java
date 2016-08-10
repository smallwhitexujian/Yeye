package com.angelatech.yeyelive.model;

/**
 * Created by xujian on 15/10/14.
 */
public class BarInfoModel extends CommonModel {
    public String barid = "";                // 吧的id
    public String baridx = "";               // 吧的idx
    public String barlevel = "";             // 吧的等级
    public String barname = "";              // 吧的名字
    public String introduce = "";            // 吧的简介
    public String barimage = "";             // 吧的背景
    public String isfollow = "0";           // 是否关注
    public String experience = "";           // 经验
    public String experiencePercent = "0";  // 经验比例
    public String notice = "";               // 公告
    public String yesterdaywage = "";         // 工资
    public int labelid = 0;                  // 标签
    public String hot = "0";                 // 热度
    public String barType = "1";
    public int silence = -1;                // 是否禁言状态
    public long coin = 0;                  // 金币
    public long diamonds = 0;               // 钻石
    public int guestlive = 0;                // 是否是自由麦 1.表示允许自由麦 0.不允许

    public String online = "";           //在线人数
    public String position = "";         //用户等级
    public String level = "";            //等级
    public int live = 0;               //直播状态
    public String live_uri = "";

    public Mic mic;
    public Mictwo mictwo;

    //一麦
    public static class Mic {
        public String headphoto;
        public String nickname;
        public String uid;
    }

    //二麦
    public static class Mictwo {
        public String url;
        public OnlineListModel user;
    }

    //在麦信息处理
    public static class MicInfo {
        public String uid;
        public String rtmpURL;
        public String micNum;
    }


    public SilenceInfo from;
    public SilenceInfo to;

    //禁言
    public static class SilenceInfo {
        public String uid;
        public String name;
    }

    public static class RadioMessage {
        public int code;
        public Radio data;
        public String msg;
    }

    public static class Radio {
        public OnlineListModel from;//发送人的消息
        public OnlineListModel to;  //接受人的消息
        public giftClass gift;      //礼物消息
        public int type;            //消息内型 8是普通喇叭。9是普通喇叭
        public int barid;           //id
        public int baridx;          //靓号
        public long balance;        //当前余额
        public String message;      //消息内容
        public int num;             //礼物个数
    }

    public static class giftClass {
        public int id;

        @Override
        public String toString() {
            return "giftClass{" +
                    "id=" + id +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "BarInfoModel{" +
                "barid='" + barid + '\'' +
                ", baridx='" + baridx + '\'' +
                ", barlevel='" + barlevel + '\'' +
                ", barname='" + barname + '\'' +
                ", introduce='" + introduce + '\'' +
                ", barimage='" + barimage + '\'' +
                ", isfollow='" + isfollow + '\'' +
                ", experience='" + experience + '\'' +
                ", experiencePercent='" + experiencePercent + '\'' +
                ", notice='" + notice + '\'' +
                ", yesterdaywage='" + yesterdaywage + '\'' +
                ", labelid=" + labelid +
                ", hot='" + hot + '\'' +
                ", barType='" + barType + '\'' +
                ", silence=" + silence +
                ", coin=" + coin +
                ", diamonds=" + diamonds +
                ", guestlive=" + guestlive +
                ", online='" + online + '\'' +
                ", position='" + position + '\'' +
                ", level='" + level + '\'' +
                ", mic=" + mic +
                ", mictwo=" + mictwo +
                ", from=" + from +
                ", to=" + to +
                '}';
    }
}
