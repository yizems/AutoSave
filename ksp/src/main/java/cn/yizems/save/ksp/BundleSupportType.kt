package cn.yizems.save.ksp

import com.google.devtools.ksp.symbol.KSTypeReference

enum class BundleSupportType {
    INT {
        override fun checkSupport(ksTypeReference: KSTypeReference): Boolean {
            return ksTypeReference.isClassWithName("kotlin.Int")
        }
    },
    INT_ARRAY {
        override fun checkSupport(ksTypeReference: KSTypeReference): Boolean {
            return ksTypeReference.isClassWithName("kotlin.IntArray")
        }
    },
    LONG {
        override fun checkSupport(ksTypeReference: KSTypeReference): Boolean {
            return ksTypeReference.isClassWithName("kotlin.Long")
        }
    },
    LONG_ARRAY {
        override fun checkSupport(ksTypeReference: KSTypeReference): Boolean {
            return ksTypeReference.isClassWithName("kotlin.LongArray")
        }
    },
    DOUBLE {
        override fun checkSupport(ksTypeReference: KSTypeReference): Boolean {
            return ksTypeReference.isClassWithName("kotlin.Double")
        }
    },
    DOUBLE_ARRAY {
        override fun checkSupport(ksTypeReference: KSTypeReference): Boolean {
            return ksTypeReference.isClassWithName("kotlin.DoubleArray")
        }
    },
    BYTE {
        private val typeQualifiedName = Byte::class.qualifiedName!!
        override fun checkSupport(ksTypeReference: KSTypeReference): Boolean {
            // 检查 property 是否是 Byte 类型
            return ksTypeReference.isClassWithName(typeQualifiedName)
        }
    },
    CHAR {
        private val typeQualifiedName = Char::class.qualifiedName!!
        override fun checkSupport(ksTypeReference: KSTypeReference): Boolean {
            return ksTypeReference.isClassWithName(typeQualifiedName)
        }
    },
    SHORT {
        private val typeQualifiedName = Short::class.qualifiedName!!
        override fun checkSupport(ksTypeReference: KSTypeReference): Boolean {
            return ksTypeReference.isClassWithName(typeQualifiedName)
        }
    },
    FLOAT {
        private val typeQualifiedName = Float::class.qualifiedName!!
        override fun checkSupport(ksTypeReference: KSTypeReference): Boolean {
            return ksTypeReference.isClassWithName(typeQualifiedName)
        }
    },
    BOOLEAN {
        private val typeQualifiedName = Boolean::class.qualifiedName!!
        override fun checkSupport(ksTypeReference: KSTypeReference): Boolean {
            return ksTypeReference.isClassWithName(typeQualifiedName)
        }
    },
    BOOLEAN_ARRAY {
        private val typeQualifiedName = BooleanArray::class.qualifiedName!!
        override fun checkSupport(ksTypeReference: KSTypeReference): Boolean {
            return ksTypeReference.isClassWithName(typeQualifiedName)
        }
    },
    CHAR_SEQUENCE {
        private val typeQualifiedName = CharSequence::class.qualifiedName!!
        override fun checkSupport(ksTypeReference: KSTypeReference): Boolean {
            // 检查是否 CharSequence 类型 以及子类
            return ksTypeReference.isClassOrSub(typeQualifiedName)
        }
    },
    SIZE {
        private val typeQualifiedName = "android.util.Size"
        override fun checkSupport(ksTypeReference: KSTypeReference): Boolean {
            return ksTypeReference.isClassWithName(typeQualifiedName)
        }
    },
    SIZEF {
        private val typeQualifiedName = "android.util.SizeF"
        override fun checkSupport(ksTypeReference: KSTypeReference): Boolean {
            return ksTypeReference.isClassWithName(typeQualifiedName)
        }
    },
    BYTE_ARRAY {
        private val typeQualifiedName = "kotlin.ByteArray"
        override fun checkSupport(ksTypeReference: KSTypeReference): Boolean {
            // 检查是否是ByteArray 类型
            return ksTypeReference.isClassWithName(typeQualifiedName)
        }
    },
    SHORT_ARRAY {
        private val typeQualifiedName = "kotlin.ShortArray"
        override fun checkSupport(ksTypeReference: KSTypeReference): Boolean {
            // 检查是否是ShortArray 类型
            return ksTypeReference.isClassWithName(typeQualifiedName)
        }
    },
    CHAR_ARRAY {
        private val typeQualifiedName = "kotlin.CharArray"
        override fun checkSupport(ksTypeReference: KSTypeReference): Boolean {
            // 检查是否是CharArray 类型
            return ksTypeReference.isClassWithName(typeQualifiedName)
        }
    },
    FLOAT_ARRAY {
        private val typeQualifiedName = "kotlin.FloatArray"
        override fun checkSupport(ksTypeReference: KSTypeReference): Boolean {
            // 检查是否是FloatArray 类型
            return ksTypeReference.isClassWithName(typeQualifiedName)
        }
    },
    SPARSE_PARCELABLE_ARRAY {
        private val typeQualifiedName = "android.util.SparseArray"
        private val parcelableQualifiedName = "android.os.Parcelable"
        override fun checkSupport(ksTypeReference: KSTypeReference): Boolean {
            // 检查是否是SparseArray 类型
            if (!ksTypeReference.isClassWithName(typeQualifiedName)) {
                return false
            }
            // 获取SparseArray 泛型参数
            return ksTypeReference.resolve().arguments
                .firstOrNull()?.type?.isClassOrSub(parcelableQualifiedName) ?: false
        }
    },
    INTEGER_ARRAYLIST {
        private val typeQualifiedName = "kotlin.collections.ArrayList"
        private val integerQualifiedName = "kotlin.Int"
        override fun checkSupport(ksTypeReference: KSTypeReference): Boolean {
            // 检查是否是ArrayList 类型
            if (!ksTypeReference.isClassWithName(typeQualifiedName)) {
                return false
            }
            // 获取ArrayList 泛型参数
            return ksTypeReference.resolve().arguments
                .firstOrNull()?.type?.isClassWithName(integerQualifiedName)
                ?: false
        }
    },
    STRING_ARRAYLIST {
        private val typeQualifiedName = "kotlin.collections.ArrayList"
        private val stringQualifiedName = "kotlin.String"
        override fun checkSupport(ksTypeReference: KSTypeReference): Boolean {
            // 检查是否是ArrayList 类型
            if (!ksTypeReference.isClassWithName(typeQualifiedName)) {
                return false
            }
            // 获取ArrayList 泛型参数
            return ksTypeReference.resolve().arguments
                .firstOrNull()?.type?.isClassWithName(stringQualifiedName)
                ?: false
        }
    },
    CHAR_SEQUENCE_ARRAYLIST {
        private val typeQualifiedName = "kotlin.collections.ArrayList"
        private val charSequenceQualifiedName = "kotlin.CharSequence"
        override fun checkSupport(ksTypeReference: KSTypeReference): Boolean {
            // 检查是否是ArrayList 类型
            if (!ksTypeReference.isClassWithName(typeQualifiedName)) {
                return false
            }
            // 获取ArrayList 泛型参数
            return ksTypeReference.resolve().arguments
                .firstOrNull()?.type
                ?.isClassOrSub(charSequenceQualifiedName)
                ?: false
        }
    },
    CHAR_SEQUENCE_ARRAY {
        private val typeQualifiedName = "kotlin.Array"
        private val charSequenceQualifiedName = "kotlin.CharSequence"
        override fun checkSupport(ksTypeReference: KSTypeReference): Boolean {
            // 检查是否是Array 类型
            if (!ksTypeReference.isClassWithName(typeQualifiedName)) {
                return false
            }
            // 获取Array 泛型参数
            return ksTypeReference.resolve().arguments
                .firstOrNull()?.type
                ?.isClassOrSub(charSequenceQualifiedName)
                ?: false
        }
    },
    PARCELABLE_ARRAY {
        private val typeQualifiedName = "kotlin.Array"
        private val parcelableQualifiedName = "android.os.Parcelable"
        override fun checkSupport(ksTypeReference: KSTypeReference): Boolean {
            // 检查是否是数组类型
            if (!ksTypeReference.isClassWithName(typeQualifiedName)) {
                return false
            }
            // 获取Array 泛型参数
            return ksTypeReference.resolve().arguments
                .firstOrNull()?.type
                ?.isClassOrSub(parcelableQualifiedName)
                ?: false
        }
    },
    PARCELABLE_ARRAYLIST {
        private val typeQualifiedName = "kotlin.collections.ArrayList"
        private val parcelableQualifiedName = "android.os.Parcelable"
        override fun checkSupport(ksTypeReference: KSTypeReference): Boolean {
            // 检查是否是ArrayList 类型
            if (!ksTypeReference.isClassWithName(typeQualifiedName)) {
                return false
            }
            // 获取ArrayList 泛型参数
            return ksTypeReference.resolve().arguments.firstOrNull()?.type
                ?.isClassOrSub(parcelableQualifiedName)
                ?: false
        }
    },
    BUNDLE {
        private val typeQualifiedName = "android.os.Bundle"
        override fun checkSupport(ksTypeReference: KSTypeReference): Boolean {
            // 检查是否是Bundle 类型
            return ksTypeReference.isClassWithName(typeQualifiedName)
        }
    },
    BINDER {
        private val typeQualifiedName = "android.os.Binder"
        override fun checkSupport(ksTypeReference: KSTypeReference): Boolean {
            // 检查是否是Binder 类型以及子类型
            return ksTypeReference.isClassOrSub(typeQualifiedName)
        }
    },
    SERIALIZABLE {
        private val typeQualifiedName = "java.io.Serializable"

        override fun checkSupport(ksTypeReference: KSTypeReference): Boolean {

            // 非 Serializable 类型
            if (!ksTypeReference.isClassOrSub(typeQualifiedName)) {
                return false
            }
            // 解析泛型是否是Bundle支持的类型
            ksTypeReference.resolve().arguments.forEach { type ->
                // 如果出现不支持的, 直接返回 false
                if (!values()
                        .any { it.checkSupport(type.type ?: return@any false) }
                ) {
                    return false
                }
            }
            return true
        }
    },
    PARCELABLE {
        private val typeQualifiedName = "android.os.Parcelable"
        override fun checkSupport(ksTypeReference: KSTypeReference): Boolean {
            // 检查是否是 Parcelable的子类型
            if (!ksTypeReference.isClassOrSub(typeQualifiedName)) {
                return false
            }
            ksTypeReference.resolve().arguments.forEach { type ->
                if (!values().any {
                        it.checkSupport(type.type ?: return@any false)
                    }) {
                    return false
                }
            }
            return true
        }
    },
    ;

    abstract fun checkSupport(ksTypeReference: KSTypeReference): Boolean
}
