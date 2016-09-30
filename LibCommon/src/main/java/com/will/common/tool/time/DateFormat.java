package com.will.common.tool.time;

import android.annotation.SuppressLint;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author xujian 时间格式转化工具
 */
@SuppressLint("SimpleDateFormat")
public class DateFormat {
	private static Map<String, SimpleDateFormat> formatMap = new HashMap<String, SimpleDateFormat>();
	private static SimpleDateFormat sdf;

	private int daysOfMonth = 0;
	private int dayOfWeek = 0;
	private int eachDayOfWeek = 0;

	/**
	 * 将String格式的时间转换成Date
	 * 
	 * @param str
	 * @param format
	 * @return
	 */
	public static Date parseDate(String str, String format) {
		if (str == null || "".equals(str) || "null".equals(str)) {
			return null;
		}
		sdf = formatMap.get(format);
		if (null == sdf) {
			sdf = new SimpleDateFormat(format, Locale.ENGLISH);
			// sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
			formatMap.put(format, sdf);
		}
		try {
			synchronized (sdf) {
				// SimpleDateFormat is not thread safe
				return sdf.parse(str);
			}
		} catch (Exception pe) {
			pe.printStackTrace();
		}
		return null;
	}

	/**
	 * 将String格式的时间转换成Date
	 * 
	 * @param str
	 * @param format
	 * @return
	 * @throws NetAPIException
	 */
	public static Date parseDateOther(String str, String format) {
		if (str == null || "".equals(str)) {
			return null;
		}
		sdf = formatMap.get(format);
		if (null == sdf) {
			sdf = new SimpleDateFormat(format, Locale.ENGLISH);
			// sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
			formatMap.put(format, sdf);
		}
		try {
			synchronized (sdf) {
				// SimpleDateFormat is not thread safe
				return sdf.parse(str);
			}
		} catch (Exception pe) {
			pe.printStackTrace();
			return null;
		}
	}

	public static String formatDate(long times) {
		sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// sdf.setTimeZone(TimeZone.getDefault());
		String str_time = sdf.format(new Date(times));
		return str_time;
	}

	public static String formatDate(long times, String format) {
		sdf = new SimpleDateFormat(format);
		String str_time = sdf.format(new Date(times));
		return str_time;
	}

	public static String formatDate(Date date) {

		sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String str_time = sdf.format(date);
		return str_time;
	}

	public static String formatDate(Date date, String format) {
		sdf = new SimpleDateFormat(format);
		String str_time = sdf.format(date);
		return str_time;
	}

	public static String getSignDate() {
		sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		String str = sdf.format(curDate);
		return str;
	}

	/**
	 * 根据传进来的字符串日期 返回日期的 字符串日期格式 2014-03-09 14:56:50 返回 03-09
	 */

	@SuppressWarnings("deprecation")
	public static String getDate(String data) {
		sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date d = sdf.parse(data);
			String month = String.valueOf(d.getMonth() + 1);
			if (month.length() < 2) {
				month = "0" + month;
			}
			String day = String.valueOf(d.getDay());
			if (day.length() < 2) {
				day = "0" + day;
			}
			return month + "-" + day;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 根据传进来的字符串日期 返回时间的 字符串日期格式 2014-03-09 14:56:50 返回 14:56
	 */
	@SuppressWarnings("deprecation")
	public static String getTime(String data) {
		sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date d = sdf.parse(data);
			return d.getHours() + ":" + d.getMinutes();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 通过生日得到年龄
	 * 
	 * @param birthday
	 * @return
	 */
	public static String getAge(String birthday) {
		if ("".equals(birthday)) {
			return "0";
		}
		sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date birthDate = sdf.parse(birthday);
			Date now = new Date(System.currentTimeMillis());
			System.out.println(now.getTime() - birthDate.getTime());
			long day = (now.getTime() - birthDate.getTime()) / (1000 * 60 * 60 * 24) + 1;
			String year = new DecimalFormat("#.00").format(day / 365f);
			String[] age = year.split("\\.");
			return age[0].trim();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "0";
	}

	/**
	 * 比较开始时间和结束时间是否相等
	 * 
	 * @param sDate
	 * @param eDate
	 * @return
	 */
	public static int compareDate(Date startDate, Date endDate) {
		sdf = new SimpleDateFormat("yyyy-MM-dd");
		int result = 0;
		Calendar sC = Calendar.getInstance();
		sC.setTime(startDate);
		Calendar eC = Calendar.getInstance();
		eC.setTime(endDate);
		result = sC.compareTo(eC);
		return result;
	}

	/**
	 * 
	 * @param sDate
	 * @param days
	 * @return
	 */
	public static Date calculateEndDate(Date sDate, int days) {
		Calendar sCalendar = Calendar.getInstance();
		sCalendar.setTime(sDate);
		sCalendar.add(Calendar.DATE, days);
		return sCalendar.getTime();
	}

	public static Date calculateEndDate(String sDate, int days) {
		Calendar sCalendar;
		try {
			sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date startDate = sdf.parse(sDate);
			sCalendar = Calendar.getInstance();
			sCalendar.setTime(startDate);
			sCalendar.add(Calendar.DATE, days);
			return sCalendar.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 是否是运年
	 * @param year
	 * @return
	 */
	public boolean isLeapYear(int year) {
		if (year % 100 == 0 && year % 400 == 0) {
			return true;
		} else if (year % 100 != 0 && year % 4 == 0) {
			return true;
		}
		return false;
	}

	/**
	 * 判断某一年某个月总天数
	 * @param isLeapyear
	 * @param month
	 * @return
	 */
	public int getDaysOfMonth(boolean isLeapyear, int month) {
		switch (month) {
		case 1:
		case 3:
		case 5:
		case 7:
		case 8:
		case 10:
		case 12:
			daysOfMonth = 31;
			break;
		case 4:
		case 6:
		case 9:
		case 11:
			daysOfMonth = 30;
			break;
		case 2:
			if (isLeapyear) {
				daysOfMonth = 29;
			} else {
				daysOfMonth = 28;
			}

		}
		return daysOfMonth;
	}

	/**
	 * 获取某年某月的第一天是星期几
	 * @param year
	 * @param month
	 * @return
	 */
	public int getWeekdayOfMonth(int year, int month) {
		Calendar cal = Calendar.getInstance();
		cal.set(year, month - 1, 1);
		dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
		return dayOfWeek;
	}

	/**
	 * 获取当前天是星期几
	 * @param year
	 * @param month
	 * @param day
	 * @return
	 */
	public int getWeekDayOfLastMonth(int year, int month, int day) {
		Calendar cal = Calendar.getInstance();
		cal.set(year, month - 1, day);
		eachDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
		return eachDayOfWeek;
	}
	/** 
     * 计算剩余日期 
     *  
     * @param remainTime 
     * @return 
     */  
    public static String calculationRemainTime(String endTime, long countDown) {  
  
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
        try {  
            Date now = new Date(System.currentTimeMillis());// 获取当前时间  
            Date endData = df.parse(endTime);  
            long l = endData.getTime() - countDown - now.getTime();  
            long day = l / (24 * 60 * 60 * 1000);  
            long hour = (l / (60 * 60 * 1000) - day * 24);  
            long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);  
            long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);  
            return "剩余" + day + "天" + hour + "小时" + min + "分" + s + "秒";  
        } catch (ParseException e) {  
            e.printStackTrace();  
        }  
        return "";  
    }


	public static long getDateValue(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.getTimeInMillis();
	}

	public static long getDateValue(String dateStr,String format) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(sdf.parse(dateStr));
		return calendar.getTimeInMillis();
	}



	
}
