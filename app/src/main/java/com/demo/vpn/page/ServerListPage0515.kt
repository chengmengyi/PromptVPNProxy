package com.demo.vpn.page

import android.content.Intent
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.vpn.R
import com.demo.vpn.adapter.ServerListAdapter
import com.demo.vpn.base_page.BasePage0515
import com.demo.vpn.bean.Server0515Bean
import com.demo.vpn.server.Connect0515Util
import kotlinx.android.synthetic.main.activity_server_list.*

class ServerListPage0515:BasePage0515() {
    override fun layout(): Int = R.layout.activity_server_list

    override fun initView() {
        immersionBar.statusBarView(view_top).init()
        rv_server.apply {
            layoutManager=LinearLayoutManager(this@ServerListPage0515)
            adapter=ServerListAdapter(this@ServerListPage0515){ clickItem(it) }
        }

        iv_back.setOnClickListener { onBackPressed() }
    }

    private fun clickItem(server0515Bean: Server0515Bean){
        val current = Connect0515Util.currentServer
        val connected = Connect0515Util.isConnected()
        if(connected&&current.ip!=server0515Bean.ip){
            AlertDialog.Builder(this).apply {
                setMessage("If you want to connect to another VPN, you need to disconnect the current connection first. Do you want to disconnect the current connection?")
                setPositiveButton("sure") { _, _ ->
                    chooseSure("server_dis_0515",server0515Bean)
                }
                setNegativeButton("cancel",null)
                show()
            }
        }else{
            if (connected){
                chooseSure("",server0515Bean)
            }else{
                chooseSure("server_con_0515",server0515Bean)
            }
        }
    }

    private fun chooseSure(result:String,server0515Bean: Server0515Bean){
        Connect0515Util.currentServer=server0515Bean
        setResult(515, Intent().apply {
            putExtra("result",result)
        })
        finish()
    }

    override fun onBackPressed() {
        finish()
    }
}