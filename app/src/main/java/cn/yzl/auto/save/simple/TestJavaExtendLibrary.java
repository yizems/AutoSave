package cn.yzl.auto.save.simple;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import cn.yzl.auto.save.AutoSaveHelper;
import cn.yzl.auto.save.base.AutoSaveRestore;
import cn.yzl.auto.save.testlib.TestActivity;

/**
 * 测试java类的自动保存,该类继承自一个library中的类
 */
public class TestJavaExtendLibrary extends TestActivity {

    @AutoSaveRestore
    private int b = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log("onCreate:" + this.hashCode());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        final long startTime = System.currentTimeMillis();
        AutoSaveHelper.registerForJava(this);
        a++;
        b++;
        log("--registerForJava--" + (System.currentTimeMillis() - startTime));

        log("----");
        log(String.valueOf(a));
        log(String.valueOf(b));

        findViewById(R.id.cl_content).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TestJavaExtendLibrary.this, LastPageActivity.class));
            }
        });

    }

    private void log(String msg) {
        Log.e("TestJavaExtendLibrary", msg);
    }

    @Override
    protected void onDestroy() {
        log("onDestroy:" + this.hashCode());
        super.onDestroy();
    }
}
