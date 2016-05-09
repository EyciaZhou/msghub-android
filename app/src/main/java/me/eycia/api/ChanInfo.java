package me.eycia.api;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by eycia on 2/27/16.
 */
public class ChanInfo implements Parcelable {
    public String Id;
    public String Title;
    public long LstModify;

    public ChanInfo(String id, String title, long lstModify) {
        this.Id = id;
        this.Title = title;
        this.LstModify = lstModify;
    }

    protected ChanInfo(Parcel in) {
        Id = in.readString();
        Title = in.readString();
        LstModify = in.readLong();
    }

    public static final Creator<ChanInfo> CREATOR = new Creator<ChanInfo>() {
        @Override
        public ChanInfo createFromParcel(Parcel in) {
            return new ChanInfo(in);
        }

        @Override
        public ChanInfo[] newArray(int size) {
            return new ChanInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Id);
        dest.writeString(Title);
        dest.writeLong(LstModify);
    }
}