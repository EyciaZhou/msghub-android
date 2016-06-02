package me.eycia.api

import android.os.Parcel
import android.os.Parcelable

data class MsgBase(var Id: String,
                   var Body: String, //length zero if not have
                   var SnapTime: Long,
                   var PubTime: Long,
                   var SourceURL: String,
                   var Title: String,
                   var SubTitle: String,
                   var CoverImg: String, //length zero if not have
                   var ViewType: Int,
                   var AuthorId: String,
                   var AuthorCoverImg: String,
                   var AuthorName: String,
                   var Tag: String,
                   var Topic: String, //length zero if not have
                   var PicRefs: Array<PicRef>
) : Parcelable, Comparable<MsgBase> {

    override fun compareTo(other: MsgBase): Int {
        return other.SnapTime.compareTo(SnapTime)
    }

    constructor(`in`: Parcel) : this(
            Id = `in`.readString(),
            Body = `in`.readString(),
            SnapTime = `in`.readLong(),
            PubTime = `in`.readLong(),
            SourceURL = `in`.readString(),
            Title = `in`.readString(),
            SubTitle = `in`.readString(),
            CoverImg = `in`.readString(),
            ViewType = `in`.readInt(),
            AuthorId = `in`.readString(),
            AuthorCoverImg = `in`.readString(),
            AuthorName = `in`.readString(),
            Tag = `in`.readString(),
            Topic = `in`.readString(),
            PicRefs = `in`.createTypedArray(PicRef.CREATOR)) {
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(Id)
        dest.writeString(Body)
        dest.writeLong(SnapTime)
        dest.writeLong(PubTime)
        dest.writeString(SourceURL)
        dest.writeString(Title)
        dest.writeString(SubTitle)
        dest.writeString(CoverImg)
        dest.writeInt(ViewType)
        dest.writeString(AuthorId)
        dest.writeString(AuthorCoverImg)
        dest.writeString(AuthorName)
        dest.writeString(Tag)
        dest.writeString(Topic)
        dest.writeTypedArray(PicRefs, flags)
    }

    companion object {

        @JvmField final val CREATOR: Parcelable.Creator<MsgBase> = object : Parcelable.Creator<MsgBase> {
            override fun createFromParcel(`in`: Parcel): MsgBase {
                return MsgBase(`in`)
            }

            override fun newArray(size: Int): Array<MsgBase?> {
                return arrayOfNulls(size)
            }
        }
    }
}
