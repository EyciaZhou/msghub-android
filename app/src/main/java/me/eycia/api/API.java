package me.eycia.api;

import android.util.Log;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.eycia.http;

/**
 * Created by eycia on 2/27/16.
 */
public class API {
    static final String ADDR_MSGS = "https://msghub.eycia.me/msgs/";
    public static final int VIEW_NORMAL = 1;
    public static final int VIEW_PICTURE = 2;

    static Pattern pattern = Pattern.compile("(\\d+)\\*(\\d+)");

    public interface Callback {
        void Succ(Object o);
        void Err(Exception e);
    }

    static public Pair<Integer, Integer> ParsePixes(String Pixes) {
        Matcher matcher = pattern.matcher(Pixes);
        matcher.find();

        Log.d("msghub", Pixes);

        int ws = Integer.parseInt(matcher.group(1));
        int hs = Integer.parseInt(matcher.group(2));

        return new Pair<>(ws, hs);
    }

    static private Msg parseMsg(JSONObject jo) throws JSONException {
        PicRef[] picRefs = null;
        if (!jo.isNull("PicRefs")) {
            picRefs = PicRef.LoadArrayFromJson(jo.getJSONArray("PicRefs"));
        }
        return new Msg(parseMsgInfo(jo), jo.getString("Body"), picRefs);
    }

    static private ChanInfo parseChanInfo(JSONObject jo) throws JSONException {
        String id = jo.getString("Id");
        String title = jo.getString("Title");
        long lstModify = jo.getLong("LastModify");

        return new ChanInfo(id, title, lstModify);
    }

    static private MsgLine parseMsgInfo(JSONObject jo) throws JSONException {
        String CoverImg = "";
        String Topic = "";
        String AuthorId = "";
        String AuthorCoverImg = "";
        String AuthorName = "";
        String[] NinePics = new String[0];
        if (!jo.isNull("CoverImg")) {
            CoverImg = jo.getString("CoverImg");
        }
        if (!jo.isNull("Topic")) {
            Topic = jo.getString("Topic");
        }
        if (!jo.isNull("AuthorId")) {
            AuthorId = jo.getString("AuthorId");
        }
        if (!jo.isNull("AuthorCoverImg")) {
            AuthorCoverImg = jo.getString("AuthorCoverImg");
        }
        if (!jo.isNull("AuthorName")) {
            AuthorName = jo.getString("AuthorName");
        }
        if (!jo.isNull("Pics")) {
            JSONArray ja = jo.getJSONArray("Pics");

            NinePics = new String[ja.length()];
            for (int i = 0; i < ja.length(); i++) {
                NinePics[i] = ja.getString(i) + "-small";
            }
        }
        return new MsgLine(AuthorCoverImg, AuthorId, AuthorName, CoverImg, jo.getString("Id"),
                jo.getLong("PubTime"), jo.getLong("SnapTime"), jo.getString("SourceURL"),
                jo.getString("SubTitle"), jo.getString("Tag"), jo.getString("Title"), Topic, jo.getInt("ViewType"), NinePics);
    }

    static public Msg Msg(String Id) throws Exception{
        final String PATH = "";

        JSONObject jo = http.GetJson(ADDR_MSGS + PATH + Id);

        if (jo.getInt("err") != 0) {
            throw new Exception(jo.getString("reason"));
        }

        return parseMsg(jo.getJSONObject("data"));
    }

    static public void MsgCallback(final String Id, final Callback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Msg msg = Msg(Id);
                    callback.Succ(msg);
                } catch (Exception e) {
                    callback.Err(e);
                }
            }
        }).start();
    }

    static public MsgLine[] Page(String ChanId, int Limit, String lstId, long lstti) throws Exception{
        final String PATH = "page";

        String url;

        if (ChanId == "") {
            url = ADDR_MSGS + String.format("page/%d/%s/%d", Limit, lstId, lstti);
        } else {
            url = ADDR_MSGS + String.format("chan/%s/page/%d/%s/%d", ChanId, Limit, lstId, lstti);
        }

        JSONObject jo = http.GetJson(url);

        if (jo.getInt("err") != 0) {
            throw new Exception(jo.getString("reason"));
        }

        JSONArray infoArray = jo.getJSONArray("data");

        MsgLine[] result = new MsgLine[infoArray.length()];

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
                    MsgBase[] msgBase = Page(ChanId, Limit, LstId, lstti);
                    callback.Succ(msgBase);
                } catch (Exception e) {
                    callback.Err(e);
                }
            }
        }).start();
    }

    static public ChanInfo[] ChansInfo() throws Exception {
        final String PATH = "chan";

        JSONObject jo = http.GetJson(ADDR_MSGS+PATH);

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
