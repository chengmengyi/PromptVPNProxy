package com.demo.vpn.ad

import com.demo.vpn.base_page.BasePage0515
import com.demo.vpn.conf.Fire0515
import com.demo.vpn.util.Limit0515Util
import com.demo.vpn.util.logPrompt0515
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Show0515FullAd(
    private val type:String,
    private val basePage0515: BasePage0515
) {
    var closeAd: (() -> Unit?)? =null

    fun show0515(noAdBack:Boolean=false, showingAd:()->Unit,closeAd:()->Unit){
        this.closeAd=closeAd
        val adByType = LoadAd0515Util.getAdByType(type)
        if(null==adByType){
            if(noAdBack){
                LoadAd0515Util.load(type)
                closeAd.invoke()
            }
        }else{
            if(LoadAd0515Util.fullAdShowing||!basePage0515.resume){
                closeAd.invoke()
                return
            }
            logPrompt0515("show $type ad")
            showingAd.invoke()
            when(adByType){
                is InterstitialAd ->{
                    adByType.fullScreenContentCallback= fullCallback
                    adByType.show(basePage0515)
                }
                is AppOpenAd ->{
                    adByType.fullScreenContentCallback= fullCallback
                    adByType.show(basePage0515)
                }
            }
        }
    }

    private val fullCallback = object : FullScreenContentCallback(){
        override fun onAdDismissedFullScreenContent() {
            super.onAdDismissedFullScreenContent()
            LoadAd0515Util.fullAdShowing =false
            closeAd()
        }

        override fun onAdShowedFullScreenContent() {
            super.onAdShowedFullScreenContent()
            LoadAd0515Util.fullAdShowing =true
            Limit0515Util.addShowNum()
            LoadAd0515Util.deleteAd(type)
        }

        override fun onAdFailedToShowFullScreenContent(p0: AdError) {
            super.onAdFailedToShowFullScreenContent(p0)
            LoadAd0515Util.fullAdShowing =false
            LoadAd0515Util.deleteAd(type)
            closeAd()
        }


        override fun onAdClicked() {
            super.onAdClicked()
            Limit0515Util.addClickNum()
        }
    }

    private fun closeAd(){
        if (type!= LoadAd0515Util.OPEN&&type!= LoadAd0515Util.BACK){
            LoadAd0515Util.load(type)
        }
        GlobalScope.launch(Dispatchers.Main) {
            delay(200L)
            if (basePage0515.resume){
                closeAd?.invoke()
            }
        }
    }
}