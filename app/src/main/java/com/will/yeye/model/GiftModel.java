package com.will.yeye.model;

/**
 * Created by Shanli_pc on 2016/3/30.
 */
public class GiftModel {

        private int id;
        private String name;
        private String price;
        private String ImageFile;
        private String imageurl;
        private String ImageName;
        private int Suffer;
        private int Charm;
        private int Rich;
        private String Description;
        private int type;
        private int sort;
        private int gifttype;
        private int state = 0;

    public static class AcceptGift extends CommonModel{
        public int type;                //消息种类
        public OnlineListModel from;    //发送的消息
        public OnlineListModel to;      //接受消息
        public int giftid;              //礼物id
        public int number;              //礼物数量
        public long coin;               //金币数量
    }
    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getID() {
        return id;
    }

    public void setID(int ID) {
        this.id = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        price = price;
    }

    public String getImageFile() {
        return ImageFile;
    }

    public void setImageFile(String imageFile) {
        ImageFile = imageFile;
    }

    public String getImageURL() {
        return imageurl;
    }

    public void setImageURL(String imageURL) {
        imageurl = imageURL;
    }

    public String getImageName() {
        return ImageName;
    }

    public void setImageName(String imageName) {
        ImageName = imageName;
    }

    public int getSuffer() {
        return Suffer;
    }

    public void setSuffer(int suffer) {
        Suffer = suffer;
    }

    public int getCharm() {
        return Charm;
    }

    public void setCharm(int charm) {
        Charm = charm;
    }

    public int getRich() {
        return Rich;
    }

    public void setRich(int rich) {
        Rich = rich;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        type = type;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        sort = sort;
    }

    public int getGifttype() {
        return gifttype;
    }

    public void setGifttype(int gifttype) {
        this.gifttype = gifttype;
    }
}
