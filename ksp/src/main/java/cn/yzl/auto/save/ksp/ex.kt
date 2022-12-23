package cn.yzl.auto.save.ksp

import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.ClassName


//@OptIn(KspExperimental::class)
//internal inline fun <reified T : Annotation> KSAnnotated.findAnnotationWithType(): T? {
//    return getAnnotationsByType(T::class).firstOrNull()
//}

val BundleWriterClass = ClassName("cn.yzl.auto.save", "BundleWriter")

val BundleClass = ClassName("android.os", "Bundle")


fun Location.isJava(): Boolean {
    if (this is FileLocation) {
        return this.filePath.endsWith(".java")
    }
    return false
}

fun Location.isKotlin(): Boolean {
    if (this is FileLocation) {
        return this.filePath.endsWith(".kt")
    }
    return false
}

/**
 * 是否是具体的某个类
 */
fun KSDeclaration.isClassWithName(qualifiedName: String): Boolean {
    // 类
    if (this is KSClassDeclaration && this.qualifiedName?.asString() == qualifiedName) {
        return true
    }
    // 别名解析
    if (this is KSTypeAlias
        && this.qualifiedName?.asString() == qualifiedName
        && this.type.isClassWithName(qualifiedName)
    ) {
        return true
    }
    return false
}

/**
 * 是否是具体的某个类
 */
fun KSTypeReference.isClassWithName(qualifiedName: String): Boolean {
    return this.resolve().declaration.isClassWithName(qualifiedName)
}

/**
 * 是否是某个类或者是它的子类
 */
fun KSDeclaration.isClassOrSub(qualifiedName: String): Boolean {
    if (this.isClassWithName(qualifiedName)) {
        return true
    }
    val superTypes = when (this) {
        is KSClassDeclaration -> this.superTypes
        is KSTypeAlias -> {
            val declare = this.type.resolve().declaration
            if (declare is KSClassDeclaration) {
                declare.superTypes
            } else {
                return false
            }
        }
        else -> return false
    }
    return superTypes.firstOrNull {
        it.isClassWithName(qualifiedName)
    } != null
}

/**
 * 是否是具体的某个类
 */
fun KSTypeReference.isClassOrSub(qualifiedName: String): Boolean {
    return this.resolve().declaration.isClassOrSub(qualifiedName)
}
