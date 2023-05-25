package com.demo.vpn

import android.app.Application
import com.demo.vpn.conf.Fire0515
import com.demo.vpn.page.ConnectPage0515
import com.demo.vpn.util.AppRegister
import com.demo.vpn.util.Limit0515Util
import com.demo.vpn.util.Tba0515Util
import com.demo.vpn.util.processName
import com.github.shadowsocks.Core
import com.google.android.gms.ads.MobileAds
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.tencent.mmkv.MMKV

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
        MobileAds.initialize(this)
        MMKV.initialize(this)
        AppRegister.register(this)
        Limit0515Util.checkLimitUser()
        Fire0515.getFireConf()
        Tba0515Util.uploadInstallSessionEvent()
    }
}