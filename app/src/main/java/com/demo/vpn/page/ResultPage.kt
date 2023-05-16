package com.demo.vpn.page

import com.demo.vpn.R
import com.demo.vpn.base_page.BasePage0515
import kotlinx.android.synthetic.main.activity_result.*

class ResultPage:BasePage0515() {
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
}