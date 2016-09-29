package com.angelatech.yeyelive;

/**
 * Created by xujian on 15/9/10.
 * 常量状态码
 */
public class GlobalDef {
    public static final int WM_USER = 0x0400;
    public static final int ROOM_STATUS_HINTHEART = 200;                //表情弹出隐藏心形

    public static final int SERVICE_STATUS_SUCCESS = WM_USER + 1000;              //服务器连接成
    public static final int SERVICE_STATUS_FAILD = WM_USER + 1001;                //连接失败
    public static final int SERVICE_STATUS_CONNETN = WM_USER + 1002;              //服务器重连10005

    public static final int WM_NET_LINK = WM_USER + 1;
    public static final int WM_ALREADY_LOGIN = WM_USER + 2;                     //已经登录
    public static final int WM_IM_SERVER_ERROR = WM_USER + 3;                   //IM服务器异常
    public static final int WM_IM_SERVER_NO_ENOUGH = WM_USER + 4;               //IM服务器不够
    public static final int WM_START_UPDATE_TIPS = WM_USER + 13;                //更新启动界面的提示
    public static final int WM_LOGIN_SERVER_ERROR = WM_USER + 14;               //登录服务器错误
    public static final int WM_ENTER_MAIN = WM_USER + 15;                       //进入主界面
    public static final int WM_GO_LOGINREGISTER = WM_USER + 23;                 //去登录注册

    public static final int WM_SIGN_ERROR = WM_USER + 16;                       //签名失效
    public static final int WM_ALL_SERVER_FAIL = WM_USER + 17;                  //服务器登陆失败
    public static final int WM_LOGIN_SUCCESS = WM_USER + 18;                    //服务器登陆成功
    public static final int WM_LOGIN_FAILED = WM_USER + 19;                     //服务器登陆失败
    public static final int WM_CREATE_BAR_ED_MESSAGE = WM_USER + 20;            //创建bar计算输入文字
    public static final int WM_ROOM_HEARTHINT = WM_USER + 21;                   //隐藏心形
    public static final int WM_ROOM_HEART = WM_USER + 22;                       //显示心形
    public static final int WM_BIG_ITEM_TIMER_START = WM_USER + 23;             //开始跑马灯
    public static final int WM_BIG_ITEM_CLOSED = WM_USER + 24;                  //关闭跑马灯
    public static final int WM_CONNECT_LOGINSERVER_FAILD = WM_USER + 25;        //外部socket登录登录服务器失败
    public static final int WM_BAR_INFO_BG = WM_USER + 26;                      //房间背景显示
    public static final int WM_INFO_PHOTO_CAMERA = WM_USER + 27;                //手机拍照获取照片
    public static final int WM_INFO_PHOTO_ALBUM = WM_USER + 28;                 //手机相册
    public static final int WM_ACTIVITY_FINISH = WM_USER + 29;                  //Activity finish
    public static final int WM_TOAST_MIC = WM_USER + 30;                        //发送消息
    public static final int WM_PARSE_XML_ERROR = WM_USER + 31;                  //解析xml错误

    //room
    public static final int SO_DOHEART = 100;                           //心跳
    public static final int WM_ROOM_LOGIN = 10001;                      //房间登陆
    public static final int WM_ROOM_LOGIN_OUT = 10002;                  //房间登出
    public static final int WM_ROOM_MESSAGE = 10003;                    //房间消息
    public static final int WM_ROOM_RECEIVE_PEOPLE = 10005;             //房间在线人数
    public static final int WM_ROOM_SILENCE = 10006;                    //房间禁止打字
    public static final int WM_ROOM_EXIT_ENTRY = 10007;                 //房间进出通知
    public static final int WM_ROOM_Kicking = 10008;                    //踢人
    public static final int WM_ROOM_SENDGIFT = 10009;                   //送礼物
    public static final int WM_ROOM_RECEIVED = 10010;                   //发送红包
    public static final int WM_ROOM_LIKENUM = 10011;                    //点赞
    public static final int WM_ROOM_SOFA = 10015;                       //沙发
    public static final int WM_ROOM_SHOWMIC = 10016;                    //上麦
    public static final int WM_ROOM_DOWMMIC = 10017;                    //下麦
    public static final int WM_ROOM_OTHERMIC = 10018;                   //抱麦
    public static final int WM_OUT_RadioBroadcast = 10019;              //广播
    public static final int WM_APPLICATION_TO_JOIN = 10020;             //申请加入bar
    public static final int WM_APPLICATION_JOIS_RESULTS = 100201;       //申请结果推送给管理员及房主
    public static final int WM_RESULTS_APPLICATION = 10021;             //处理加入房间申请
    public static final int WM_APPLICATION_RESULTS = 100211;            //推送给有权限的申请者
    public static final int WM_APPLICATION_RESULTS_OTHERS = 100212;     //推送给房间其他人员
    public static final int WM_MEMBERS_GETOUT = 10022;                  //成员踢出
    public static final int WM_MEMBERS_GETOUT_RESULTS=100221;           //推送给被踢人结果
    public static final int WM_MEMBERS_GETOUT_OTHERS = 100222;          //推送给其他人
    public static final int WM_MANAGEMENT_IMPROVE = 10023;              //提升为管理
    public static final int WM_MANAGEMENT_IMPROVES = 100231;            //推送给被提升管理者。
    public static final int WM_MANAGEMENT_IMPROVES_OTHERS = 100232;     //推送给其他房间成员
    public static final int WM_MANAGEMENT_DEMOTION = 10024;             //管理降级
    public static final int WM_DEMOTION= 100241;                        //推送给被降级的那个人
    public static final int WM_DEMOTION_OTHERS = 100242;                //推送给房间内其他成员
    public static final int WM_IMPROVES_LIST = 10025;                   //获取申请加入申请列表
    public static final int WM_SETTING_FREEWHEAT = 10029;               //房主设置是否自由上麦
    public static final int WM_SDP = 10030;                             //视频房间连接成功
    public static final int WM_CANDIDATE = 10031;                       //直播连接
    public static final int WM_LIVE_SHOWMIC = 10032;                    //有人直播了
    public static final int WM_LIVE_STOP = 10033;                       //视频停播了

    //房间错误code
    public static final int SUCCESS_0                       = 0;     ///< 成功
    public static final int UNKNOWN_ERROR_1                 = 1;     ///< 失败，未知原因
    public static final int SUCCESS_1000                    = 1000;  ///< 成功
    public static final int UNPACK_ERROR_1001               = 1001;  ///< 解包错误
    public static final int NOTENTER_SUCCESS_1002           = 1002;  ///< 未进吧成功
    public static final int USER_NOTFOUND_1003              = 1003;  ///< 用户未找到
    public static final int BAR_NOTFOUND_1004               = 1004;  ///< 吧未找到
    public static final int WITHOUT_MIC_1005                = 1005;  ///< 无剩余麦
    public static final int ONLINE_THAN_LIMIT_1006          = 1006;  ///< 吧在线人数超出上限
    public static final int NOT_NORMAL_LOGOUT_1007          = 1007;  ///< 已进过吧，未正常退出
    public static final int SUPPRESSED_1008                 = 1008;  ///< 被禁言
    public static final int NO_PERMISSION_OPE_1009          = 1009;  ///< 无权限操作
    public static final int NOT_SUFFICIENT_FUNDS_1010       = 1010;  ///< 余额不足
    public static final int KICKED_OUT_1011                 = 1011;  ///< 被踢出
    public static final int NOT_SUFFICIENT_COIN_1012        = 1012;  ///< 币不足
    public static final int GIFT_NONEXISTENT_1013           = 1013;  ///< 礼物不存在
    public static final int SIGNATURE_VERIFY_FAILE_1014     = 1014;  ///< 签名校验失败
    public static final int BAR_LOCKING_1015                = 1015;  ///< 吧被锁定
    public static final int HAVE_MIC_1016                   = 1016;  ///< 已在麦
    public static final int UNUSE_MIC_1017                  = 1017;  ///< 未占有麦
    public static final int JOIN_BAR_1018                   = 1018;  ///< 已加入吧
    public static final int OTHER_ADMIN_PROCESS_1019        = 1019;  ///< 其他管理员已经处理
    public static final int TARGET_USER_NOT_MEMBER_1020     = 1020;  ///< 目标用户不是bar成员
    public static final int TARGET_USER_NOT_ADMIN_1021      = 1021;  ///< 目标用户不是管理员
    public static final int TARGET_USER_NOT_ADMIN_1023      = 1023;  ///< 不是房主无权操作
    public static final int TARGET_USER_NOT_ADMIN_1024      = 1024;  ///< 请求参数错误

    public static final int CONNECTED_20000                 = 20000; ///< 连接服务器成功
    public static final int DISCONNECTED_20001              = 20001; ///< 断开连接
    public static final int DISCONNECTED_200011             = 200011;///< 连接已经断开
    public static final int ROOM_IP_PARSE_ERROR_20002       = 20002; ///< 房间ip地址无法解析
    public static final int CONNECT_SERVER_ERROR_20003      = 20003; ///< 连接服务器失败
    public static final int PARSEPACKET_EXCEPTION_20004     = 20004; ///< 解析数据包异常



    //特殊处理字段
    public static final String APPEND_FOLLOW =  "[@0_0@Follow]";
    public static final String APPEND_SHARED =  "[@0_0@shared]";

}
