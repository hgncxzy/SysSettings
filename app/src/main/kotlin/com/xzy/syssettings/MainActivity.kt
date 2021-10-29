package com.xzy.syssettings

import android.app.Activity
import android.os.Bundle
import com.xzy.syssettings.utils.LogUtil
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        LogUtil.wInfo("activity启动")
        btn6.setOnClickListener {
            startActivity(Intent(this, MainActivity2::class.java))
            finish()
        }
    }

    override fun onStop() {
        super.onStop()
        LogUtil.wInfo("activity finish")
        finish()
    }
}
