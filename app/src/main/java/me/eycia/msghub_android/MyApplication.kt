package me.eycia.msghub_android

import android.app.Application
import android.content.Context
import android.widget.Toast
import me.eycia.api.UserBaseInfo
import java.net.CookieHandler
import java.net.CookieManager

/**
 * Created by eycia on 16/5/10.
 */
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        MyApplication.appContext = applicationContext

        CookieHandler.setDefault(CookieManager())
    }

    companion object {
        var appContext: Context? = null
            private set


        var userBaseInfo: UserBaseInfo? = null
            set(userBaseInfo) {
                field = userBaseInfo
            }

        fun toast(info: String?) {
            Toast.makeText(appContext, info, Toast.LENGTH_SHORT).show()
        }
    }
}