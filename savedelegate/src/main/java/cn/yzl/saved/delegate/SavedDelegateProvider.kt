/*
* MIT License
* 
* Copyright (c) 2021 yizems
* 
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
* 
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*/

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
