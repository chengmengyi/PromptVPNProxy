package com.demo.vpn.conf

import com.demo.vpn.bean.Server0515Bean

object Fire0515 {

    fun getFireConf(){
        writeServer(Local0515.localServerList)
    }

    private fun writeServer(list:ArrayList<Server0515Bean>){
        list.forEach { it.writeServerId() }
    }
}