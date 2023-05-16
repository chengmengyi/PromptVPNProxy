package com.demo.vpn.util

import com.demo.vpn.BuildConfig

object LimitUtil {
    var limitArea=false

    fun checkLimitUser(){
        HttpUtil.getIpInfo {
            if(!BuildConfig.DEBUG){
                limitArea=HttpUtil.countryCode.limitArea0515()
            }
        }
    }

    private fun String.limitArea0515()=contains("IR")||contains("MO")||contains("HK")||contains("CN")
}