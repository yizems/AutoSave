package cn.yzl.saved.delegate

import android.os.Bundle
import androidx.savedstate.SavedStateRegistry

/**
 *
 * @property obj Any
 * @constructor
 */
class SavedDelegateProvider(private val obj: Any) : SavedStateRegistry.SavedStateProvider {

    private var cacheBundle: Bundle? = null

    /**
     * 是否已经从[SavedStateRegistry]读取过数据
     */
    private var isRestored = false

    /**
     * 获取 回复的 Bundle, 由于 [SavedStateRegistry] 获取或会移除 对应的信息,所以这里需要做下缓存 供 [AbsSavedDelegate] 调用
     *
     * @param savedStateRegistry SavedStateRegistry
     * @return Bundle?
     */
    fun getSavedBundle(savedStateRegistry: SavedStateRegistry): Bundle? {
        if (!isRestored) {
            cacheBundle = savedStateRegistry.consumeRestoredStateForKey(
                SavedDelegateHelper.KEY
            )
            isRestored = true
        }
        return cacheBundle
    }

    override fun saveState(): Bundle {
        cacheBundle = null
        return SavedDelegateHelper.saveProperties(obj)
    }
}