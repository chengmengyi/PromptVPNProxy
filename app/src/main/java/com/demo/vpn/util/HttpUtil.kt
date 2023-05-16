package com.demo.vpn.util

import android.webkit.WebView
import com.demo.vpn.app0515
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONObject

object HttpUtil {
    var ip=""
    var countryCode=""

    fun getIpInfo(callback:()->Unit){
        if(ip.isNotEmpty()){
            callback.invoke()
            return
        }
        OkGo.get<String>("https://ipapi.co/json")
            .headers("User-Agent", WebView(app0515).settings.userAgentString)
            .execute(object : StringCallback(){
                override fun onSuccess(response: Response<String>?) {
                    runCatching {
                        val jsonObject = JSONObject(response?.body()?.toString())
                        countryCode=jsonObject.optString("country_code")
                        ip=jsonObject.optString("ip")
                    }
                    callback.invoke()
                }

                override fun onError(response: Response<String>?) {
                    super.onError(response)
                    callback.invoke()                }
            })
    }
}