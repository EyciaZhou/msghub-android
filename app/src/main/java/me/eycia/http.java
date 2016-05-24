package me.eycia;

import android.util.Log;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

public class http {
    public static StringBuffer getRequestData(Map<String, String> params) {

        StringBuffer stringBuffer = new StringBuffer();
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                stringBuffer.append(entry.getKey())
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), "UTF-8"))
                        .append("&");
            }
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuffer;
    }

    static public JSONObject GetJson(String Url) throws IOException, JSONException {
        URL url = new URL(Url);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        try {
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            String ins = new String(ByteStreams.toByteArray(in), Charsets.UTF_8);
            JSONObject jo = new JSONObject(ins);

            return jo;

        } finally {
            urlConnection.disconnect();
        }
    }

    static public JSONObject Post(String Url, Map<String, String> par) throws IOException, JSONException {
        byte[] data = getRequestData(par).toString().getBytes();

        URL url = new URL(Url);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setConnectTimeout(3000);
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(true);
        urlConnection.setRequestMethod("POST");
        urlConnection.setUseCaches(false);
        urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        urlConnection.setRequestProperty("Content-Length", String.valueOf(data.length));
        OutputStream outputStream = urlConnection.getOutputStream();
        outputStream.write(data);

        try {
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            String ins = new String(ByteStreams.toByteArray(in), Charsets.UTF_8);
            JSONObject jo = new JSONObject(ins);

            Log.d("msghub", "jo:" + ins);

            return jo;

        } finally {
            urlConnection.disconnect();
        }

    }
}
