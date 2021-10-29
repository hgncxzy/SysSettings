package com.xzy.syssettings.utils

import android.content.Context
import android.os.SystemClock
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import android.app.AlarmManager

object TimeUtil {

    /**
     * 获取时间(包含日期)
     *
     * @return
     */
    val currentTime: String
        get() {
            val timeFmt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            timeFmt.timeZone = TimeZone.getTimeZone("GMT+8")
            return timeFmt.format(Date())
        }

    /**
     * 获取时间(包含日期格式photo)
     *
     * @return
     */
    val currentTimeFmt2: String
        get() {
            val timeFmt2 = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
            timeFmt2.timeZone = TimeZone.getTimeZone("GMT+8")
            return timeFmt2.format(Date())
        }

    /**
     * 获取日期
     *
     * @return
     */
    val currentDay: String
        get() {
            val dateFmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateFmt.timeZone = TimeZone.getTimeZone("GMT+8")
            return dateFmt.format(Date())
        }

    /**
     * 获取当前时分秒
     *
     * @return HH:mm:ss
     */
    val currentHour: String
        get() {
            val hourFmt = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            hourFmt.timeZone = TimeZone.getTimeZone("GMT+8")
            return hourFmt.format(Date())
        }

    /**
     * 日期加一天
     */
    fun addCurrentDay(): String {
        val sf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        sf.timeZone = TimeZone.getTimeZone("GMT+8")
        val c = Calendar.getInstance()
        c.add(Calendar.DAY_OF_MONTH, 1)
        return sf.format(c.time)
    }

    /**
     * 日期加 days 天
     */
    fun addDay(days: Int): String {
        val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        dateTimeFormat.timeZone = TimeZone.getTimeZone("GMT+8")
        val c = Calendar.getInstance()
        c.add(Calendar.DAY_OF_MONTH, days)
        return dateTimeFormat.format(c.time)
    }

    /**
     * 将 hh:mm:ss 格式的时间转为秒
     *
     * @param time
     * @return
     */
    fun getSecond(time: String): Long {
        val oo = time.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val hour = Integer.parseInt(oo[0]).toLong()
        val min = Integer.parseInt(oo[1]).toLong()
        val s = Integer.parseInt(oo[2]).toLong()
        return s + min * 60 + hour * 60 * 60
    }

    /**
     * 将 hh:mm:ss 格式的时间转为时 int
     *
     * @param time
     * @return
     */
    fun getHour(time: String): Int {
        val oo = time.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        return Integer.parseInt(oo[0])
    }

    /**
     *
     * @Title: getDeltaT
     * @Description: 得到两个时间的差值 (days)
     * @param @param startDate 起始时间
     * @param @param endDate 截至时间
     * @return long 时间差
     * @throws
     */
    fun getDeltaTimeDays(startDate: String, endDate: String): Int {
        val dateFmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        dateFmt.timeZone = TimeZone.getTimeZone("GMT+8")
        try {
            val d1 = dateFmt.parse(startDate)
            val d2 = dateFmt.parse(endDate)
            val diff = d1.time - d2.time // 这样得到的差值是微秒级别
            return (diff / (1000 * 60 * 60 * 24)).toInt()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return -1
    }

    /**
     *将毫秒值转换为日期时间
     * milliSecond = 1551798059000L **/
    @JvmStatic
    fun milliSecondToTimeString(milliSecond: Long) {
        val date = Date()
        date.time = milliSecond
        println(SimpleDateFormat().format(date))
    }

    /** 设置系统时间*/
    @Throws(IOException::class)
    fun setTime(hour: Int, minute: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        val timeMills: Long = calendar.timeInMillis
        SystemClock.setCurrentTimeMillis(timeMills)
    }

    /** 设置系统日期*/
    @Throws(IOException::class)
    fun setDate(year: Int, month: Int, day: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, day)
        val timeMills: Long = calendar.timeInMillis
        SystemClock.setCurrentTimeMillis(timeMills)
    }

    /**
     * 设置系统是否自动获取时间
     * @param context Activity's context.
     * @param checked If checked > 0, it will auto set date.
     */
    fun setAutoDateTime(context: Context, checked: Int) {
        Settings.Global.putInt(
            context.contentResolver,
            Settings.Global.AUTO_TIME, checked
        )
    }

    /**
     * 判断系统是否自动获取时间
     * @param context Activity's context.
     * @return If date is auto setting.
     */
    fun checkDateAutoSet(context: Context): Boolean {
        return try {
            Settings.Global.getInt(
                context.contentResolver,
                Settings.Global.AUTO_TIME
            ) > 0
        } catch (exception: SettingNotFoundException) {
            exception.printStackTrace()
            false
        }
    }

    /** 设置系统时区*/
    fun setTimeZone(context: Context, timeZone: String?) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setTimeZone(timeZone)
    }

    /**
     * 设置系统是否自动获取时区
     * @param context Activity's context.
     * @param checked If checked > 0, it will auto set timezone.
     */
    fun setAutoTimeZone(context: Context, checked: Int) {
        Settings.Global.putInt(
            context.contentResolver,
            Settings.Global.AUTO_TIME_ZONE, checked
        )
    }

    /**
     * 判断系统是否自动获取时区
     * @param context Activity's context.
     * @return If timezone is auto setting.
     */
    fun checkTimeZoneAutoSet(context: Context): Boolean {
        return try {
            Settings.Global.getInt(
                context.contentResolver,
                Settings.Global.AUTO_TIME_ZONE
            ) > 0
        } catch (exception: SettingNotFoundException) {
            exception.printStackTrace()
            false
        }
    }
}
