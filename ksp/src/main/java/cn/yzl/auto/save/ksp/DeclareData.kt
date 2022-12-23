package cn.yzl.auto.save.ksp

import com.google.devtools.ksp.isPrivate
import com.google.devtools.ksp.isProtected
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.ksp.toTypeName

/**
 * 解析出来的属性信息
 */
class DeclareData(
    val parent: KSClassDeclaration,
    private val property: KSPropertyDeclaration,
    val type: KSClassDeclaration,
) {
    private lateinit var bundleType: BundleSupportType

    val isJava by lazy {
        parent.location.isJava()
    }

    val propertySimpleName by lazy {
        property.simpleName.asString()
    }

    /**
     * 解析bundle类型 ,如果解析不到,则报错
     */
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

    /**
     * 生成保存数据的代码
     */
    fun getSaveCodeBlock(logger: KSPLogger): CodeBlock {
        if (property.isPrivate() || property.isProtected()) {
            return CodeBlock.of(
                """%T.saveToBundle(bundle, "$propertySimpleName", reflectGetHostValue(host, "$propertySimpleName"))""".trimIndent(),
                BundleWriterClass
            )
        }
        return CodeBlock.of(
            """%T.saveToBundle(bundle, "$propertySimpleName", host.${propertySimpleName})""",
            BundleWriterClass
        )
    }

    /**
     * 生成恢复数据的代码
     */
    fun getRestoreCodeBlock(logger: KSPLogger): CodeBlock {
        // 反射调用
        if (property.isPrivate() || property.isProtected()) {
            return CodeBlock.of("""reflectSetValue(host,"$propertySimpleName",bundle.get("$propertySimpleName"))""")
        }

        val codeBuilder = CodeBlock.builder()
            .addStatement("""val $propertySimpleName = bundle.get("$propertySimpleName")""")

        if (property.type.resolve().isMarkedNullable) {
            codeBuilder.addStatement(
                """host.${propertySimpleName} = $propertySimpleName as? %T""",
                property.type.toTypeName()
            )
            return codeBuilder.build()
        }
        codeBuilder.add(
            """
            |if ($propertySimpleName != null) {
            |  host.${propertySimpleName} = $propertySimpleName as %T
            |}
            |""".trimMargin(),
            property.type.toTypeName()
        )
        return codeBuilder.build()
    }
}
