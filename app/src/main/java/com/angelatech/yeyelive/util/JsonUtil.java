package com.angelatech.yeyelive.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by jjfly on 16-3-1.
 */
public class JsonUtil {

    /**
     * 使用：创建一个list对象，对象里面直接放入add(bean)
     * @param arrayListJson
     * @return 传入一个list直接转化为_json对象 支持多层
     */
    private static String toListJson(List arrayListJson){
        try {
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            return gson.toJson(arrayListJson);
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static String toJson(Object src, Type typeOfSrc){
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        try {
            return gson.toJson(src, typeOfSrc);
        } catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public static String toJson(Object src){
        // 利用gson对象生成json字符串
        Gson gson = new Gson();
        String jsonString = gson.toJson(src);
        return jsonString;
    }


    public static <T> List<T> fromJsonList(String json){
        List<T> datas = JsonUtil.fromJson(json,new TypeToken<List<T>>(){}.getType());
        return datas;
    }


    /**
     * bean 解析
     * @param json
     * @param clazz
     * @return
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            Gson gson = buildGson();
            return gson.fromJson(json, clazz);
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 支持多层解析 只需要传入对应type
     * @param json
     * @param typeOfT
     * @return
     *
     * new TypeToken<CommonParseModel<?>>() {}.getType()
     *
     */
    public static <T> T fromJson(String json, Type typeOfT) {
        try {
            Gson gson = buildGson();
            return gson.fromJson(json, typeOfT);
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private static Gson buildFieldsWithoutExposeAnnotationGson(){
        GsonBuilder builder = new GsonBuilder();
        builder.excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                .excludeFieldsWithoutExposeAnnotation();
        Gson gson = builder.create();
        return gson;
    }

    public static Gson buildGson(){
        GsonBuilder builder = new GsonBuilder();
        builder.excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC);
        Gson gson = builder.create();
        return gson;
    }

}
