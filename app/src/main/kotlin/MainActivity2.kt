package com.xzy.syssettings

import android.app.Activity
import android.os.Bundle
import android.os.SystemClock
import android.widget.Toast

import kotlinx.android.synthetic.main.activity_main2.*
import java.text.SimpleDateFormat
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import com.xzy.syssettings.language.LanguageType
import com.xzy.syssettings.language.LanguageUtil.changeAppLanguage
import com.xzy.syssettings.language.LanguageUtil.changeSystemLanguage
import com.xzy.syssettings.language.Sp
import com.xzy.syssettings.utils.LogUtil
import com.xzy.syssettings.utils.TimeUtil.checkDateAutoSet
import com.xzy.syssettings.utils.TimeUtil.checkTimeZoneAutoSet
import com.xzy.syssettings.utils.TimeUtil.setAutoDateTime
import com.xzy.syssettings.utils.TimeUtil.setAutoTimeZone
import com.xzy.syssettings.utils.TimeUtil.setDate
import com.xzy.syssettings.utils.TimeUtil.setTime
import com.xzy.syssettings.utils.TimeUtil.setTimeZone
import java.util.Locale
import java.util.TimeZone
import java.util.Date

class MainActivity2 : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        LogUtil.wInfo("activity启动")
        btn1.setOnClickListener {
            // http://www.360doc.com/content/16/0413/10/2198695_550224518.shtml
            // 修改系统时区
            setTimeZone(this, "GMT+08:00")
            Toast.makeText(this, "" + TimeZone.getDefault(), Toast.LENGTH_LONG).show()
            tv_result.text = TimeZone.getDefault().toString()
        }

        btn11.setOnClickListener {
            // 修改系统时区
            setTimeZone(this, "GMT+01:00")
            Toast.makeText(this, "" + TimeZone.getDefault(), Toast.LENGTH_LONG).show()
            tv_result.text = TimeZone.getDefault().toString()
        }
        btn12.setOnClickListener {
            // 设置系统是否自动获取时区
            setAutoTimeZone(this, 1)
        }
        btn13.setOnClickListener {
            // 判断系统是否自动获取时区
            val result = checkTimeZoneAutoSet(this)
            Toast.makeText(this, "判断系统是否自动获取时区：$result", Toast.LENGTH_LONG).show()
        }

        btn2.setOnClickListener {
            // https://blog.csdn.net/qq_24451593/article/details/80542324
            // 修改系统时间
            SystemClock.setCurrentTimeMillis(System.currentTimeMillis() - 1 * 60 * 60 * 1000)
            val date = Date()
            date.time = System.currentTimeMillis()
            Toast.makeText(this, SimpleDateFormat().format(date), Toast.LENGTH_LONG).show()
            tv_result.text = SimpleDateFormat().format(date)
        }

        btn22.setOnClickListener {
            // https://blog.csdn.net/qq_24451593/article/details/80542324
            // 修改系统时间
            SystemClock.setCurrentTimeMillis(System.currentTimeMillis() + 1 * 60 * 60 * 1000)
            val date = Date()
            date.time = System.currentTimeMillis()
            Toast.makeText(this, SimpleDateFormat().format(date), Toast.LENGTH_LONG).show()
            tv_result.text = SimpleDateFormat().format(date)
        }

        btn3.setOnClickListener {
            // 设置系统时间(仅设置xx:xx)
            setTime(22, 49)
        }

        btn31.setOnClickListener {
            // 设置系统日期(仅设置到xxxx年xx月xx日)
            setDate(2021, 11, 1) // 2021.12.01
        }

        btn32.setOnClickListener {
            // 设置系统是否自动获取时间
            setAutoDateTime(this, 1)
        }

        btn33.setOnClickListener {
            // 判断系统是否自动获取时间
            val result = checkDateAutoSet(this)
            tv_result.text = "判断系统是否自动获取时间:$result"
        }

        btn4.setOnClickListener {
            // 修改系统地区
            // https://www.cnblogs.com/GloriousOnion/archive/2012/05/09/2491792.html
            changeSystemLanguage(Locale("zh", "CN"), this)
            changeAppLanguage(LanguageType.CHINESE.language, this)
        }

        btn44.setOnClickListener {
            // 修改系统地区
            changeSystemLanguage(Locale("en", "US"), this)
            changeAppLanguage(LanguageType.ENGLISH.language, this)
        }

        btn5.setOnClickListener {
            // 获取当前地区和语言
            val country = Locale.getDefault().country
            val language = Locale.getDefault().language
            Toast.makeText(this, "修改后$language---$country", Toast.LENGTH_LONG).show()
            tv_result.text = "$language---$country"
        }

        btn6.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        btn_reboot.setOnClickListener {
            // 重新启动到 fastboot模式
            val pManager = getSystemService(Context.POWER_SERVICE) as PowerManager
            pManager.reboot("")
        }

        doWork()
    }

    private fun doWork() {
        // 根据系统首选语言确定刚进入时需要显示的界面
        when (Sp.get("language")) {
            LanguageType.ENGLISH.language -> {
                tv_test.text = getString(R.string.test)
            }
            LanguageType.CHINESE.language -> {
                tv_test.text = getString(R.string.test)
            }
        }
        // 切换为中文
        btn_zh.setOnClickListener {
            if (Sp.get("language") == "zh") {
                // 如果当前已经是中文，则不做任何操作
                return@setOnClickListener
            }
            changeSystemLanguage(Locale.SIMPLIFIED_CHINESE, this)
            changeAppLanguage(LanguageType.CHINESE.language, this)
        }

        // 切换为英文
        btn_en.setOnClickListener {
            if (Sp.get("language") == "en") {
                // 如果当前已经是英文，则不做任何操作
                return@setOnClickListener
            }
            changeSystemLanguage(Locale.ENGLISH, this)
            changeAppLanguage(LanguageType.ENGLISH.language, this)
        }
    }

    override fun onStop() {
        super.onStop()
        LogUtil.wInfo("activity finish")
        finish()
    }
}
