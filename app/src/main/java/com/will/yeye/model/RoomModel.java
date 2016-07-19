package com.will.yeye.model;

import com.will.yeye.db.model.BasicUserInfoDBModel;

import java.io.Serializable;

/**
 * User: cbl
 * Date: 2015/11/17
 * Time: 15:06
 */
public class RoomModel implements Serializable {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHeatDay() {
        return heatDay;
    }

    public void setHeatDay(String heatDay) {
        this.heatDay = heatDay;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomtype) {
        this.roomType = roomtype;
    }

    public void setUserInfoDBModel(BasicUserInfoDBModel userInfoDBModel) {
        this.userInfoDBModel = userInfoDBModel;
    }

    public BasicUserInfoDBModel getUserInfoDBModel() {
        return this.userInfoDBModel;
    }

    public int getLikenum() {
        return likenum;
    }

    public void setLikenum(int likenum) {
        this.likenum = likenum;
    }

    public int getLivenum() {
        return livenum;
    }

    public void setLivenum(int livenum) {
        this.livenum = livenum;
    }

    public int getLivecoin() {
        return livecoin;
    }

    public void setLivecoin(int livecoin) {
        this.livecoin = livecoin;
    }

    public String getLivetime() {
        return livetime;
    }

    public void setLivetime(String livetime) {
        this.livetime = livetime;
    }

    public void addlivenum() {
        livenum++;
    }

    public void addLivecoin(int coin) {
        livecoin += coin;
    }

    public void setIdx(String value) {
        idx = value;
    }

    public String getIdx() {
        return idx;
    }

    public String getRtmpip() { return rtmpip; }

    public void setRtmpip(String Rtmpip) {  this.rtmpip = Rtmpip; }

    public String getRtmpwatchaddress() { return rtmpwatchaddress; }

    public void setRtmpwatchaddress(String rtmpwatchaddress) {  this.rtmpwatchaddress = rtmpwatchaddress; }


    private String name;
    private int id;
    private String idx;
    private String level;
    private String ip;
    private String rtmpip;
    private String rtmpwatchaddress;
    private int port;
    private String heatDay;
    private int likenum;
    private String roomType; //播放还是观看
    private int livenum = 0;
    private int livecoin = 0;
    private String livetime = "00:00:01";
    private BasicUserInfoDBModel userInfoDBModel;
}
