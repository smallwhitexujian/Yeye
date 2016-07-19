package com.will.yeye.activity.function;

import android.content.Context;

import com.will.common.string.json.JsonUtil;
import com.will.yeye.application.App;
import com.will.yeye.model.ChatLineModel;

/**
 * Created by Shanli_pc on 2016/3/22.
 */

public class ChatManager {
    private Context context;

    public ChatManager(Context context) {
        this.context = context;
    }

    /**
     * 收到 消息
     *
     * @param object obj
     */
    public void receivedChatMessage(Object object) {
        AddChatMessage(object);
    }

    /**
     * 收到红包信息
     *
     * @param object
     */
    public void receiverRedEnvelope(Object object) {
        AddChatMessage(object);
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
        ChatLineModel chatLineModel = JsonUtil.fromJson(object.toString(), ChatLineModel.class);
        if (chatLineModel != null) {
            AddChatMessage(chatLineModel);
        }
    }

    /**
     * 聊天记录初始化，
     */
    public void AddChatMessage(ChatLineModel chatLineModel) {
        App.mChatlines.add(chatLineModel);
        int maxSize = 80;
        if (App.mChatlines.size() >= maxSize) {
            for (int i = 0; i < App.mChatlines.size(); i++) {
                if (i < (maxSize / 2)) {
                    App.mChatlines.remove(i);
                }
            }
        }
    }
}
