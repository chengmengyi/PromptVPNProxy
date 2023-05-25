package com.demo.vpn

import android.app.Application
import com.demo.vpn.conf.Fire0515
import com.demo.vpn.page.ConnectPage0515
import com.demo.vpn.util.AppRegister
import com.demo.vpn.util.HttpUtil
import com.demo.vpn.util.LimitUtil
import com.demo.vpn.util.processName
import com.github.shadowsocks.Core
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize

lateinit var app0515: App0515
class App0515:Application() {
    override fun onCreate() {
        super.onCreate()
        app0515=this
        Core.init(this,ConnectPage0515::class)
        if (!packageName.equals(processName(this))){
            return
        }
        Firebase.initialize(this)
        AppRegister.register(this)
        LimitUtil.checkLimitUser()
        Fire0515.getFireConf()
    }
}