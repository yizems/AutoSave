package cn.yizems.save.ksp

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.symbol.FileLocation
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.Location


@OptIn(KspExperimental::class)
internal inline fun <reified T : Annotation> KSAnnotated.findAnnotationWithType(): T? {
    return getAnnotationsByType(T::class).firstOrNull()
}

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
