package me.eycia

import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

object http {
    fun getRequestData(params: Map<String, String>): StringBuffer {

        val stringBuffer = StringBuffer()
        try {
            for (entry in params.entries) {
                stringBuffer.append(entry.key).append("=").append(URLEncoder.encode(entry.value, "UTF-8")).append("&")
            }
            stringBuffer.deleteCharAt(stringBuffer.length - 1)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return stringBuffer
    }

    @Throws(IOException::class, JSONException::class)
    fun GetJson(Url: String): JSONObject = JSONObject(URL(Url).readText())

    @Throws(IOException::class, JSONException::class)
    fun Post(Url: String, par: Map<String, String>): JSONObject {
        val data = getRequestData(par).toString().toByteArray()

        val url = URL(Url)

        val urlConnection = url.openConnection() as HttpURLConnection
        with (urlConnection) {
            connectTimeout = 3000
            doInput = true
            doOutput = true
            requestMethod = "POST"
            useCaches = false
            setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
            setRequestProperty("Content-Length", data.size.toString())
            outputStream.write(data)
        }

        try {
            return JSONObject(urlConnection.inputStream.bufferedReader().readText())
        } finally {
            urlConnection.disconnect()
        }

    }
}
