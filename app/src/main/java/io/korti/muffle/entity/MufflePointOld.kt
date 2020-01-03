package io.korti.muffle.entity

import android.os.Parcel
import android.os.Parcelable

@Deprecated(message = "Is obsolete. Should only be used for UI currently until the view models are ready.")
data class MufflePointOld(val lat: Float, val lng: Float, val name: String, var enable: Boolean = true,
                          var active: Boolean = false) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readFloat(),
        parcel.readFloat(),
        parcel.readString().orEmpty(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte()
    )

    fun getStatus(): String {
        return when {
            enable.not() -> {
                "Disabled"
            }
            active -> {
                "Active"
            }
            else -> {
                "Not active"
            }
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeFloat(lat)
        parcel.writeFloat(lng)
        parcel.writeString(name)
        parcel.writeByte(if (enable) 1 else 0)
        parcel.writeByte(if (active) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MufflePointOld> {
        override fun createFromParcel(parcel: Parcel): MufflePointOld {
            return MufflePointOld(parcel)
        }

        override fun newArray(size: Int): Array<MufflePointOld?> {
            return arrayOfNulls(size)
        }
    }

}