package me.eycia.api;

import android.os.Looper;
import android.util.Pair;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.eycia.http;
import me.eycia.msghub_android.MyApplication;

/**
 * Created by eycia on 2/27/16.
 */
public class API {
    public static final int VIEW_NORMAL = 1;
    public static final int VIEW_PICTURE = 2;
    private static final String ADDR_MSGS = "https://msghub.eycia.me/msgs/";
    private static Pattern pattern = Pattern.compile("(\\d+)\\*(\\d+)");

    static public void FireACallback(final callbackValueGenerator gv, final Callback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final Object v = gv.Generate();
                    if (v == null) {
                        throw new Exception("result is nll");
                    } else {
                        //callOnUI(callback.GetActivity(), new Runnable() {
                        //    @Override
                        //    public void run() {
                        callback.Successful(v);
                        //    }
                        //});
                    }
                } catch (final Exception e) {
                    //callOnUI(callback.GetActivity(), new Runnable() {
                    //    @Override
                    //    public void run() {
                    e.printStackTrace();

                    Looper.prepare();
                    Toast.makeText(MyApplication.getAppContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    Looper.loop();

                    callback.Error(e);
                    //    }
                    //});
                }
            }
        }).start();
    }

    static public void MsgCallback(final String Id, final Callback callback) {
        FireACallback(new callbackValueGenerator() {
            @Override
            public Object Generate() throws Exception {
                return APIMsg(Id);
            }
        }, callback);
    }

    static public void PageCallback(final String ChanId, final int Limit, final String LstId, final long lstti, final Callback callback) {
        FireACallback(new callbackValueGenerator() {
            @Override
            public Object Generate() throws Exception {
                return APIPage(ChanId, Limit, LstId, lstti);
            }
        }, callback);
    }

    static public void ChansInfoCallback(final Callback callback) {
        FireACallback(new callbackValueGenerator() {
            @Override
            public Object Generate() throws Exception {
                return APIChansInfo();
            }
        }, callback);
    }

    static private Pair<Integer, Integer> parsePixesFromStringToIntPair(String Pixes) {
        Matcher matcher = pattern.matcher(Pixes);
        if (!matcher.find()) {
            return new Pair<>(0, 0);
        }
        int ws = Integer.parseInt(matcher.group(1));
        int hs = Integer.parseInt(matcher.group(2));

        return new Pair<>(ws, hs);
    }

    static private Msg parseMsgFromJson(JSONObject jo) throws JSONException {
        PicRef[] picRefs = new PicRef[0];
        if (!jo.isNull("PicRefs")) {
            picRefs = LoadPicRefArrayFromJson(jo.getJSONArray("PicRefs"));
        }
        return new Msg(parseMsgBaseFromJson(jo), jo.getString("Body"), picRefs);
    }

    static private MsgLine parseMsgLineFromJson(JSONObject jo) throws JSONException {
        String[] NinePics = new String[0];

        if (!jo.isNull("Pics")) {
            JSONArray ja = jo.getJSONArray("Pics");
            NinePics = new String[ja.length()];
            for (int i = 0; i < ja.length(); i++) {
                NinePics[i] = ja.getString(i) + "-small";
            }
        }
        return new MsgLine(parseMsgBaseFromJson(jo), NinePics);
    }

    static private ChanInfo parseChanInfoFromJson(JSONObject jo) throws JSONException {
        return new ChanInfo(jo.getString("Id"), jo.getString("Title"), jo.getLong("LastModify"));
    }

    static private MsgBase parseMsgBaseFromJson(JSONObject jo) throws JSONException {
        String CoverImg = "";
        String Topic = "";
        String AuthorId = "";
        String AuthorCoverImg = "";
        String AuthorName = "";
        if (!jo.isNull("CoverImg")) CoverImg = jo.getString("CoverImg") + "-small";
        if (!jo.isNull("Topic")) Topic = jo.getString("Topic");
        if (!jo.isNull("AuthorId")) AuthorId = jo.getString("AuthorId");
        if (!jo.isNull("AuthorCoverImg"))
            AuthorCoverImg = jo.getString("AuthorCoverImg") + "-small";
        if (!jo.isNull("AuthorName")) AuthorName = jo.getString("AuthorName");

        return new MsgBase(AuthorCoverImg, AuthorId, AuthorName, CoverImg, jo.getString("Id"),
                jo.getLong("PubTime"), jo.getLong("SnapTime"), jo.getString("SourceURL"),
                jo.getString("SubTitle"), jo.getString("Tag"), jo.getString("Title"), Topic, jo.getInt("ViewType"));
    }

    static private void throwAPIError(JSONObject jo) throws Exception {
        if (jo.getInt("err") != 0) {
            throw new Exception(jo.getString("reason"));
        }
    }

    static private Msg APIMsg(String Id) throws Exception {
        final String PATH = "";

        JSONObject jo = http.GetJson(ADDR_MSGS + PATH + Id);
        throwAPIError(jo);

        return parseMsgFromJson(jo.getJSONObject("data"));
    }

    static private MsgLine[] APIPage(String ChanId, int Limit, String lstId, long lstti) throws Exception {
        final String PATH = "page";

        String url;

        if (ChanId.equals("")) {
            url = ADDR_MSGS + String.format("page/%d/%s/%d", Limit, lstId, lstti);
        } else {
            url = ADDR_MSGS + String.format("chan/%s/page/%d/%s/%d", ChanId, Limit, lstId, lstti);
        }

        JSONObject jo = http.GetJson(url);
        throwAPIError(jo);

        JSONArray infoArray = jo.getJSONArray("data");

        MsgLine[] result = new MsgLine[infoArray.length()];
        for (int i = 0; i < infoArray.length(); i++) {
            result[i] = parseMsgLineFromJson(infoArray.getJSONObject(i));
        }

        return result;
    }

    static private ChanInfo[] APIChansInfo() throws Exception {
        final String PATH = "chan";

        JSONObject jo = http.GetJson(ADDR_MSGS + PATH);
        throwAPIError(jo);

        JSONArray infoArray = jo.getJSONArray("data");

        ChanInfo[] result = new ChanInfo[infoArray.length()];

        for (int i = 0; i < infoArray.length(); i++) {
            result[i] = parseChanInfoFromJson(infoArray.getJSONObject(i));
        }

        return result;
    }

    public static PicRef LoadPicRefFromJson(JSONObject jo) throws JSONException {
        String Ref = "";
        String Pixes = "";

        if (!jo.isNull("Ref")) {
            Ref = jo.getString("Ref");
        }

        if (!jo.isNull("Pixes")) {
            Pixes = jo.getString("Pixes");
        }

        return new PicRef(jo.getString("Description"), parsePixesFromStringToIntPair(Pixes), Ref, jo.getString("Url"));
    }

    public static PicRef[] LoadPicRefArrayFromJson(JSONArray ja) throws JSONException {
        PicRef[] result = new PicRef[ja.length()];
        for (int i = 0; i < ja.length(); i++) {
            result[i] = LoadPicRefFromJson(ja.getJSONObject(i));
        }
        return result;
    }

    public interface Callback {
        void Successful(Object o);

        void Error(Exception e);
    }

    private interface callbackValueGenerator {
        Object Generate() throws Exception;
    }
}
