/*
* MIT License
* 
* Copyright (c) 2021 yizems
* 
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
* 
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*/

package cn.yzl.saved.delegate

import android.os.Bundle
import android.os.Parcelable
import android.util.Size
import android.util.SizeF
import java.io.Serializable
import kotlin.reflect.KProperty1
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSubtypeOf

/**
 * 将值写入到 Bundle 中, 由于值类型不同,需要调用Bundle 的各种put方法
 */
interface BundleWriter {
    /**
     * 保存属性到 bundle
     *
     * @param bundle Bundle 需要保存到的bundle
     * @param name String 属性名字
     * @param value Any? 属性值
     * @param property KProperty1<Any, *> 属性反射对象
     * @param obj Any 属性所在的类实体对象
     */
    fun saveToBundle(
        bundle: Bundle,
        name: String,
        property: KProperty1<Any, *>,
        obj: Any
    )
}

/**
 * 默认的方法适配器
 */
open class DefaultBundleWriter : BundleWriter {
    @Suppress("UNCHECKED_CAST")
    override fun saveToBundle(
        bundle: Bundle,
        name: String,
        property: KProperty1<Any, *>,
        obj: Any
    ) {

        //没有初始化的 SavedDelegateLateInit 是不能调用 get方法的,会报异常
        if (!SavedDelegateLateInit.isInitNoError(obj, property)) {
            return
        }
        val value = property.get(obj)

        when (value) {
            is Bundle -> bundle.putBundle(name, value)
            is Byte -> bundle.putByte(name, value)
            is Char -> bundle.putChar(name, value)
            is Short -> bundle.putShort(name, value)
            is Float -> bundle.putFloat(name, value)
            is Double -> bundle.putDouble(name, value)
            is Int -> bundle.putInt(name, value)
            is CharSequence -> bundle.putCharSequence(name, value)
            is Parcelable -> bundle.putParcelable(name, value)
            is Size -> bundle.putSize(name, value)
            is SizeF -> bundle.putSizeF(name, value)
            is Serializable -> bundle.putSerializable(name, value)
            is Boolean -> bundle.putBoolean(name, value)

            //list
            is ArrayList<*> -> {
//                bundle.putIntegerArrayList()
//                bundle.putParcelableArrayList()
//                bundle.putStringArrayList()
//                bundle.putCharSequenceArrayList()
                //泛型type
                val type = property.returnType.arguments[0].type
                    ?: throw IllegalArgumentException("不支持的数据类型:${name}:ArrayList<*>")
                when {
                    type.isSubtypeOf(Int::class.createType()) -> {
                        bundle.putIntegerArrayList(
                            name,
                            value as ArrayList<Int>
                        )
                    }
                    type.isSubtypeOf(Parcelable::class.createType()) -> {
                        bundle.putParcelableArrayList(
                            name,
                            value as ArrayList<Parcelable>
                        )
                    }

                    type.isSubtypeOf(String::class.createType()) -> {
                        bundle.putStringArrayList(
                            name,
                            value as ArrayList<String>
                        )
                    }

                    type.isSubtypeOf(CharSequence::class.createType()) -> {
                        bundle.putCharSequenceArrayList(
                            name,
                            value as ArrayList<CharSequence>
                        )
                    }
                    else -> {
                        throw IllegalArgumentException("不支持的数据类型:${name}:ArrayList<${type}>")
                    }
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

                val type = property.returnType.arguments[0].type
                    ?: throw IllegalArgumentException("不支持的数据类型:${name}:ArrayList<*>")
                when {
                    type.isSubtypeOf(Parcelable::class.createType()) -> {
                        bundle.putParcelableArray(
                            name,
                            value as Array<Parcelable>
                        )
                    }

                    type.isSubtypeOf(String::class.createType()) -> {
                        bundle.putStringArray(
                            name,
                            value as Array<String>
                        )
                    }
                    else -> {
                        throw IllegalArgumentException("不支持的数据类型:${name}:Array<${type}>,如果是基本数据类型,请使用 IntArray 之类的数据")
                    }
                }

            }
            null -> {
                return
            }
            else -> {
                throw IllegalArgumentException("不支持的数据类型:${name}:${property.returnType}")
            }
        }
    }
}
