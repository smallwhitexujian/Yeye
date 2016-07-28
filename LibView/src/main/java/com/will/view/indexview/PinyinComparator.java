package com.will.view.indexview;

import java.util.Comparator;

/**
 * Created by jjfly on 16-7-28.
 */
public class PinyinComparator implements Comparator<String> {
    public int compare(String o1, String o2) {
        String str1 = PingYinUtil.getPingYin(o1);
        String str2 = PingYinUtil.getPingYin(o2);
        return str1.compareTo(str2);
    }
}
