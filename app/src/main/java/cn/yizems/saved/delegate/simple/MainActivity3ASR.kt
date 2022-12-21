package cn.yizems.saved.delegate.simple

import java.lang.ref.WeakReference


interface AutoSaveRestoreParent<T> {
//    fun setHost(host)
}


class MainActivity3ASR : MainActivity() {
    private lateinit var host: WeakReference<MainActivity3>

    fun test() {
    }
}
