package com.demo.vpn.server

import com.demo.vpn.bean.Server0515Bean
import com.demo.vpn.conf.Local0515

object Server0515Util {

    fun getAllServerList():ArrayList<Server0515Bean>{
        val list= arrayListOf<Server0515Bean>()
        list.add(Server0515Bean())
        list.addAll(Local0515.localServerList)
        return list
    }

    fun getFastServerBean():Server0515Bean = Local0515.localServerList.random()
}