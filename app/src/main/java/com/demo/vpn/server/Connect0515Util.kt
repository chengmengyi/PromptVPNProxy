package com.demo.vpn.server

import android.os.Bundle
import com.demo.vpn.ad.LoadAd0515Util
import com.demo.vpn.base_page.BasePage0515
import com.demo.vpn.bean.Server0515Bean
import com.demo.vpn.conf.Fire0515
import com.demo.vpn.interfaces.IServerStateListener
import com.demo.vpn.util.HttpUtil
import com.demo.vpn.util.Point0515Util
import com.github.shadowsocks.Core
import com.github.shadowsocks.aidl.IShadowsocksService
import com.github.shadowsocks.aidl.ShadowsocksConnection
import com.github.shadowsocks.bg.BaseService
import com.github.shadowsocks.preference.DataStore
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object Connect0515Util : ShadowsocksConnection.Callback {
    private var autoConnect=false
    private var basePage:BasePage0515?=null
    private var connectState = BaseService.State.Stopped
    var currentServer= Server0515Bean()
    var lastServer= Server0515Bean()
    var fastServer= Server0515Bean()
    private val sc= ShadowsocksConnection(true)
    private var serviceStateListener:IServerStateListener?=null

    fun init(basePage:BasePage0515,serviceStateListener:IServerStateListener){
        this.basePage=basePage
        this.serviceStateListener=serviceStateListener
        sc.connect(basePage,this)
    }

    fun getConnectedIp():String{
        return if(currentServer.isFast()){
            fastServer.ip
        }else{
            currentServer.ip
        }
    }

    fun connect(autoConnect:Boolean){
        this.autoConnect=autoConnect
        connectState= BaseService.State.Connecting
        GlobalScope.launch {
            var profileId=0L
            if(currentServer.isFast()){
                fastServer=Server0515Util.smartServerList.random()
                profileId = fastServer.getServerId()
            }else{
                profileId = currentServer.getServerId()
            }
            DataStore.profileId = profileId
            Core.startService()
        }
    }

    fun disconnect(){
        connectState= BaseService.State.Stopping
        GlobalScope.launch {
            Core.stopService()
        }
    }

    fun isConnected()= connectState== BaseService.State.Connected

    fun isDisconnected()= connectState== BaseService.State.Stopped

    fun connectServerSuccess(connect: Boolean)=if (connect) isConnected() else isDisconnected()

    fun appToBackgroundSetState(state: BaseService.State){
        connectState=state
        if(state==BaseService.State.Connected){
            currentServer= lastServer
        }
    }

    override fun stateChanged(state: BaseService.State, profileName: String?, msg: String?) {
        this.connectState=state
        if (isConnected()){
            lastServer= currentServer
            HttpUtil.serverHeartUpload(true)
            Point0515Util.setPoint("prompt_vs")
            if (autoConnect&&Fire0515.planTwo){
                LoadAd0515Util.planTwoClearAllAd()
            }
        }
        if (isDisconnected()){
            HttpUtil.serverHeartUpload(false)
            ConnectTime0515Util.endTime()
            val bundle = Bundle()
            bundle.putLong("time",ConnectTime0515Util.getAllTime())
            Point0515Util.setPoint("prompt_vt", bundle = bundle)
            serviceStateListener?.disConnectSuccess()
        }
    }

    override fun onServiceConnected(service: IShadowsocksService) {
        val state = BaseService.State.values()[service.state]
        this.connectState=state
        if (isConnected()){
            lastServer= currentServer
            serviceStateListener?.onServiceConnected()
        }
    }

    override fun onBinderDied() {
        basePage?.let {
            sc.disconnect(it)
        }
    }

    fun onDestroy(){
        onBinderDied()
        basePage=null
        serviceStateListener=null
    }
}