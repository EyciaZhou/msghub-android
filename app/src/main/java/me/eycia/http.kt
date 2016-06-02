package me.eycia

import android.util.Log
import com.google.common.base.Charsets
import com.google.common.io.ByteStreams
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedInputStream
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
    fun GetJson(Url: String): JSONObject {
        val url = URL(Url)
        val urlConnection = url.openConnection() as HttpURLConnection

        try {
            val `in` = BufferedInputStream(urlConnection.inputStream)
            val ins = String(ByteStreams.toByteArray(`in`), Charsets.UTF_8)
            val jo = JSONObject(ins)

            return jo

        } finally {
            urlConnection.disconnect()
        }
    }

    @Throws(IOException::class, JSONException::class)
    fun Post(Url: String, par: Map<String, String>): JSONObject {
        val data = getRequestData(par).toString().toByteArray()

        val url = URL(Url)
        val urlConnection = url.openConnection() as HttpURLConnection
        urlConnection.connectTimeout = 3000
        urlConnection.doInput = true
        urlConnection.doOutput = true
        urlConnection.requestMethod = "POST"
        urlConnection.useCaches = false
        urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
        urlConnection.setRequestProperty("Content-Length", data.size.toString())
        val outputStream = urlConnection.outputStream
        outputStream.write(data)

        try {
            val `in` = BufferedInputStream(urlConnection.inputStream)
            val ins = String(ByteStreams.toByteArray(`in`), Charsets.UTF_8)
            val jo = JSONObject(ins)

            Log.d("msghub", "jo:" + ins)

            return jo

        } finally {
            urlConnection.disconnect()
        }

    }
}
