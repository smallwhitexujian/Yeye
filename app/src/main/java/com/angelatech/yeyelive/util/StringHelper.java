package com.angelatech.yeyelive.util;

import java.text.DecimalFormat;

/**
 * 字符串辅助类
 */
public class StringHelper {


    /**
     * 替换字符串中你其中几位
     * @param str
     * @param start 从0开始计算
     * @param count
     * @param replacement
     * @return
     */
    public static String replaceStr(String str, int start,int count, char replacement) {
        if (str == null || start < 0) {
            return str;
        }
        char[] chs = str.toCharArray();
        int end = Math.min(start + count , chs.length);
        while (start < end) {
            chs[start++] = replacement;
        }
        return new String(chs);
    }

    public static String formatStr(String prefix,Object obj,String suffix){
        return String.format(prefix+"%s"+suffix,obj);
    }

    /**
     * 多字符串合并
     * @param strs
     * @return
     * 提供内存优化方式合并，尽量不用+号连接
     * 以后可能会对参数的个数进行限制
     */
    public static String stringMerge(String ...strs){
//        if(strs.length > 100){
//            throw new RuntimeException("too much args !");
//        }
        StringBuilder buffer = new StringBuilder();
        for(String str:strs){
            buffer.append(str);
        }
        return buffer.toString();
    }


    /**
     *  获取文件扩展名
     */
    public static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot >-1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }
    /**
     * 获取不带扩展名的文件名
     *
     */
    public static String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot >-1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }


//    /**
//     * 字符串截取
//     * @param str
//     * @param start
//     * @param count
//     * @return
//     */
//    public static String strSplit(String str,int start,int count){
//        if(str == null || "".equals(str)){
//            return str;
//        }
//        int strlen = str.length();
//        int end = Math.min(start + count ,strlen);
//
//
//        return str;
//    }


    public static String getThousandFormat(long coin){
        DecimalFormat df = new DecimalFormat("#,###");
        String m = df.format(coin);
        return  m;
    }

    public static String getThousandFormat(String coin){
        long coinValue = 0;
        try{
            coinValue = Long.valueOf(coin);
        }catch (NumberFormatException e){
            coinValue = 0;
        }
        DecimalFormat df = new DecimalFormat("#,###");
        String m = df.format(coinValue);
        return  m;
    }




}
