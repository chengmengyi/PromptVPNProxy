package com.demo.vpn.util

import android.os.Bundle
import android.webkit.WebSettings
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.android.installreferrer.api.ReferrerDetails
import com.demo.vpn.app0515
import com.github.shadowsocks.Core
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject

object Tba0515Util {

    fun uploadInstallSessionEvent(){
        HttpUtil.getIpInfo {
            readInstallReferrerClient()
            uploadSessionEvent()
        }
    }


    private fun uploadSessionEvent(){
        GlobalScope.launch {
            HttpUtil.uploadEvent(getCommonJson().apply { put("diameter",JSONObject()) })
        }
    }

    private fun readInstallReferrerClient(){
        if(checkInstallHasReferrerPrompt()&& checkInstallNoReferrerPrompt()){
            return
        }
        val referrerClient = InstallReferrerClient.newBuilder(Core.app).build()
        referrerClient.startConnection(object : InstallReferrerStateListener {
            override fun onInstallReferrerSetupFinished(responseCode: Int) {
                runCatching {
                    when (responseCode) {
                        InstallReferrerClient.InstallReferrerResponse.OK -> {
                            val response = referrerClient.installReferrer
                            uploadInstallEvent(response)
                        }
                        else->{
                            uploadInstallEvent(null)
                        }
                    }
                }
                runCatching {
                    referrerClient.endConnection()
                }
            }
            override fun onInstallReferrerServiceDisconnected() {
            }
        })
    }

    private fun uploadInstallEvent(response: ReferrerDetails?){
        if ((null==response&& checkInstallNoReferrerPrompt())||(null!=response&& checkInstallHasReferrerPrompt())){
            return
        }
        GlobalScope.launch {
            val commonJson = getCommonJson()
            commonJson.put("penh", JSONObject().apply {
                put("debra",getBuild())
                put("sneak", WebSettings.getDefaultUserAgent(app0515))
                put("lie","ovid")
                put("heptane",getFirstInstallTime(app0515))
                put("testy",getLastUpdateTime(app0515))
                if (null==response){
                    put("hornet","")
                    put("mare","")
                    put("teak", 0)
                    put("prude", 0)
                    put("palmetto", 0)
                    put("hashish", 0)
                }else{
                    put("hornet",response.installReferrer)
                    put("mare",response.installVersion)
                    put("teak", response.referrerClickTimestampSeconds)
                    put("prude", response.installBeginTimestampSeconds)
                    put("palmetto", response.referrerClickTimestampServerSeconds)
                    put("hashish", response.installBeginTimestampServerSeconds)
                }
            })
            HttpUtil.uploadEvent(commonJson,install = true)
        }
    }

    fun uploadPointEvent(key:String,jsonObject: JSONObject?=null){
        HttpUtil.getIpInfo {
            GlobalScope.launch {
                val com = getCommonJson().apply { put("knuckle", key) }
                if(null!=jsonObject){
                    com.put(key,jsonObject)
                }
                HttpUtil.uploadEvent(com)
            }
        }
    }

    fun getCommonJson():JSONObject{
        return JSONObject().apply {
            put("ammeter",JSONObject().apply {
                put("langur", getScreenRes(app0515))
                put("footmen", System.currentTimeMillis())
                put("ink", getAndroidId(app0515))
                put("grail", getGaid(app0515))
                put("alder", getZoneOffset())
                put("vista", "cloud")
                put("lust", getOsVersion())
            })
            put("sperm",JSONObject().apply {
                put("angela", getLogId())
                put("fuji", getBundleId(app0515))
                put("obey", getOperator(app0515))
                put("adjacent", getAppVersion(app0515))
                put("lofty", getManufacturer())
            })
            put("siskin",JSONObject().apply {
                put("gauche", getNetworkType(app0515))
                put("noose", HttpUtil.ip)
                put("crayon", getBrand())
                put("oratorio", getSystemLanguage())
                put("occident", getOsCountry())
                put("cabot", getDeviceModel())
                put("din", getDistinctId(app0515))
            })
        }
    }
}