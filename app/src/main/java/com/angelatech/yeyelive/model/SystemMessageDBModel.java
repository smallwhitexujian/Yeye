//package com.will.live.model;
//
//import com.activeandroid.Model;
//import com.activeandroid.annotation.Column;
//import com.activeandroid.annotation.Table;
//import com.activeandroid.query.Delete;
//import com.activeandroid.query.Select;
//import com.will.live.DBConst;
//
//import java.util.List;
//
///**
// * 系统消息数据库模型
// */
//
//@Table(name = DBConst.TABLE_FOR_SYSTEMMESSAGE)
//public class SystemMessageDBModel extends Model {
//
//    @Column(name = "type_code")
//    public int type_code;
//
//    @Column(name = "data")
//    public String data;
//
//    @Column(name = "content")
//    public String content;
//
//    @Column(name = "localtime")
//    public long localtime;
//
//    @Column(name = "datetime")
//    public String datetime;
//
//    @Column(name = "uid")
//    public String uid;//用户id
//
//
//    public List<SystemMessageDBModel> load(String uid) {
//        List<SystemMessageDBModel> datas = new Select()
//                .from(SystemMessageDBModel.class)
//                .where("uid=?", uid)
//                .orderBy("localtime DESC")
//                .execute();
//        return datas;
//    }
//
//
//    public void clearSystemMessage(String uid) {
//        new Delete().from(SystemMessageDBModel.class).where("uid = ? ", uid).execute();
//    }
//
//    @Override
//    public String toString() {
//        return "SystemMessageDBModel{" +
//                "type_code='" + type_code + '\'' +
//                ", data='" + data + '\'' +
//                ", content='" + content + '\'' +
//                ", localtime=" + localtime +
//                ", datetime='" + datetime + '\'' +
//                ", uid=" + uid +
//                '}';
//    }
//}
