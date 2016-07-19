//package com.will.live.model;
//
//import com.activeandroid.Model;
//import com.activeandroid.annotation.Column;
//import com.activeandroid.annotation.Table;
//import com.activeandroid.query.Delete;
//import com.activeandroid.query.Select;
//import com.activeandroid.query.Update;
//import com.will.live.DBConst;
//
//import java.io.Serializable;
//import java.util.Map;
//
///**
// * 用户信息数据库
// */
//
//@Table(name = DBConst.TABLE_FOR_CACHEUSER)
//public class BasicUserInfoDBModel extends Model implements Serializable {
//
//    @Column(name = "userid", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
//    public String userid;
//    @Column(name = "idx")
//    public String idx;
//    @Column(name = "token")
//    public String token;
//    @Column(name = "nickname")
//    public String nickname;
//    @Column(name = "sex")
//    public String sex;
//    @Column(name = "headurl")
//    public String headurl;
//    @Column(name = "sign")
//    public String sign;
//    @Column(name = "diamonds")
//    public String diamonds;
//    @Column(name = "fansNum")
//    public String fansNum;
//    @Column(name = "followNum")
//    public String followNum;
//    @Column(name = "intimacy")
//    public String Intimacy;
//
//
//    //查找消息
//    public synchronized BasicUserInfoDBModel load() {
//        BasicUserInfoDBModel data = new Select()
//                .from(BasicUserInfoDBModel.class)
//                .executeSingle();
//        if (data == null) {
//            return null;
//        }
//        return data;
//    }
//
//    public synchronized BasicUserInfoDBModel load(String userId) {
//        BasicUserInfoDBModel data = new Select()
//                .from(BasicUserInfoDBModel.class)
//                .where("userid=?", userId)
//                .executeSingle();
//        if (data == null) {
//            return null;
//        }
//        return data;
//    }
//
////
////   //增加
////   public synchronized void addAll(List<BasicUserInfoDBModel> messageRecordDBModels){
////      ActiveAndroid.beginTransaction();
////      try {
////         for (BasicUserInfoDBModel model:messageRecordDBModels) {
////            model.save();
////         }
////         ActiveAndroid.setTransactionSuccessful();
////      }
////      finally {
////         ActiveAndroid.endTransaction();
////      }
////   }
//
//
//    //删除
//    public synchronized void deleteMessageRecord(String userid) {
//        new Delete().from(BasicUserInfoDBModel.class).where("userid = ?", userid).execute();
//    }
//
//    public synchronized void update(String key, String value, String userid) {
//        new Update(BasicUserInfoDBModel.class)
//                .set(key + "=?", value)
//                .where("userid=?", userid)
//                .execute();
//    }
//
//
//
//    public synchronized void update(Map<String, String> params, String userid) {
//        StringBuffer buffer = new StringBuffer();
//        for (Map.Entry<String, String> entry : params.entrySet()) {
//            String key = entry.getKey();
//            String value = entry.getValue();
//            buffer.append(key + "=" + value);
//            buffer.append(",");
//        }
//        String sets = buffer.substring(0, buffer.length() - 1);
//        new Update(BasicUserInfoDBModel.class)
//                .set(sets)
//                .where("userid=?", userid)
//                .execute();
//    }
//
//    @Override
//    public String toString() {
//        return "BasicUserInfoDBModel{" +
//                "userid='" + userid + '\'' +
//                ", idx='" + idx + '\'' +
//                ", token='" + token + '\'' +
//                ", nickname='" + nickname + '\'' +
//                ", sex='" + sex + '\'' +
//                ", headurl='" + headurl + '\'' +
//                ", sign='" + sign + '\'' +
//                ", diamonds='" + diamonds + '\'' +
//                ", fansNum='" + fansNum + '\'' +
//                ", followNum='" + followNum + '\'' +
//                ", Intimacy='" + Intimacy + '\'' +
//                '}';
//    }
//}
