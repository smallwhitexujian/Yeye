package com.angelatech.yeyelive.util;

import com.angelatech.yeyelive.model.OnlineListModel;

import java.util.List;

/**
 * User: cbl
 * Date: 2016/8/22
 * Time: 14:20
 * 二分法
 */
public class BinarySearch {
    /**
     * 递归方法实现二分查找法.
     *
     * @param start 数组第一位置
     * @param end   最高
     * @param item  要查找的值.
     * @return 返回值.
     */
    public static int binSearch(List<OnlineListModel> list, int start, int end, OnlineListModel item) {
        if (list.size() == 0)
            return 0;
        if (item.isrobot.equals("1")) //机器人
            return end;
        if (start <= end) {
            int mid = (start + end) / 2;
            if (list.get(mid).isrobot.equals("1"))
                return binSearch(list, start, mid - 1, item);
            else
                return mid;
        } else
            return start;
    }
}
