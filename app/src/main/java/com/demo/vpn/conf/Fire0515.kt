package com.demo.vpn.conf

import com.demo.vpn.BuildConfig
import com.demo.vpn.bean.Server0515Bean
import com.demo.vpn.util.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.tencent.mmkv.MMKV
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

object Fire0515 {
    var firebaseSuccess=false
    var planTwo=false

    private var prompt_s="1"
    private var prompt_r="50"
    private var prompt_f="2"

    fun getFireConf(){
        readReferrer()
//        writeServer(Local0515.localServerList)

        if (!BuildConfig.DEBUG){
            val remoteConfig = Firebase.remoteConfig
            remoteConfig.fetchAndActivate().addOnCompleteListener {
                if (it.isSuccessful){
                    saveConfigAd(remoteConfig.getString("prompt_ad"))
                    parsePromptConfig(remoteConfig.getString("prompt_config"))
                    firebaseSuccess=true
                }
            }
        }
    }

    private fun parsePromptConfig(string: String){
        runCatching {
            val jsonObject = JSONObject(string)
            prompt_s=jsonObject.optString("prompt_s")
            prompt_r=jsonObject.optString("prompt_r")
            prompt_f=jsonObject.optString("prompt_f")
        }
    }

    private fun saveConfigAd(string: String){
        Limit0515Util.setMaxNum(string)
        MMKV.defaultMMKV().encode("prompt_ad",string)
    }

    private fun writeServer(list:ArrayList<Server0515Bean>){
        list.forEach { it.writeServerId() }
    }

    fun getLocalAdStr():String{
        val s = MMKV.defaultMMKV().decodeString("prompt_ad") ?: ""
        if(s.isEmpty()) {
            return Local0515.localAd234
        }
        return s
    }

    fun isBuyUser():Boolean{
        if (BuildConfig.DEBUG){
            return true
        }
        return readReferrerLocal().buyUser()
    }

    fun isFB():Boolean{
        if (BuildConfig.DEBUG){
            return true
        }
        return readReferrerLocal().isFB()
    }


    fun randomPlan(){
        planTwo=false
        if((!AppRegister.isHotLoad&&prompt_s=="1")||prompt_s=="2"){
            val nextInt = Random().nextInt(100)
            planTwo = str2Int(prompt_r)>=nextInt
        }
    }

    fun canShowInterAd():Boolean{
        //1表示全部展示插屏，2表示买量展示插屏，3表示fb买量展示插屏，4表示不展示插屏；
        return when(prompt_f){
            "1"-> true
            "2"-> isBuyUser()
            "3"-> isFB()
            else-> false
        }
    }
}