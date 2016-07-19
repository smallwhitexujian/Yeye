package com.angelatech.yeyelive.model;

/**
 * Created by jjfly on 16-2-26.
 */
public class SystemMessageType {


//    动态 ： 1.评论 2.回复 3.点赞   5.关注好友
//    升级 ： 10.个人升级 11.bar升级
//    金币 ： 20.（任务）邀请好友 21.工资 22.红包返币1
//    VIP  ： 30. vip(即将)到期通知
//    系统消息：40 反馈  41：活动

    //


    public static final int NOTICE_MOMENTS_COMMENTS = 1;//动态评论
    public static final int NOTICE_MOMENTS_REPLY = 2;//动态回复
    public static final int NOTICE_MOMENTS_LIKE = 3;//动态点赞
    public static final int NOTICE_FOCUS_ON = 5;//关注

    public static final int NOTICE_PERSONER_PROMOTION = 10;//个人升级通知
    public static final int NOTICE_ROOM_PROMOTION = 11;//房间升级

    public static final int NOTICE_INVITATION_SUC = 20;//邀请
    public static final int NOTICE_SALARY = 21;  //工资
    public static final int NOTICE_RED_PACKET = 22;//红包


    public static final int NOTICE_VIP_TIMEOUT = 30;//会员到期

    public static final int NOTICE_FEEDBACK = 40;//反馈

    public static final int NOTICE_ACTIVITIES = 41;//活动


    public static final int NOTICE_LIVE = 50;//直播

    public static final int NOTICE_LIVE_FEEDBACK = 51;


}
