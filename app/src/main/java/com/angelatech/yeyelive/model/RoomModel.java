package com.angelatech.yeyelive.model;

import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;

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

    public String getRtmpip() {
        return rtmpip;
    }

    public void setRtmpip(String Rtmpip) {
        this.rtmpip = Rtmpip;
    }

    public String getRtmpwatchaddress() {
        return rtmpwatchaddress;
    }

    public void setRtmpwatchaddress(String rtmpwatchaddress) {
        this.rtmpwatchaddress = rtmpwatchaddress;
    }

    public void setLiveid(String liveid) {
        this.liveid = liveid;
    }

    public String getLiveid() {
        return this.liveid;
    }

    private String name;            //直播用户名称
    private int id;                 //直播房间ID
    private String idx;             //直播用户IDX
    private String level;           //直播用户等级
    private String ip;              //房间IP
    private int port;               //端口
    private String rtmpip;          //直播地址
    private String rtmpwatchaddress; //观看视频地址
    private String heatDay;         //热度
    private int likenum;
    private String roomType;        //直播状态

    public String getBarcoverurl() {
        return barcoverurl;
    }

    public void setBarcoverurl(String barcoverurl) {
        this.barcoverurl = barcoverurl;
    }

    private String barcoverurl;     //直播封面
    private int livenum = 0;
    private int livecoin = 0;
    private String livetime = "00:00:01";
    private BasicUserInfoDBModel userInfoDBModel; //
    private String pwd;

    public String getPwd(){return pwd;}

    public void setPwd(String pwd){this.pwd = pwd;}

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    private String ticket;//门票价格
    public BasicUserInfoModel getLoginUser() {
        return loginUser;
    }

    public void setLoginUser(BasicUserInfoModel loginUser) {
        this.loginUser = loginUser;
    }

    private BasicUserInfoModel loginUser; //当前登录用户信息
    private String liveid;

    @Override
    public String toString() {
        return "RoomModel{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", idx='" + idx + '\'' +
                ", level='" + level + '\'' +
                ", ip='" + ip + '\'' +
                ", rtmpip='" + rtmpip + '\'' +
                ", rtmpwatchaddress='" + rtmpwatchaddress + '\'' +
                ", port=" + port +
                ", heatDay='" + heatDay + '\'' +
                ", likenum=" + likenum +
                ", roomType='" + roomType + '\'' +
                ", livenum=" + livenum +
                ", livecoin=" + livecoin +
                ", livetime='" + livetime + '\'' +
                ", userInfoDBModel=" + userInfoDBModel +
                ", liveid='" + liveid + '\'' +
                '}';
    }
}
