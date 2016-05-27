package me.eycia.api;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Pair;
import android.widget.Toast;

import com.facebook.common.util.Hex;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
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

    static private String TryGetString(JSONObject jo, String key, String _default) throws JSONException {
        if (jo.isNull(key)) {
            return _default;
        }
        return jo.getString(key);
    }

    static private void throwAPIError(JSONObject jo) throws Exception {
        if (jo.getInt("err") != 0) {
            throw new Exception(jo.getString("reason"));
        }
    }

    static public class Msgs {
        static private final String ADDR_MSGS = "https://msghub.eycia.me/msgs/";
        static private Pattern pattern = Pattern.compile("(\\d+)\\*(\\d+)");

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

        static private PicRef[] parsePicRefArrayFromJson(JSONArray ja) throws JSONException {
            PicRef[] result = new PicRef[ja.length()];
            for (int i = 0; i < ja.length(); i++) {
                result[i] = parsePicRefFromJson(ja.getJSONObject(i));
            }
            return result;
        }

        static private Msg parseMsgFromJson(JSONObject jo) throws JSONException {
            PicRef[] picRefs = new PicRef[0];
            if (!jo.isNull("PicRefs")) {
                picRefs = parsePicRefArrayFromJson(jo.getJSONArray("PicRefs"));
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


        static private PicRef parsePicRefFromJson(JSONObject jo) throws JSONException {
            String Ref = TryGetString(jo, "Ref", "");
            String Pixes = TryGetString(jo, "Pixes", "");
            return new PicRef(jo.getString("Description"), parsePixesFromStringToIntPair(Pixes), Ref, jo.getString("Url"));
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

    }

    static private String Sha256AndHex(String normal) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(normal.getBytes());
            byte[] digest = md.digest();
            normal = Hex.encodeHex(digest, false);
        } catch (NoSuchAlgorithmException e) {
            //never happen
            e.printStackTrace();
        }

        return normal;
    }

    static public class User {
        static private final String ADDR_USER = "https://msghub.eycia.me/usr/api/";

        static public abstract class LoginTask extends ApiTask<UserBaseInfo> {
            String uname, password;

            @Override
            protected UserBaseInfo Task() throws Exception {
                return APILogin(uname, password);
            }

            public LoginTask(String uname, String password) {
                this.uname = uname;
                this.password = Sha256AndHex(password);
            }
        }

        static public abstract class SignTask extends ApiTask<String> {
            String username, password, nickname, email;

            @Override
            protected String Task() throws Exception {
                return APISign(username, password, nickname, email);
            }

            public SignTask(String email, String nickname, String password, String username) {
                this.email = email;
                this.nickname = nickname;
                this.password = Sha256AndHex(password);
                this.username = username;
            }
        }

        static public abstract class ChangeAvatarTask extends ApiTask<String> {
            @Override
            protected String Task() throws Exception {
                return APIChangeAvatarToken();
            }
        }

        static private UserBaseInfo parseUserInfoFromJson(JSONObject jo) throws JSONException {
            return new UserBaseInfo(jo.getString("Email"), jo.getString("Id"),
                    jo.getString("Nickname"), jo.getString("Username"));
        }

        static private UserBaseInfo APILogin(String uname, String password) throws Exception {
            final String PATH = "login";

            Map<String,String> par = new HashMap<>();
            par.put("uname", uname);
            par.put("pwd", password);

            JSONObject jo = http.Post(ADDR_USER + PATH, par);
            throwAPIError(jo);

            return parseUserInfoFromJson(jo.getJSONObject("data"));
        }

        static private String APISign(String username, String password, String nickname, String email) throws Exception {
            final String PATH = "sign";

            Map<String,String> par = new HashMap<>();
            par.put("username", username);
            par.put("password", password);
            par.put("nickname", nickname);
            par.put("email", email);

            JSONObject jo = http.Post(ADDR_USER + PATH, par);
            throwAPIError(jo);

            return jo.getString("data");
        }

        static private String APIChangeAvatarToken() throws Exception {
            final String PATH = "head/token";

            JSONObject jo = http.GetJson(ADDR_USER + PATH);
            throwAPIError(jo);

            return jo.getString("data");
        }
    }
}
