package cn.yzl.saved.delegate

import androidx.savedstate.SavedStateRegistryOwner
import java.lang.IllegalArgumentException
import kotlin.reflect.KProperty

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
     *
     * @param name String
     * @return T?
     */
    protected open fun readValueFromBundle(
        savedStateRegistryOwner: SavedStateRegistryOwner,
        name: String
    ) {
        val bundle = SavedDelegateHelper.getOrCreateSavedProvider(savedStateRegistryOwner)
            .getSavedBundle(savedStateRegistryOwner.savedStateRegistry) ?: return
        val temp = bundle.get(name)

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
class SavedDelegate<T>(
    val init: (() -> T)? = null
) : AbsSavedDelegate<T>() {

    /** 是否已经初始化 */
    private var isInit = false

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        this.v = value
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T? {
        if (!isInit) {
            initV()
        }

        if (thisRef !is SavedStateRegistryOwner) {
            throw IllegalArgumentException("宿主不是[SavedStateRegistryOwner]的实现类:::${if (thisRef == null) "NULL" else thisRef::class.qualifiedName}")
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
class SavedDelegateNotNull<T>(
    val init: (() -> T)
) : AbsSavedDelegate<T>() {

    private var isInit = false

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.v = value
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
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

