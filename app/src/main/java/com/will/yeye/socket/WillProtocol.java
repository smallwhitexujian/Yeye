package com.will.yeye.socket;

import com.framework.socket.model.ProtocolWrap;
import com.framework.socket.protocol.Protocol;
import com.framework.socket.util.ByteUtil;
import com.will.common.string.json.JsonUtil;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jjfly on 15-10-20.
 */
public class WillProtocol extends Protocol{

    public static final int PACK_LEN = 4;
    public static final int PACK_TYPE_LEN = 4;
    public static final String TAG = "WillProtocol";
    public static final String KEY_PROTOCOL_LEN = TAG + "_len";
    public static final String KEY_PROTOCOL_TYPE = TAG + "_type";
    public static final String KEY_PROTOCOL_DATA = TAG + "_data";

    public static final int CODE_SUCC = 1;//成功
    public static final int CODE_NO_MORE_SERVER = -1;//无可用从服务器
    public static final int CODE_SIGN_ERROR = 0;//sign验证失败

    public static final String CODE_SUCC_STR  = "1";
    public static final String CODE_SIGN_ERROR_STR = "0";//sign异常
    public static final String CODE_NO_MORE_SERVER_STR = "-1";//无可用服务器
    public static final String CODE_FAILD_STR =  "0";

    //发送操作码
    public static final int LOGIN_TYPE_VALUE = 2001;//登录包
    public static final int LEAVE_TYPE_VALUE = 2006; //离开
    public static final int KICK_OUT_TYPE_VALUE = 2007; //踢出
    public static final int BROADCAST_TYPE_VALUE = 2102; //发送/接收广播
    public static final int PRIVATE_CHAT_TYPE_VALUE = 2103;//发送/接收私聊
    public static final int SEARCH_TYPE_VALUE = 2104;//查询用户
    public static final int GIFT_CHAT_TYPE_VALUE = 2105;//发送礼物
    public static final int REVIEW_TYPE_VALUE = 2106;//发送/接收评论
    public static final int LIKE_TYPE_VALUE = 2107;//发送/接收赞
    public static final int FOCUS_TYPE_VALUE = 2108;//关注发送/接收通知
    public static final int VOICE_CHAT_TYPE_VALUE = 2109;//语音聊天
    public static final int REFRESH_BALANCE_TYPE_VALUE =  2110;//刷新币
    public static final int UPPICTURE_STATE_VALUE = 3000; //上传图片状态
    public static final int BEATHEART_TYPE_VALYE = 100;//心跳


    //房间操作码
    public static final int ENTER_VOICEROOM_TYPE_VALUE = 10001;


    /**
     * 解包 返回包解析
     * @param pack
     * @return
     */
    public Map<String, byte[]> parseWillPackage(byte[] pack) {
        int pack_data_len;
        //四字节长度+四字节操作码
        if (pack == null || pack.length < PACK_LEN + PACK_TYPE_LEN) {
            return null;
        }
        Map<String, byte[]> protocolMap = new HashMap<String, byte[]>();
        byte[] lenBuffer = new byte[PACK_LEN];
        System.arraycopy(pack, 0, lenBuffer, 0, PACK_LEN);
        int dataLen = ByteUtil.bytes2Int(lenBuffer, 0, ByteUtil.BIG_ENDIAN);
        protocolMap.put(KEY_PROTOCOL_LEN, lenBuffer);

        byte[] typeBuffer = new byte[PACK_TYPE_LEN];
        System.arraycopy(pack, PACK_LEN, typeBuffer, 0, PACK_TYPE_LEN);
        protocolMap.put(KEY_PROTOCOL_TYPE, typeBuffer);

        byte[] dataBuffer = new byte[dataLen - PACK_LEN - PACK_TYPE_LEN];
        System.arraycopy(pack, PACK_LEN + PACK_TYPE_LEN, dataBuffer, 0, dataLen - PACK_LEN -PACK_TYPE_LEN);
        protocolMap.put(KEY_PROTOCOL_DATA, dataBuffer);
        return protocolMap;
    }

    /**
     * 获取数据包长
     * @param parcel
     * @return
     */
    @Override
    public int getDataLen(byte[] parcel) {
        //四字节长度+四字节操作码
        int packLen = PACK_LEN;
        int packTypeLen = PACK_TYPE_LEN;
        if (parcel == null || parcel.length < packLen + packTypeLen) {
            return -1;
        }
        byte[] lenBuffer = new byte[packLen];
        System.arraycopy(parcel, 0, lenBuffer, 0, packLen);
        int dataLen = ByteUtil.bytes2Int(lenBuffer, 0, ByteUtil.BIG_ENDIAN);
        return dataLen;
    }

    /**
     * 获得数据源
     * @param parcel
     * @return
     */
    @Override
    public byte[] getData(byte[] parcel) {
        Map<String, byte[]> packMap = parseWillPackage(parcel);
        return packMap.get(KEY_PROTOCOL_DATA);
    }

    public byte[] getData(byte[] parcel,int dataLen) {
        byte[] dataBuffer = new byte[dataLen];
        System.arraycopy(parcel, PACK_LEN + PACK_TYPE_LEN, dataBuffer, 0, dataLen - PACK_LEN -PACK_TYPE_LEN);
        return dataBuffer;
    }

    /**
     * 操作码 状态码
     * @param parcel
     * @return
     */
    @Override
    public int getType(byte[] parcel) {
        Map<String, byte[]> packMap = parseWillPackage(parcel);
        byte[] typeAry = packMap.get(KEY_PROTOCOL_TYPE);
        return ByteUtil.bytes2Int(typeAry, 0, ByteUtil.BIG_ENDIAN);
    }

    /**
     * 包头长度
     * @return
     */
    @Override
    public int getHeadLen() {
        return PACK_LEN+PACK_TYPE_LEN;
    }



    //系统消息类型
    public static byte[] getParcel(int typeValue,String jsonStr){
        int dataLen = PACK_LEN + PACK_TYPE_LEN + jsonStr.getBytes().length;
        byte[] pack = parcel(dataLen, typeValue, jsonStr);
        return pack;
    }

    //系统消息类型
    public static byte[] sendMessage(int typeValue,String jsonStr){
        int dataLen = PACK_LEN + PACK_TYPE_LEN + jsonStr.getBytes().length;
        byte[] pack = parcel(dataLen, typeValue, jsonStr);
        return pack;
    }

    /**
     * 拼包方法
     * @param packLen   包长
     * @param typeValue 操作码
     * @param jsonStr   json数据
     * @return
     */
    private static byte[] parcel(int packLen, int typeValue, String jsonStr) {
        byte[] parcelAry = new byte[packLen];
        byte[] packLenAry = ByteUtil.INT32_2_INT8(packLen, ByteUtil.BIG_ENDIAN);
        byte[] typeAry = ByteUtil.INT32_2_INT8(typeValue, ByteUtil.BIG_ENDIAN);
        System.arraycopy(packLenAry, 0, parcelAry, 0, PACK_LEN);
        System.arraycopy(typeAry, 0, parcelAry, PACK_LEN, PACK_TYPE_LEN);
        if (jsonStr != null) {
            System.arraycopy(jsonStr.getBytes(), 0, parcelAry, PACK_LEN+PACK_TYPE_LEN, jsonStr.getBytes().length);
        }
        return parcelAry;
    }

    /**
     * 获取协议wrap对象[对象中包括type(操作码，数据data）
     *
     * @param parcel
     * @param type
     * @param <T>
     * @return
     *
     */
    public <T> ProtocolWrap<T> getProtocolWrap(byte[] parcel,Type type){
        int typeValue = getType(parcel);
        byte[] data = getData(parcel);
        String json = new String(data).trim();
        T dataObj = JsonUtil.fromJson(json, type);
        ProtocolWrap<T> protocolWrap = new ProtocolWrap<>();
        protocolWrap.setType(typeValue);
        protocolWrap.setData(dataObj);
        return protocolWrap;
    }
}
