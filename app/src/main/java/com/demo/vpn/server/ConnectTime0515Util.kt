package com.demo.vpn.server

import com.demo.vpn.interfaces.IConnectTimeInterface
import com.demo.vpn.util.HttpUtil
import kotlinx.coroutines.*

object ConnectTime0515Util {
    private var connectTime=0L
    private var timeJob:Job?=null
    private var iConnectTimeInterface:IConnectTimeInterface?=null

    fun setConnectTimeInterface(iConnectTimeInterface:IConnectTimeInterface?){
        this.iConnectTimeInterface=iConnectTimeInterface
    }

    fun startTime(){
        if (null!= timeJob) return
        timeJob = GlobalScope.launch(Dispatchers.Main) {
            while (null!=timeJob) {
                iConnectTimeInterface?.connectTimeCallback(connectTime)
                connectTime++
                if(connectTime%60==0L){
                    HttpUtil.serverHeartUpload(true)
                }
                delay(1000L)
            }
        }
    }

    fun endTime(){
        timeJob?.cancel()
        timeJob=null
    }

    fun resetTime(){
        connectTime=0L
    }

    fun getAllTime()=connectTime
}