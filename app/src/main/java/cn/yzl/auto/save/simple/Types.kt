package cn.yzl.auto.save.simple

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class PDemo(
    var name: String = "PDemo",
    val boolean: Boolean = false
) : Parcelable

class Sdemo(
    var name: String = "",
    var age: Int = 1,
) : Serializable {

    override fun toString(): String {
        return "Sdemo(name='$name', age=$age)"
    }

}
