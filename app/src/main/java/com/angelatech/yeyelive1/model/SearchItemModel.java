package com.angelatech.yeyelive1.model;

import java.io.Serializable;

/**
 * Created by jjfly on 16-3-28.
 */
public class SearchItemModel implements Serializable{

    public static final String HAVE_FOLLOW = "1";
    public static final String HAVE_NO_FOLLOW = "0";

    public String userid;
    public String idx;
    public String nickname;
    public String sex;
    public String headurl;
    public String isfollow;
    public String isv = "0";


}
