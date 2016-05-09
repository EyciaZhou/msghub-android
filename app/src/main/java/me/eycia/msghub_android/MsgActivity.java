package me.eycia.msghub_android;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.Pair;
import android.view.ViewTreeObserver;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.HashMap;
import java.util.Map;

import me.eycia.api.API;
import me.eycia.api.Msg;
import me.eycia.api.PicRef;

public class MsgActivity extends AppCompatActivity {

    private WebView webView;
    private Msg msg;

    private String ImgRaw = "<center>\n" +
            "<div style=\"width:%dpx\">\n" +
            "<img height=\"%dpx\" width=\"%dpx\" src=\"%s\" border=\"2\">\n" +
            "<div style=\"text-align:left\">\n" +
            "%s\n" +
            "</div>\n" +
            "</div>\n" +
            "</center>" +
            "<p></p>";

    private Pair<Integer, Integer> Scale(Pair<Integer, Integer> ps) {
        float width = webView.getWidth() / webView.getScale() - 50;

        float rate = width / ps.first;

        return Pair.create((int) (ps.first * rate), (int) (ps.second * rate));
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (msg != null) {
            Update();
        }
    }

    private String GenBody() {
        String result = msg.Body;
        if (msg.PicRefs == null) {
            return result;
        }
        for (PicRef p: msg.PicRefs) {
            if (p.Ref != null && p.Pixes != null) {
                Pair<Integer, Integer> ps = Scale(API.ParsePixes(p.Pixes));
                //Log.i("msghub", p.Pixes);
                //Log.i("msghub", String.valueOf(ps.first));
                //Log.i("msghub", String.valueOf(ps.second));

                String Des = "";
                if (p.Description != null) {
                    Des = p.Description;
                }

                result = result.replace(p.Ref, String.format(ImgRaw,
                        ps.first, ps.second, ps.first, p.Url, Des));
            }
        }

        return result;
    }

    private void Update() {
        webView.loadDataWithBaseURL("", GenBody(), null, "utf-8", "");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_msg);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        webView = (WebView) findViewById(R.id.webView);

        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        String msgid = intent.getStringExtra("msgid");

        if (savedInstanceState == null) {

            API.MsgCallback(msgid, new API.Callback() {
                @Override
                public void Succ(final Object o) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            msg = (Msg) o;
                            Update();
                        }
                    });
                }

                @Override
                public void Err(final Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

        } else {
            msg = savedInstanceState.getParcelable("msg");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelable("msg", msg);
    }
}
