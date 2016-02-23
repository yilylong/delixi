package com.delixi.price;

import android.app.Application;
import android.os.Environment;

import java.io.File;

/**
 * Created by long on 2016/2/23.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        init();
        super.onCreate();
    }

    private void init() {
        File appFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/delixi/");
        if(!appFolder.exists()){
            appFolder.mkdirs();
        }
    }
}
