package me.eycia.msghub_android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;
import android.widget.Toast;

public class MsgActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        final WebView webView = (WebView) findViewById(R.id.webView);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        String msgid = intent.getStringExtra("msgid");

        API.MsgCallback(msgid, new API.Callback() {
            @Override
            public void Succ(final Object o) {


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Msg msg = (Msg) o;
                        webView.loadDataWithBaseURL("", msg.Body, "text/html", "utf-8", null);
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
    }
}
