package com.demo.vpn.server

import com.demo.vpn.interfaces.IConnectTimeInterface
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
}