package com.demo.vpn.bean

import com.github.shadowsocks.database.Profile
import com.github.shadowsocks.database.ProfileManager

class Server0515Bean(
    val pwd:String="",
    val account:String="",
    val port:Int=0,
    val country:String="Faster Server",
    val city:String="",
    val ip:String="",
) {

    fun isFast()=country=="Faster Server"&&ip.isEmpty()

    fun getServerId():Long{
        ProfileManager.getActiveProfiles()?.forEach {
            if (it.host==ip&&it.remotePort==port){
                return it.id
            }
        }
        return 0L
    }

    fun writeServerId(){
        val profile = Profile(
            id = 0L,
            name = "$country - $city",
            host = ip,
            remotePort = port,
            password = pwd,
            method = account
        )

        var id:Long?=null
        ProfileManager.getActiveProfiles()?.forEach {
            if (it.remotePort==profile.remotePort&&it.host==profile.host){
                id=it.id
                return@forEach
            }
        }
        if (null==id){
            ProfileManager.createProfile(profile)
        }else{
            profile.id=id!!
            ProfileManager.updateProfile(profile)
        }
    }

}