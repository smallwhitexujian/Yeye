package com.will.yeye.model;

import java.io.Serializable;

/**
 *  用于传递用户信息
 */
public class UserInfoModel implements Serializable{

    public String userid;
    public String nickname;
    public String headurl;
    public String isfollow;
    public boolean isout = false;

    //是否是房主
    public boolean isHost = false;






}
