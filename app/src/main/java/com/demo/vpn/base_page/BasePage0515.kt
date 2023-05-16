package com.demo.vpn.base_page

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.demo.vpn.util.metrics
import com.gyf.immersionbar.ImmersionBar

abstract class BasePage0515:AppCompatActivity() {
    var resume=false
    protected lateinit var immersionBar: ImmersionBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        metrics()
        setContentView(layout())
        immersionBar=ImmersionBar.with(this).apply {
            statusBarAlpha(0f)
            autoDarkModeEnable(true)
            statusBarDarkFont(false)
            init()
        }
        initView()
    }

    abstract fun layout():Int

    abstract fun initView()

    override fun onResume() {
        super.onResume()
        resume=true
    }

    override fun onPause() {
        super.onPause()
        resume=false
    }

    override fun onStop() {
        super.onStop()
        resume=false
    }
}