package com.will.common.tool.time;

import android.content.ContentResolver;
import android.content.Context;

import com.will.common.log.DebugLogs;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * shanli 2015-10-11  日期类
 */
public class DateTimeTool {

    public static String FormatString = "yyyy-MM-dd";


    public static final int FORMAT_SECONDS = 0;
    public static final int FORMAT_MINUTE = 1;
    public static final int FORMAT_HOUR = 2;


    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static long getCompareValue(Calendar src, Calendar dist, int type) {
        if (src == null || dist == null) {
            throw new IllegalArgumentException();
        }
        long result = 0;
        long i = dist.getTimeInMillis() - src.getTimeInMillis();//得到的毫秒书
        switch (type) {
            case FORMAT_HOUR:
                result = i / (1000 * 60 * 60);
                break;
            case FORMAT_MINUTE:
                result = i / (1000 * 60);
                break;
            case FORMAT_SECONDS:
                result = i / 1000;
                break;
            default:
                result = i;
                break;
        }
        return result;
    }


    public static long getCompareValue(String src, String dist, int type) {
        if (src == null || dist == null) {
            throw new IllegalArgumentException();
        }
        long result = 0;
        Calendar cal_src = Calendar.getInstance();
        Calendar cal_dist = Calendar.getInstance();
        try {
            cal_dist.setTime(sdf.parse(dist));
            cal_src.setTime(sdf.parse(src));
            long i = cal_dist.getTimeInMillis() - cal_src.getTimeInMillis();//得到的毫秒书
            switch (type) {
                case FORMAT_HOUR:
                    result = i / (1000 * 60 * 60);
                    break;
                case FORMAT_MINUTE:
                    result = i / (1000 * 60);//分数
                    break;
                case FORMAT_SECONDS:
                    result = i / 1000;
                    break;
                default:
                    result = i;
                    break;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }


    public static long getCompareValue(Date src, Date dist, int type) {
        if (src == null || dist == null) {
            throw new IllegalArgumentException();
        }
        long result = 0;
        Calendar cal_src = Calendar.getInstance();
        Calendar cal_dist = Calendar.getInstance();
        cal_dist.setTime(dist);
        cal_src.setTime(src);
        long i = cal_dist.getTimeInMillis() - cal_src.getTimeInMillis();//得到的毫秒书
        switch (type) {
            case FORMAT_HOUR:
                result = i / (1000 * 60 * 60);
                break;
            case FORMAT_MINUTE:
                result = i / (1000 * 60);//分数
                break;
            case FORMAT_SECONDS:
                result = i / 1000;
                break;
            default:
                result = i;
                break;
        }
        return result;
    }

    public static long getCompareValue(long startTime, long endTime, int type) {
        long result;
        long i = endTime - startTime;
        switch (type) {
            case FORMAT_HOUR:
                result = i / (1000 * 60 * 60);
                break;
            case FORMAT_MINUTE:
                result = i / (1000 * 60);//分数
                break;
            case FORMAT_SECONDS:
                result = i / 1000;
                break;
            default:
                result = i;
                break;
        }
        return result;
    }

    public static String getFormatTimeStr(final String formatStr) {
        SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        String dateTimeStr = sdf.format(curDate);
        return dateTimeStr;
    }

    public static boolean overOneDay(String historyTime) {
        SimpleDateFormat ssdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar historyTimeCal = Calendar.getInstance();
        Calendar currentTimeCal = Calendar.getInstance();
        try {
            historyTimeCal.setTime(ssdf.parse(historyTime));
            currentTimeCal.setTime(ssdf.parse(ssdf.format(new Date(System.currentTimeMillis()))));
            long i = currentTimeCal.getTimeInMillis() - historyTimeCal.getTimeInMillis();
            long result = i / (1000 * 60 * 60);
            if (result > 24) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    //格式化
    public static String DateFormat(long value, String formatString) {
        SimpleDateFormat format = new SimpleDateFormat(formatString, Locale.getDefault());
        return format.format(new Date(value));
    }

    //格式化
    public static String DateFormat(Date value, String formatString) {
        SimpleDateFormat format = new SimpleDateFormat(formatString,
                Locale.getDefault());
        return format.format(value);
    }


    /**
     * 时间转换
     * @param time 时间
     * @param type 转换 时 分 秒
     * @return long
     */
    public static long GetNowTime(long time,int type) {
        long result;
        switch (type) {
            case FORMAT_HOUR:
                result = time / (1000 * 60 * 60);
                break;
            case FORMAT_MINUTE:
                result = time / (1000 * 60);//分数
                break;
            case FORMAT_SECONDS:
                result = time / 1000;
                break;
            default:
                result = time;
                break;
        }
        return  result;
    }

    public static String DateFormat(long value) {
        return DateFormat(value, "yyyy-MM-dd HH:mm:ss");
    }

    // 获得当前系统时间制式
    public static int getDate12_24(Context c) {
        try {
            ContentResolver cv = c.getContentResolver();
            // 获取当前系统设置
            String strTimeFormat = android.provider.Settings.System.getString(
                    cv, android.provider.Settings.System.TIME_12_24);
            if (strTimeFormat.equals("24")) {
                return 24;
            }
            if (strTimeFormat.equals("12")) {
                return 12;
            }
            return 12;
        } catch (Exception ex) {
            return 12;
        }
    }

//    // 根据日期格式化为近期
//    public static String DateFormat2(Context c, long value) {
//        try {
//            //SimpleDateFormat format = new SimpleDateFormat("yyyy-M-d");
//
//            Date Userdate = new Date(value); //new Date(value);
//
//            //Date d;
//
//            // d = format.parse(Userdate.getYear() + "-" + Userdate.getMonth()
//            //        + "-" + Userdate.getDay());
//
//            Date now = new Date();
//            //Date nowLong = new Date(System.currentTimeMillis());
//
//            //Date now = format.parse(nowLong.getYear() + "-"
//            //        + nowLong.getMonth() + "-" + nowLong.getDay());
//            //long datadiff = now.getTime() - Userdate.getTime();
//
//            //int diff = (int) (datadiff / (1000 * 60 * 60 * 24));
//
//            int diff = 2;
//            if (now.getYear() == Userdate.getYear() && now.getMonth() == Userdate.getMonth()) {
//                if (now.getDay() == Userdate.getDay()) {
//                    diff = 0;
//                } else {
//                    diff = 1;
//                }
//            }
//            if (diff < 1) {
//                return DateFormat(Userdate, "H:mm");
////                if (getDate12_24(c) > 12) {
////                    return DateFormat(Userdate, "HH:mm");
////                } else {
////                    if (Userdate.getHours() >= 12) {
////                        return "PM " + DateFormat(Userdate, "hh:mm");
////                    } else {
////
////                        return "AM " + DateFormat(Userdate, "hh:mm");
////                    }
////                }
//            } else if (diff == 1) {
//                //"last "+
//                return DateFormat(Userdate, "M-d H:mm");
//            } else {
//                return DateFormat(Userdate, "M-d H:mm");
//            }
//
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        return "";
//    }
//
//    // 根据日期格式化为近期
//    public static String DateFormat3(Context c, long value) {
//        try {
//            SimpleDateFormat format = new SimpleDateFormat("yyyy-M-d");
//
//            Date Userdate = new Date(value);
//
//            Date d;
//
//            d = format.parse(Userdate.getYear() + "-" + Userdate.getMonth()
//                    + "-" + Userdate.getDay());
//
//            Date nowLong = new Date(System.currentTimeMillis());
//
//            Date now = format.parse(nowLong.getYear() + "-"
//                    + nowLong.getMonth() + "-" + nowLong.getDay());
//
//            long datadiff = now.getTime() - d.getTime();
//
//            int diff = (int) (datadiff / (1000 * 60 * 60 * 24));
//            if (diff < 1) {
//
//                if (getDate12_24(c) > 12) {
//                    return DateFormat(value, "H:mm");
//                } else {
//
//                    if (Userdate.getHours() >= 12) {
//                        return "AM " + DateFormat(value, "h:mm");
//                    } else {
//
//                        return "PM " + DateFormat(value, "h:mm");
//                    }
//                }
//            } else {
//                if (getDate12_24(c) > 12) {
//                    return DateFormat(value, "MM-dd H:mm");
//                } else {
//
//                    if (Userdate.getHours() >= 12) {
//                        return DateFormat(value, "MM-dd") + "PM "
//                                + DateFormat(value, "h:mm");
//                    } else {
//
//                        return DateFormat(value, "MM-dd") + "AM "
//                                + DateFormat(value, "h:mm");
//                    }
//                }
//            }
//
//        } catch (ParseException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        return "";
//    }

    //将秒转换为00:00:00格式
    public static String DateFormathms(int milliSecondTime) {
        DebugLogs.e("mm" + milliSecondTime);
        int hour = milliSecondTime / (60 * 60);
        int minute = (milliSecondTime - hour * 60 * 60) / 60;
        int seconds = milliSecondTime - hour * 60 * 60 - minute * 60;

        if (seconds >= 60) {
            seconds = seconds % 60;
            minute += seconds / 60;
        }
        if (minute >= 60) {
            minute = minute % 60;
            hour += minute / 60;
        }

        String sh = "";
        String sm = "";
        String ss = "";
        if (hour < 10) {
            sh = "0" + String.valueOf(hour);
        } else {
            sh = String.valueOf(hour);
        }
        if (minute < 10) {
            sm = "0" + String.valueOf(minute);
        } else {
            sm = String.valueOf(minute);
        }
        if (seconds < 10) {
            ss = "0" + String.valueOf(seconds);
        } else {
            ss = String.valueOf(seconds);
        }

        return sh + ":" + sm + ":" + ss;
    }

    //获取当前日期于时间
    public static String GetDateTimeNow() {
        return DateFormat(GetDateTimeNowlong());
    }

    //获取当前日期于时间
    public static String GetDateTimeNow(String fomat) {
        return DateFormat(GetDateTimeNowlong(), fomat);
    }

    //获取当前日期于时间
    public static long GetDateTimeNowlong() {
        return System.currentTimeMillis();
    }

    //把日期转为字符串
    public static String ConverToString(Date date) {
        DateFormat df = new SimpleDateFormat(FormatString);

        return df.format(date);
    }

    //把字符串转为日期
    public static Date ConverToDate(String strDate) throws Exception {
        DateFormat df = new SimpleDateFormat(FormatString);
        return df.parse(strDate);
    }

    //把字符串转为日期
    public static Date ConverToDate(String strDate, String format) throws Exception {
        DateFormat df = new SimpleDateFormat(format);
        return df.parse(strDate);
    }

    public static boolean overMinute(long datetime, int min) {
        int rate = 60 * 1000;
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal2.setTimeInMillis(datetime);
        return ((cal1.getTimeInMillis() - cal2.getTimeInMillis()) / rate) > min;
    }

    public static long getCurrentUnixTime() {
        return System.currentTimeMillis() / 1000;
    }
}
