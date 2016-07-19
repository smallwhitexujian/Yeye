package com.framework.socket.util;

/**
 * Created by jjfly on 15-12-21.
 * 状态记录辅助类：
 *
 *
 *
 */
public class StatusRecordUtil {


    //如果不为0,就是1
    public static void getByteStatusRecord(byte statusRecord,HandleStatus handleStatus){
        int byteSize = Byte.SIZE;
        for(int i = 0;i<byteSize; i++){
            int status = statusRecord & (((int) Math.pow(2, i))&0xff);
            if(handleStatus != null){
                handleStatus.handleCurrStatus(i,status);
            }
        }
    }
    
    //index 从0开始
    public static byte saveByteStatusRecord(byte statusRecord,int index,boolean status){
        int byteSize = Byte.SIZE;
        if(index > byteSize-1){
            throw  new RuntimeException("index is out of byte size");
        }
        if(status){
            statusRecord = (byte) ((statusRecord |(((int) Math.pow(2, index))&0xff)) & 0xff);
        }
        return statusRecord;
    }










    public interface HandleStatus{
        void handleCurrStatus(int index, int status);
    }




}
