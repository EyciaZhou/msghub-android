package me.eycia.api;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by eycia on 3/3/16.
 */
public class Msg extends MsgBase implements Parcelable {
    public String Body;
    public PicRef[] PicRefs;

    protected Msg(Parcel in) {
        super(in);
        Body = in.readString();
        PicRefs = in.createTypedArray(PicRef.CREATOR);
    }

    public Msg(String authorCoverImg, String authorId, String authorName, String coverImg, String id, long pubTime, long snapTime, String sourceURL, String subTitle, String tag, String title, String topic, int viewType, String body, PicRef[] picRefs) {
        super(authorCoverImg, authorId, authorName, coverImg, id, pubTime, snapTime, sourceURL, subTitle, tag, title, topic, viewType);
        Body = body;
        PicRefs = picRefs;
    }

    public Msg(MsgBase mb, String body, PicRef[] picRefs) {
        super(mb.AuthorCoverImg, mb.AuthorId, mb.AuthorName, mb.CoverImg, mb.Id, mb.PubTime,
                mb.SnapTime, mb.SourceURL, mb.SubTitle, mb.Tag, mb.Title, mb.Topic, mb.ViewType);
        Body = body;
        PicRefs = picRefs;
    }

    public Msg(Parcel in, String body, PicRef[] picRefs) {
        super(in);
        Body = body;
        PicRefs = picRefs;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(Body);
        dest.writeTypedArray(PicRefs, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Msg> CREATOR = new Creator<Msg>() {
        @Override
        public Msg createFromParcel(Parcel in) {
            return new Msg(in);
        }

        @Override
        public Msg[] newArray(int size) {
            return new Msg[size];
        }
    };
}
