package com.demo.vpn.util

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.blankj.utilcode.util.ActivityUtils
import com.demo.vpn.interfaces.IAppToBackgroundInterface
import com.demo.vpn.page.ConnectPage0515
import com.demo.vpn.page.MainPage0515
import com.google.android.gms.ads.AdActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object AppRegister {
    var front=true
    var isHotLoad=false
    private var hotReload=false
    private var job: Job?=null
    private var iAppToBackgroundInterface:IAppToBackgroundInterface?=null


    fun setIAppToBackgroundInterface(iAppToBackgroundInterface:IAppToBackgroundInterface?){
        this.iAppToBackgroundInterface=iAppToBackgroundInterface
    }

    fun register(application: Application){
        application.registerActivityLifecycleCallbacks(callback)
    }

    private val callback=object : Application.ActivityLifecycleCallbacks{
        private var pages=0
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

        override fun onActivityStarted(activity: Activity) {
            pages++
            job?.cancel()
            job=null
            if (pages==1){
                front=true
                if (hotReload){
                    isHotLoad=true
                    activity.startActivity(Intent(activity, MainPage0515::class.java))
                }
                hotReload=false
            }
        }

        override fun onActivityResumed(activity: Activity) {}

        override fun onActivityPaused(activity: Activity) {}

        override fun onActivityStopped(activity: Activity) {
            pages--
            if (pages<=0){
                front=false
                iAppToBackgroundInterface?.appToBackground()
                job= GlobalScope.launch {
                    delay(2500L)
                    ActivityUtils.finishActivity(MainPage0515::class.java)
                    ActivityUtils.finishActivity(AdActivity::class.java)
                    delay(500)
                    hotReload=true
                }
            }
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

        override fun onActivityDestroyed(activity: Activity) {}
    }
}