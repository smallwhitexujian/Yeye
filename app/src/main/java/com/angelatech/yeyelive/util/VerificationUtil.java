package com.angelatech.yeyelive.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: cbl
 * Date: 2016/7/14
 * Time: 14:17
 * 验证 工具类
 */
public class VerificationUtil {

    private final static Pattern emailPat = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
    private final static String lettersNumber = "^(?![^a-zA-Z]+$)(?!\\D+$).{6,22}$";
    private final static String smallImage = "?imageView2/2/w/80";
    private final static String smallImage100 = "?imageView2/2/w/100";
    private final static String filePath = "file.iamyeye.com";

    /**
     * 验证手机号
     *
     * @param str 字符串
     * @return bool
     */
    public static boolean isMobile(String str) {
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern.compile("^[1][3,4,5,8,7][0-9]{9}$");
        m = p.matcher(str);
        b = m.matches();
        return b;
    }

    /**
     * 验证邮箱
     *
     * @param email 字符串
     * @return bool
     */
    public static boolean isEmail(String email) {
        if (email == null || email.trim().length() == 0)
            return false;
        return emailPat.matcher(email).matches();
    }

    /**
     * 验证 输入 是否为空
     *
     * @param input 输入字符
     * @return bool
     */
    public static boolean isEmpty(String input) {
        if (input == null || "".equals(input))
            return true;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }
        return true;
    }

    /**
     * 验证码字符串 是否包含数字和字母
     *
     * @param password 密码
     * @return bool
     */
    public static boolean isContainLetterNumber(String password) {
        return Pattern.compile(lettersNumber).matcher(password).find();
    }

    /**
     * 七牛图片处理
     *
     * @param url 图片地址
     * @return return 80
     */
    public static String getImageUrl(String url) {
        if (url.indexOf(filePath) > 0) {
            return url + smallImage;
        }
        return url;
    }

    /**
     * 七牛图片处理
     *
     * @param url 图片地址
     * @return return 100
     */
    public static String getImageUrl100(String url) {
        if (url.indexOf(filePath) > 0) {
            return url + smallImage100;
        }
        return url;
    }

    /**
     * 从字符串中截取连续6位数字
     * 用于从短信中获取动态密码
     *
     * @param str 短信内容
     * @return 截取得到的4位验证码
     */
    public static String getDynamicPassword(String str) {
        Pattern continuousNumberPattern = Pattern.compile("[0-9\\.]+");
        Matcher m = continuousNumberPattern.matcher(str);
        String dynamicPassword = "";
        while (m.find()) {
            if (m.group().length() == 4) {
                dynamicPassword = m.group();
            }
        }
        return dynamicPassword;
    }
}
