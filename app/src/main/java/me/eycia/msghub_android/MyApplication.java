package me.eycia.msghub_android;

import android.app.Application;
import android.content.Context;

/**
 * Created by eycia on 16/5/10.
 */
public class MyApplication extends Application {

    private static Context context;

    public void onCreate() {
        super.onCreate();
        MyApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }
}