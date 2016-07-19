package com.will.yeye.model;

/**
 * 系统广播模型
 */
public class SystemBroadModel<T> {
    public String type_code;
    public String content;
    public String datetime;
    public T data;


    public static class FeedbackBroadCast{
        public String msg;
    }


    public static class LiveBroadCast{
        public String uid;
        public String headurl;
        public String nickname;
        public String roomid;
        public String roomip;

        @Override
        public String toString() {
            return "LiveBroadCast{" +
                    "uid:'" + uid + ',' +
                    ", headurl:'" + headurl + ',' +
                    ", nickname:'" + nickname + ',' +
                    ", roomid:'" + roomid + ',' +
                    ", roomip:'" + roomip + ',' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "SystemBroadModel{" +
                "type_code='" + type_code + '\'' +
                ", content='" + content + '\'' +
                ", datetime='" + datetime + '\'' +
                ", data=" + data +
                '}';
    }
}
