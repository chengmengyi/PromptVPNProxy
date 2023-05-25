package com.demo.vpn.util

import com.demo.vpn.BuildConfig
import com.demo.vpn.util.Limit0515Util.limitArea
import com.tencent.mmkv.MMKV
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

object Limit0515Util {
    var limitArea=false

    private var maxShow0515=50
    private var maxClick0515=5

    private var click0515=0
    private var show0515=0

    private val refreshNativeMap = hashMapOf<String,Boolean>()

    fun checkRefreshNative(type:String)= refreshNativeMap[type]?:true

    fun setRefreshBool(type: String,boolean: Boolean){
        refreshNativeMap[type]=boolean
    }

    fun removeRefreshMap(){
        refreshNativeMap.clear()
    }

    fun checkLimitUser(){
        HttpUtil.getIpInfo {
            if(!BuildConfig.DEBUG){
                limitArea=HttpUtil.countryCode.limitArea0515()
            }
        }
    }

    fun setMaxNum(string: String){
        runCatching {
            val jsonObject = JSONObject(string)
            maxShow0515=jsonObject.optInt("prompt_show")
            maxClick0515=jsonObject.optInt("prompt_click")
        }
    }

    fun readCurrentNum(){
        click0515= MMKV.defaultMMKV().decodeInt(numKey("maxClick0515"),0)
        show0515= MMKV.defaultMMKV().decodeInt(numKey("maxShow0515"),0)
    }

    fun addClickNum(){
        click0515++
        saveNum("maxClick0515", click0515)
    }

    fun addShowNum(){
        show0515++
        saveNum("maxShow0515", show0515)
    }

    fun adNumLimit()= click0515>= maxClick0515|| show0515>= maxShow0515

    private fun saveNum(key:String,value:Int){
        MMKV.defaultMMKV().encode(numKey(key),value)
    }

    private fun numKey(key:String)="${SimpleDateFormat("yyyy-MM-dd").format(Date(System.currentTimeMillis()))}_${key}"


    private fun String.limitArea0515()=contains("IR")||contains("MO")||contains("HK")||contains("CN")
}