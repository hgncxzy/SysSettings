package com.xzy.syssettings.language

import android.annotation.TargetApi
import android.app.Activity
import android.app.backup.BackupManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import android.text.TextUtils
import android.widget.Toast
import com.xzy.syssettings.MainActivity2
import java.lang.Exception
import java.lang.reflect.Method

import java.util.Locale

/**
 * Created by xzy .
 */
enum class LanguageType(language: String?) {

    CHINESE("zh"),
    ENGLISH("en");

    var language: String? = language
        get() {
            return field ?: ""
        }
}

@Suppress("unused", "DEPRECATION")
object LanguageUtil {
    private val TAG = "LanguageUtil"
    var sharedPreferences: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null

    /**
     * @param context 上下文
     * @param newLanguage 想要切换的语言类型 比如 "en" ,"zh"
     */
    private fun changeAppLanguage(context: Context, newLanguage: String) {
        if (TextUtils.isEmpty(newLanguage)) {
            return
        }
        val resources = context.resources
        val configuration = resources.configuration
        // 获取想要切换的语言类型
        val locale = getLocaleByLanguage(newLanguage)
        configuration.setLocale(locale)
        // updateConfiguration
        val dm = resources.displayMetrics
        resources.updateConfiguration(configuration, dm)
    }

    private fun getLocaleByLanguage(language: String): Locale {
        // default
        var locale = Locale.SIMPLIFIED_CHINESE
        // chinese
        if (language == LanguageType.CHINESE.language) {
            locale = Locale.SIMPLIFIED_CHINESE
        }
        // english
        if (language == LanguageType.ENGLISH.language) {
            locale = Locale.ENGLISH
        }
        return locale
    }

    fun attachBaseContext(context: Context, language: String): Context {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            updateResources(context, language)
        } else {
            context
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    private fun updateResources(context: Context, language: String): Context {
        val resources = context.resources
        val locale = getLocaleByLanguage(language)
        val configuration = resources.configuration
        configuration.setLocale(locale)
        configuration.locales = LocaleList(locale)
        return context.createConfigurationContext(configuration)
    }

    /**
     * 这个方法不需要系统签名
     * 经过测试：android 8.0 以下的版本需要更新 configuration 和 resources，
     * android 8.0 以上只需要将当前的语言环境写入 Sp 文件即可。
     * 测试机型 android4.4、android6.0、android7.0、android7.1、android8.1
     * 然后，重新创建当前页面。
     * @param language
     */
    fun changeAppLanguage(language: String?, activity: Activity) {
        // 版本低于 android 8.0 不执行该方法
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            // 注意，这里的 context 不能传 Application 的 context
            changeAppLanguage(activity, language!!)
        }
        Sp.put("language", language!!)
        // 不同的版本，使用不同的重启方式，达到最好的效果
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            // 6.0 以及以下版本，使用这种方式，并给 activity 添加启动动画效果，可以规避黑屏和闪烁问题
            val intent = Intent(activity, MainActivity2::class.java)
            // intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            activity.startActivity(intent)
            activity.finish()
        } else {
            // 6.0 以上系统直接调用重新创建函数，可以达到无缝切换的效果
            activity.recreate()
        }
    }

    /**
     * 这个方法需要系统签名
     * */
    fun changeSystemLanguage(locale: Locale?, context: Context) {
        if (locale != null) {
            try {
                val classActivityManagerNative = Class.forName("android.app.ActivityManagerNative")
                val getDefault: Method = classActivityManagerNative.getDeclaredMethod("getDefault")
                val objIActivityManager: Any = getDefault.invoke(classActivityManagerNative)

                val classIActivityManager = Class.forName("android.app.IActivityManager")
                val getConfiguration: Method =
                    classIActivityManager.getDeclaredMethod("getConfiguration")
                val config: Configuration =
                    getConfiguration.invoke(objIActivityManager) as Configuration
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    config.locales = LocaleList(locale)
                } else {
                    config.setLocale(locale)
                }
                val clzConfig = Class
                    .forName("android.content.res.Configuration")
                val userSetLocale = clzConfig
                    .getField("userSetLocale")
                userSetLocale[config] = true
                val clzParams = arrayOf<Class<*>>(
                    Configuration::class.java
                )
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // 该代码决定是否修改系统语言设置（需要有系统签名和sharedUserId）
                    val updatePersistentConfiguration: Method =
                        classIActivityManager.getDeclaredMethod(
                            "updatePersistentConfiguration",
                            *clzParams
                        )
                    updatePersistentConfiguration.invoke(objIActivityManager, config)
                    BackupManager.dataChanged("com.android.providers.settings")
                } else {
                    // updateConfiguration
                    val configuration = context.resources.configuration
                    // 获取想要切换的语言类型
                    configuration.setLocale(locale)
                    // updateConfiguration
                    val dm = context.resources.displayMetrics
                    context.resources.updateConfiguration(configuration, dm)
                    // 下面的代码决定是否修改系统语言设置（需要有系统签名和sharedUserId）
                    val updateConfiguration: Method =
                        classIActivityManager.getDeclaredMethod(
                            "updateConfiguration",
                            *clzParams
                        )
                    updateConfiguration.invoke(objIActivityManager, config)
                    BackupManager.dataChanged("com.android.providers.settings")
                }

                Toast.makeText(
                    context,
                    "language:" + locale.language + "--country:" + locale.country,
                    Toast.LENGTH_SHORT
                ).show()
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        }
    }
}
