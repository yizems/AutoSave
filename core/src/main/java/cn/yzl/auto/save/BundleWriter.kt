package cn.yzl.auto.save

import android.os.Bundle
import android.os.Parcelable
import android.util.Size
import android.util.SizeF
import java.io.Serializable


/**
 * 将值写入到 Bundle 中, 由于值类型不同,需要调用Bundle 的各种put方法
 */
object BundleWriter {

    @Suppress("UNCHECKED_CAST")
    fun saveToBundle(
        bundle: Bundle,
        name: String,
        value: Any?
    ) {
        when (value) {
            is Bundle -> bundle.putBundle(name, value)
            is Byte -> bundle.putByte(name, value)
            is Char -> bundle.putChar(name, value)
            is Short -> bundle.putShort(name, value)
            is Float -> bundle.putFloat(name, value)
            is Double -> bundle.putDouble(name, value)
            is Int -> bundle.putInt(name, value)
            is CharSequence -> bundle.putCharSequence(name, value)
            is Size -> bundle.putSize(name, value)
            is SizeF -> bundle.putSizeF(name, value)
            is Boolean -> bundle.putBoolean(name, value)

            //list
            is ArrayList<*> -> {
                val first = value.firstOrNull()
                when (first) {
                    is Int -> bundle.putIntegerArrayList(name, value as ArrayList<Int>)
                    is String -> bundle.putStringArrayList(name, value as ArrayList<String>)
                    is CharSequence -> bundle.putCharSequenceArrayList(
                        name,
                        value as ArrayList<CharSequence>
                    )
                    is Parcelable -> bundle.putParcelableArrayList(
                        name,
                        value as ArrayList<Parcelable>
                    )
                    null -> bundle.putIntegerArrayList(name, arrayListOf())
                    else -> throw IllegalArgumentException("不支持的类型:::${first.javaClass.name}")
                }
            }

            //array
            is IntArray -> {
                bundle.putIntArray(name, value)
            }
            is LongArray -> {
                bundle.putLongArray(name, value)
            }
            is ByteArray -> {
                bundle.putByteArray(name, value)
            }
            is FloatArray -> {
                bundle.putFloatArray(name, value)
            }
            is DoubleArray -> {
                bundle.putDoubleArray(name, value)
            }
            is CharArray -> {
                bundle.putCharArray(name, value)
            }
            is ShortArray -> {
                bundle.putShortArray(name, value)
            }
            is BooleanArray -> {
                bundle.putBooleanArray(name, value)
            }

            is Array<*> -> {
//                bundle.putParcelableArray()
//                bundle.putStringArray()
                if (value.isEmpty()) {
                    bundle.putStringArray(name, arrayOf())
                } else {
                    val first = value.firstNotNullOf { it }
                    when (first) {
                        is String -> bundle.putStringArray(name, value as Array<String>)
                        is Parcelable -> bundle.putParcelableArray(name, value as Array<Parcelable>)
                        else -> throw IllegalArgumentException("不支持的类型:::${first.javaClass.name}")
                    }
                }
            }
            is Parcelable -> bundle.putParcelable(name, value)
            is Serializable -> bundle.putSerializable(name, value)
            null -> {
                return
            }
            else -> {
                throw IllegalArgumentException("不支持的数据类型:${name}:${value.javaClass.name}")
            }
        }
    }
}
