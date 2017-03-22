package com.angelatech.yeyelive1;

/**
 * Created by shanli on 15/9/14.
 * 请求和返回码定义
 */
public class CommonResultCode {

    //接口成功返回值
    public static final String INTERFACE_RETURN_CODE = "1000";

    //拍照获取图片
    public static final int SET_ADD_PHOTO_CAMERA = 10001;
    //从相册获取图片
    public static final int SET_ADD_PHOTO_ALBUM = 10002;
    //图像裁剪返回代码
    public static final int REQUEST_CROP_PICTURE = 10003;
    //发送动态返回编码
    public static final int SEND_DYNAMIC_CODE = 10004;

    //上传头像返回代码
    public static final int SEND_USER_HEAD_CODE = 10005;

    //选择吧
    public static final int SELECT_BAR_CODE = 10006;

    //播放语音介绍
    public static final int PLAY_VOICEINTR = 10007;

}