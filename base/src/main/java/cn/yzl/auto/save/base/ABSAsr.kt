package cn.yzl.auto.save.base

import android.os.Bundle

/**
 * 辅助类的父类,针对Java文件
 */
abstract class ABSAsr {

    /**
     * 父类的辅助类集合
     */
    protected val superAsrs = mutableListOf<ABSAsr>()

//    如果父类有相关辅助类会在实现类中生成如下代码
//    init {
//        superAsrs.add("XXXASR")
//    }

    /**
     * 保存数据, 通过 KSP 生成代码实现
     */
    abstract fun saveToBundle(host: Any, bundle: Bundle)

    /**
     * 读取数据,通过 KSP 生成代码实现
     */
    abstract fun readFromBundle(host: Any, bundle: Bundle)

    /**
     * 反射获取数据, 因为生成的辅助类是访问不了 private/protect 的属性的
     */
    protected fun reflectGetHostValue(host: Any, fieldName: String): Any? {
        return host.javaClass.getDeclaredField(fieldName)
            .apply {
                isAccessible = true
            }.get(host)
    }

    /**
     * 反射设置数据, 因为生成的辅助类是访问不了 private/protect 的属性的
     */
    protected fun reflectSetValue(host: Any, fieldName: String, value: Any?) {
        host.javaClass.getDeclaredField(fieldName)
            .apply {
                isAccessible = true
            }.set(host, value)
    }

    /**
     * 调用父类的 ASR 保存数据
     */
    protected fun saveSuperTypeToBundleForJava(host: Any, bundle: Bundle) {
        superAsrs.forEach {
            it.saveToBundle(host, bundle)
        }
    }

    /**
     * 调用父类的 ASR 读取数据
     */
    protected fun readSuperTypeFromBundleForJava(host: Any, bundle: Bundle) {
        superAsrs.forEach {
            it.readFromBundle(host, bundle)
        }
    }
}
