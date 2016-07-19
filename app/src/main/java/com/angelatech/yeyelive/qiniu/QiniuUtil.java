package com.angelatech.yeyelive.qiniu;

import android.content.Context;

import com.angelatech.yeyelive.CommonUrlConfig;
import com.angelatech.yeyelive.web.HttpFunction;
import com.will.web.handle.HttpBusinessCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: jjfly
 * Since: 2016年05月11日 09:51
 * Desc:
 * FIXME:
 */
public class QiniuUtil extends HttpFunction{


    public QiniuUtil(Context context) {
        super(context);
    }


    //获取七牛凭证
    public void getQiniuCertificate(String userid, String token, HttpBusinessCallback callback){
        Map<String, String> params = new HashMap<>();
        params.put("token", token);
        params.put("userid", userid);
        httpGet(CommonUrlConfig.GetQiniuUpToken,params,callback);
    }

    //http://videotest.vvago.com/api/Picture/PicQiniu?Token=B3B783A0A3A7E814173AAAA3F5364FD3&userid=10007&type=1&id=1&imageurl=http://123
    public void callBusinessServer(String userid,String token,String imageurl,String id,HttpBusinessCallback callback){
        Map<String, String> params = new HashMap<>();
        params.put("token", token);
        params.put("userid", userid);
        params.put("type",1+"");
        params.put("imageurl",imageurl);
        params.put("id",id);//
        httpGet(CommonUrlConfig.PicQiniu,params,callback);
    }

    /**
     *
     * @param baseUrl
     * @param width
     * @param height
     * @return 构造裁剪尺寸的图片url
     * 参考:
     * http://developer.qiniu.com/article/kodo/kodo-developer/download-process.html
     */
    public static String getCropSizeUrl(String baseUrl,int width,int height){
        if(baseUrl == null || width <= 0 || height <= 0){
            throw new IllegalArgumentException();
        }
        StringBuilder buffer = new StringBuilder();
        buffer.append(baseUrl)
                .append("?")
                .append("imageView2/1")
                .append("w/"+width)
                .append("h/"+height);
        return buffer.toString();

    }



}
