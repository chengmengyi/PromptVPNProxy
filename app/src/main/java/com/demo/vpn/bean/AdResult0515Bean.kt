package com.demo.vpn.bean

class AdResult0515Bean(
    val adResult:Any?=null,
    val adTime:Long=0L
) {
    fun adExpired()=(System.currentTimeMillis() - adTime) >=3600L*1000
}