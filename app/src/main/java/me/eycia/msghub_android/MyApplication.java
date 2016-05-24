package me.eycia.msghub_android;

import android.app.Application;
import android.content.Context;

import java.net.CookieHandler;
import java.net.CookieManager;

import me.eycia.api.UserBaseInfo;

/**
 * Created by eycia on 16/5/10.
 */
public class MyApplication extends Application {
    private static Context context;
    private UserBaseInfo userBaseInfo;

    public void onCreate() {
        super.onCreate();
        MyApplication.context = getApplicationContext();

        CookieHandler.setDefault(new CookieManager());
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }

    public UserBaseInfo getUserBaseInfo() {
        return userBaseInfo;
    }

    public void setUserBaseInfo(UserBaseInfo userBaseInfo) {
        this.userBaseInfo = userBaseInfo;
    }
}