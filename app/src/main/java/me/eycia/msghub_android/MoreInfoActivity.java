package me.eycia.msghub_android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.webkit.WebSettings;
import android.webkit.WebView;

import me.eycia.api.Msg;
import me.eycia.api.PicRef;

public class MoreInfoActivity extends AppCompatActivity {
    private MoreInfoData mMoreInfoData;
    private WebView webView;

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
        if (mMoreInfoData.GetMsg() != null) {
            NotifyDateUpdate();
        }
    }

    private String GenBody() {
        Msg msg = mMoreInfoData.GetMsg();
        Log.d("msghub", msg.Body);

        String result = msg.Body;
        if (msg.PicRefs == null) {
            return result;
        }
        for (PicRef p: msg.PicRefs) {
            if (p.Ref != null) {
                Pair<Integer, Integer> ps = Scale(new Pair<>(p.px, p.py));

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

    public void NotifyDateUpdate() {
        webView.loadDataWithBaseURL("", GenBody(), null, "utf-8", "");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String msgid = intent.getStringExtra("msgid");

        mMoreInfoData = new MoreInfoData(this, msgid, savedInstanceState);

        setContentView(R.layout.activity_msg);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        webView = (WebView) findViewById(R.id.webView);

        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (savedInstanceState == null) {
            mMoreInfoData.UpdateMsg();
        } else {
            NotifyDateUpdate();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        mMoreInfoData.onSaveInstanceState(savedInstanceState);
    }
}
