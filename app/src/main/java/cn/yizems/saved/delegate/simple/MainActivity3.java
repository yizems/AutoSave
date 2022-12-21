package cn.yizems.saved.delegate.simple;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import cn.yizems.auto.save.SavedDelegateHelper;
import cn.yizems.auto.save.base.AutoSaveRestore;

public class MainActivity3 extends AppCompatActivity {

    @AutoSaveRestore
    private int a = 10;
    @AutoSaveRestore("bbbb")
    private String b = "hahah";
    @AutoSaveRestore
    private boolean c = false;
    @AutoSaveRestore
    private int[] d = new int[]{1, 2, 3};
    @AutoSaveRestore
    private PDemo pDemo;
    @AutoSaveRestore
    private Sdemo sDemo;

    PDemo pDemo2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        final long startTime = System.currentTimeMillis();
        SavedDelegateHelper.registerForJava(this);
        log("--registerForJava--" + (System.currentTimeMillis() - startTime));

        log("----");
        log(String.valueOf(a));
        log(String.valueOf(b));
        log(String.valueOf(c));
        log(b.length() + "");
        log(pDemo.toString());
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
                startActivity(new Intent(MainActivity3.this, Main2Activity.class));
            }
        });

    }

    private void log(String msg) {
        Log.e("MainActivity3", msg);
    }


    public static void start(Context context) {
        context.startActivity(new Intent(context, MainActivity3.class)
                .putExtra("pDemo", new PDemo("7777", true))
                .putExtra("c", true)
        );
    }
}
