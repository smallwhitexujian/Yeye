package org.cocos2dx.lib.util;

import android.os.Handler;
import android.util.Log;


/**
 * Created by jjfly on 16-8-15.
 */
public class Cocos2dxGiftCallback {

    public static final int MSG_FINISH = 0xFFFF+1;

    public static final String TAG = Cocos2dxGiftCallback.class.getSimpleName();
    private static Handler mHandler;

    public static void onCreate(Handler handler){
        mHandler = handler;
    }

    public static void onDestroy(){
        mHandler = null;
    }

    public static void onFinish(){
        Log.e(TAG,"qiang onFinish ");
        if(mHandler != null){
            mHandler.obtainMessage(MSG_FINISH).sendToTarget();
        }
    }

    public void finish(){
        Log.e(TAG,"qiang ----------finish ");
    }


}
