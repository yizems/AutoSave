package cn.yizems.save.ksp

import cn.yizems.auto.save.base.AutoSaveRestore
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
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

    private val codeGenerator = environment.codeGenerator
    private val logger = environment.logger

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation(AUTO_SAVE_RESTORE_CLASS_NAME)

        val noHandleList = symbols.filter { !it.validate() }.toList()

        val declareDatas = mutableListOf<DeclareData>()


        // 检查注解合法性
        symbols.forEach {
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

//            logger.warn("location:${it.location}", it)
//            logger.warn("type:${it.type.resolve().declaration}", it)
//            logger.warn("parent:${parent}", it)

            declareDatas.add(
                DeclareData(
                    parent, it,
                    it.type.resolve().declaration as KSClassDeclaration
                ).apply { this.resolveBundleType(logger) }
            )
        }

        val grouping = declareDatas.groupBy { it.parent }

        grouping.forEach {
            logger.warn("parent:${it.key},size:${it.value.size}", it.key)
            // 生成代码


        }


//        for (type in resolver.getSymbolsWithAnnotation(AUTO_SAVE_RESTORE_CLASS_NAME)) {
//            // For the smart cast
//            if (type !is KSPropertyDeclaration) {
//                logger.error(
//                    "@AutoSaveRestore can't be applied to $type: must be a member property or field",
//                    type
//                )
//                continue
//            }
//
//            val originatingFile = type.containingFile!!
//            val adapterGenerator = adapterGenerator(logger, resolver, type) ?: return emptyList()
//            try {
//                val preparedAdapter = adapterGenerator
//                    .prepare(generateProguardRules) { spec ->
//                        spec.toBuilder()
//                            .apply {
//                                generatedAnnotation?.let(::addAnnotation)
//                            }
//                            .addOriginatingKSFile(originatingFile)
//                            .build()
//                    }
//                preparedAdapter.spec.writeTo(codeGenerator, aggregating = false)
//                preparedAdapter.proguardConfig?.writeTo(codeGenerator, originatingFile)
//            } catch (e: Exception) {
//                logger.error(
//                    "Error preparing ${type.simpleName.asString()}: ${e.stackTrace.joinToString("\n")}"
//                )
//            }
//        }
        return noHandleList
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
