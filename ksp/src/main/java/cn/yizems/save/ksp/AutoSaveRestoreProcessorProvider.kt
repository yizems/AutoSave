package cn.yizems.save.ksp

import cn.yizems.auto.save.base.AutoSaveRestore
import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.impl.kotlin.KSPropertyDeclarationImpl
import com.google.devtools.ksp.validate

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

    //    private val codeGenerator = environment.codeGenerator
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
        logger.warn("--------------checkKotlin")
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
            clz.getDeclaredProperties()
                .map { it as KSPropertyDeclarationImpl }
                .filter { propertyDeclared ->
                    propertyDeclared.ktDeclaration.text.contains("SavedDelegates.")
                }.forEach { propertyDeclared ->
                    // 开始判定类型
                    // 如果不是Bundle支持的类型,直接报错
                    val bundleType =
                        BundleSupportType.values()
                            .firstOrNull { it.checkSupport(propertyDeclared.type) }
                    logger.warn(
                        "${propertyDeclared.type},bundleType:$bundleType, ${propertyDeclared.simpleName.asString()}",
                        propertyDeclared
                    )
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
                ).apply { this.resolveBundleType(logger) }
            )
        }

//        val grouping = declareDatas.groupBy { it.parent }


//        grouping.forEach {
//            logger.warn("parent:${it.key},size:${it.value.size}", it.key)
//        }
    }

//    private fun adapterGenerator(
//        logger: KSPLogger,
//        resolver: Resolver,
//        originalType: KSDeclaration,
//    ): AdapterGenerator? {
//        val type = targetType(originalType, resolver, logger) ?: return null
//
//        val properties = mutableMapOf<String, PropertyGenerator>()
//        for (property in type.properties.values) {
//            val generator = property.generator(logger, resolver, originalType)
//            if (generator != null) {
//                properties[property.name] = generator
//            }
//        }
//
//        for ((name, parameter) in type.constructor.parameters) {
//            if (type.properties[parameter.name] == null && !parameter.hasDefault) {
//                // TODO would be nice if we could pass the parameter node directly?
//                logger.error("No property for required constructor parameter $name", originalType)
//                return null
//            }
//        }
//
//        // Sort properties so that those with constructor parameters come first.
//        val sortedProperties = properties.values.sortedBy {
//            if (it.hasConstructorParameter) {
//                it.target.parameterIndex
//            } else {
//                Integer.MAX_VALUE
//            }
//        }
//
//        return AdapterGenerator(type, sortedProperties)
//    }
}
//
///** Writes this config to a [codeGenerator]. */
//private fun ProguardConfig.writeTo(codeGenerator: CodeGenerator, originatingKSFile: KSFile) {
//    val file = codeGenerator.createNewFile(
//        dependencies = Dependencies(aggregating = false, originatingKSFile),
//        packageName = "",
//        fileName = outputFilePathWithoutExtension(targetClass.canonicalName),
//        extensionName = "pro"
//    )
//    // Don't use writeTo(file) because that tries to handle directories under the hood
//    OutputStreamWriter(file, StandardCharsets.UTF_8)
//        .use(::writeTo)
//}
