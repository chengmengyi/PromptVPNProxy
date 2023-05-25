package com.demo.vpn.ad

import com.demo.vpn.app0515
import com.demo.vpn.bean.Ad0515Bean
import com.demo.vpn.bean.AdResult0515Bean
import com.demo.vpn.conf.Fire0515
import com.demo.vpn.server.Connect0515Util
import com.demo.vpn.util.HttpUtil
import com.demo.vpn.util.Limit0515Util
import com.demo.vpn.util.Tba0515Util
import com.demo.vpn.util.logPrompt0515
import com.google.android.gms.ads.*
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAdOptions
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject

object LoadAd0515Util {
    const val OPEN="prompt_open"
    const val HOME="prompt_cil"
    const val RESULT="prompt_but"
    const val CONNECT="prompt_intal"
    const val BACK="prompt_goback"

    var fullAdShowing=false

    private val loadingList= arrayListOf<String>()
    private val adResultMap= hashMapOf<String,AdResult0515Bean>()

    private val loadAdIpMap= hashMapOf<String,String>()
    private val loadAdCityMap= hashMapOf<String,String?>()

    fun load(type:String,tryNum:Int=0){
        if(!canLoadInterAd(type)||getLoadingAd(type)||getHasCacheAd(type)|| adNumLimit()){
            return
        }
        val adList = getAdList(type)
        if(adList.isNotEmpty()){
            loadingList.add(type)
            loop0515LoadAd(type,adList.iterator(),tryNum)
        }
    }

    private fun loop0515LoadAd(type: String, iterator: Iterator<Ad0515Bean>, tryNum:Int){
        load0515AdByType(type,iterator.next()){
            if(null==it){
                if(iterator.hasNext()){
                    loop0515LoadAd(type,iterator,tryNum)
                }else{
                    loadingList.remove(type)
                    if(tryNum>0){
                        load(type, tryNum = 0)
                    }
                }
            }else{
                loadingList.remove(type)
                adResultMap[type]=it
            }
        }
    }

    private fun load0515AdByType(type: String, adBean: Ad0515Bean, result: (bean: AdResult0515Bean?) -> Unit){
        logPrompt0515("start load $type ad, ${adBean.toString()}")
        when(adBean.format){
            "open"->{
                AppOpenAd.load(
                    app0515,
                    adBean.id,
                    AdRequest.Builder().build(),
                    AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
                    object : AppOpenAd.AppOpenAdLoadCallback() {
                        override fun onAdLoaded(p0: AppOpenAd) {
                            logPrompt0515("load ad success----$type")
                            saveLoadIp(type)
                            p0.setOnPaidEventListener {
                                upload0515Event(type, it, p0.responseInfo, adBean)
                            }
                            result.invoke(AdResult0515Bean(adTime = System.currentTimeMillis(), adResult = p0))
                        }

                        override fun onAdFailedToLoad(p0: LoadAdError) {
                            super.onAdFailedToLoad(p0)
                            logPrompt0515("load ad fail----$type---${p0.message}")
                            result.invoke(null)
                        }
                    }
                )
            }
            "interstitial"->{
                InterstitialAd.load(
                    app0515,
                    adBean.id,
                    AdRequest.Builder().build(),
                    object : InterstitialAdLoadCallback() {
                        override fun onAdFailedToLoad(p0: LoadAdError) {
                            super.onAdFailedToLoad(p0)
                            logPrompt0515("load ad fail----$type---${p0.message}")
                            result.invoke(null)
                        }

                        override fun onAdLoaded(p0: InterstitialAd) {
                            logPrompt0515("load ad success----$type")
                            saveLoadIp(type)
                            p0.setOnPaidEventListener {
                                upload0515Event(type, it, p0.responseInfo, adBean)
                            }
                            result.invoke(AdResult0515Bean(adTime = System.currentTimeMillis(), adResult = p0))
                        }
                    }
                )
            }
            "native"->{
                AdLoader.Builder(
                    app0515,
                    adBean.id,
                ).forNativeAd {p0->
                    logPrompt0515("load ad success----$type")
                    saveLoadIp(type)
                    p0.setOnPaidEventListener {
                        upload0515Event(type, it, p0.responseInfo, adBean)
                    }
                    result.invoke(AdResult0515Bean(adTime = System.currentTimeMillis(), adResult = p0))
                }
                    .withAdListener(object : AdListener(){
                        override fun onAdFailedToLoad(p0: LoadAdError) {
                            super.onAdFailedToLoad(p0)
                            logPrompt0515("load ad fail----$type---${p0.message}")
                            result.invoke(null)
                        }

                        override fun onAdClicked() {
                            super.onAdClicked()
                            Limit0515Util.addClickNum()
                        }
                    })
                    .withNativeAdOptions(
                        NativeAdOptions.Builder()
                            .setAdChoicesPlacement(
                                NativeAdOptions.ADCHOICES_TOP_LEFT
                            )
                            .build()
                    )
                    .build()
                    .loadAd(AdRequest.Builder().build())
            }
        }
    }

    private fun getAdList(type: String):List<Ad0515Bean>{
        val list= arrayListOf<Ad0515Bean>()
        runCatching {
            val jsonArray = JSONObject(Fire0515.getLocalAdStr()).getJSONArray(type)
            for (index in 0 until jsonArray.length()){
                val jsonObject = jsonArray.getJSONObject(index)
                list.add(
                    Ad0515Bean(
                        jsonObject.optString("prompt_id"),
                        jsonObject.optString("prompt_format"),
                        jsonObject.optString("prompt_source"),
                        jsonObject.optInt("prompt_ppro"),
                    )
                )
            }
        }
        return list.filter { it.source == "admob" }.sortedByDescending { it.pr }
    }

    private fun getLoadingAd(type: String)= loadingList.contains(type)

    fun adNumLimit():Boolean{
        if(Limit0515Util.adNumLimit()){
            logPrompt0515("click num or show num limit")
            return true
        }
        return false
    }

    private fun getHasCacheAd(type: String):Boolean{
        if(adResultMap.containsKey(type)){
            val resultAdBean = adResultMap[type]
            if(null!=resultAdBean?.adResult){
                return if(resultAdBean.adExpired()){
                    deleteAd(type)
                    false
                }else{
                    logPrompt0515("$type cache")
                    true
                }
            }
        }
        return false
    }

    fun canLoadInterAd(type: String):Boolean{
        if((type== CONNECT||type== BACK)&&!Fire0515.canShowInterAd()){
            return false
        }
        return true
    }

    fun deleteAd(type:String){
        adResultMap.remove(type)
    }

    fun getAdByType(type: String)= adResultMap[type]?.adResult

    fun preLoadAd(){
        load(OPEN, tryNum = 1)
        load(RESULT)
        load(CONNECT)
        load(HOME)
    }

    fun planTwoClearAllAd(){
        arrayOf(BACK, CONNECT, HOME, RESULT).forEach {
            adResultMap.remove(it)
            load(it)
        }
    }

    fun disconnectVpnClearAllAd(){
        arrayOf(BACK, CONNECT, HOME, RESULT).forEach {
            val adResult0515Bean = adResultMap[it]
            if(null==adResult0515Bean||adResult0515Bean.adExpired()){
                load(it)
            }
        }
    }

    private fun upload0515Event(type: String, value: AdValue, responseInfo: ResponseInfo?, adBean: Ad0515Bean){
        GlobalScope.launch {
            val commonJson = Tba0515Util.getCommonJson()
            commonJson.apply {
                put("day",JSONObject().apply {
                    put("verbena",value.valueMicros)
                    put("rise",value.currencyCode)
                    put("distort",getAdNetWork(responseInfo?.mediationAdapterClassName?:""))
                    put("poplin","admob")
                    put("estella",adBean.id)
                    put("icarus",type)
                    put("christie","")
                    put("sickbed", getAdFormat(adBean.format))
                    put("cranston",getPrecisionType(value.precisionType))
                    put("sedan", MobileAds.getVersion().toString())
                    put("wafer",loadAdIpMap[type]?:"")
                    put("andorra",getCurrentIp())
                    put("system","influx")
                })

                put("flathead",JSONObject().apply {
                    put("r_city",loadAdCityMap[type]?:"null")
                    put("s_city",get0515CityName())
                })
            }
            HttpUtil.uploadEvent(commonJson)
        }
    }

    private fun saveLoadIp(adType: String){
        loadAdIpMap[adType]=getCurrentIp()
        loadAdCityMap[adType]=get0515CityName()
    }

    private fun get0515CityName() = if(Connect0515Util.isConnected()){
        if (Connect0515Util.currentServer.isFast()){
            Connect0515Util.fastServer.city
        }else{
            Connect0515Util.currentServer.city
        }

    }else{
        "null"
    }

    private fun getCurrentIp()=if(Connect0515Util.isConnected()){
        if (Connect0515Util.currentServer.isFast()){
            Connect0515Util.fastServer.ip
        }else{
            Connect0515Util.currentServer.ip
        }
    }else{
        HttpUtil.ip
    }

    private fun getAdNetWork(string: String):String{
        if(string.contains("facebook")) return "facebook"
        else if(string.contains("admob")) return "admob"
        return ""
    }

    private fun getAdFormat(adType: String):String{
        when(adType){
            "open"->return "open"
            "interstitial"->return "interstitial"
            "native"->return "native"
        }
        return ""
    }

    private fun getPrecisionType(precisionType:Int)=when(precisionType){
        1->"ESTIMATED"
        2->"PUBLISHER_PROVIDED"
        3->"PRECISE"
        else->"UNKNOWN"
    }
}