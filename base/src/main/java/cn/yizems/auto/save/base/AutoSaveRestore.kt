package cn.yizems.auto.save.base

/**
 * 自动保存和恢复注解
 * 如果加在类上，表示该类的所有字段都会被自动保存和恢复
 */
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD, AnnotationTarget.CLASS)
annotation class AutoSaveRestore(
    /**
     * 字段名
     * 使用场景:  如果第一次是需要从intent中获取,那么可以使用该字段自动初始化
     * 流程为: 先尝试从 restore state中获取,如果没有,则从 intent/argument 中获取并初始化
     */
    val value: String = "",
)
