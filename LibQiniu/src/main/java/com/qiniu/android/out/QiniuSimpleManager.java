package com.qiniu.android.out;

import com.qiniu.android.common.Zone;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.KeyGenerator;
import com.qiniu.android.storage.Recorder;
import com.qiniu.android.storage.UpCancellationSignal;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;


/**
 * User: cbl
 * Date: 2016/4/18
 * Time: 15:49
 */
public class QiniuSimpleManager{

    private final String TAG = QiniuSimpleManager.class.getClasses().toString();

    private boolean isCancelled = false;
    private Recorder recorder = null;
    private KeyGenerator keyGen = null;
    private Configuration config = new Configuration.Builder()
            .chunkSize(256 * 1024)  //分片上传时，每片的大小。 默认 256K
            .putThreshhold(512 * 1024)  // 启用分片上传阀值。默认 512K
            .connectTimeout(10) // 链接超时。默认 10秒
            .responseTimeout(60) // 服务器响应超时。默认 60秒
            .recorder(recorder)  // recorder 分片上传时，已上传片记录器。默认 null
            .recorder(recorder, keyGen)  // keyGen 分片上传时，生成标识符，用于片记录器区分是那个文件的上传记录
            .zone(Zone.zone0) // 设置区域，指定不同区域的上传域名、备用域名、备用IP。默认 Zone.zone0
            .build();
    // 重用 uploadManager。一般地，只需要创建一个 uploadManager 对象
    private UploadManager uploadManager  = new UploadManager(config);


    public QiniuSimpleManager() {
    }


    public void upPhoto(String token,String data, String key,UpCompletionHandler upCompletionHandler, UploadOptions uploadOptions) {
        //data = null;  //<File对象、或 文件路径、或 字节数组>
        //String key = "";  //<指定七牛服务上的文件名，或 null >;
        //String token = "";//<从服务端SDK获取 >;
        uploadManager.put(data, key, token, upCompletionHandler, uploadOptions);
    }

    /**
     * 取消 上传
     *
     * @param data byte[]
     * //* @param uploadManager       UploadManager
     * //* @param upCompletionHandler UpCompletionHandler
     *
     */
    public void CancelUpload(String token,byte[] data, String key,UpCompletionHandler upCompletionHandler,UpProgressHandler upProgressHandler) {
        cancel();
        uploadManager.put(data, key, token, upCompletionHandler, new UploadOptions(null, null, false, upProgressHandler,
                new UpCancellationSignal() {
                    public boolean isCancelled() {
                        return isCancelled;
                    }
                }));
    }


    private void cancel() {
        isCancelled = true;
    }
}
