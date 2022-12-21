package cn.yizems.auto.save

import android.os.Bundle
import androidx.savedstate.SavedStateRegistryOwner
import kotlin.reflect.KProperty

/**
 * 抽象的 SavedDelegate
 * @property v 保存的数据
 * @constructor
 */
@Suppress("UNCHECKED_CAST")
abstract class AbsSavedDelegate<T> {

    protected open var v: T? = null

    protected var propertyName: String? = null

    /**
     * 从 恢复数据中获取该值,如果获取不到,返回 v
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
        val bundle = SavedDelegateHelper.getProvider(owner)
            ?.getSavedBundle(owner.savedStateRegistry) ?: return

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

    /**
     * 可重复调用,但是只会执行一次,
     * 在 kotlin 代理方法的 get set 中均调用一次
     */
    fun register2Provider(owner: Any?, propertyName: String) {
        if (owner !is SavedStateRegistryOwner) {
            throw IllegalArgumentException("宿主不是[SavedStateRegistryOwner]的实现类:::${if (owner == null) "NULL" else owner::class.qualifiedName}")
        }

        SavedDelegateHelper.registerWithLifecycle(owner, owner.savedStateRegistry, owner)
        val provider = SavedDelegateHelper.getProvider(owner) ?: return
        this.propertyName = propertyName
        provider.addDelegate(this)
    }

    open fun save2Bundle(bundle: Bundle) {
        propertyName ?: return // 如果为null,为没有使用过, 即没有读取过,也没有写入过,不需要保存
        BundleWriter.saveToBundle(bundle, propertyName!!, v)
    }
}


/**
 * 默认实现,可为Null
 * @param T
 * @property init Function0<T>? 初始化
 * @constructor
 */
@Suppress("UNCHECKED_CAST")
class SavedDelegateNullable<T>(
    val init: (() -> T)? = null
) : AbsSavedDelegate<T>() {

    /** 是否已经初始化 */
    private var isInit = false

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        this.v = value
        register2Provider(thisRef, property.name)
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T? {
        if (!isInit) {
            initV()
        }
        register2Provider(thisRef, property.name)
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
class SavedDelegateNotNull<T>(
    val init: (() -> T)
) : AbsSavedDelegate<T>() {

    private var isInit = false

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.v = value
        register2Provider(thisRef, property.name)
    }

    @Throws(IllegalArgumentException::class)
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        if (!isInit) {
            initV()
        }
        if (thisRef !is SavedStateRegistryOwner) {
            throw IllegalArgumentException("宿主不是[SavedStateRegistryOwner]的实现类:::${if (thisRef == null) "NULL" else thisRef::class.qualifiedName}")
        }
        register2Provider(thisRef, property.name)
        readValueFromBundle(thisRef, property.name)
        return v!!
    }

    private fun initV() {
        v = init.invoke()
        isInit = true
    }

    override fun save2Bundle(bundle: Bundle) {
        if (!isInit || propertyName == null || v == null) {
            return
        }
        super.save2Bundle(bundle)
    }
}

/**
 * lateinit var 代理,使用前一定要初始化,不为null
 *
 * @param T
 */
class SavedDelegateLateInit<T> : AbsSavedDelegate<T>() {

    @Throws(java.lang.IllegalStateException::class)
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        register2Provider(thisRef, property.name)
        readValueFromBundle(thisRef, property.name)
        if (v == null) {
            throw IllegalStateException("the property (${property.name}) can not be null,do you forget init it?")
        }
        return v!!
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        v = value
        register2Provider(thisRef, property.name)
    }

    fun isInit(owner: Any, property: KProperty<*>): Boolean {
        readValueFromBundle(owner, property.name)
        return v != null
    }

    override fun save2Bundle(bundle: Bundle) {
        if (propertyName == null || v == null) {
            return
        }
        BundleWriter.saveToBundle(bundle, propertyName!!, v)
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
    fun <T> nullable(init: (() -> T)? = null) = SavedDelegateNullable<T>(init)

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
