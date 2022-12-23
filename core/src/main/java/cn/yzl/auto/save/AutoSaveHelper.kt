package cn.yzl.auto.save

import android.os.Bundle
import android.util.SparseArray
import android.util.SparseBooleanArray
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.savedstate.SavedStateRegistry
import cn.yzl.auto.save.base.ABSAsr

/**
 * 自动保存工具主类
 */
internal object AutoSaveHelper {
    const val KEY = "cn.yzl.auto.save.AutoSave"

    /**
     * 缓存的 SavedDelegateProvider ,key 为 activity/fragment 的hashcode
     * 当反注册的时候,会自动移除
     */
    private val providers: SparseArray<AutoSaveDelegatesProvider> = SparseArray(8)

    /**
     * 防止 [restoreFieldForJava] 重复调用的情况下导致的重复读取
     */
    private val javaRestoreFlag = SparseBooleanArray(8)

    /**
     * 注册
     *
     * @param savedStateRegistry SavedStateRegistry
     * @param obj Any
     */
    private fun registerSavedProvider(
        savedStateRegistry: SavedStateRegistry,
        obj: Any
    ) {
        if (isRegistered(obj)) {
            return
        }
        val provider = getOrCreateSavedProvider(obj)
        savedStateRegistry.registerSavedStateProvider(KEY, provider)
    }

    private fun isRegistered(host: Any) = providers[host.hashCode()] != null

    /**
     * 注册
     *
     * @param lifecycleOwner LifecycleOwner 销毁时自动反注册 [unRegisterSavedProvider]
     * @param savedStateRegistry SavedStateRegistry
     * @param host Any
     */
    internal fun registerWithLifecycle(
        lifecycleOwner: LifecycleOwner,
        savedStateRegistry: SavedStateRegistry,
        host: Any
    ) {
        registerSavedProvider(
            savedStateRegistry,
            host
        )

        lifecycleOwner.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    unRegisterSavedProvider(
                        savedStateRegistry,
                        host
                    )
                    lifecycleOwner.lifecycle.removeObserver(this)
                    javaRestoreFlag.delete(host.hashCode())
                }
            }
        })
    }

    /**
     * 从 [PROVIDERS] 获取或创建一个 SavedDelegateProvider 放入 providers中
     * @param host Any
     * @return SavedDelegateProvider
     */
    private fun getOrCreateSavedProvider(host: Any): AutoSaveDelegatesProvider {
        var provider = providers[host.hashCode()]
        if (provider != null) {
            return provider
        }
        provider = AutoSaveDelegatesProvider(host)
        providers.put(host.hashCode(), provider)
        return provider
    }

    internal fun getProvider(host: Any): AutoSaveDelegatesProvider? {
        return providers[host.hashCode()]
    }

    /**
     * 反注册, 需要在 onDestroy 的时候调用
     * @param savedStateRegistry SavedStateRegistry
     * @param obj 一般指 Activity 或者 Fragment
     */
    internal fun unRegisterSavedProvider(savedStateRegistry: SavedStateRegistry, obj: Any) {
        providers.remove(obj.hashCode())
        savedStateRegistry.unregisterSavedStateProvider(KEY)
    }

    /**
     * 保存数据,会通过 [SavedStateRegistry]注册的[androidx.savedstate.SavedStateRegistry.SavedStateProvider] 自动调用
     *
     * @param host Any
     * @return Bundle
     */
    internal fun saveJavaField(host: Any, saveBundle: Bundle) {
        if (isKtClass(host)) {
            return
        }
        javaRestoreFlag.put(host.hashCode(), false)
        //for java
        if (!isKtClass(host)) {
            getJavaAsr(host).saveToBundle(host, saveBundle)
        }
    }

    @JvmStatic
    fun registerForJava(act: ComponentActivity) {
        registerWithLifecycle(act, act.savedStateRegistry, act)
        restoreFieldForJava(act, act.savedStateRegistry)
    }

    @JvmStatic
    fun registerForJava(frg: Fragment) {
        registerWithLifecycle(frg, frg.savedStateRegistry, frg)
        restoreFieldForJava(frg, frg.savedStateRegistry)
    }

    /**
     * 恢复Java对象的数据
     */
    private fun restoreFieldForJava(
        host: Any,
        savedStateRegistry: SavedStateRegistry,
    ) {
        if (javaRestoreFlag[host.hashCode()]) {
            return
        }
        javaRestoreFlag.put(host.hashCode(), true)
        val provider = getOrCreateSavedProvider(host)
        val restoreBundle = provider.getSavedBundle(savedStateRegistry) ?: return
        getJavaAsr(host).readFromBundle(host, restoreBundle)
    }

    /**
     * 判断一个文件是不是kotlin类型
     * 利用 kotlin 编译后有 Metadata 注解
     *
     * @return ture kotlin, false java
     */
    private fun isKtClass(host: Any): Boolean {
        return host.javaClass.isAnnotationPresent(Metadata::class.java)
    }

    private fun getJavaAsr(host: Any): ABSAsr {
        val clazz = Class.forName(host.javaClass.canonicalName + "ASR")
        return clazz.getField("INSTANCE").get(null) as ABSAsr
    }
}
