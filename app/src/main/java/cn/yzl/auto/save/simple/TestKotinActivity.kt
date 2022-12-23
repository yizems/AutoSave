package cn.yzl.auto.save.simple

import android.os.Bundle
import android.os.Parcelable
import android.text.SpannableStringBuilder
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import cn.yzl.auto.save.AutoSaveDelegates
import cn.yzl.auto.save.base.AutoSaveRestore
import kotlinx.android.synthetic.main.activity_main.*

/**
 * 测试kotlin类中的自动保存
 */
@AutoSaveRestore
open class TestKotinActivity : AppCompatActivity() {

    var a: Int? by AutoSaveDelegates.nullable { 10 }

    var b: String? by AutoSaveDelegates.nullable { "hahha" }

    var c: Boolean? by AutoSaveDelegates.nullable { false }

    var d: IntArray by AutoSaveDelegates.notNull {
        IntArray(10) { it }
    }

    var pDemo by AutoSaveDelegates.notNull { PDemo() }

    var sDemo by AutoSaveDelegates.nullable { Sdemo() }

    private var pDemo2 by AutoSaveDelegates.lateInit<PDemo>()

    //region test ksp error check

//    private var error1 by SavedDelegates.lateInit<Any>()
//    private var error2 by SavedDelegates.lateInit<ArrayList<Any>>()

    //endregion


    override fun onCreate(savedInstanceState: Bundle?) {
        log("onCreate:" + this.hashCode())

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
            TestJava.start(this)
        }
        if (savedInstanceState == null) {
            pDemo2 = PDemo("pDemo22")
        }
        val startTime = System.currentTimeMillis()
        log("--registerSimple--" + (System.currentTimeMillis() - startTime))
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
        log(pDemo2.toString())

        log("----")
        val arry = arrayOf<Parcelable>()
        Bundle().putCharSequence("key", SpannableStringBuilder())
    }


    private fun log(msg: String?) {
        Log.e("TestKotinActivity", msg ?: "NULL")
    }

    override fun onDestroy() {
        log("onDestroy:" + this.hashCode())
        super.onDestroy()
    }
}
