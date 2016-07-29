package com.angelatech.yeyelive.mediaplayer.util;

import android.content.res.Configuration;

import com.will.common.log.DebugLogs;

/**
 * Author: jjfly
 * Date: 2016-06-07 18:10
 * FIXME:
 * DESC:
 */
public class PlayerUtil {


    public static String showTime(int time) {
        // 将ms转换为s
        time /= 1000;
        int hour = time / (60 * 60);
        int minute = time / 60; // 求分
        int second = time % 60; // 求秒
        minute %= 60;
        if (hour > 0) {
            return String.format("%02d:%02d:%02d", hour, minute, second);
        } else {
            return String.format("%02d:%02d", minute, second);
        }

    }

    public static String showTime2(int time) {
        // 将ms转换为s
        time /= 1000;
        int hour = time / (60 * 60);
        int minute = time / 60; // 求分
        int second = time % 60; // 求秒
        minute %= 60;
        return String.format("%2d:%02d:%02d", hour, minute, second);
    }

    public static String showTime3(int time){
        // 将ms转换为s
        int hour = time / (60 * 60);
        int minute = time / 60; // 求分
        int second = time % 60; // 求秒
        minute %= 60;
        if (hour > 0) {
            return String.format("%02d:%02d:%02d", hour, minute, second);
        } else {
            return String.format("%02d:%02d", minute, second);
        }
    }

    //缩放视频大小
    public static int[] scaleVideoSize(int orientation, int videoWidth, int videoHeight, int screenWidth, int screenHeight) {
        DebugLogs.e("==============={" +
                "videoWidth=" + videoWidth +
                ", videoHeight=" + videoHeight +
                ", screenWidth=" + screenWidth +
                ", screenHeight=" + screenHeight +
                '}');
        int videoSizeAry[] = new int[2];
        if(orientation == Configuration.ORIENTATION_LANDSCAPE){
            //横屏
            if (videoHeight > screenHeight) {
                float vHeight = (float) videoHeight / (float) screenHeight;
                // 计算出缩放大小,取接近的正值
                videoHeight = (int) Math.ceil((float) videoHeight / vHeight);
                videoWidth = (int) Math.ceil((float) videoWidth / vHeight);

                videoSizeAry[0] = videoWidth;
                videoSizeAry[1] = videoHeight;
            } else {
                float vHeight = (float) screenHeight / (float) videoHeight;
                // 计算出缩放大小,取接近的正值
                videoWidth = (int) Math.ceil((float) videoWidth * vHeight);
                videoHeight = (int) Math.ceil((float) videoHeight * vHeight);

                videoSizeAry[0] = videoWidth;
                videoSizeAry[1] = videoHeight;
            }

        }
        else{
            if (videoWidth > screenWidth) {
                float vWidth = (float) videoWidth / (float) screenWidth;
                // 计算出缩放大小,取接近的正值
                videoWidth = (int) Math.ceil((float) videoWidth / vWidth);
                videoHeight = (int) Math.ceil((float) videoHeight / vWidth);

                videoSizeAry[0] = videoWidth;
                videoSizeAry[1] = videoHeight;
            } else {
                float vWidth = (float) screenWidth / (float) videoWidth;
                // 计算出缩放大小,取接近的正值
                videoWidth = (int) Math.ceil((float) videoWidth * vWidth);
                videoHeight = (int) Math.ceil((float) videoHeight * vWidth);

                videoSizeAry[0] = videoWidth;
                videoSizeAry[1] = videoHeight;
            }
        }
        return videoSizeAry;
    }

    //1、横屏
    //高度充满
    //2、竖屏
    //宽度充满
    //缩放视频大小
//    public static int[] scaleVideoSize(boolean fullScreen, int videoWidth, int videoHeight, int screenWidth, int screenHeight) {
//        int videoSizeAry[] = new int[2];
//        // 获取视频的宽度和高度
//        // 如果按钮文字为窗口则设置为窗口模式
//        if (!fullScreen) {
//           /*
//            * 如果为全屏模式则改为适应内容的，前提是视频宽高小于屏幕宽高，如果大于宽高 我们要做缩放
//            * 如果视频的宽高度有一方不满足我们就要进行缩放. 如果视频的大小都满足就直接设置并居中显示。
//            */
//            if (videoWidth > screenWidth || videoHeight > screenHeight) {
//                // 计算出宽高的倍数
//                float vWidth = (float) videoWidth / (float) screenWidth;
//                float vHeight = (float) videoHeight / (float) screenHeight;
//                // 获取最大的倍数值，按大数值进行缩放
//                float max = Math.max(vWidth, vHeight);
//                // 计算出缩放大小,取接近的正值
//                videoWidth = (int) Math.ceil((float) videoWidth / max);
//                videoHeight = (int) Math.ceil((float) videoHeight / max);
//
//                videoSizeAry[0] = videoWidth;
//                videoSizeAry[1] = videoHeight;
//            }
//            else{
//                //放大
//                float vWidth = (float) screenWidth / (float) videoWidth;
//                float vHeight = (float) screenHeight / (float) videoHeight;
//                // 获取最大的倍数值，按大数值进行缩放
//                float min = Math.min(vWidth, vHeight);
//                // 计算出缩放大小,取接近的正值
//                videoWidth = (int) Math.ceil((float) videoWidth * min);
//                videoHeight = (int) Math.ceil((float) videoHeight * min);
//
//                videoSizeAry[0] = videoWidth;
//                videoSizeAry[1] = videoHeight;
//            }
//
//        } else {
//            // 设置全屏
//            videoSizeAry[0] = screenWidth;
//            videoSizeAry[1] = screenHeight;
//        }
//        return videoSizeAry;
//    }


}
