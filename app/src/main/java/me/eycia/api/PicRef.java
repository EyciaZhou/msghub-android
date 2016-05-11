package me.eycia.api;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Pair;

/**
 * Created by eycia on 3/3/16.
 */
public class PicRef implements Parcelable{
    public String Url;
    public String Ref;
    public int px, py;
    public String Description;

    public PicRef(String description, Pair<Integer, Integer> pixes, String ref, String url) {
        Description = description;
        px = pixes.first;
        py = pixes.second;
        Ref = ref;
        Url = url;
    }

    protected PicRef(Parcel in) {
        Url = in.readString();
        Ref = in.readString();
        px = in.readInt();
        py = in.readInt();
        Description = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Url);
        dest.writeString(Ref);
        dest.writeInt(px);
        dest.writeInt(py);
        dest.writeString(Description);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PicRef> CREATOR = new Creator<PicRef>() {
        @Override
        public PicRef createFromParcel(Parcel in) {
            return new PicRef(in);
        }

        @Override
        public PicRef[] newArray(int size) {
            return new PicRef[size];
        }
    };
}
