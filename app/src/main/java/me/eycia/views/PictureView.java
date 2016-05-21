package me.eycia.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
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
public class PictureView extends BaseView {
    TextView ItemTitle;
    TextView ItemTime;
    TextView ItemText;
    TextView ItemAuthor;
    LinearLayout Body;
    SimpleDraweeView ItemAuthorHead;
    SimpleDraweeView Pics[] = new SimpleDraweeView[9];
    int width;

    MsgLine msgLine;
    Activity activity;

    public PictureView(Activity activity, View view) {
        super(view);
        this.activity = activity;
    }

    public void SetUpdateNine() {
        Body.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                width = Body.getWidth();
                UpdateNineLayout();
            }
        });
    }

    public void UpdateNineLayout() {
        for (int i = 0; i < msgLine.Pics.length; i++) {
            this.Pics[i].setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams lp = this.Pics[i].getLayoutParams();
            if (width > 0) {
                lp.height = lp.width = width / 3;
            }
            this.Pics[i].setLayoutParams(lp);
        }

        for (int i = msgLine.Pics.length; i < 9; i++) {
            this.Pics[i].setVisibility(View.GONE);
        }
    }

    @Override
    public void UpdateInfo(MsgLine msgLine) {
        this.msgLine = msgLine;

        this.ItemTitle.setVisibility(View.VISIBLE);
        this.ItemText.setVisibility(View.VISIBLE);
        this.ItemTitle.setText(msgLine.Title);
        this.ItemAuthor.setText(msgLine.AuthorName);
        this.ItemText.setText(msgLine.SubTitle);
        if (this.ItemTitle.length() == 0) {
            this.ItemTitle.setVisibility(View.GONE);
        }
        if (this.ItemText.length() == 0) {
            this.ItemText.setVisibility(View.GONE);
        }

        this.ItemTime.setText(new PrettyTime().format(new Date(msgLine.PubTime * 1000)));
        this.ItemAuthorHead.setImageURI(Uri.parse(msgLine.AuthorCoverImg));

        this.SetUpdateNine();

        this.UpdateNineLayout();

        for (int i = 0; i < msgLine.Pics.length; i++) {
            this.Pics[i].setImageURI(Uri.parse(msgLine.Pics[i] + "-small"));
        }

        for (int i = msgLine.Pics.length; i < 9; i++) {
            this.Pics[i].setImageURI(Uri.EMPTY);
        }
    }

    static class OnPictureClickListener implements View.OnClickListener {
        int clicked_pic;
        PictureView pictureView;

        public OnPictureClickListener(int clicked_pic, PictureView pictureView) {
            this.pictureView = pictureView;
            this.clicked_pic = clicked_pic;
        }

        @Override
        public void onClick(View v) {
            if (pictureView != null) {
                Intent intent = new Intent(pictureView.activity, guider.class);
                intent.putExtra("mid", pictureView.msgLine.Id);
                intent.putExtra("clicked_pic", clicked_pic);

                pictureView.activity.startActivity(intent);
            }
        }
    }

    public static PictureView GetView(Context context, ViewGroup parent, Activity activity) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View convertView = inflater.inflate(R.layout.picture_view_item, parent, false);

        PictureView viewHolder = new PictureView(activity, convertView);

        viewHolder.ItemTitle = (TextView) convertView.findViewById(R.id.ItemTitle);
        viewHolder.ItemTime = (TextView) convertView.findViewById(R.id.ItemTime);
        viewHolder.ItemText = (TextView) convertView.findViewById(R.id.ItemText);
        viewHolder.ItemAuthorHead = (SimpleDraweeView) convertView.findViewById(R.id.ListCover);
        viewHolder.ItemAuthor = (TextView) convertView.findViewById(R.id.ItemAuthor);
        viewHolder.Body = (LinearLayout) convertView.findViewById(R.id.body);
        viewHolder.Pics[0] = (SimpleDraweeView) convertView.findViewById(R.id.PIC11);
        viewHolder.Pics[1] = (SimpleDraweeView) convertView.findViewById(R.id.PIC12);
        viewHolder.Pics[2] = (SimpleDraweeView) convertView.findViewById(R.id.PIC13);
        viewHolder.Pics[3] = (SimpleDraweeView) convertView.findViewById(R.id.PIC21);
        viewHolder.Pics[4] = (SimpleDraweeView) convertView.findViewById(R.id.PIC22);
        viewHolder.Pics[5] = (SimpleDraweeView) convertView.findViewById(R.id.PIC23);
        viewHolder.Pics[6] = (SimpleDraweeView) convertView.findViewById(R.id.PIC31);
        viewHolder.Pics[7] = (SimpleDraweeView) convertView.findViewById(R.id.PIC32);
        viewHolder.Pics[8] = (SimpleDraweeView) convertView.findViewById(R.id.PIC33);
        viewHolder.width = viewHolder.Body.getMeasuredWidth();

        viewHolder.SetUpdateNine();

        //viewHolder.ItemText.setAutoLinkMask(Linkify.WEB_URLS);
        //viewHolder.ItemText.setMovementMethod(LinkMovementMethod.getInstance());

        for (int i = 0; i < 9; i++) {
            viewHolder.Pics[i].setOnClickListener(new OnPictureClickListener(i, viewHolder));
        }

        convertView.setTag(viewHolder);

        return viewHolder;
    }

    @Override
    public int ViewType() {
        return API.VIEW_PICTURE;
    }
}
