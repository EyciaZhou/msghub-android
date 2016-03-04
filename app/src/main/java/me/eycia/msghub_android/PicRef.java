package me.eycia.msghub_android;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by eycia on 3/3/16.
 */
public class PicRef implements Parcelable{
    public String Pid;
    public String Ref;
    public String Pixes;
    public String Description;

    public PicRef(String Pid, String Ref, String Pixes, String Description) {
        this.Pid = Pid;
        this.Ref = Ref;
        this.Pixes = Pixes;
        this.Description = Description;
    }

    public static PicRef LoadFromJson(JSONObject jo) throws JSONException {
        String Ref = null;
        String Pixes = null;

        if (!jo.isNull("Ref")) {
            Ref = jo.getString("Ref");
        }

        if (!jo.isNull("Pixes")) {
            Pixes = jo.getString("Pixes");
        }

        return new PicRef(jo.getString("Pid"), Ref, Pixes, jo.getString("Description"));
    }

    public static PicRef[] LoadArrayFromJson(JSONArray ja) throws JSONException {
        PicRef[] result = new PicRef[ja.length()];
        for (int i = 0; i < ja.length(); i++) {
            result[i] = LoadFromJson(ja.getJSONObject(i));
        }
        return result;
    }

    protected PicRef(Parcel in) {
        Pid = in.readString();
        Ref = in.readString();
        Pixes = in.readString();
        Description = in.readString();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Pid);
        dest.writeString(Ref);
        dest.writeString(Pixes);
        dest.writeString(Description);
    }
}
