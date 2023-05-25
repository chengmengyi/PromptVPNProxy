package com.demo.vpn.util

import android.util.Base64
import android.util.Log
import android.webkit.WebView
import com.demo.vpn.BuildConfig
import com.demo.vpn.app0515
import com.demo.vpn.server.Connect0515Util
import com.demo.vpn.server.Server0515Util
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONObject

object HttpUtil {
    var ip=""
    var countryCode=""
    var requestServerIng=false

    private val TBA= if (BuildConfig.DEBUG) "https://test-robe.secureprompt.net/corsica/strung/drench" else "https://robe.secureprompt.net/kappa/pyroxene/clam"
    private val SERVER=if (BuildConfig.DEBUG) "https://test.promptvpnproxy.com" else "https://api.promptvpnproxy.com"

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
                    callback.invoke()
                }
            })
    }


    fun uploadEvent(data: JSONObject,install:Boolean=false){
        val path="$TBA?cabot=${getDeviceModel()}&fuji=${getBundleId(app0515)}&gauche=${getNetworkType(app0515)}"
        logPrompt0515(path)
        logPrompt0515(data.toString())
        OkGo.post<String>(path)
            .retryCount(3)
            .headers("content-type","application/json")
            .headers("lofty", getManufacturer())
            .headers("angela", getLogId())
            .upJson(data)
            .execute(object :StringCallback(){
                override fun onSuccess(response: Response<String>?) {
                    if (install){
                        if (data.getJSONObject("penh").optString("mare").isEmpty()){
                            installNoReferrerPrompt()
                        }else{
                            installHasReferrerPrompt()
                        }
                    }
                    logPrompt0515("=onSuccess==${response?.body()?.toString()}==")
                }

                override fun onError(response: Response<String>?) {
                    super.onError(response)
                    logPrompt0515("=onError==${response?.body()?.toString()}==")
                }
            })
    }

    fun get0515ServerList(){
        if(!requestServerIng){
            requestServerIng=true
            Tba0515Util.uploadPointEvent("prompt_req_servers")
            OkGo.get<String>("$SERVER/swi/cmdk/")
                .headers("AISN", getOsCountry())
                .headers("SNM", app0515.packageName)
                .headers("SKW", getAndroidId(app0515))
                .execute(object : StringCallback(){
                    override fun onSuccess(response: Response<String>?) {
                        requestServerIng=false
                        runCatching {
                            val s = response?.body()?.toString() ?: ""
                            val substring = s.substring(2, s.length - 3)
                            val string = String(Base64.decode(substring.reversed(), Base64.DEFAULT))
                            Tba0515Util.uploadPointEvent("prompt_get_servers")
                            Server0515Util.parseServerJson(string)
                        }
                    }

                    override fun onError(response: Response<String>?) {
                        super.onError(response)
                        requestServerIng=false
                    }
                })
        }
    }

    fun serverHeartUpload(prerequisite:Boolean){
        val url="https://${Connect0515Util.getConnectedIp()}/wiml/aap/?adverbs=${getBundleId(app0515)}&workman=${getAppVersion(app0515)}&diode=${getAppVersion(app0515)}&prerequisite=${if (prerequisite) "op" else "pa"}&custom=ss"
        OkGo.get<String>(url)
            .headers("AISN", getOsCountry())
            .headers("SNM", app0515.packageName)
            .headers("SKW", getAndroidId(app0515))
            .execute(object : StringCallback(){
                override fun onSuccess(response: Response<String>?) {}

                override fun onError(response: Response<String>?) {
                    super.onError(response)
                }
            })
    }
}