package cn.yzl.saved.delegate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.savedstate.SavedStateRegistry
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

/**
 * 自动保存工具主类
 *
 */
object SavedDelegateHelper {
    const val KEY = "cn.yzl.SavedDelegate"
    /**
     * 缓存的 SavedDelegateProvider ,key 为 activity/fragment 的hashcode
     * 当反注册的时候,会自动移除
     */
    private val providers: HashMap<Int, SavedDelegateProvider> = HashMap()

    /**
     * 属性值写入到 Bundle中的方法
     */
    private var bundleWriter: BundleWriter = DefaultBundleWriter()

    /**
     * 自定义 [BundleWriter]
     * @param bundleWriter BundleWriter
     */
    fun setBundleWriter(bundleWriter: BundleWriter) {
        this.bundleWriter = bundleWriter
    }

    /**
     * 自动注册,默认情况下[ComponentActivity] 实现了[SavedStateRegistry]的获取和[LifecycleOwner]
     *
     * @param act ComponentActivity
     */
    fun registerSimple(act: ComponentActivity) {
        registerWithLifecycle(
            act,
            act.savedStateRegistry,
            act
        )
    }

    fun registerSimple(fragment: Fragment) {
        registerWithLifecycle(
            fragment,
            fragment.savedStateRegistry,
            fragment
        )
    }

    /**
     * 注册
     *
     * @param savedStateRegistry SavedStateRegistry
     * @param obj Any
     */
    fun registerSavedProvider(
        savedStateRegistry: SavedStateRegistry,
        obj: Any
    ) {
        val provider = getOrCreateSavedProvider(obj)
        savedStateRegistry.registerSavedStateProvider(KEY, provider)
    }

    /**
     * 注册
     *
     * @param lifecycleOwner LifecycleOwner 销毁时自动反注册 [unRegisterSavedProvider]
     * @param savedStateRegistry SavedStateRegistry
     * @param obj Any
     */
    fun registerWithLifecycle(
        lifecycleOwner: LifecycleOwner,
        savedStateRegistry: SavedStateRegistry,
        obj: Any
    ) {
        registerSavedProvider(
            savedStateRegistry,
            obj
        )
        lifecycleOwner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                unRegisterSavedProvider(
                    savedStateRegistry,
                    obj
                )
            }
        })
    }

    /**
     * 从 [providers] 获取或创建一个 SavedDelegateProvider 放入 providers中
     * @param obj Any
     * @return SavedDelegateProvider
     */
    fun getOrCreateSavedProvider(obj: Any): SavedDelegateProvider {
        var provider = providers[obj.hashCode()]
        if (provider != null) {
            return provider
        }
        provider = SavedDelegateProvider(obj = obj)
        providers[obj.hashCode()] = provider
        return provider
    }

    /**
     * 反注册, 需要在 onDestroy 的时候调用
     * @param savedStateRegistry SavedStateRegistry
     * @param obj 一般指 Activity 或者 Fragment
     */
    fun unRegisterSavedProvider(savedStateRegistry: SavedStateRegistry, obj: Any) {
        providers.remove(obj.hashCode())
        savedStateRegistry.unregisterSavedStateProvider(KEY)
    }

    /**
     * 保存数据,会通过 [SavedStateRegistry]注册的[androidx.savedstate.SavedStateRegistry.SavedStateProvider] 自动调用
     *
     * @param obj Any
     * @return Bundle
     */
    internal fun saveProperties(obj: Any): Bundle {
        val bundle = Bundle()
        obj.javaClass.kotlin.memberProperties
            .filter {
                it.isAccessible = true
                it.getDelegate(obj) is AbsSavedDelegate<*>
            }
            .forEach {
                bundleWriter.saveToBundle(bundle, it.name, it.get(obj), it)
            }
        return bundle
    }
}