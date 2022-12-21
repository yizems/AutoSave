package cn.yizems.save.ksp

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.symbol.KSAnnotated


@OptIn(KspExperimental::class)
internal inline fun <reified T : Annotation> KSAnnotated.findAnnotationWithType(): T? {
    return getAnnotationsByType(T::class).firstOrNull()
}
