package cn.yzl.saved.delegate.simple

import androidx.fragment.app.Fragment
import cn.yzl.saved.delegate.SavedDelegateHelper

class Fragment: Fragment() {
    fun a(){
        SavedDelegateHelper.registerSimple(this)
    }
}