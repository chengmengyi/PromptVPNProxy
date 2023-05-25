package com.demo.vpn.util

import android.os.Bundle
import com.demo.vpn.BuildConfig
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject

object Point0515Util {

    private var fire:FirebaseAnalytics?=null

    init {
        if (!BuildConfig.DEBUG){
            fire=Firebase.analytics
        }
    }

    fun setPoint(key:String,bundle: Bundle= Bundle()){
        runCatching {
            var jsonObject:JSONObject?=null
            if(key=="prompt_vt"){
                val time = bundle.getLong("time")
                jsonObject=JSONObject().apply { put("time",time) }
                logPrompt0515("point====$key =====time:${time}")
            }else{
                logPrompt0515("point====$key =====")
            }
            Tba0515Util.uploadPointEvent(key,jsonObject)
        }
        fire?.logEvent(key,bundle)
    }
}