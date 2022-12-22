package cn.yizems.save.ksp

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration

class DeclareData(
    val parent: KSClassDeclaration,
    val property: KSPropertyDeclaration,
    val type: KSClassDeclaration,
) {
    private lateinit var bundleType: BundleSupportType

    val isJava by lazy {
        parent.location.isJava()
    }

    fun resolveBundleType(logger: KSPLogger) {
        bundleType =
            BundleSupportType.values()
                .firstOrNull {
                    it.checkSupport(property.type)
                }.let {
                    if (it == null) {
                        logger.error(
                            "not support type ${property}", property
                        )
                        throw IllegalArgumentException("not support type ${property}")
                    } else {
                        it
                    }
                }

    }

    fun getBundleType(): BundleSupportType {
        return bundleType
    }
}
