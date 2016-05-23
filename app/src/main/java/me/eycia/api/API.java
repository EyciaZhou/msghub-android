package me.eycia.api;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
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

    static private abstract class ApiTask<Result> {
        private class asyncTask extends AsyncTask<Void, Void, Result> {
            private Exception Error;

            private void dealWithError(Exception e) {
                e.printStackTrace();
                Toast.makeText(MyApplication.getAppContext(), e.toString(), Toast.LENGTH_SHORT).show();
                onError(e);
            }

            @Override
            protected void onPostExecute(Result result) {
                if (Error != null) {
                    dealWithError(Error);
                } else if (result != null) {
                    onSuccess(result);
                } else {
                    dealWithError(new Exception("no Exception throw, but the Result is null"));
                }
                onFinish();
            }

            @Override
            protected Result doInBackground(Void... params) {
                try {
                    return Task();
                } catch (Exception e) {
                    Error = e;
                    return null;
                }
            }
        }

        private asyncTask mAsyncTask;

        //Override by API
        abstract protected Result Task() throws Exception;

        //Override in somewhere like Activity
        abstract protected void onSuccess(@NonNull Result result);

        protected void onError(@NonNull Exception e) {
        }

        protected void onFinish() {
        }

        public ApiTask() {
            this.mAsyncTask = new asyncTask();
        }

        public void execute() {
            this.mAsyncTask.execute();
        }
    }

    static public abstract class FullMessageGetTask extends ApiTask<Msg> {
        String Id;

        public FullMessageGetTask(String id) {
            this.Id = id;
        }

        @Override
        protected Msg Task() throws Exception {
            return APIMsg(Id);
        }
    }

    static public abstract class PageGetTask extends ApiTask<MsgLine[]> {
        String ChanId, LstId;
        int Limit;
        long lstti;

        public PageGetTask(String ChanId, int Limit, String LstId, long lstti) {
            this.ChanId = ChanId;
            this.Limit = Limit;
            this.LstId = LstId;
            this.lstti = lstti;
        }

        @Override
        protected MsgLine[] Task() throws Exception {
            return APIPage(ChanId, Limit, LstId, lstti);
        }
    }

    static public abstract class ChansInfoTask extends ApiTask<ChanInfo[]> {
        @Override
        protected ChanInfo[] Task() throws Exception {
            return APIChansInfo();
        }
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
                NinePics[i] = ja.getString(i);
            }
        }
        return new MsgLine(parseMsgBaseFromJson(jo), NinePics);
    }

    static private ChanInfo parseChanInfoFromJson(JSONObject jo) throws JSONException {
        return new ChanInfo(jo.getString("Id"), jo.getString("Title"), jo.getLong("LastModify"));
    }

    static private String TryGetString(JSONObject jo, String key, String _default) throws JSONException {
        if (!jo.isNull(key)) {
            return _default;
        }
        return jo.getString(key);
    }

    static private MsgBase parseMsgBaseFromJson(JSONObject jo) throws JSONException {
        String CoverImg = TryGetString(jo, "CoverImg", "");
        String Topic = TryGetString(jo, "Topic", "");
        String AuthorId = TryGetString(jo, "AuthorId", "");
        String AuthorCoverImg = TryGetString(jo, "AuthorCoverImg", "");
        String AuthorName = TryGetString(jo, "AuthorName", "");

        if (!AuthorCoverImg.equals(""))
            AuthorCoverImg = jo.getString("AuthorCoverImg") + "-small";

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
        String Ref = TryGetString(jo, "Ref", "");
        String Pixes = TryGetString(jo, "Pixes", "");

        return new PicRef(jo.getString("Description"), parsePixesFromStringToIntPair(Pixes), Ref, jo.getString("Url"));
    }

    public static PicRef[] LoadPicRefArrayFromJson(JSONArray ja) throws JSONException {
        PicRef[] result = new PicRef[ja.length()];
        for (int i = 0; i < ja.length(); i++) {
            result[i] = LoadPicRefFromJson(ja.getJSONObject(i));
        }
        return result;
    }
}
