package me.eycia.msghub_android;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by eycia on 2/28/16.
 */
public class MsgInfo implements Parcelable, Comparable<MsgInfo> {
    public String Id;
    public long SnapTime;
    public long PubTime;
    public String SourceURL;
    public String Title;
    public String SubTitle;
    public String CoverImgId;   //length zero if not have
    public int ViewType;
    public String Frm;
    public String Tag;
    public String Topic;        //length zero if not have

    public MsgInfo(String Id, long SnapTime, long PubTime, String SourceURL, String Title,
                    String SubTitle, String CoverImgId, int ViewType, String Frm, String Tag, String Topic) {
        this.Id = Id;
        this.SnapTime = SnapTime;
        this.PubTime = PubTime;
        this.SourceURL = SourceURL;
        this.Title = Title;
        this.SubTitle = SubTitle;
        this.CoverImgId = CoverImgId;
        this.ViewType = ViewType;
        this.Frm = Frm;
        this.Tag = Tag;
        this.Topic = Topic;
    }

    public MsgInfo() {
    }

    protected MsgInfo(Parcel in) {
        Id = in.readString();
        SnapTime = in.readLong();
        PubTime = in.readLong();
        SourceURL = in.readString();
        Title = in.readString();
        SubTitle = in.readString();
        CoverImgId = in.readString();
        ViewType = in.readInt();
        Frm = in.readString();
        Tag = in.readString();
        Topic = in.readString();
    }

    public static final Creator<MsgInfo> CREATOR = new Creator<MsgInfo>() {
        @Override
        public MsgInfo createFromParcel(Parcel in) {
            return new MsgInfo(in);
        }

        @Override
        public MsgInfo[] newArray(int size) {
            return new MsgInfo[size];
        }
    };

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
        dest.writeString(CoverImgId);
        dest.writeInt(ViewType);
        dest.writeString(Frm);
        dest.writeString(Tag);
        dest.writeString(Topic);
    }

    @Override
    public int compareTo(MsgInfo another) {
        return Long.compare(another.SnapTime, SnapTime);
    }
}
