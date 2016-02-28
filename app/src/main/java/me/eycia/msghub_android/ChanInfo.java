package me.eycia.msghub_android;

import org.json.JSONObject;

/**
 * Created by eycia on 2/27/16.
 */
public class ChanInfo {
    public String Id;
    public String Title;
    public long LstModify;

    public ChanInfo(String id, String title, long lstModify) {
        this.Id = id;
        this.Title = title;
        this.LstModify = lstModify;
    }
}