package com.demo.vpn.util

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.demo.vpn.BuildConfig
import com.demo.vpn.R
import com.demo.vpn.app0515
import com.demo.vpn.conf.Fire0515
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.tencent.mmkv.MMKV
import java.lang.Exception
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

fun logPrompt0515(string: String){
    if(BuildConfig.DEBUG){
        Log.e("qwer",string)
    }
}

fun View.showView(show:Boolean){
    visibility=if (show) View.VISIBLE else View.GONE
}

fun get0515ServerLogo(name:String)=when(name.replace(" ".toRegex(), "").toLowerCase()){
    "australia"-> R.drawable.australia
    "belgium"-> R.drawable.belgium
    "brazil"-> R.drawable.brazil
    "canada"-> R.drawable.canada
    "france"-> R.drawable.france
    "germany"-> R.drawable.germany
    "india"-> R.drawable.india
    "ireland"-> R.drawable.ireland
    "italy"-> R.drawable.italy
    "koreasouth"-> R.drawable.koreasouth
    "netherlands"-> R.drawable.netherlands
    "newzealand"-> R.drawable.newzealand
    "norway"-> R.drawable.norway
    "singapore"-> R.drawable.singapore
    "sweden"-> R.drawable.sweden
    "switzerland"-> R.drawable.switzerland
    "unitedkingdom"-> R.drawable.unitedkingdom
    "unitedstates"-> R.drawable.unitedstates
    "japan"-> R.drawable.japan
    else-> R.drawable.fast
}

fun Context.getNetStatus(): Int {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetworkInfo = connectivityManager.activeNetworkInfo
    if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
        if (activeNetworkInfo.type == ConnectivityManager.TYPE_WIFI) {
            return 2
        } else if (activeNetworkInfo.type == ConnectivityManager.TYPE_MOBILE) {
            return 0
        }
    } else {
        return 1
    }
    return 1
}

fun Context.showToast(string: String){
    Toast.makeText(this,string, Toast.LENGTH_LONG).show()
}

fun transTime(t:Long):String{
    try {
        val shi=t/3600
        val fen= (t % 3600) / 60
        val miao= (t % 3600) % 60
        val s=if (shi<10) "0${shi}" else shi
        val f=if (fen<10) "0${fen}" else fen
        val m=if (miao<10) "0${miao}" else miao
        return "${s}:${f}:${m}"
    }catch (e: Exception){}
    return "00:00:00"
}

fun processName(applicationContext: Application): String {
    val pid = android.os.Process.myPid()
    var processName = ""
    val manager = applicationContext.getSystemService(Application.ACTIVITY_SERVICE) as ActivityManager
    for (process in manager.runningAppProcesses) {
        if (process.pid === pid) {
            processName = process.processName
        }
    }
    return processName
}



fun AppCompatActivity.metrics(){
    val metrics: DisplayMetrics = resources.displayMetrics
    val td = metrics.heightPixels / 760f
    val dpi = (160 * td).toInt()
    metrics.density = td
    metrics.scaledDensity = td
    metrics.densityDpi = dpi
}

fun String.buyUser()=contains("fb4a")|| contains("gclid")|| contains("not%20set")|| contains("youtubeads")|| contains("%7B%22")

fun String.isFB()=contains("fb4a")|| contains("facebook")

fun readReferrer(){
    if(readReferrerLocal().isEmpty()){
        val referrerClient = InstallReferrerClient.newBuilder(app0515).build()
        referrerClient.startConnection(object : InstallReferrerStateListener {
            override fun onInstallReferrerSetupFinished(responseCode: Int) {
                runCatching {
                    when (responseCode) {
                        InstallReferrerClient.InstallReferrerResponse.OK -> {
                            val installReferrer = referrerClient.installReferrer.installReferrer
                            MMKV.defaultMMKV().encode("prompt",installReferrer)
                        }
                        else->{}
                    }
                }
                runCatching {
                    referrerClient.endConnection()
                }
            }
            override fun onInstallReferrerServiceDisconnected() {
            }
        })
    }
}

fun readReferrerLocal()= MMKV.defaultMMKV().decodeString("prompt")?:""

fun str2Int(string: String):Int{
    try {
        return string.toInt()
    }catch (e:Exception){

    }
    return 0
}

fun getBuild():String = "build/${Build.VERSION.RELEASE}"

fun getDistinctId(context: Context)= encrypt(getAndroidId(context))

fun encrypt(raw: String): String {
    var md5Str = raw
    runCatching {
        val md = MessageDigest.getInstance("MD5")
        md.update(raw.toByteArray())
        val encryContext = md.digest()
        var i: Int
        val buf = StringBuffer("")
        for (offset in encryContext.indices) {
            i = encryContext[offset].toInt()
            if (i < 0) {
                i += 256
            }
            if (i < 16) {
                buf.append("0")
            }
            buf.append(Integer.toHexString(i))
        }
        md5Str = buf.toString()
    }
    return md5Str
}

fun getScreenRes(context: Context):String{
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val displayMetrics = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(displayMetrics)
    return displayMetrics.density.toString()
}

fun getNetworkType(context: Context):String{
    runCatching {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
            if (activeNetworkInfo.type == ConnectivityManager.TYPE_WIFI) {
                return "wifi"
            } else if (activeNetworkInfo.type == ConnectivityManager.TYPE_MOBILE) {
                return "mobile"
            }
        } else {
            return "no"
        }
        return "no"
    }
    return "no"
}

fun getZoneOffset()= TimeZone.getDefault().rawOffset/3600/1000

fun getGaid(context: Context)=try {
    AdvertisingIdClient.getAdvertisingIdInfo(context).id
}catch (e:Exception){
    ""
}

fun getAppVersion(context: Context)=context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_META_DATA).versionName

fun getOsVersion()= Build.VERSION.RELEASE

fun getLogId()= UUID.randomUUID().toString()

fun getBrand()= android.os.Build.BRAND

fun getBundleId(context: Context)=context.packageName

fun getManufacturer()= Build.MANUFACTURER

fun getDeviceModel()= Build.MODEL

fun getAndroidId(context: Context): String {
    runCatching {
        val id: String = Settings.Secure.getString(
            context.getContentResolver(),
            Settings.Secure.ANDROID_ID
        )
        return if ("9774d56d682e549c" == id) "" else id ?: ""
    }
    return ""
}

fun getSystemLanguage():String{
    val default = Locale.getDefault()
    return "${default.language}_${default.country}"
}

fun getOsCountry()= Locale.getDefault().country

fun getOperator(context: Context):String{
    runCatching {
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return telephonyManager.networkOperator
    }
    return ""
}

fun getFirstInstallTime(context: Context):Long{
    runCatching {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        return packageInfo.firstInstallTime
    }
    return System.currentTimeMillis()
}

fun getLastUpdateTime(context: Context):Long{
    runCatching {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        return packageInfo.lastUpdateTime
    }
    return System.currentTimeMillis()
}

fun installNoReferrerPrompt(){
    MMKV.defaultMMKV().encode("install_no_referrer_prompt",1)
}

fun checkInstallNoReferrerPrompt()= MMKV.defaultMMKV().decodeInt("install_no_referrer_prompt")==1

fun installHasReferrerPrompt(){
    MMKV.defaultMMKV().encode("install_has_referrer_prompt",1)
}

fun checkInstallHasReferrerPrompt()= MMKV.defaultMMKV().decodeInt("install_has_referrer_prompt")==1
