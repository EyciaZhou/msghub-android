package me.eycia.msghub_android;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import java.net.CookieHandler;
import java.net.CookieManager;

import me.eycia.api.UserBaseInfo;

/**
 * Created by eycia on 16/5/10.
 */
public class MyApplication extends Application {
    private static Context context;
    private static UserBaseInfo userBaseInfo;

    public void onCreate() {
        super.onCreate();
        MyApplication.context = getApplicationContext();

        CookieHandler.setDefault(new CookieManager());
    }

    public static void showToast(String info) {
        Toast.makeText(context, info, Toast.LENGTH_SHORT).show();
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }

    public static UserBaseInfo getUserBaseInfo() {
        return userBaseInfo;
    }

    public void setUserBaseInfo(UserBaseInfo userBaseInfo) {
        MyApplication.userBaseInfo = userBaseInfo;
    }
}