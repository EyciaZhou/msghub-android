package me.eycia.api;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by eycia on 2/28/16.
 */
public class MsgBase implements Parcelable {
    public String Id;
    public long SnapTime;
    public long PubTime;
    public String SourceURL;
    public String Title;
    public String SubTitle;
    public String CoverImg;   //length zero if not have
    public int ViewType;
    public String AuthorId;
    public String AuthorCoverImg;
    public String AuthorName;
    public String Tag;
    public String Topic;        //length zero if not have

    protected MsgBase(Parcel in) {
        Id = in.readString();
        SnapTime = in.readLong();
        PubTime = in.readLong();
        SourceURL = in.readString();
        Title = in.readString();
        SubTitle = in.readString();
        CoverImg = in.readString();
        ViewType = in.readInt();
        AuthorId = in.readString();
        AuthorCoverImg = in.readString();
        AuthorName = in.readString();
        Tag = in.readString();
        Topic = in.readString();
    }

    public static final Creator<MsgBase> CREATOR = new Creator<MsgBase>() {
        @Override
        public MsgBase createFromParcel(Parcel in) {
            return new MsgBase(in);
        }

        @Override
        public MsgBase[] newArray(int size) {
            return new MsgBase[size];
        }
    };

    public MsgBase(String authorCoverImg, String authorId, String authorName, String coverImg, String id, long pubTime, long snapTime, String sourceURL, String subTitle, String tag, String title, String topic, int viewType) {
        AuthorCoverImg = authorCoverImg;
        AuthorId = authorId;
        AuthorName = authorName;
        CoverImg = coverImg;
        Id = id;
        PubTime = pubTime;
        SnapTime = snapTime;
        SourceURL = sourceURL;
        SubTitle = subTitle;
        Tag = tag;
        Title = title;
        Topic = topic;
        ViewType = viewType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Id);
        dest.writeLong(SnapTime);
        dest.writeLong(PubTime);
        dest.writeString(SourceURL);
        dest.writeString(Title);
        dest.writeString(SubTitle);
        dest.writeString(CoverImg);
        dest.writeInt(ViewType);
        dest.writeString(AuthorId);
        dest.writeString(AuthorCoverImg);
        dest.writeString(AuthorName);
        dest.writeString(Tag);
        dest.writeString(Topic);
    }
}
