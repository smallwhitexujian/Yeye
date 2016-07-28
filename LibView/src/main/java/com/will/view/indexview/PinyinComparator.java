package com.will.view.indexview;

/**
 * Created by jjfly on 16-7-28.
 */
public class PinyinComparator {
    public int compare(Object o1, Object o2) {
        String str1 = PingYinUtil.getPingYin((String) o1);
        String str2 = PingYinUtil.getPingYin((String) o2);
        return str1.compareTo(str2);
    }
}
