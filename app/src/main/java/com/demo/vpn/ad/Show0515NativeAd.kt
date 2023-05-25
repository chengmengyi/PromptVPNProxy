package com.demo.vpn.ad

import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.utils.widget.ImageFilterView
import com.blankj.utilcode.util.SizeUtils
import com.demo.vpn.R
import com.demo.vpn.base_page.BasePage0515
import com.demo.vpn.util.Limit0515Util
import com.demo.vpn.util.logPrompt0515
import com.demo.vpn.util.showView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import kotlinx.coroutines.*

class Show0515NativeAd(
    private val type:String,
    private val basePage0515: BasePage0515
) {
    private var lastNativeAd:NativeAd?=null
    private var checkNativeAdJob:Job?=null

    fun show(){
        if(!Limit0515Util.checkRefreshNative(type)){
            return
        }
        LoadAd0515Util.load(type)
        endCheck()
        checkNativeAdJob = GlobalScope.launch(Dispatchers.Main)  {
            delay(300L)
            if (!basePage0515.resume){
                return@launch
            }
            while (true){
                if(!isActive){
                    break
                }
                val adByType = LoadAd0515Util.getAdByType(type)
                if(null!=adByType&&adByType is NativeAd){
                    cancel()
                    if(basePage0515.resume){
                        lastNativeAd?.destroy()
                        lastNativeAd=adByType
                        startShowNativeAd()
                    }
                }
                delay(1000L)
            }
        }
    }
    
    private fun startShowNativeAd(){
        if(null==lastNativeAd){
            return
        }
        logPrompt0515("show $type ad ")
        val ad_native_view = basePage0515.findViewById<NativeAdView>(R.id.ad_native_view)
        ad_native_view.iconView=basePage0515.findViewById(R.id.ad_logo)
        (ad_native_view.iconView as ImageFilterView).setImageDrawable(lastNativeAd?.icon?.drawable)

        ad_native_view.callToActionView=basePage0515.findViewById(R.id.ad_install)
        (ad_native_view.callToActionView as AppCompatTextView).text=lastNativeAd?.callToAction

        ad_native_view.mediaView=basePage0515.findViewById(R.id.ad_cover)
        lastNativeAd?.mediaContent?.let {
            ad_native_view.mediaView?.apply {
                setMediaContent(it)
                setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                outlineProvider = provider
            }
        }

        ad_native_view.bodyView=basePage0515.findViewById(R.id.ad_desc)
        (ad_native_view.bodyView as AppCompatTextView).text=lastNativeAd?.body


        ad_native_view.headlineView=basePage0515.findViewById(R.id.ad_title)
        (ad_native_view.headlineView as AppCompatTextView).text=lastNativeAd?.headline

        ad_native_view.setNativeAd(lastNativeAd!!)
        basePage0515.findViewById<AppCompatImageView>(R.id.ad_default).showView(false)
        showNativeFinish()
    }

    private fun showNativeFinish(){
        Limit0515Util.addShowNum()
        LoadAd0515Util.deleteAd(type)
        LoadAd0515Util.load(type)
        Limit0515Util.setRefreshBool(type,false)
    }

    private val provider = object : ViewOutlineProvider() {
        override fun getOutline(view: View?, outline: Outline?) {
            if (view == null || outline == null) return
            outline.setRoundRect(
                0,
                0,
                view.width,
                view.height,
                SizeUtils.dp2px(12F).toFloat()
            )
            view.clipToOutline = true
        }
    }

    fun endCheck(){
        checkNativeAdJob?.cancel()
        checkNativeAdJob=null
    }
}