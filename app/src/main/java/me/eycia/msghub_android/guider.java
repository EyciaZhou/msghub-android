package me.eycia.msghub_android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import me.eycia.api.API;
import me.eycia.api.Msg;

public class guider extends AppCompatActivity {
    String mid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guider);

        if (savedInstanceState != null) {
            mid = savedInstanceState.getString("mid");
        } else {
            Intent intent = getIntent();
            mid = intent.getStringExtra("mid");
        }

        API.MsgCallback(mid, new API.Callback() {
            @Override
            public void Successful(final Object o) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Msg m = (Msg) (o);
                        Intent intent_to = null;
                        if (m.ViewType == API.VIEW_NORMAL) {
                            intent_to = new Intent(guider.this, MoreInfoActivity.class);
                        } else if (m.ViewType == API.VIEW_PICTURE) {
                            intent_to = new Intent(guider.this, pictures.class);
                        }
                        intent_to.putExtra("m", m);
                        guider.this.startActivity(intent_to);
                        finish();
                    }
                });
            }

            @Override
            public void Error(Exception e) {

            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("mid", mid);
    }
}
