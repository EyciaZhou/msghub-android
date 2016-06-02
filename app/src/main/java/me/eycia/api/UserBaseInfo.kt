package me.eycia.api

import android.os.Parcel
import android.os.Parcelable

data class UserBaseInfo(var Id: String,
                        var Username: String,
                        var Email: String,
                        var Nickname: String,
                        var HeadUrl: String) : Parcelable {


    constructor(`in`: Parcel) : this(Id = `in`.readString(),
            Username = `in`.readString(),
            Email = `in`.readString(),
            Nickname = `in`.readString(),
            HeadUrl = `in`.readString()) {
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(Id)
        dest.writeString(Username)
        dest.writeString(Email)
        dest.writeString(Nickname)
        dest.writeString(HeadUrl)
    }

    companion object {

        @JvmField final val CREATOR: Parcelable.Creator<UserBaseInfo> = object : Parcelable.Creator<UserBaseInfo> {
            override fun createFromParcel(`in`: Parcel): UserBaseInfo {
                return UserBaseInfo(`in`)
            }

            override fun newArray(size: Int): Array<UserBaseInfo?> {
                return arrayOfNulls(size)
            }
        }
    }
}
