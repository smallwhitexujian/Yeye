package com.angelatech.yeyelive;

/**
 * Created by jjfly on 15-10-11.
 * <p/>
 * 广播使用常量
 */
public class Constant {
    //本地广播
    public static final String CODE_KEY = Constant.class.getName() + "_code_key";
    public static final String PROMPT_KEY = Constant.class.getName() + "_prompt_key";

    //私聊
    // public static final String FROM_ID_KEY = Constant.class.getName()+"_from_id_key";
    public static final String FROM_USERINFO_KEY = Constant.class.getName() + "_from_userinfo_key";
    public static final String OPEN_LAYOUT_CODE_KEY = Constant.class.getName() + "_open_layout_code_key";

    //关注
    public static final String PEOPLE_ID_KEY = Constant.class.getName() + "_people_id_key";

    public static final String AAC_SUFFIX = ".aac";

    //xml 下载
    public static final String XML_URL_KEY = "xml_url_key";
    public static final String XML_FILE_PATH_KEY = "xml_file_path_key";
    public static final String XML_FILE_TYPE_KEY = "xml_file_type_key";

    public static final String isFollow = "1"; //1已关注 0未关注

    public static final String XML_NAME_GIFT = "gift_xml";
    public static final String XML_NAME_MESSAGE = "message_xml";
    public static final String XML_NAME_BASE = "base_xml";

    //充值
    public static final String APP_ID_WEI_XIN = "";
    public static final String WEIXIN_PAY_RESULT_ACTION = "weixin.pay.result.action";

    public static int comeFrom = 0; // 1来自房间 5查看用户头像


    //全局默认常量
    public static final String SEX_MALE = "1";
    public static final String SEX_FEMALE = "0";

    //登录方式
    public static final String Login_phone = "0";
    public static final String Login_facebook = "1";
    public static final String Login_wx = "2";
    public static final String Login_qq = "3";
    public static final String REFRESH_SYSTEM_MESSAGE = "com.will.maracuyalive.system_message_refresh_action";
}
