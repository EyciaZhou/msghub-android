package me.eycia.msghub_android;

import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.facebook.drawee.view.SimpleDraweeView;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;

import me.eycia.api.Msg;
import me.eycia.api.PicRef;

public class MoreInfoActivity extends AppCompatActivity {
    private Msg m;
    private WebView mWebView;
    private SimpleDraweeView mTitleImg;
    private LinearLayout mGradient;

    private Pair<Integer, Integer> Scale(Pair<Integer, Integer> ps) {
        float width = mWebView.getWidth() / mWebView.getScale() - 50;

        float rate = width / ps.first;

        return Pair.create((int) (ps.first * rate), (int) (ps.second * rate));
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        int postion = mWebView.getVerticalScrollbarPosition();
        NotifyDateUpdate();
        mWebView.setVerticalScrollbarPosition(postion);
    }

    private String Card = "<div class=\"shadow round all\">\n" +
            "<table border=\"0px\">\n" +
            "\t<tr>\n" +
            "\t<td rowspan=\"3\" width=\"40px\">\n" +
            "\t<img class=\"author_cover\" src=\"%s\">\n" +
            "\t</img>\n" +
            "\t</td>\n" +
            "\n" +
            "\t<td height=\"16px\">\n" +
            "\t\t<div class=\"author_name\">\n" +
            "\t\t\t%s\n" +
            "\t\t</div>\n" +
            "\t</td>\n" +
            "\t</tr>\n" +
            "\n" +
            "\t<tr>\n" +
            "\t<td height=\"10px\">\n" +
            "\t<div class=\"time\">\n" +
            "\t%s\n" +
            "\t\t</div>\n" +
            "\t</td>\n" +
            "\t</tr>\n" +
            "\n" +
            "\t<tr>\n" +
            "\t<td>\n" +
            "\t<div>\n" +
            "\t\t</div>\n" +
            "\t</td>\n" +
            "\t</tr>\n" +
            "\n" +
            "\t<tr>\n" +
            "\t<td colspan=\"2\">\n" +
            "\t\t<div  class=\"content\">\n" +
            "\t\t%s\n" +
            "\t\t</div>\n" +
            "\t\t</td>\n" +
            "\t</tr>\n" +
            "\n" +
            "</table>\n" +
            "\n" +
            "</div>\n" +
            "\n" +
            "<style>\n" +
            "\n" +
            ".all {\n" +
            "\tpadding: 7px;\n" +
            "\tmargin: 6px;\n" +
            "\tbackground: #ffffff;\n" +
            "}\n" +
            "\n" +
            ".round {\n" +
            "\tborder-radius: 3px;\n" +
            "}\n" +
            "\n" +
            ".shadow {\n" +
            "\t-moz-box-shadow:0px 1px 3px #aaaaaa; \n" +
            "\t-webkit-box-shadow:0px 1px 3px #aaaaaa; \n" +
            "\tbox-shadow:0px 1px 3px #aaaaaa;\n" +
            "}\n" +
            "\n" +
            ".author_cover {\n" +
            "\tdisplay: inline;\n" +
            "\twidth:40px;\n" +
            "\theight:40px; \n" +
            "\tborder-radius:99em;\n" +
            "\tbackground-color:white;\n" +
            "\tmargin-right: 7px;\n" +
            "}\n" +
            "\n" +
            ".author_name {\n" +
            "\tfont-weight: normal;\n" +
            "\tfont-size: 16px;\n" +
            "}\n" +
            "\n" +
            ".time {\n" +
            "\tfont-size: 10px;\n" +
            "\tcolor: #65d87a;\n" +
            "\tmargin-left: 1px;\n" +
            "\tmargin-top: 1px;\n" +
            "}\n" +
            "\n" +
            ".content {\n" +
            "\tmargin-top: 7px;\n" +
            "\tfont-weight: bold;\n" +
            "\tfont-size:16px;\n" +
            "}\n" +
            "</style>\n";

    private String GetImageHtml(int picWidth, int picHeight, String url, String Des) {
        String ImgHtml = "<center>\n" +
                "<div style=\"width:%dpx\">\n" +
                "<img height=\"%dpx\" width=\"%dpx\" src=\"%s\" style=\"-moz-box-shadow:0px 1px 3px #333333; -webkit-box-shadow:0px 1px 3px #333333; box-shadow:0px 1px 3px #333333;\">\n" +
                "<div style=\"text-align:left;font-size:small;color:#888888;padding-top:5px;padding-left:10px\">\n" +
                "%s\n" +
                "</div>\n" +
                "</div>\n" +
                "</center>" +
                "<p></p>";

        return String.format(ImgHtml, picWidth, picHeight, picWidth, url, Des);
    }

    private String GenBody() {
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

                result = result.replace(p.Ref, GetImageHtml(ps.first, ps.second, p.Url, Des));
            }
        }

        result = "<body style=\"background:#f8f8f8\">" + String.format(Card, m.AuthorCoverImg, m.AuthorName, new PrettyTime().format(new Date(m.PubTime * 1000)), m.Title) + result;

        return result;
    }

    public void NotifyDateUpdate() {
        if (m != null) {
            mWebView.loadDataWithBaseURL("", GenBody(), null, "utf-8", "");
            mTitleImg.setImageURI(Uri.parse(m.CoverImg));
            getSupportActionBar().setTitle(m.Title);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_msg);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        mTitleImg = (SimpleDraweeView) findViewById(R.id.ItemCover);
        mGradient = (LinearLayout) findViewById(R.id.gradient);

        mWebView = (WebView) findViewById(R.id.webView);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        mWebView.setWebContentsDebuggingEnabled(true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final AppBarLayout mAppBarLayout = (AppBarLayout) findViewById(R.id.appbar2);
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int maxScroll = appBarLayout.getTotalScrollRange();
                float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;

                Rect rectangle = new Rect();
                getWindow().getDecorView().getWindowVisibleDisplayFrame(rectangle);
                mTitleImg.setAlpha(1 - percentage);
                mGradient.setAlpha(1 - percentage);
            }
        });

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
