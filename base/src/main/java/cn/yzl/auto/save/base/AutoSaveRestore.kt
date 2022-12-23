package cn.yzl.auto.save.base

/**
 * 自动保存和恢复注解
 * 1. kotlin 文件使用再类上, 只为了标记让KSP能够扫描到, 并且做安全的类型检查
 * 2. Java文件在属性上使用, 用于生成代码,并且做安全的类型检查
 */
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD, AnnotationTarget.CLASS)
annotation class AutoSaveRestore(
    /**
     * 字段名
     * 使用场景:  如果第一次是需要从intent中获取,那么可以使用该字段自动初始化
     * 流程为: 先尝试从 restore state中获取,如果没有,则从 intent/argument 中获取并初始化
     */
//    val value: String = "",
)
