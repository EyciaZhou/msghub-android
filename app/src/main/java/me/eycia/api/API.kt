package me.eycia.api

import android.net.Uri
import android.os.AsyncTask
import android.util.Log
import android.util.Pair
import com.facebook.common.util.Hex
import com.google.common.io.ByteStreams
import com.qiniu.android.common.Zone
import com.qiniu.android.http.ResponseInfo
import com.qiniu.android.storage.Configuration
import com.qiniu.android.storage.UploadManager
import me.eycia.http
import me.eycia.msghub_android.MyApplication
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.regex.Pattern

object API {
    const val VIEW_NORMAL = 1
    const val VIEW_PICTURE = 2

    abstract class ApiTask<Result> {
        data class answer<Result>(val result: Result?, val error: Exception?)
        private inner class asyncTask : AsyncTask<Void, Void, answer<Result>>() {
            private fun dealWithError(e: Exception) {
                e.printStackTrace()
                MyApplication.toast(e.message)
                onError(e)
            }

            override fun onPostExecute(answer: answer<Result>?) {
                when (answer) {
                    null -> dealWithError(Exception("no Exception throw, but the Result is null"))
                    else -> when (answer.error) {
                        null -> when (answer.result) {
                            null -> dealWithError(Exception("no Exception throw, but the Result is null"))
                            else -> try {
                                onSuccess(answer.result)
                            } catch (e: Exception) {
                                dealWithError(e)
                            }
                        }
                        else -> dealWithError(answer.error)
                    }
                }
                onFinish()
            }

            override fun doInBackground(vararg params: Void): answer<Result>? {
                try {
                    return answer(Task(), null)
                } catch (e: Exception) {
                    return answer(null, e)
                }
            }
        }

        private val mAsyncTask: asyncTask

        //Override by API
        @Throws(Exception::class)
        protected abstract fun Task(): Result

        //Override in somewhere like Activity
        @Throws(Exception::class)
        protected abstract fun onSuccess(result: Result)

        protected open fun onError(e: Exception) {
        }

        protected open fun onFinish() {
        }

        init {
            this.mAsyncTask = asyncTask()
        }

        fun execute() {
            this.mAsyncTask.execute()
        }
    }

    @Throws(JSONException::class)
    private fun TryGetString(jo: JSONObject, key: String, _default: String): String {
        if (jo.isNull(key)) {
            return _default
        }
        return jo.getString(key)
    }

    @Throws(Exception::class)
    private fun throwAPIError(jo: JSONObject?) {
        if (jo != null) {
            if (jo.getInt("err") != 0) {
                throw Exception(jo.getString("reason"))
            }
        } else {
            throw Exception("result is null")
        }
    }

    object Msgs {
        private val ADDR_MSGS = "https://msghub.eycia.me/msgs/"
        private val pattern = Pattern.compile("(\\d+)\\*(\\d+)")

        abstract class FullMessageGetTask(internal var Id: String) : ApiTask<MsgBase>() {

            @Throws(Exception::class)
            override fun Task(): MsgBase {
                return APIMsg(Id)
            }
        }

        abstract class PageGetTask(internal var ChanId: String, internal var Limit: Int, internal var LstId: String, internal var lstti: Long) : ApiTask<Array<MsgBase>>() {

            @Throws(Exception::class)
            override fun Task(): Array<MsgBase> {
                return APIPage(ChanId, Limit, LstId, lstti)
            }
        }

        abstract class ChansInfoTask : ApiTask<Array<ChanInfo>>() {

            @Throws(Exception::class)
            override fun Task(): Array<ChanInfo> {
                return APIChansInfo()
            }
        }

        private fun parsePixesFromStringToIntPair(Pixes: String): Pair<Int, Int> {
            val matcher = pattern.matcher(Pixes)
            if (!matcher.find()) {
                return Pair(0, 0)
            }
            val ws = Integer.parseInt(matcher.group(1))
            val hs = Integer.parseInt(matcher.group(2))

            return Pair(ws, hs)
        }

        @Throws(JSONException::class)
        private fun parsePicRefArrayFromJson(ja: JSONArray): Array<PicRef> {
            return Array(ja.length(), { i -> parsePicRefFromJson(ja.getJSONObject(i)) })
        }

        @Throws(JSONException::class)
        private fun parseChanInfoFromJson(jo: JSONObject): ChanInfo {
            return ChanInfo(jo.getString("Id"), jo.getString("Title"), jo.getLong("LastModify"))
        }

        @Throws(JSONException::class)
        private fun parseMsgBaseFromJson(jo: JSONObject): MsgBase {
            val CoverImg = TryGetString(jo, "CoverImg", "")
            val Topic = TryGetString(jo, "Topic", "")
            val AuthorId = TryGetString(jo, "AuthorId", "")
            val AuthorCoverImg = TryGetString(jo, "AuthorCoverImg", "")
            val AuthorName = TryGetString(jo, "AuthorName", "")
            val Body = TryGetString(jo, "Body", "")
            var picRefs = emptyArray<PicRef>()
            if (!jo.isNull("PicRefs")) {
                picRefs = parsePicRefArrayFromJson(jo.getJSONArray("PicRefs"))
            }

            return MsgBase(jo.getString("Id"), Body, jo.getLong("SnapTime"), jo.getLong("PubTime"),
                    jo.getString("SourceURL"), jo.getString("Title"), jo.getString("SubTitle"),
                    CoverImg, jo.getInt("ViewType"), AuthorId, AuthorCoverImg, AuthorName, jo.getString("Tag"),
                    Topic, picRefs)
        }


        @Throws(JSONException::class)
        private fun parsePicRefFromJson(jo: JSONObject): PicRef {
            val Ref = TryGetString(jo, "Ref", "")
            val Pixes = TryGetString(jo, "Pixes", "")
            val pxs = parsePixesFromStringToIntPair(Pixes)
            return PicRef(jo.getString("Url"), Ref, pxs.first, pxs.second, jo.getString("Description"))
        }

        @Throws(Exception::class)
        private fun APIMsg(Id: String): MsgBase {
            val PATH = ""

            val jo = http.GetJson(ADDR_MSGS + PATH + Id)
            throwAPIError(jo)

            return parseMsgBaseFromJson(jo.getJSONObject("data"))
        }

        @Throws(Exception::class)
        private fun APIPage(ChanId: String, Limit: Int, lstId: String, lstti: Long): Array<MsgBase> {
            val url: String

            if (ChanId == "") {
                url = ADDR_MSGS + String.format("page/%d/%s/%d", Limit, lstId, lstti)
            } else {
                url = ADDR_MSGS + String.format("chan/%s/page/%d/%s/%d", ChanId, Limit, lstId, lstti)
            }

            val jo = http.GetJson(url)
            throwAPIError(jo)

            val infoArray = jo.getJSONArray("data")
            return Array(infoArray.length(), { i -> parseMsgBaseFromJson(infoArray.getJSONObject(i)) })
        }

        @Throws(Exception::class)
        private fun APIChansInfo(): Array<ChanInfo> {
            val PATH = "chan"

            val jo = http.GetJson(ADDR_MSGS + PATH)
            throwAPIError(jo)

            val infoArray = jo.getJSONArray("data")
            return Array(infoArray.length(), { i -> parseChanInfoFromJson(infoArray.getJSONObject(i)) })
        }

    }

    private fun Sha256AndHex(normal: String): String {
        try {
            val md = MessageDigest.getInstance("SHA-256")
            md.update(normal.toByteArray())
            val digest = md.digest()
            return Hex.encodeHex(digest, false)
        } catch (e: NoSuchAlgorithmException) {
            //never happen
            e.printStackTrace()
            return ""
        }
    }

    object User {
        private val ADDR_USER = "https://msghub.eycia.me/usr/api/"

        abstract class LoginTask(internal var uname: String, password: String) : ApiTask<UserBaseInfo>() {
            internal var password: String

            @Throws(Exception::class)
            override fun Task(): UserBaseInfo {
                return APILogin(uname, password)
            }

            init {
                this.password = Sha256AndHex(password)
            }
        }

        abstract class SignTask(internal var email: String, internal var nickname: String, password: String, internal var username: String) : ApiTask<String>() {
            internal var password: String

            @Throws(Exception::class)
            override fun Task(): String {
                return APISign(username, password, nickname, email)
            }

            init {
                this.password = Sha256AndHex(password)
            }
        }

        abstract class ChangeAvatarTask(internal var uri: Uri) : ApiTask<String>() {
            internal var o_info: ResponseInfo? = null
            internal var o_response: JSONObject? = null

            @Throws(Exception::class)
            override fun Task(): String {
                val token = APIChangeAvatarToken()

                val config = Configuration.Builder().connectTimeout(10).responseTimeout(10).zone(Zone.zone0).build()
                val uploadManager = UploadManager(config)

                val inputStream = MyApplication.appContext!!.contentResolver.openInputStream(uri)
                val bytes = ByteStreams.toByteArray(inputStream!!)

                val userBaseInfo = MyApplication.userBaseInfo
                if (userBaseInfo == null) {
                    throw Exception("用户没有登录")
                }

                val latch = CountDownLatch(1)

                uploadManager.put(bytes, userBaseInfo.Id, token, { key, info, response ->
                    o_info = info
                    o_response = response
                    latch.countDown()
                }, null)

                latch.await()

                if (!o_info!!.isOK || o_response == null) {
                    throw Exception("上传头像时错误:" + o_info!!.error)
                }

                throwAPIError(o_response)

                Log.d("msghub", o_response!!.toString())

                Thread.sleep(2000)

                return o_response!!.getString("data")
            }
        }

        @Throws(JSONException::class)
        private fun parseUserInfoFromJson(jo: JSONObject): UserBaseInfo {
            return UserBaseInfo(jo.getString("Email"), jo.getString("Head"), jo.getString("Id"),
                    jo.getString("Nickname"), jo.getString("Username"))
        }

        @Throws(Exception::class)
        private fun APILogin(uname: String, password: String): UserBaseInfo {
            val PATH = "login"

            val par = HashMap<String, String>()
            par.put("uname", uname)
            par.put("pwd", password)

            val jo = http.Post(ADDR_USER + PATH, par)
            throwAPIError(jo)

            return parseUserInfoFromJson(jo.getJSONObject("data"))
        }

        @Throws(Exception::class)
        private fun APISign(username: String, password: String, nickname: String, email: String): String {
            val PATH = "sign"

            val par = HashMap<String, String>()
            par.put("username", username)
            par.put("password", password)
            par.put("nickname", nickname)
            par.put("email", email)

            val jo = http.Post(ADDR_USER + PATH, par)
            throwAPIError(jo)

            return jo.getString("data")
        }

        @Throws(Exception::class)
        private fun APIChangeAvatarToken(): String {
            val PATH = "head/token"

            val jo = http.GetJson(ADDR_USER + PATH)
            throwAPIError(jo)

            return jo.getString("data")
        }
    }
}