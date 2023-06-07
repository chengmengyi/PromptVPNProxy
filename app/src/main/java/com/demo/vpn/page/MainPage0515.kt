package com.demo.vpn.page

import android.animation.ValueAnimator
import android.content.Intent
import android.view.animation.LinearInterpolator
import androidx.core.animation.doOnEnd
import com.demo.vpn.BuildConfig
import com.demo.vpn.R
import com.demo.vpn.ad.LoadAd0515Util
import com.demo.vpn.ad.LoadAd0515Util.preLoadAd
import com.demo.vpn.ad.Show0515FullAd
import com.demo.vpn.base_page.BasePage0515
import com.demo.vpn.conf.Fire0515
import com.demo.vpn.server.Connect0515Util
import com.demo.vpn.util.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*

class MainPage0515 : BasePage0515() {
    private var waitJob:Job?=null
    private var progressAnimator: ValueAnimator?=null
    private val showOpenAd = Show0515FullAd(LoadAd0515Util.OPEN,this)

    override fun layout(): Int = R.layout.activity_main

    override fun initView() {
        HttpUtil.get0515ServerList()
        Limit0515Util.readCurrentNum()
        Limit0515Util.removeRefreshMap()
        waitFirebaseData()
        startAnimator()
    }

    private fun waitFirebaseData(){
        if(BuildConfig.DEBUG){
            preLoadAd()
        }else{
            if (!AppRegister.isHotLoad&&!Fire0515.firebaseSuccess){
                var time=0
                waitJob=GlobalScope.launch {
                    while (true){
                        if (!isActive) {
                            break
                        }
                        time++
                        if(Fire0515.firebaseSuccess||time>=8){
                            cancel()
                            runOnUiThread { preLoadAd() }
                        }
                        delay(500L)
                    }
                }
            }else{
                preLoadAd()
            }
        }
    }

    private fun startAnimator(){
        progressAnimator = ValueAnimator.ofInt(0, 100).apply {
            duration = 10000L
            interpolator = LinearInterpolator()
            addUpdateListener {
                val progress = it.animatedValue as Int
                progress_view.progress = progress
                val pro = (10 * (progress / 100.0F)).toInt()
                if(pro in 2..9){
                    showOpenAd.show0515(
                        showingAd = {
                            endAnimator()
                            progress_view.progress = 100
                        },
                        closeAd = {
                            toConnectPage()
                        }
                    )
                }else if (pro>=10){
                    toConnectPage()
                }
            }
            start()
        }
    }


    private fun toConnectPage(autoConnect:Boolean=false){
        startActivity(Intent(this, ConnectPage0515::class.java).apply {
            putExtra("autoConnect",autoConnect)
        })
        finish()
    }

    private fun endAnimator(){
        progressAnimator?.removeAllUpdateListeners()
        progressAnimator?.cancel()
        progressAnimator=null
    }


    override fun onResume() {
        super.onResume()
        if (progressAnimator?.isPaused==true){
            progressAnimator?.resume()
        }
    }

    override fun onPause() {
        super.onPause()
        progressAnimator?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        endAnimator()
        waitJob?.cancel()
        waitJob=null
    }

    override fun onBackPressed() {

    }
}