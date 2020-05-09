package cn.yzl.saved.delegate

import androidx.savedstate.SavedStateRegistryOwner
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.isAccessible

/**
 * 抽象的 SavedDelegate
 * @property v 保存的数据
 * @property savedStateRegistryOwner SavedStateRegistryOwner
 * @constructor
 */
@Suppress("UNCHECKED_CAST")
abstract class AbsSavedDelegate<T> {

    protected open var v: T? = null

    /**
     * 从 回复数据中获取该值,如果获取不到,返回 v
     * @param owner 属性所在对象
     * @param name String 属性名字
     * @return T?
     */
    @Throws(IllegalArgumentException::class)
    protected open fun readValueFromBundle(
        owner: Any?,
        name: String
    ) {
        if (owner !is SavedStateRegistryOwner) {
            throw IllegalArgumentException("宿主不是[SavedStateRegistryOwner]的实现类:::${if (owner == null) "NULL" else owner::class.qualifiedName}")
        }
        val bundle = SavedDelegateHelper.getOrCreateSavedProvider(owner)
            .getSavedBundle(owner.savedStateRegistry) ?: return

        if (!bundle.containsKey(name)) {
            return
        }

        val temp = bundle.get(name)
        bundle.remove(name)

        v = if (temp == null) {
            null
        } else {
            temp as T
        }
    }
}


/**
 * 默认实现,可为Null
 * @param T
 * @property init Function0<T>? 初始化
 * @constructor
 */
@Suppress("UNCHECKED_CAST")
open class SavedDelegateNullable<T>(
    val init: (() -> T)? = null
) : AbsSavedDelegate<T>() {

    /** 是否已经初始化 */
    private var isInit = false

    open operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        this.v = value
    }

    open operator fun getValue(thisRef: Any?, property: KProperty<*>): T? {
        if (!isInit) {
            initV()
        }
        readValueFromBundle(thisRef, property.name)
        return v
    }

    private fun initV() {
        v = init?.invoke()
        isInit = true
    }
}

/**
 * 不为Null的属性
 *
 * @param T
 * @property init Function0<T>
 * @constructor
 */
@Suppress("UNCHECKED_CAST")
open class SavedDelegateNotNull<T>(
    val init: (() -> T)
) : AbsSavedDelegate<T>() {

    private var isInit = false

    open operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.v = value
    }

    @Throws(IllegalArgumentException::class)
    open operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        if (!isInit) {
            initV()
        }
        if (thisRef !is SavedStateRegistryOwner) {
            throw IllegalArgumentException("宿主不是[SavedStateRegistryOwner]的实现类:::${if (thisRef == null) "NULL" else thisRef::class.qualifiedName}")
        }
        readValueFromBundle(thisRef, property.name)
        return v!!
    }

    private fun initV() {
        v = init.invoke()
        isInit = true
    }
}

/**
 * lateinit var 代理,使用前一定要初始化,不为null
 *
 * @param T
 */
open class SavedDelegateLateInit<T>() : AbsSavedDelegate<T>() {

    @Throws(java.lang.IllegalStateException::class)
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        readValueFromBundle(thisRef, property.name)
        if (v == null) {
            throw IllegalStateException("the property (${property.name}) can not be null,do you forget init it?")
        }
        return v!!
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        v = value
    }


    fun isInit(owner: Any, property: KProperty<*>): Boolean {
        readValueFromBundle(owner, property.name)
        return v != null
    }


    companion object {

        /**
         * 是否已经初始化
         * @param delegate Any
         * @return Boolean
         */
        fun isInit(owner: Any, property: KMutableProperty0<Any>): Boolean {
            property.isAccessible = true
            val delegate = property.getDelegate()
            if (delegate is SavedDelegateLateInit<*>) {
                return delegate.isInit(owner, property)
            } else {
                throw java.lang.IllegalArgumentException("必须接受")
            }
        }

        fun isInit(owner: Any, property: KProperty1<Any, *>): Boolean {
            property.isAccessible = true
            val delegate = property.getDelegate(owner)
            if (delegate is SavedDelegateLateInit<*>) {
                return delegate.isInit(owner, property)
            } else {
                throw java.lang.IllegalArgumentException("必须接受")
            }
        }

        /**
         * 是否已经初始化
         * 不会报错,非 [SavedDelegateLateInit] 类型,会返回 true
         * @param delegate Any
         * @return Boolean
         */
        fun isInitNoError(owner: Any, property: KMutableProperty0<*>): Boolean {
            property.isAccessible = true
            val delegate = property.getDelegate()
            if (delegate is SavedDelegateLateInit<*>) {
                property.getDelegate()
                return delegate.isInit(owner, property)
            } else {
                return true
            }
        }

        fun isInitNoError(owner: Any, property: KProperty1<Any, *>): Boolean {
            property.isAccessible = true
            val delegate = property.getDelegate(owner)
            if (delegate is SavedDelegateLateInit<*>) {
                return delegate.isInit(owner, property)
            } else {
                return true
            }
        }
    }
}

/**
 * 代理集合便捷方法
 */
object SavedDelegates {
    /**
     * 可以为null 的代理
     * @return SavedDelegateNullable<T>
     */
    fun <T> nullable() = SavedDelegateNullable<T>()

    /**
     * 不为null的代理,需要提供初始化函数
     * @param init Function0<T>
     * @return SavedDelegateNotNull<T>
     */
    fun <T> notNull(init: (() -> T)) = SavedDelegateNotNull(init)

    /**
     * lateinit var 实现,使用前需要初始化
     * @return SavedDelegateLateInit<T>
     */
    fun <T> lateInit() = SavedDelegateLateInit<T>()
}
