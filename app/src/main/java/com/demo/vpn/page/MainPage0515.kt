package com.demo.vpn.page

import android.animation.ValueAnimator
import android.content.Intent
import android.view.animation.LinearInterpolator
import androidx.core.animation.doOnEnd
import com.demo.vpn.R
import com.demo.vpn.base_page.BasePage0515
import kotlinx.android.synthetic.main.activity_main.*

class MainPage0515 : BasePage0515() {
    private var progressAnimator: ValueAnimator?=null

    override fun layout(): Int = R.layout.activity_main

    override fun initView() {
        startAnimator()
    }

    private fun startAnimator(){
        progressAnimator = ValueAnimator.ofInt(0, 100).apply {
            duration = 3000L
            interpolator = LinearInterpolator()
            addUpdateListener {
                val progress = it.animatedValue as Int
                progress_view.progress = progress
//                val pro = (10 * (progress / 100.0F)).toInt()
//                if(pro in 2..9){
//                    showOpenAd.showOpenAd(
//                        showing = {
//                            stopAnimator()
//                            progress_view.progress = 100
//                        },
//                        close = {
//                            checkPlan()
//                        }
//                    )
//                }else if (pro>=10){
//                    checkPlan()
//                }
            }
            doOnEnd { toConnectPage() }
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
    }

    override fun onBackPressed() {

    }
}