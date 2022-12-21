package cn.yizems.auto.save

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.savedstate.SavedStateRegistry
import cn.yizems.auto.save.base.AutoSaveRestore
import java.lang.reflect.Modifier

/**
 * 自动保存工具主类
 */
object SavedDelegateHelper {
    const val KEY = "cn.yizems.SavedDelegate"

    /**
     * 缓存的 SavedDelegateProvider ,key 为 activity/fragment 的hashcode
     * 当反注册的时候,会自动移除
     */
    private val providers: HashMap<Int, SavedDelegateProvider> = HashMap()

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
        if (isRegisted(obj)) {
            return
        }
        val provider = getOrCreateSavedProvider(obj)
        savedStateRegistry.registerSavedStateProvider(KEY, provider)
    }

    private fun isRegisted(obj: Any) = providers.containsKey(obj.hashCode())

    /**
     * 注册
     *
     * @param lifecycleOwner LifecycleOwner 销毁时自动反注册 [unRegisterSavedProvider]
     * @param savedStateRegistry SavedStateRegistry
     * @param obj Any
     */
    internal fun registerWithLifecycle(
        lifecycleOwner: LifecycleOwner,
        savedStateRegistry: SavedStateRegistry,
        obj: Any
    ) {
        registerSavedProvider(
            savedStateRegistry,
            obj
        )

        lifecycleOwner.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    unRegisterSavedProvider(
                        savedStateRegistry,
                        obj
                    )
                    lifecycleOwner.lifecycle.removeObserver(this)
                }
            }
        })
    }

    /**
     * 从 [providers] 获取或创建一个 SavedDelegateProvider 放入 providers中
     * @param obj Any
     * @return SavedDelegateProvider
     */
    private fun getOrCreateSavedProvider(obj: Any): SavedDelegateProvider {
        var provider = providers[obj.hashCode()]
        if (provider != null) {
            return provider
        }
        provider = SavedDelegateProvider(obj = obj)
        providers[obj.hashCode()] = provider
        return provider
    }

    internal fun getProvider(obj: Any): SavedDelegateProvider? {
        return providers[obj.hashCode()]
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
     * @param obj Any
     * @return Bundle
     */
    internal fun saveJavaField(obj: Any, saveBundle: Bundle): Bundle {
        val startTime = System.currentTimeMillis()

        val bundle = Bundle()


        // for kotlin
        if (!isKtClass(obj)) {
            //for java
            Log.e("AAAAAAAA3", (System.currentTimeMillis() - startTime).toString())

//            obj.javaClass.kotlin.memberProperties.filter { property ->
//                property.annotations.any { it is AutoSaveRestore }
//            }.forEach { property ->
//                val annotation = property.annotations.filterIsInstance<AutoSaveRestore>()
//                    .firstOrNull() ?: return@forEach
//
//                try {
//                    val keyName = annotation.value.ifBlank {
//                        property.name
//                    }
//                    bundleWriter.saveToBundle(bundle, keyName, property, obj)
//                } catch (e: IllegalArgumentException) {
//                    e.printStackTrace()
//                    if (!annotation.ignoreInvalidType) {
//                        throw e
//                    }
//                }
//            }
        }
//        Log.e("MainActivity", "saveProperties cost time: ${System.currentTimeMillis() - startTime}")
        return bundle
    }

    @JvmStatic
    fun registerForJava(act: ComponentActivity) {
        registerWithLifecycle(act, act.savedStateRegistry, act)
        restoreFieldForJava(act, act.savedStateRegistry, act.intent.extras)
    }

    @JvmStatic
    fun registerForJava(frg: Fragment) {
        registerWithLifecycle(frg, frg.savedStateRegistry, frg)
        restoreFieldForJava(frg, frg.savedStateRegistry, frg.arguments)
    }

    private fun restoreFieldForJava(
        obj: Any,
        savedStateRegistry: SavedStateRegistry,
        startBundle: Bundle?
    ) {
        val provider = getOrCreateSavedProvider(obj)
        val restoreBundle = provider.getSavedBundle(savedStateRegistry)

        obj.javaClass.declaredFields
            .filterNot { Modifier.isFinal(it.modifiers) || Modifier.isStatic(it.modifiers) }
            .filter { field ->
                field.annotations.any { it is AutoSaveRestore }
            }.forEach { field ->
                val annotation = field.annotations.filterIsInstance<AutoSaveRestore>()
                    .firstOrNull() ?: return@forEach

                val keyName = annotation.value.ifBlank {
                    field.name
                }
                val value = restoreBundle?.get(keyName) ?: startBundle?.get(keyName)
                if (value != null) {
                    field.isAccessible = true
                    field.set(obj, value)
                }
            }
    }

    /**
     * 判断一个文件是不是kotlin类型
     * 利用 kotlin 编译后有 kotlin.metaData 注解
     *
     * @return ture kotlin, false java
     */
    private fun isKtClass(`object`: Any): Boolean {
        val annotations = `object`.javaClass.annotations
        for (i in annotations.indices) {
            if (annotations[i].toString().contains("kotlin")) {
                return true
            }
        }
        return false
    }

}
