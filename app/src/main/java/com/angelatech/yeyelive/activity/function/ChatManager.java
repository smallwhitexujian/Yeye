package com.angelatech.yeyelive.activity.function;

import android.app.Activity;

import com.angelatech.yeyelive.application.App;
import com.angelatech.yeyelive.fragment.CallFragment;
import com.angelatech.yeyelive.model.ChatLineModel;
import com.angelatech.yeyelive.util.JsonUtil;
import com.angelatech.yeyelive.util.PausableThreadPoolExecutor;
import com.angelatech.yeyelive.util.PriorityRun;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by Shanli_pc on 2016/3/22.
 * 消息管理处理
 */
public class ChatManager {
    private PausableThreadPoolExecutor fixedThreadPool;
    private Activity mContext;

    public ChatManager(Activity activity) {
        int threadnum = Runtime.getRuntime().availableProcessors();
        int corePoolSize = threadnum + 1;
        int maximumPoolSize = threadnum * 2 + 1;
        fixedThreadPool = new PausableThreadPoolExecutor(corePoolSize, maximumPoolSize, 0L, TimeUnit.SECONDS, new PriorityBlockingQueue<Runnable>());
        this.mContext = activity;
    }

    /**
     * 收到 消息
     *
     * @param object obj
     */
    public void receivedChatMessage(final Object object) {
        AddChatMessage(object);
    }

    public void receivedChatMessage(final Object object, final CallFragment f) {
        messageQueue(object, f);
    }

    //消息处理
    private void messageQueue(final Object object, final CallFragment f) {
        fixedThreadPool.execute(new PriorityRun(0) {
            @Override
            public void doSth() {
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AddChatMessage(object);
                        f.notifyData();
                    }
                });
            }
        });
    }

    /**
     * 聊天消息初始化
     *
     * @param uid   发送的用户id
     * @param name  发送的用户昵称
     * @param photo 发送的用户头像
     * @param msg   发送的内容
     */
    public ChatLineModel setChatLineModel(String uid, String name, String photo, String msg, String lv) {
        ChatLineModel chat = new ChatLineModel();
        ChatLineModel.from from = new ChatLineModel.from();
        from.uid = uid;
        from.name = name;
        from.headphoto = photo;
        from.level = lv;
        chat.message = msg;
        chat.from = from;
        return chat;
    }

    /**
     * 添加 消息
     *
     * @param object obj
     */
    public void AddChatMessage(Object object) {
        if (object != null) {
            ChatLineModel chatLineModel = JsonUtil.fromJson(object.toString(), ChatLineModel.class);
            if (chatLineModel != null) {
                AddChatMessage(chatLineModel);
            }
        }
    }

    /**
     * 聊天记录初始化，
     */
    public void AddChatMessage(final ChatLineModel chatLineModel) {
        App.mChatlines.add(chatLineModel);
    }

    public void pause() {
        if (fixedThreadPool!=null){
            fixedThreadPool.pause();
        }
    }

    public void resume() {
        if (fixedThreadPool!=null){
            fixedThreadPool.resume();
        }
    }
}
