package com.demo.vpn.util

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.util.DisplayMetrics
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.demo.vpn.R
import java.lang.Exception

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