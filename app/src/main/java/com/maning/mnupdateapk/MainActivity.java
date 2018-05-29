package com.maning.mnupdateapk;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.maning.updatelibrary.UpdateUtils;

/**
 * Created by Administrator on 2018/5/29.
 */

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //监听回调
        UpdateUtils.getInstance().initCallBack(this);
    }


    //下载
    public void text(View view) {
        String path = "http://app-global.pgyer.com/3728c6809840fc7fe6b4a57de5790b9c.apk?attname=app-debug.apk&sign=73c6fefdc6d5d7f26dc262d37b1bee10&t=5b0d0fc2";
        UpdateUtils.getInstance().shownUpDialog(this, path);
    }


}
