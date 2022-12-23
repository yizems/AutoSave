package cn.yzl.auto.save.ksp

import cn.yzl.auto.save.base.ABSAsr
import cn.yzl.auto.save.base.AutoSaveRestore
import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.hasAnnotation
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.impl.kotlin.KSPropertyDeclarationImpl
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.addOriginatingKSFile
import com.squareup.kotlinpoet.ksp.kspDependencies
import com.squareup.kotlinpoet.ksp.writeTo

/**
 * KSP 主入口
 */
class AutoSaveRestoreProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return AutoSaveRestoreSymbolProcessor(environment)
    }
}

private class AutoSaveRestoreSymbolProcessor(
    environment: SymbolProcessorEnvironment
) : SymbolProcessor {

    companion object {
        val AUTO_SAVE_RESTORE_CLASS_NAME = AutoSaveRestore::class.qualifiedName!!
    }

    private val codeGenerator = environment.codeGenerator
    private val logger = environment.logger

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation(AUTO_SAVE_RESTORE_CLASS_NAME)

        val noHandleList = symbols.filter { !it.validate() }.toList()


        handleKotlin(symbols)

        handleJava(symbols)

        return noHandleList
    }

    /**
     * 处理kotlin 中使用使用
     * 1. 检查 kotlin 类中使用是否规范
     */
    private fun handleKotlin(symbols: Sequence<KSAnnotated>) {
//        logger.warn("--------------checkKotlin")
        // AutoSaveRestore 不能在 kotlin 的属性上使用
        symbols.filterIsInstance<KSPropertyDeclaration>()
            .forEach {
                if (it.location.isKotlin()) {
                    logger.error("AutoSaveRestore can not be used on kotlin property", it)
                    throw IllegalArgumentException("AutoSaveRestore can not be used on kotlin property")
                }
            }

        val ktClass = symbols.filterIsInstance<KSClassDeclaration>()
        // AutoSaveRestore 在类上只能用于Kotlin类
        ktClass.forEach {
            if (!it.location.isKotlin()) {
                logger.error("AutoSaveRestore can only be used on kotlin class", it)
                throw IllegalArgumentException("AutoSaveRestore can only be used on Kotlin class")
            }
        }
        ktClass.forEach { clz ->
            clz.getDeclaredProperties()  //获取声明的属性
                .map { it as KSPropertyDeclarationImpl }
                .filter { propertyDeclared -> // 是否使用了代理
                    propertyDeclared.ktDeclaration.text.contains("AutoSaveDelegates.")
                }.forEach { propertyDeclared ->
                    // 开始判定类型
                    // 如果不是Bundle支持的类型,直接报错
                    val bundleType =
                        BundleSupportType.values()
                            .firstOrNull { it.checkSupport(propertyDeclared.type) }
//                    logger.warn(
//                        "${propertyDeclared.type},bundleType:$bundleType, ${propertyDeclared.simpleName.asString()}",
//                        propertyDeclared
//                    )
                    if (bundleType == null) {
                        logger.error(
                            "AutoSaveRestore can not be used on ${propertyDeclared.simpleName.asString()}, because ${propertyDeclared.type.resolve()} is not supported by Bundle",
                            propertyDeclared
                        )
                        return
                    }
                }
        }
    }

    /**
     * 处理Java类中的使用
     */
    private fun handleJava(symbols: Sequence<KSAnnotated>) {
        val declareDatas = mutableListOf<DeclareData>()

        // 检查注解合法性
        symbols.filterNot { it is KSClassDeclaration }.forEach {
            if (it !is KSPropertyDeclaration) {
                logger.error(
                    "@AutoSaveRestore can't be applied to $it: must be a member property or field",
                    it
                )
                throw IllegalArgumentException("@AutoSaveRestore can't be applied to $it: must be a member property or field")
            }
            if (!it.isMutable) {
                logger.error(
                    "@AutoSaveRestore can't be applied to $it: must be mutable",
                    it
                )
                throw IllegalArgumentException("@AutoSaveRestore can't be applied to $it: must be mutable")
            }
            if (it.isDelegated()) {
                logger.error(
                    "@AutoSaveRestore can't be applied to $it: must not use delegate",
                    it
                )
                throw IllegalArgumentException("@AutoSaveRestore can't be applied to $it: must not use delegate")
            }
            val parent = it.parent

            if (parent !is KSClassDeclaration) {
                logger.error(
                    "@AutoSaveRestore can't be applied to $it: must be member properties",
                    it
                )
                throw IllegalArgumentException("@AutoSaveRestore can't be applied to $it: must be member properties")
            }

            if (parent.isCompanionObject) {
                logger.error(
                    "@AutoSaveRestore can't be applied to $it: must not CompanionObject",
                    it
                )
                throw IllegalArgumentException("@AutoSaveRestore can't be applied to $it: must not CompanionObject")
            }

            declareDatas.add(
                DeclareData(
                    parent, it,
                    it.type.resolve().declaration as KSClassDeclaration
                ).apply {
                    // 解析bundle类型 ,如果解析不到,则报错
                    this.resolveBundleType(logger)
                }
            )
        }

        val grouping = declareDatas.groupBy { it.parent }

        grouping.forEach {
            generateJavaASR(it.key, it.value)
        }
    }

    /**
     * 生成Java辅助代码
     */
    private fun generateJavaASR(parent: KSClassDeclaration, properties: List<DeclareData>) {

//        logger.warn("parent:${parent},size:${properties.size}", parent)
        val packageName = parent.packageName.asString()
        val hostSimpleNameStr = parent.simpleName.asString()
        val hostClassName = ClassName(packageName, hostSimpleNameStr)
        val targetSimpleName = hostSimpleNameStr + "ASR"

        // 开始生成辅助类
        val spec = FileSpec.builder(packageName, targetSimpleName)
            .addType(
                TypeSpec.objectBuilder(targetSimpleName)
                    .addOriginatingKSFile(parent.containingFile!!) // 用于增量编译
                    .superclass(ABSAsr::class)
                    .apply {
                        // 解析父类, 并查找有注解的属性
                        val initBlockBuilder = CodeBlock.builder()
                        resolveParent(parent, initBlockBuilder)
                        this.addInitializerBlock(initBlockBuilder.build())
                    }
                    .addFunction(
                        FunSpec.builder("saveToBundle")
                            .addParameter("host", Any::class)
                            .addParameter("bundle", ClassName("android.os", "Bundle"))
                            .addModifiers(KModifier.OVERRIDE)
                            .addCode("host as %T", hostClassName)
                            .apply {
                                properties.forEach {
                                    addCode("\n")
                                    addCode(it.getSaveCodeBlock(logger))
                                }
                                addCode("\n")
                                addCode("saveSuperTypeToBundleForJava(host, bundle)")
                            }
                            .build()
                    ).addFunction(
                        FunSpec.builder("readFromBundle")
                            // 忽略方法过时
                            .addAnnotation(
                                AnnotationSpec.builder(Suppress::class)
                                    .addMember("%S", "UNCHECKED_CAST")
                                    .addMember("%S", "DEPRECATION")
                                    .build()
                            )
                            .addModifiers(KModifier.OVERRIDE)
                            .addParameter("host", Any::class)
                            .addParameter("bundle", ClassName("android.os", "Bundle"))
                            .addCode("host as %T", hostClassName)
                            .apply {
                                properties.forEach {
                                    addCode("\n")
                                    addCode(it.getRestoreCodeBlock(logger))
                                }
                                addCode("\n")
                                addCode("readSuperTypeFromBundleForJava(host, bundle)")
                            }
                            .build()
                    ).build()
            )
            .build()
        spec.writeTo(codeGenerator, spec.kspDependencies(true))
    }

    /**
     * 解析父类, 并查找有注解的属性, 此类可能再依赖中
     * 这也是为何 AutoSaveRestore 需要至少保留到 BINARY 阶段的原因
     */
    private fun resolveParent(
        parent: KSClassDeclaration,
        initBlockBuilder: CodeBlock.Builder
    ) {

        parent.superTypes
            .map { it.resolve().declaration }
            .filterIsInstance<KSClassDeclaration>()
            .filter {
                val qName = it.qualifiedName?.asString() ?: return@filter false
                if (qName.startsWith("androidx.") || qName.startsWith("android.")) {
                    return@filter false
                }
                return@filter true
            }.forEach { clzz ->
                if (clzz.getDeclaredProperties()
                        .any { it.hasAnnotation(AUTO_SAVE_RESTORE_CLASS_NAME) }
                ) {
                    initBlockBuilder.addStatement(
                        "superAsrs.add(%T)",
                        ClassName(clzz.packageName.asString(), clzz.simpleName.asString() + "ASR"),
                    )
                    resolveParent(clzz, initBlockBuilder)
                }
            }
    }
}
