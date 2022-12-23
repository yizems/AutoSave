package cn.yzl.auto.save.simple;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import cn.yzl.auto.save.AutoSaveHelper;
import cn.yzl.auto.save.base.AutoSaveRestore;

/**
 * 测试java类的自动保存
 */
public class TestJava extends AppCompatActivity {

    @AutoSaveRestore
    private int a = 10;
    @AutoSaveRestore
    private String b = "hahah";
    @AutoSaveRestore
    private boolean c = false;
    @AutoSaveRestore
    int[] d = new int[]{1, 2, 3};
    @AutoSaveRestore
    PDemo pDemo;
    @AutoSaveRestore
    Sdemo sDemo;

    @AutoSaveRestore
    ArrayList<PDemo> pDemos = new ArrayList<>();

    @AutoSaveRestore
    PDemo pDemo2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log("onCreate:" + this.hashCode());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        final long startTime = System.currentTimeMillis();
        AutoSaveHelper.registerForJava(this);
        log("--registerForJava--" + (System.currentTimeMillis() - startTime));

        log("----");
        log(String.valueOf(a));
        log(String.valueOf(b));
        log(String.valueOf(c));
        log(b.length() + "");
        if (pDemo != null) {
            log(pDemo.toString());
        }
        if (sDemo != null) {
            log(sDemo.toString());
        }
        log("----");

        findViewById(R.id.cl_content).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                a = 11;
                b = "hahah2";
                c = true;
                d = new int[]{1, 2, 3, 4};
                pDemo = new PDemo("pDemo2", false);
                sDemo = new Sdemo("sDemo2", 15);
                startActivity(new Intent(TestJava.this, TestJavaExtendLibrary.class));
            }
        });

    }


    @Override
    protected void onDestroy() {
        log("onDestroy:" + this.hashCode());
        super.onDestroy();
    }

    private void log(String msg) {
        Log.e("TestJava", msg);
    }


    public static void start(Context context) {
        context.startActivity(new Intent(context, TestJava.class)
                .putExtra("pDemo", new PDemo("7777", true))
                .putExtra("c", true)
        );
    }
}
