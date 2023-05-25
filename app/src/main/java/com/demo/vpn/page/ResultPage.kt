package com.demo.vpn.page

import com.demo.vpn.R
import com.demo.vpn.ad.LoadAd0515Util
import com.demo.vpn.ad.Show0515NativeAd
import com.demo.vpn.base_page.BasePage0515
import com.demo.vpn.util.Limit0515Util
import kotlinx.android.synthetic.main.activity_result.*

class ResultPage:BasePage0515() {
    private val showResultAd = Show0515NativeAd(LoadAd0515Util.RESULT,this)

    override fun layout(): Int = R.layout.activity_result

    override fun initView() {
        immersionBar.statusBarView(view_top).init()
        iv_back.setOnClickListener { finish() }

        val connect = intent.getBooleanExtra("connect", false)
        if(!connect){
            tv_result.text="Disconnected succeeded"
            iv_result.setImageResource(R.drawable.result2)
        }
    }

    override fun onResume() {
        super.onResume()
        showResultAd.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        showResultAd.endCheck()
        Limit0515Util.setRefreshBool(LoadAd0515Util.RESULT,true)
    }
}