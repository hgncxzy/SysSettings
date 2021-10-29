package com.xzy.syssettings

import android.app.Application
import android.util.Log
import com.xzy.syssettings.utils.LogUtil
import com.xzy.syssettings.language.Sp
import java.util.Locale

class AppContext : Application() {

    override fun onCreate() {
        super.onCreate()
        LogUtil.init()
        // 异常信息注册
        Thread.setDefaultUncaughtExceptionHandler(AppErrorCatchHandler())
        LogUtil.wInfo("AppContext onCreate>>>")
        INSTANCE = this
        Log.d("", "初始化 Application")
        // 获取系统当前的语言环境
        val locale = Locale.getDefault().language
        Sp.put("language", locale)
    }

    override fun onTerminate() {
        LogUtil.wInfo("AppContext onTerminate>>>")
        super.onTerminate()
    }

    companion object {
        lateinit var INSTANCE: AppContext
    }
}