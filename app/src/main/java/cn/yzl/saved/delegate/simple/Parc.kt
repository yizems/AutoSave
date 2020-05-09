package cn.yzl.saved.delegate.simple

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class PDemo(
    var name: String = "PDemo",
    val boolean: Boolean = false
) : Parcelable

class Sdemo : Serializable {
    var name = "Sdemo"
    var age = 1

    override fun toString(): String {
        return "Sdemo(name='$name', age=$age)"
    }

}