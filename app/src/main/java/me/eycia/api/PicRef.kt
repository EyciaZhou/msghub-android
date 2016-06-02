package me.eycia.api

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by eycia on 3/3/16.
 */
data class PicRef(var Url: String,
                  var Ref: String,
                  var px: Int = 0,
                  var py: Int = 0,
                  var Description: String) : Parcelable {

    constructor(`in`: Parcel) : this(
            Url = `in`.readString(),
            Ref = `in`.readString(),
            px = `in`.readInt(),
            py = `in`.readInt(),
            Description = `in`.readString()) {
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(Url)
        dest.writeString(Ref)
        dest.writeInt(px)
        dest.writeInt(py)
        dest.writeString(Description)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {

        @JvmField final val CREATOR: Parcelable.Creator<PicRef> = object : Parcelable.Creator<PicRef> {
            override fun createFromParcel(`in`: Parcel): PicRef {
                return PicRef(`in`)
            }

            override fun newArray(size: Int): Array<PicRef?> {
                return arrayOfNulls(size)
            }
        }
    }
}
