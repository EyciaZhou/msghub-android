package me.eycia.api

import android.os.Parcel
import android.os.Parcelable

data class ChanInfo(var Id: String, var Title: String, var LstModify: Long) : Parcelable {

    constructor(`in`: Parcel) : this(Id = `in`.readString(), Title = `in`.readString(), LstModify = `in`.readLong()) {
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(Id)
        dest.writeString(Title)
        dest.writeLong(LstModify)
    }

    companion object {

        @JvmField final val CREATOR: Parcelable.Creator<ChanInfo> = object : Parcelable.Creator<ChanInfo> {
            override fun createFromParcel(`in`: Parcel): ChanInfo {
                return ChanInfo(`in`)
            }

            override fun newArray(size: Int): Array<ChanInfo?> {
                return arrayOfNulls(size)
            }
        }
    }
}