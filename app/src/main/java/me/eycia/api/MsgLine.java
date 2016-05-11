package me.eycia.api;

import android.os.Parcel;
import android.os.Parcelable;

public class MsgLine extends MsgBase implements Parcelable, Comparable<MsgLine> {
    public String[] Pics;

    public MsgLine(String authorCoverImg, String authorId, String authorName, String coverImg, String id, long pubTime, long snapTime, String sourceURL, String subTitle, String tag, String title, String topic, int viewType, String[] pics) {
        super(authorCoverImg, authorId, authorName, coverImg, id, pubTime, snapTime, sourceURL, subTitle, tag, title, topic, viewType);
        this.Pics = pics;
    }

    public MsgLine(MsgBase mb, String[] pics) {
        super(mb.AuthorCoverImg, mb.AuthorId, mb.AuthorName, mb.CoverImg, mb.Id, mb.PubTime,
                mb.SnapTime, mb.SourceURL, mb.SubTitle, mb.Tag, mb.Title, mb.Topic, mb.ViewType);
        Pics = pics;
    }

    public MsgLine(Parcel in, String[] pics) {
        super(in);
        this.Pics = pics;
    }

    protected MsgLine(Parcel in) {
        super(in);
        Pics = in.createStringArray();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeStringArray(Pics);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MsgLine> CREATOR = new Creator<MsgLine>() {
        @Override
        public MsgLine createFromParcel(Parcel in) {
            return new MsgLine(in);
        }

        @Override
        public MsgLine[] newArray(int size) {
            return new MsgLine[size];
        }
    };

    @Override
    public int compareTo(MsgLine another) {
        return Long.compare(another.SnapTime, SnapTime);
    }


}
