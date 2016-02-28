package me.eycia;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by eycia on 2/27/16.
 */
public class http {

    static public JSONObject GetJson(String Url) throws IOException, JSONException {
        URL url = new URL(Url);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection(); //TODO: https

        try {
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            String ins = new String(ByteStreams.toByteArray(in), Charsets.UTF_8);
            JSONObject jo = new JSONObject(ins);

            return jo;

        } finally {
            urlConnection.disconnect();
        }
    }
}
