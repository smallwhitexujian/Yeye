package com.angelatech.yeyelive1.model;

import java.io.Serializable;

/**
 * User: cbl
 * Date: 2016/6/16
 * Time: 13:43
 * 录像model
 */
public class VideoModel extends LiveVideoModel implements Serializable {
    public String videoid;
    public String playnum;
    public String durations;
    public String playaddress;
    public String addtime;

}
