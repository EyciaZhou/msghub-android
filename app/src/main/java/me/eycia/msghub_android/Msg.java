package me.eycia.msghub_android;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by eycia on 3/3/16.
 */
public class Msg extends MsgInfo implements Parcelable {
    public String Body;

    public Msg(String Id, long SnapTime, long PubTime, String SourceURL, String Title, String SubTitle, String CoverImgId, int ViewType, String Frm, String Tag, String Topic, String Body) {
        super(Id, SnapTime, PubTime, SourceURL, Title, SubTitle, CoverImgId, ViewType, Frm, Tag, Topic);
        this.Body = Body;
    }

    public Msg() {
        super();
    }

    /*
    public MsgInfo(String Id, long SnapTime, long PubTime, String SourceURL, String Title,
                    String SubTitle, String CoverImgId, int ViewType, String Frm, String Tag, String Topic) {
     */

    public Msg(MsgInfo msgInfo, String body) {
        super(msgInfo.Id, msgInfo.SnapTime, msgInfo.PubTime, msgInfo.SourceURL, msgInfo.Title,
                msgInfo.SubTitle, msgInfo.CoverImgId, msgInfo.ViewType, msgInfo.Frm, msgInfo.Tag, msgInfo.Topic);
        this.Body = body;
    }

    protected Msg(Parcel in) {
        super(in);
        Body = in.readString();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(Body);
    }
}
