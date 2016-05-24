package me.eycia.api;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * Created by eycia on 16/5/23.
 */
public class UserBaseInfo implements Parcelable {
    public String Id;
    public String Username;
    public String Email;
    public String Nickname;

    public void Print() {
        Log.d("UserBaseInfo", "email:" + Email);
        Log.d("UserBaseInfo", "id:" + Id);
        Log.d("UserBaseInfo", "nickname:" + Nickname);
        Log.d("UserBaseInfo", "username:" + Username);
    }

    protected UserBaseInfo(Parcel in) {
        Id = in.readString();
        Username = in.readString();
        Email = in.readString();
        Nickname = in.readString();
    }

    public static final Creator<UserBaseInfo> CREATOR = new Creator<UserBaseInfo>() {
        @Override
        public UserBaseInfo createFromParcel(Parcel in) {
            return new UserBaseInfo(in);
        }

        @Override
        public UserBaseInfo[] newArray(int size) {
            return new UserBaseInfo[size];
        }
    };

    public UserBaseInfo(String email, String id, String nickname, String username) {
        Email = email;
        Id = id;
        Nickname = nickname;
        Username = username;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Id);
        dest.writeString(Username);
        dest.writeString(Email);
        dest.writeString(Nickname);
    }
}
