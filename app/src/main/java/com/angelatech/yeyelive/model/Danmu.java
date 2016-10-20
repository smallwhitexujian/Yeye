package com.angelatech.yeyelive.model;

import android.graphics.Bitmap;

/**
 * Created by feiyang on 16/3/2.
 */
public class Danmu {
    public long   id;
    public int    userId;
    public String type;
    public Bitmap avatarUrl;
    public String content;

    public Danmu(long id, int userId, String type, Bitmap avatarUrl, String content) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.avatarUrl = avatarUrl;
        this.content = content;
    }
}
