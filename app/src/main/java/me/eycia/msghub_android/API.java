package me.eycia.msghub_android;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
