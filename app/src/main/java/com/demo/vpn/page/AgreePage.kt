package com.demo.vpn.page

import com.demo.vpn.R
import com.demo.vpn.base_page.BasePage0515
import kotlinx.android.synthetic.main.activity_agree.*

class AgreePage:BasePage0515() {
    override fun layout(): Int = R.layout.activity_agree

    override fun initView() {
        immersionBar.statusBarView(view_top).init()
        iv_back.setOnClickListener { finish() }
        web.apply {
            settings.javaScriptEnabled=true
            loadUrl("")
        }
    }
}