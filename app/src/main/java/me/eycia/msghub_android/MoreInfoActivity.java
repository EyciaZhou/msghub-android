package me.eycia.msghub_android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.webkit.WebSettings;
import android.webkit.WebView;

import me.eycia.api.Msg;
import me.eycia.api.PicRef;

public class MoreInfoActivity extends AppCompatActivity {
    private Msg m;
    private WebView webView;

    private Pair<Integer, Integer> Scale(Pair<Integer, Integer> ps) {
        float width = webView.getWidth() / webView.getScale() - 50;

        float rate = width / ps.first;

        return Pair.create((int) (ps.first * rate), (int) (ps.second * rate));
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        NotifyDateUpdate();
    }

    private String GenBody() {
        String ImgRaw = "<center>\n" +
                "<div style=\"width:%dpx\">\n" +
                "<img height=\"%dpx\" width=\"%dpx\" src=\"%s\" border=\"2\">\n" +
                "<div style=\"text-align:left\">\n" +
                "%s\n" +
                "</div>\n" +
                "</div>\n" +
                "</center>" +
                "<p></p>";

        Msg msg = m;

        String result = msg.Body;
        if (msg.PicRefs == null) {
            return result;
        }
        for (PicRef p : msg.PicRefs) {
            if (p.Ref != null && !p.Ref.equals("")) {
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
        if (m != null) {
            webView.loadDataWithBaseURL("", GenBody(), null, "utf-8", "");
        }
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


        if (savedInstanceState != null) {
            m = savedInstanceState.getParcelable("m");
        } else {
            Intent intent = getIntent();
            m = intent.getParcelableExtra("m");
        }

        if (m != null) {
            NotifyDateUpdate();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelable("m", m);
    }
}
