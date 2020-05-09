package cn.yzl.saved.delegate.simple

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import cn.yzl.saved.delegate.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var a: Int? by SavedDelegates.nullable { 10 }

    var b: String? by SavedDelegates.nullable { "hahha" }

    var c: Boolean? by SavedDelegates.nullable { false }

    var d: IntArray by SavedDelegates.notNull {
        IntArray(10) { it }
    }

    var pDemo by SavedDelegates.notNull { PDemo() }
    var sDemo by SavedDelegates.nullable { Sdemo() }

    var pDemo2 by SavedDelegates.lateInit<PDemo>()

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
            pDemo.name += "1"
            if (sDemo == null) {
                sDemo = Sdemo().apply {
                    age = 100
                }
            } else {
                sDemo = null
            }
            pDemo2 = PDemo("pDemo2")
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
        log(pDemo.toString())
        log(sDemo?.toString() ?: "sDemo=null")
        if (sDemo != null) {
            log(System.identityHashCode(sDemo).toString())
        }
        if (SavedDelegateLateInit.isInitNoError(
                this,
                this::pDemo2
            )
        ) {
            log(pDemo2.toString())
        } else {
            log("pDemo2 is not init")
        }

        log("----")
    }


    private fun log(msg: String?) {
        Log.e("hahaha", msg ?: "NULL")
    }

}
