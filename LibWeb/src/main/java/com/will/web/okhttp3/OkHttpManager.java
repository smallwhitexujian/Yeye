package com.will.web.okhttp3;

import com.will.web.ErrorMapHelper;
import com.will.web.HttpManager;
import com.will.web.callback.HttpCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by xujian on 16/1/20.
 * okHttp请求方式封装
 * 1,返回值 只会返回code[200..300)之间的请求
 * 2,可以支持Http body内容（发送到服务器不在from表单中，需要使用流的方式接）请求 post json数据或者get请求数据.
 * 3,可以支持get和post同步
 * 4,修改单独get请求
 * 5,修改单独post from表单请求 提交表单形式
 */
public class OkHttpManager extends HttpManager {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");

    //严格控制http请求
    private void init() {
//        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//                .detectDiskReads().detectDiskWrites().detectNetwork()
//                .penaltyLog().build());
//        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//                .detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
//                .penaltyLog().penaltyDeath().build());
    }

    /**
     * post body json数据
     * 回调出结果
     * @param url      请求地址
     * @param callback 反馈结果
     * @param params   参数
     */
    public void Request(int method, String url, Map<String,String> params,final HttpCallback callback) {
        if (url == null || url.equals("")) {
            return;
        }
        if (params == null) {
            return;
        }
        init();//Android 2.3提供一个称为严苛模式（StrictMode）的调试特性
        OkHttpClient client = getClient();
        Request request = null;
        if (method == Method.GET) {
            String strUrl = restructureURL(Method.GET, url, params);
            request = new Request.Builder().url(strUrl).build();
        } else {
            FormBody.Builder builder = new FormBody.Builder();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.add(entry.getKey(), entry.getValue());
            }
            RequestBody requestBody = builder.build();
            request = new Request.Builder().url(url).post(requestBody).build();
        }
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Map errorMap = new HashMap();
                errorMap.put(ErrorMapHelper.KEY_CALL,call);
                errorMap.put(ErrorMapHelper.KEY_EXCEPTION,e);
                callback.onFailure(errorMap);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String result = response.body().string();
                    callback.onSuccess(result);
                }
            }
        });
    }


    /**
     * get 请求
     *
     * @param url    地址
     * @param params 参数
     * @return 结果
     */
    public String getRequest(String url, Map<String, String> params) {
        if (url == null || url.equals("")) {
            return null;
        }
        init();//Android 2.3提供一个称为严苛模式（StrictMode）的调试特性
        String strUrl = restructureURL(Method.GET, url, params);
        //创建okHttpClient对象
        OkHttpClient client = getClient();
        //创建一个Request
        Request request = new Request.Builder().url(strUrl).build();
        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String postRequest(String url,Map<String,String> params){
        if (url == null || url.equals("")) {
            return null;
        }
        init();//Android 2.3提供一个称为严苛模式（StrictMode）的调试特性
        OkHttpClient client = getClient();
        String json = getJson(params);
        Request request = null;
        if (json != null) {
            RequestBody body = RequestBody.create(JSON, json);
            request = new Request.Builder().url(url).post(body).build();
        }
        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;


    }

    /**
     * post from 提交请求(一般使用这个)
     */
    public String postRequest(String url, RequestBody params) {
        if (url == null || url.equals("")) {
            return null;
        }
        init();//Android 2.3提供一个称为严苛模式（StrictMode）的调试特性
        OkHttpClient client = getClient();
        Request request = new Request.Builder().url(url).post(params).build();
        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }


    /**
     * post  from 请求(一般使用这个)
     */
    public void postRequest(String url, RequestBody params, final HttpCallback callback) {
        if (url == null || url.equals("")) {
            return;
        }
        init();//Android 2.3提供一个称为严苛模式（StrictMode）的调试特性
        OkHttpClient client = getClient();
        Request request = new Request.Builder().url(url).post(params).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Map errorMap = new HashMap();
                errorMap.put(ErrorMapHelper.KEY_CALL,call);
                errorMap.put(ErrorMapHelper.KEY_EXCEPTION,e);
                callback.onFailure(errorMap);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String result = response.body().string();
                    callback.onSuccess(result);
                }
            }
        });
    }


    private String getJson(Map<String,String> params){
        JSONObject jsonObject = new JSONObject();
        String json = null;
        boolean error = false;
        for(Map.Entry<String,String> entry:params.entrySet()){
            String key = entry.getKey();
            String value = entry.getValue();
            if(key == null || value == null){
                continue;
            }
            try {
                jsonObject.put(key,value);
            } catch (JSONException e) {
                e.printStackTrace();
                error = true;
            }
            if(error){
                break;
            }
        }
        json = jsonObject.toString();
        return json;
    }


    /**
     * 上传单个文件
     * @param urlStr
     * @param uploadKey
     * @param file
     * @param mediaType
     * @return
     */
    @Override
    public String upload(String urlStr, String uploadKey, File file, MediaType mediaType) {
        Request request = getFileUploadRequest(urlStr,uploadKey,file,mediaType);
        String result = null;
        try {
            Response response = getClient().newCall(request).execute();
            result = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public String upload(String urlStr,Map<String,String> params,String uploadKey, File file, MediaType mediaType) {
        Request request = getFileUploadRequest(urlStr,params,uploadKey,file,mediaType);
        String result = null;
        try {
            Response response = getClient().newCall(request).execute();
            result = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }



    @Override
    public void download() {

    }

    public static Request getFileUploadRequest(String url,String name,File file,MediaType mediaType){
        MultipartBody.Builder builder = new MultipartBody.Builder();
        if (file.exists() && name != null && !"".equals(name)) {
            builder.addFormDataPart(name,file.getName(), RequestBody.create(mediaType,file));
        }
        RequestBody requestBody = builder.build();
        return new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
    }

    public static Request getFileUploadRequest(String url,Map<String,String> params,String name,File file,MediaType mediaType){
        MultipartBody.Builder builder = new MultipartBody.Builder();
        if (file.exists() && name != null && !"".equals(name)) {
            builder.addFormDataPart(name,file.getName(), RequestBody.create(mediaType,file));
        }
        for(Map.Entry<String,String> entry:params.entrySet()){
            String key = entry.getKey();
            String value = entry.getValue();
            builder.addFormDataPart(key,value);
        }
        RequestBody requestBody = builder.build();
        return new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
    }



    /**
     *
     * @param url
     * @param name
     * @param file
     * @return
     */
    public static Request getPicUploadRequest(String url,String name,File file){
        MediaType PNG_TYPE = MediaType.parse("image/png");
        MediaType JPG_TYPE = MediaType.parse("image/jpg");

        MediaType mediaType = PNG_TYPE;
        MultipartBody.Builder builder = new MultipartBody.Builder();
        if (file.exists() && name != null && !"".equals(name)) {
            if(".jpg".endsWith(file.getName())){
                mediaType = JPG_TYPE;
            }
            if(".png".endsWith(file.getName())){
                mediaType = PNG_TYPE;
            }
            builder.addFormDataPart(name,file.getName(), RequestBody.create(mediaType,file));
        }
        RequestBody requestBody = builder.build();
        return new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
    }


    public static Request getPicUploadRequest(String url,String name,File file,Map<String,String> params){
        MediaType PNG_TYPE = MediaType.parse("image/png");
        MediaType JPG_TYPE = MediaType.parse("image/jpg");

        MediaType mediaType = PNG_TYPE;
        MultipartBody.Builder builder = new MultipartBody.Builder();
        if (file.exists() && name != null && !"".equals(name)) {
            if(".jpg".endsWith(file.getName())){
                mediaType = JPG_TYPE;
            }
            if(".png".endsWith(file.getName())){
                mediaType = PNG_TYPE;
            }
            builder.addFormDataPart(name,file.getName(), RequestBody.create(mediaType,file));
        }
        for(Map.Entry<String,String> entry:params.entrySet()){
            String key = entry.getKey();
            String value = entry.getValue();
            builder.addFormDataPart(key,value);
        }
        RequestBody requestBody = builder.build();
        return new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
    }





    private OkHttpClient getClient(){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
//                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
        return client;
    }



}
