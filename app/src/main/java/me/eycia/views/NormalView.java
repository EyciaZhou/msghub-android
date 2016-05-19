package me.eycia.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;

import me.eycia.api.API;
import me.eycia.api.MsgLine;
import me.eycia.msghub_android.R;
import me.eycia.msghub_android.guider;

/**
 * Created by eycia on 16/5/11.
 */
public class NormalView extends BaseView {
    TextView ItemTitle;
    TextView ItemTime;
    TextView ItemText;
    SimpleDraweeView ListCover;

    MsgLine msgLine;
    Activity activity;

    public NormalView(Activity activity, View view) {
        super(view);
        this.activity = activity;
    }

    @Override
    public int ViewType() {
        return API.VIEW_NORMAL;
    }

    @Override
    public void UpdateInfo(MsgLine msgLine) {
        this.msgLine = msgLine;

        ItemTitle.setText(msgLine.Title);
        ItemText.setText(msgLine.SubTitle);
        ItemTime.setText(new PrettyTime().format(new Date(msgLine.PubTime * 1000)));
        ListCover.setImageURI(Uri.parse(msgLine.CoverImg + "-small"));
    }

    static class OnNormalClickListener implements View.OnClickListener {
        NormalView normalView;

        public OnNormalClickListener(NormalView normalView) {
            this.normalView = normalView;
        }

        @Override
        public void onClick(View v) {
            if (normalView.msgLine != null) {
                Intent intent = new Intent(normalView.activity, guider.class);
                intent.putExtra("mid", normalView.msgLine.Id);

                normalView.activity.startActivity(intent);
            }
        }
    }

    public static NormalView GetView(Context context, ViewGroup parent, Activity activity) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View convertView = inflater.inflate(R.layout.normal_view_item, parent, false);

        NormalView viewHolder = new NormalView(activity, convertView);

        viewHolder.ItemTitle = (TextView) convertView.findViewById(R.id.ItemTitle);
        viewHolder.ItemTime = (TextView) convertView.findViewById(R.id.ItemTime);
        viewHolder.ItemText = (TextView) convertView.findViewById(R.id.ItemText);
        viewHolder.ListCover = (SimpleDraweeView) convertView.findViewById(R.id.ListCover);

        convertView.setOnClickListener(new OnNormalClickListener(viewHolder));

        convertView.setTag(viewHolder);

        return viewHolder;
    }

}
