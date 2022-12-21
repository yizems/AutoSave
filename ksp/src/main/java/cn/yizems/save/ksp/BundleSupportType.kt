package cn.yizems.save.ksp

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration

enum class BundleSupportType {
    INT {
        override fun checkSupport(property: KSPropertyDeclaration): Boolean {
            return property.type.resolve().declaration.qualifiedName?.asString() == "kotlin.Int"
        }
    },
    INT_ARRAY {
        override fun checkSupport(property: KSPropertyDeclaration): Boolean {
            return property.type.resolve().declaration.qualifiedName?.asString() == "kotlin.IntArray"
        }
    },
    LONG {
        override fun checkSupport(property: KSPropertyDeclaration): Boolean {
            return property.type.resolve().declaration.qualifiedName?.asString() == "kotlin.Long"
        }
    },
    LONG_ARRAY {
        override fun checkSupport(property: KSPropertyDeclaration): Boolean {
            return property.type.resolve().declaration.qualifiedName?.asString() == "kotlin.LongArray"
        }
    },
    DOUBLE {
        override fun checkSupport(property: KSPropertyDeclaration): Boolean {
            return property.type.resolve().declaration.qualifiedName?.asString() == "kotlin.Double"
        }
    },
    DOUBLE_ARRAY {
        override fun checkSupport(property: KSPropertyDeclaration): Boolean {
            return property.type.resolve().declaration.qualifiedName?.asString() == "kotlin.DoubleArray"
        }
    },
    BYTE {
        private val typeQualifiedName = Byte::class.qualifiedName
        override fun checkSupport(property: KSPropertyDeclaration): Boolean {
            // 检查 property 是否是 Byte 类型
            return property.type.resolve().declaration.qualifiedName?.asString() == typeQualifiedName
        }
    },
    CHAR {
        private val typeQualifiedName = Char::class.qualifiedName
        override fun checkSupport(property: KSPropertyDeclaration): Boolean {
            return property.type.resolve().declaration.qualifiedName?.asString() == typeQualifiedName
        }
    },
    SHORT {
        private val typeQualifiedName = Short::class.qualifiedName
        override fun checkSupport(property: KSPropertyDeclaration): Boolean {
            return property.type.resolve().declaration.qualifiedName?.asString() == typeQualifiedName
        }
    },
    FLOAT {
        private val typeQualifiedName = Float::class.qualifiedName
        override fun checkSupport(property: KSPropertyDeclaration): Boolean {
            return property.type.resolve().declaration.qualifiedName?.asString() == typeQualifiedName
        }
    },
    BOOLEAN {
        private val typeQualifiedName = Boolean::class.qualifiedName
        override fun checkSupport(property: KSPropertyDeclaration): Boolean {
            return property.type.resolve().declaration.qualifiedName?.asString() == typeQualifiedName
        }
    },
    BOOLEAN_ARRAY {
        private val typeQualifiedName = BooleanArray::class.qualifiedName
        override fun checkSupport(property: KSPropertyDeclaration): Boolean {
            return property.type.resolve().declaration.qualifiedName?.asString() == typeQualifiedName
        }
    },
    CHAR_SEQUENCE {
        private val typeQualifiedName = CharSequence::class.qualifiedName
        override fun checkSupport(property: KSPropertyDeclaration): Boolean {
            // 检查是否 CharSequence 类型 以及子类
            return property.type.resolve().declaration.qualifiedName?.asString() == typeQualifiedName
                    || (property.type.resolve().declaration as? KSClassDeclaration)?.superTypes?.any {
                it.resolve().declaration.qualifiedName?.asString() == typeQualifiedName
            } == true
        }
    },
    SIZE {
        private val typeQualifiedName = "android.util.Size"
        override fun checkSupport(property: KSPropertyDeclaration): Boolean {
            return property.type.resolve().declaration.qualifiedName?.asString() == typeQualifiedName
        }
    },
    SIZEF {
        private val typeQualifiedName = "android.util.SizeF"
        override fun checkSupport(property: KSPropertyDeclaration): Boolean {
            return property.type.resolve().declaration.qualifiedName?.asString() == typeQualifiedName
        }
    },
    BYTE_ARRAY {
        private val typeQualifiedName = "kotlin.ByteArray"
        override fun checkSupport(property: KSPropertyDeclaration): Boolean {
            // 检查是否是ByteArray 类型
            return property.type.resolve().declaration.qualifiedName?.asString() == typeQualifiedName
        }
    },
    SHORT_ARRAY {
        private val typeQualifiedName = "kotlin.ShortArray"
        override fun checkSupport(property: KSPropertyDeclaration): Boolean {
            // 检查是否是ShortArray 类型
            return property.type.resolve().declaration.qualifiedName?.asString() == typeQualifiedName
        }
    },
    CHAR_ARRAY {
        private val typeQualifiedName = "kotlin.CharArray"
        override fun checkSupport(property: KSPropertyDeclaration): Boolean {
            // 检查是否是CharArray 类型
            return property.type.resolve().declaration.qualifiedName?.asString() == typeQualifiedName
        }
    },
    FLOAT_ARRAY {
        private val typeQualifiedName = "kotlin.FloatArray"
        override fun checkSupport(property: KSPropertyDeclaration): Boolean {
            // 检查是否是FloatArray 类型
            return property.type.resolve().declaration.qualifiedName?.asString() == typeQualifiedName
        }
    },
    SPARSE_PARCELABLE_ARRAY {
        private val typeQualifiedName = "android.util.SparseArray"
        private val parcelableQualifiedName = "android.os.Parcelable"
        override fun checkSupport(property: KSPropertyDeclaration): Boolean {
            // 检查是否是SparseArray 类型
            if (property.type.resolve().declaration.qualifiedName?.asString() != typeQualifiedName) {
                return false
            }
            // 获取SparseArray 泛型参数
            val typeArgument =
                property.type.resolve().arguments.firstOrNull()?.type?.resolve()?.declaration as? KSClassDeclaration
                    ?: return false
            // 检查SparseArray 中的元素是 Parcelable的子类型
            return typeArgument.qualifiedName?.asString() == parcelableQualifiedName
                    || typeArgument.superTypes.any {
                (it as? KSClassDeclaration)?.qualifiedName?.asString() == parcelableQualifiedName
            }
        }
    },
    INTEGER_ARRAYLIST {
        private val typeQualifiedName = "kotlin.collections.ArrayList"
        private val integerQualifiedName = "kotlin.Int"
        override fun checkSupport(property: KSPropertyDeclaration): Boolean {
            // 检查是否是ArrayList 类型
            if (property.type.resolve().declaration.qualifiedName?.asString() != typeQualifiedName) {
                return false
            }
            // 获取ArrayList 泛型参数
            val typeArgument =
                property.type.resolve().arguments.firstOrNull()?.type?.resolve()?.declaration as? KSClassDeclaration
                    ?: return false
            // 检查ArrayList 中的元素是 Int 类型
            return typeArgument.qualifiedName?.asString() == integerQualifiedName
        }
    },
    STRING_ARRAYLIST {
        private val typeQualifiedName = "kotlin.collections.ArrayList"
        private val stringQualifiedName = "kotlin.String"
        override fun checkSupport(property: KSPropertyDeclaration): Boolean {
            // 检查是否是ArrayList 类型
            if (property.type.resolve().declaration.qualifiedName?.asString() != typeQualifiedName) {
                return false
            }
            // 获取ArrayList 泛型参数
            val typeArgument =
                property.type.resolve().arguments.firstOrNull()?.type?.resolve()?.declaration as? KSClassDeclaration
                    ?: return false
            // 检查ArrayList 中的元素是 String 类型
            return typeArgument.qualifiedName?.asString() == stringQualifiedName
        }
    },
    CHAR_SEQUENCE_ARRAYLIST {
        private val typeQualifiedName = "kotlin.collections.ArrayList"
        private val charSequenceQualifiedName = "kotlin.CharSequence"
        override fun checkSupport(property: KSPropertyDeclaration): Boolean {
            // 检查是否是ArrayList 类型
            if (property.type.resolve().declaration.qualifiedName?.asString() != typeQualifiedName) {
                return false
            }
            // 获取ArrayList 泛型参数
            val typeArgument =
                property.type.resolve().arguments.firstOrNull()?.type?.resolve()?.declaration as? KSClassDeclaration
                    ?: return false
            // 检查ArrayList 中的元素是 CharSequence 类型
            return typeArgument.qualifiedName?.asString() == charSequenceQualifiedName
        }
    },
    CHAR_SEQUENCE_ARRAY {
        private val typeQualifiedName = "kotlin.Array"
        private val charSequenceQualifiedName = "kotlin.CharSequence"
        override fun checkSupport(property: KSPropertyDeclaration): Boolean {
            // 检查是否是Array 类型
            if (property.type.resolve().declaration.qualifiedName?.asString() != typeQualifiedName) {
                return false
            }
            // 获取Array 泛型参数
            val typeArgument =
                property.type.resolve().arguments.firstOrNull()?.type?.resolve()?.declaration as? KSClassDeclaration
                    ?: return false
            // 检查Array 中的元素是 CharSequence 类型
            return typeArgument.qualifiedName?.asString() == charSequenceQualifiedName
        }
    },
    PARCELABLE_ARRAY {
        private val typeQualifiedName = "kotlin.Array"
        private val parcelableQualifiedName = "android.os.Parcelable"
        override fun checkSupport(property: KSPropertyDeclaration): Boolean {
            // 检查是否是数组类型
            if (property.type.resolve().declaration.qualifiedName?.asString() != typeQualifiedName) {
                return false
            }
            // 获取Array 泛型参数
            val typeArgument =
                property.type.resolve().arguments.firstOrNull()?.type?.resolve()?.declaration as? KSClassDeclaration
                    ?: return false
            // 检查数组中的元素是 Parcelable的子类型
            return typeArgument.qualifiedName?.asString() == parcelableQualifiedName
                    || typeArgument.superTypes.any {
                (it as? KSClassDeclaration)?.qualifiedName?.asString() == parcelableQualifiedName
            }
        }
    },
    PARCELABLE_ARRAYLIST {
        private val typeQualifiedName = "kotlin.collections.ArrayList"
        private val parcelableQualifiedName = "android.os.Parcelable"
        override fun checkSupport(property: KSPropertyDeclaration): Boolean {
            // 检查是否是ArrayList 类型
            if (property.type.resolve().declaration.qualifiedName?.asString() != typeQualifiedName) {
                return false
            }
            // 获取ArrayList 泛型参数
            val typeArgument =
                property.type.resolve().arguments.firstOrNull()?.type?.resolve()?.declaration as? KSClassDeclaration
                    ?: return false
            // 检查ArrayList 中的元素是 Parcelable的子类型
            return typeArgument.qualifiedName?.asString() == parcelableQualifiedName
                    || typeArgument.superTypes.any {
                (it as? KSClassDeclaration)?.qualifiedName?.asString() == parcelableQualifiedName
            }
        }
    },
    SERIALIZABLE {
        private val typeQualifiedName = "java.io.Serializable"
        override fun checkSupport(property: KSPropertyDeclaration): Boolean {
            // 检查是否是Serializable 类型
            return property.type.resolve().declaration.qualifiedName?.asString() == typeQualifiedName
                    || (property.type.resolve().declaration as KSClassDeclaration).superTypes.any {
                it.resolve().declaration.qualifiedName?.asString() == typeQualifiedName
            }
        }
    },
    PARCELABLE {
        private val typeQualifiedName = "android.os.Parcelable"
        override fun checkSupport(property: KSPropertyDeclaration): Boolean {
            // 检查是否是 Parcelable的子类型
            return property.type.resolve().declaration.qualifiedName?.asString() == typeQualifiedName
                    || (property.type.resolve().declaration as? KSClassDeclaration)?.superTypes?.any {
                it.resolve().declaration.qualifiedName?.asString() == typeQualifiedName
            } == true
        }
    },
    BUNDLE {
        private val typeQualifiedName = "android.os.Bundle"
        override fun checkSupport(property: KSPropertyDeclaration): Boolean {
            // 检查是否是Bundle 类型
            return property.type.resolve().declaration.qualifiedName?.asString() == typeQualifiedName
        }
    },
    BINDER {
        private val typeQualifiedName = "android.os.Binder"
        override fun checkSupport(property: KSPropertyDeclaration): Boolean {
            // 检查是否是Binder 类型以及子类型
            return property.type.resolve().declaration.qualifiedName?.asString() == typeQualifiedName
                    || (property.type.resolve().declaration as KSClassDeclaration).superTypes.any {
                (it as? KSClassDeclaration)?.qualifiedName?.asString() == typeQualifiedName
            }
        }
    },
    ;

    abstract fun checkSupport(property: KSPropertyDeclaration): Boolean
}
