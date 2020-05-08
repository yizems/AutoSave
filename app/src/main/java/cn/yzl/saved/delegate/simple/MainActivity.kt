package cn.yzl.saved.delegate.simple

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import cn.yzl.saved.delegate.SavedDelegate
import cn.yzl.saved.delegate.SavedDelegateHelper
import cn.yzl.saved.delegate.SavedDelegateNotNull
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var a: Int? by SavedDelegate() { 10 }

    var b: String? by SavedDelegate() { "hahha" }

    var c: Boolean? by SavedDelegate { false }

    var d: IntArray by SavedDelegateNotNull {
        IntArray(10) { it }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        second.setOnClickListener {
            a = (a ?: 0) + 1
            b = null
            c = true
            d.forEachIndexed { index, i ->
                d[index] *= 10
            }
            startActivity(Intent(this, Main2Activity::class.java))
        }
        SavedDelegateHelper.registerSimple(this)
    }

    override fun onResume() {
        super.onResume()
        log("----")
        log(a?.toString())
        log(b?.toString())
        log(c?.toString())
        log(d.joinToString(separator = ","))
//        log(e?.toString())
//        log(a?.toString())

        log("----")
    }


    private fun log(msg: String?) {
        Log.e("hahaha", msg ?: "NULL")
    }

}
