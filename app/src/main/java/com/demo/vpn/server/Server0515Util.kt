package com.demo.vpn.server

import android.util.Log
import androidx.fragment.app.FragmentManager
import com.demo.vpn.bean.Server0515Bean
import com.demo.vpn.conf.Local0515
import com.demo.vpn.util.HttpUtil
import com.demo.vpn.view.LoadingDialog
import org.json.JSONObject

object Server0515Util {
    val smartServerList= arrayListOf<Server0515Bean>()
    val allServerList= arrayListOf<Server0515Bean>()

//    fun getAllServerList():ArrayList<Server0515Bean>{
//        val list= arrayListOf<Server0515Bean>()
//        list.add(Server0515Bean())
//        list.addAll(Local0515.localServerList)
//        return list
//    }
//
//    fun getFastServerBean():Server0515Bean = Local0515.localServerList.random()

    fun checkSmartSererListEmpty(manager: FragmentManager, isEmpty:(isEmpty:Boolean)->Unit){
        if(smartServerList.isEmpty()){
            HttpUtil.get0515ServerList()
            LoadingDialog{ isEmpty.invoke(true) }.show(manager,"Loading0515Dialog")
        }else{
            isEmpty.invoke(false)
        }
    }

    fun parseServerJson(string: String){
        Log.e("qweraaa",string)
        runCatching {
            val jsonObject = JSONObject(string)
            if(jsonObject.optInt("code")==200){
                val data = jsonObject.getJSONObject("data")
                val smart = data.getJSONArray("aCKcgdy")
                if(smart.length()>0){
                    smartServerList.clear()
                    for (index in 0 until smart.length()){
                        val json = smart.getJSONObject(index)
                        val serverBean = Server0515Bean(
                            pwd = json.optString("QjYCFocx"),
                            port = json.optInt("Ryx"),
                            country = json.optString("faEiPT"),
                            city = json.optString("lKQV"),
                            ip = json.optString("tzvNQXetEI"),
                            account = json.optString("uhMlvJ"),
                        )
                        serverBean.writeServerId()
                        smartServerList.add(serverBean)
                    }
                }
                val all = data.getJSONArray("yZrdYhdb")
                if(all.length()>0){
                    allServerList.clear()
                    for (index in 0 until all.length()){
                        val json = all.getJSONObject(index)
                        val serverBean = Server0515Bean(
                            pwd = json.optString("QjYCFocx"),
                            port = json.optInt("Ryx"),
                            country = json.optString("faEiPT"),
                            city = json.optString("lKQV"),
                            ip = json.optString("tzvNQXetEI"),
                            account = json.optString("uhMlvJ"),
                        )
                        serverBean.writeServerId()
                        allServerList.add(serverBean)
                    }
                }
            }
        }
    }
}