package cn.yzl.auto.save.testlib;


import androidx.appcompat.app.AppCompatActivity;

import cn.yzl.auto.save.base.AutoSaveRestore;

public class TestActivity extends AppCompatActivity {
    @AutoSaveRestore
    public int a = 0;
}
