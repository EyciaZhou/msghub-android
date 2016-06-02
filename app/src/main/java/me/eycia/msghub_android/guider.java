package me.eycia.msghub_android;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import me.eycia.api.API;
import me.eycia.api.MsgBase;

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

        new API.Msgs.FullMessageGetTask(mid) {
            @Override
            protected void onSuccess(@NonNull MsgBase msg) {
                Intent intent_to;
                if (msg.getViewType() == API.VIEW_NORMAL) {
                    intent_to = new Intent(guider.this, MoreInfoActivity.class);
                } else /*if (msg.ViewType == API.VIEW_PICTURE)*/ {
                    intent_to = new Intent(guider.this, pictures.class);
                    intent_to.putExtra("clicked_pic", getIntent().getIntExtra("clicked_pic", 0));
                }
                intent_to.putExtra("m", msg);
                guider.this.startActivity(intent_to);
                finish(); //close the window of "guider"
            }
        }.execute();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("mid", mid);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
