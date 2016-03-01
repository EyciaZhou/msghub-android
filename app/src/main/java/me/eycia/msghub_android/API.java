package me.eycia.msghub_android;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

import me.eycia.http;

/**
 * Created by eycia on 2/27/16.
 */
public class API {
    //static final String ADDR = "http://msghub.eycia.me/";
    static final String ADDR = "http://aliyun1.eycia.me:4000/msgs/";

    public interface Callback {
        void Succ(Object o);
        void Err(Exception e);
    }

    static private ChanInfo parseChanInfo(JSONObject jo) throws JSONException {
        String id = jo.getString("Id");
        String title = jo.getString("Title");
        long lstModify = jo.getLong("LastModify");

        return new ChanInfo(id, title, lstModify);
    }

    static private MsgInfo parseMsgInfo(JSONObject jo) throws JSONException {
        String CoverImgId = "";
        String Topic = "";
        if (!jo.isNull("CoverImgId")) {
            CoverImgId = jo.getString("CoverImgId");
        }
        if (!jo.isNull("Topic")) {
            Topic = jo.getString("Topic");
        }
        return new MsgInfo(jo.getString("Id"), jo.getLong("SnapTime"), jo.getLong("PubTime"), jo.getString("SourceURL"),
                jo.getString("Title"), jo.getString("SubTitle"), CoverImgId, jo.getInt("ViewType"), jo.getString("Frm"),
                jo.getString("Tag"), Topic);
    }

    static public MsgInfo[] Page(String ChanId, int Limit, String lstId, long lstti) throws Exception{
        final String PATH = "page";

        String url;

        if (ChanId == "") {
            url = ADDR + String.format("page/%d/%s/%d", Limit, lstId, lstti);
        } else {
            url = ADDR + String.format("chan/%s/page/%d/%s/%d", ChanId, Limit, lstId, lstti);
        }

        JSONObject jo = http.GetJson(url);

        if (jo.getInt("err") != 0) {
            throw new Exception(jo.getString("reason"));
        }

        JSONArray infoArray = jo.getJSONArray("data");

        MsgInfo[] result = new MsgInfo[infoArray.length()];

        for (int i = 0; i < infoArray.length(); i++) {
            result[i] = parseMsgInfo(infoArray.getJSONObject(i));
        }

        return result;
    }

    static public void PageCallback(final String ChanId, final int Limit, final String LstId, final long lstti, final Callback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MsgInfo[] msgInfo = Page(ChanId, Limit, LstId, lstti);
                    callback.Succ(msgInfo);
                } catch (Exception e) {
                    callback.Err(e);
                }
            }
        }).start();
    }

    static public ChanInfo[] ChansInfo() throws Exception {
        final String PATH = "chan";

        JSONObject jo = http.GetJson(ADDR+PATH);

        if (jo.getInt("err") != 0) {
            throw new Exception(jo.getString("reason"));
        }

        JSONArray infoArray = jo.getJSONArray("data");

        ChanInfo[] result = new ChanInfo[infoArray.length()];

        for (int i = 0; i < infoArray.length(); i++) {
            result[i] = parseChanInfo(infoArray.getJSONObject(i));
        }

        return result;
    }

    static public void ChansInfoCallback(final Callback callback) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        ChanInfo[] chansInfo = ChansInfo();
                        callback.Succ(chansInfo);
                    } catch (Exception e) {
                        callback.Err(e);
                    }
                }
            }).start();
    }
}
