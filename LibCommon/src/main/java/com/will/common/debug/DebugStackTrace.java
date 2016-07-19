package com.will.common.debug;/**
 * Created by jjfly on 16-6-6.
 */

import com.will.common.log.DebugLogs;

/**
 * Author: jjfly
 * Date: 2016-06-06 10:21
 * FIXME:
 * DESC:
 */
public class DebugStackTrace {

    private final static String TAG = "StackTrace:";

    public static void getStackTraceInfo() {
        Throwable ex = new Throwable();
        StackTraceElement[] stackElements = ex.getStackTrace();
        if (stackElements != null) {
            for (int i = 0; i < stackElements.length; i++) {
//                DebugLogs.e(TAG+stackElements[i].getClassName());
//                DebugLogs.e(TAG+stackElements[i].getFileName());
//                DebugLogs.e(TAG+stackElements[i].getLineNumber());
//                DebugLogs.e(TAG+stackElements[i].getMethodName());

                DebugLogs.e(TAG+
                        i+" "+
                        " ClassName:"+stackElements[i].getClassName()+
                        " FileName:"+stackElements[i].getFileName()+
                        " LineNumber:"+stackElements[i].getLineNumber()+
                        " MethodName:"+stackElements[i].getMethodName());
            }
        }

    }


}
