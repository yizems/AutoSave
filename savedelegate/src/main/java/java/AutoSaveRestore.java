package java;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自动保存和恢复注解
 * 如果加在类上，表示该类的所有字段都会被自动保存和恢复
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface AutoSaveRestore {

    /**
     * 字段名
     * 使用场景:  如果第一次是需要从intent中获取,那么可以使用该字段自动初始化
     * 流程为: 先尝试从 restore state中获取,如果没有,则从 intent/argument 中获取并初始化
     */
    String value() default "";

    /**
     * 是否忽略类型错误,true, 如果不是Bundle支持的类型,则忽略,不保存, false,则抛出异常
     */
    boolean ignoreInvalidType() default false;
}

