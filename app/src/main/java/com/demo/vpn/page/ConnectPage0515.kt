package com.demo.vpn.page

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.content.pm.PackageManager
import android.net.VpnService
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AlertDialog
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.SizeUtils
import com.demo.vpn.R
import com.demo.vpn.ad.LoadAd0515Util
import com.demo.vpn.ad.Show0515FullAd
import com.demo.vpn.ad.Show0515NativeAd
import com.demo.vpn.base_page.BasePage0515
import com.demo.vpn.conf.Fire0515
import com.demo.vpn.interfaces.IAppToBackgroundInterface
import com.demo.vpn.interfaces.IConnectTimeInterface
import com.demo.vpn.interfaces.IServerStateListener
import com.demo.vpn.server.Connect0515Util
import com.demo.vpn.server.Connect0515Util.connectServerSuccess
import com.demo.vpn.server.Connect0515Util.currentServer
import com.demo.vpn.server.ConnectTime0515Util
import com.demo.vpn.server.Server0515Util
import com.demo.vpn.util.*
import com.github.shadowsocks.bg.BaseService
import com.github.shadowsocks.utils.StartService
import kotlinx.android.synthetic.main.activity_connect.*
import kotlinx.android.synthetic.main.connect_drawer.*
import kotlinx.android.synthetic.main.connect_home.*
import kotlinx.coroutines.*

class ConnectPage0515:BasePage0515(), IServerStateListener, IConnectTimeInterface, IAppToBackgroundInterface {
    private var jobTime=-1
    private var canClick=true
    private var permission=false
    private var connect=false
    private var autoConnect=false
    private var connectJob:Job?=null
    private var moveAnimator:ValueAnimator?=null
    private var connectingAnimator:ObjectAnimator?=null
    private val instance=SizeUtils.dp2px(55F)

    private val showHomeAd = Show0515NativeAd(LoadAd0515Util.HOME,this)
    private val showConnectAd = Show0515FullAd(LoadAd0515Util.CONNECT,this)

    private val registerResult=registerForActivityResult(StartService()) {
        if (!it && permission) {
            permission = false
            Point0515Util.setPoint("prompt_vg")
            checkHasServerBean()
        } else {
            canClick=true
        }
    }

    override fun layout(): Int = R.layout.activity_connect

    override fun initView() {
        immersionBar.statusBarView(view_top).init()
        setServerInfo()
        ConnectTime0515Util.setConnectTimeInterface(this)
        Connect0515Util.init(this,this)
        AppRegister.setIAppToBackgroundInterface(this)

        setClickListener()
        if(AppRegister.isHotLoad){
            hideGuide()
        }
        checkAutoConnect(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { checkAutoConnect(it) }
    }

    private fun checkAutoConnect(intent: Intent){
        if(intent.getBooleanExtra("autoConnect",false)){
            autoConnect=true
            clickConnectBtn()
        }
    }

    private fun setClickListener(){
        llc_choose_server.setOnClickListener {
            if(canClick&&!drawer_layout.isOpen){
                startActivityForResult(Intent(this,ServerListPage0515::class.java),515)
            }
            if (!connect&&Connect0515Util.isConnected()){
                appToBackground()
            }
        }

        iv_connect_btn_bg.setOnClickListener { clickConnectBtn() }
        llc_agree.setOnClickListener {
            if(canClick&&drawer_layout.isOpen){
                startActivity(Intent(this,AgreePage::class.java))
            }
        }
        llc_share.setOnClickListener {
            if(canClick&&drawer_layout.isOpen){
                val pm = packageManager
                val packageName=pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES).packageName
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(
                    Intent.EXTRA_TEXT,
                    "https://play.google.com/store/apps/details?id=${packageName}"
                )
                startActivity(Intent.createChooser(intent, "share"))
            }
        }
        iv_drawer.setOnClickListener {
            if(!drawer_layout.isOpen&&canClick){
                drawer_layout.openDrawer(Gravity.LEFT)
            }
            if (!connect&&Connect0515Util.isConnected()){
                appToBackground()
            }
        }
        guide_view.setOnClickListener {  }
        guide_lottie_view.setOnClickListener { clickConnectBtn() }
        iv_center.setOnClickListener {
            if(Connect0515Util.isDisconnected()){
                clickConnectBtn()
            }
        }
    }

    private fun clickConnectBtn(){
        if(Limit0515Util.limitArea){
            AlertDialog.Builder(this).apply {
                setCancelable(false)
                setMessage("Due to the policy reason , this service is not available in your country")
                setPositiveButton("confirm") { _, _ -> finish() }
                show()
            }
            return
        }
        if(!canClick){
            return
        }
        hideGuide()
        canClick=false
        if(Connect0515Util.isConnected()){
            LoadAd0515Util.disconnectVpnClearAllAd()
            setConnectingView(false)
            startConnectJob(false)
            startConnectAnimator()
        }else{
            if(!autoConnect){
                Point0515Util.setPoint("prompt_vclick")
            }
            setServerInfo()
            if (getNetStatus()==1){
                AlertDialog.Builder(this).apply {
                    setMessage("Network request timed out. Please make sure your network is connected")
                    setPositiveButton("OK", null)
                    show()
                }
                canClick=true
                return
            }
            if (VpnService.prepare(this) != null) {
                permission = true
                registerResult.launch(null)
                return
            }

            checkHasServerBean()
        }
    }

    private fun checkHasServerBean(){
        if(currentServer.isFast()){
            Server0515Util.checkSmartSererListEmpty(supportFragmentManager){
                if(it){
                    canClick=true
                }else{
                    startConnect()
                }
            }
        }else{
            startConnect()
        }
    }

    private fun startConnect(){
        Point0515Util.setPoint("prompt_vc")
        setConnectingView(true)
        ConnectTime0515Util.resetTime()
        startConnectJob(true)
        startConnectAnimator()
    }

    private fun startConnectJob(connect:Boolean){
        this.connect=connect
        jobTime=0
        connectJob=GlobalScope.launch(Dispatchers.Main) {
            while (true) {
                if (!isActive) {
                    break
                }
                delay(1000)
                jobTime++
                if (jobTime==3){
                    if (connect){
                        Connect0515Util.connect(autoConnect)
                        autoConnect=false
                    }else{
                        Connect0515Util.disconnect()
                    }
                }

                if (jobTime in 3..9){
                    if (connectServerSuccess(connect)){
                        if(null!=LoadAd0515Util.getAdByType(LoadAd0515Util.CONNECT)){
                            cancel()
                            showConnectAd.show0515(
                                showingAd = {
                                    connectComplete(false)
                                },
                                closeAd = { connectComplete() }
                            )
                        }else{
                            if(!Fire0515.canShowInterAd()||LoadAd0515Util.adNumLimit()){
                                cancel()
                                connectComplete()
                            }
                        }
                    }
                }else if (jobTime >= 10) {
                    cancel()
                    connectComplete()
                }
            }
        }
    }

    private fun connectComplete(toResult:Boolean=true){
        runOnUiThread {
            stopConnectAnimator()
            llc_connect.translationX=getTranslationX(100)
            if (connectServerSuccess(connect)){
                if (connect){
                    setConnectedView()
                    ConnectTime0515Util.startTime()
                }else{
                    setStoppedView()
                    setServerInfo()
                }
                if (toResult&&AppRegister.front&& ActivityUtils.getTopActivity().javaClass.name==ConnectPage0515::class.java.name){
                    startActivity(Intent(this,ResultPage::class.java).apply {
                        putExtra("connect",connect)
                    })
                }
            }else{
                setStoppedView()
                if(connect){
                    Point0515Util.setPoint("prompt_vf")
                }
                showToast(if (connect) "Connect Fail" else "Disconnect Fail")
            }
            canClick=true
        }
    }

    private fun startConnectAnimator(){
        moveAnimator=ValueAnimator.ofInt(0, 100).apply{
            duration=10000L
            interpolator = LinearInterpolator()
            addUpdateListener {
                val pro = it.animatedValue as Int
                llc_connect.translationX=getTranslationX(pro)
            }
            start()
        }
    }

    private fun stopConnectAnimator(){
        moveAnimator?.removeAllUpdateListeners()
        moveAnimator?.cancel()
        moveAnimator=null
    }

    private fun stopConnectJob(){
        connectJob?.cancel()
        connectJob=null
    }

    private fun getTranslationX(pro:Int):Float{
        val fl = instance/ 100F * pro
        return if (connect) fl else instance-fl
    }

    private fun setStoppedView(){
        tv_connect.text="Connect"
        tv_connect_time.text="00:00:00"
        stopConnectingAnimator()
        iv_center.setImageResource(R.drawable.home3)
        iv_connect.setImageResource(R.drawable.home8)
    }

    private fun setConnectedView(){
        tv_connect.text="Stop"
        stopConnectingAnimator()
        iv_center.setImageResource(R.drawable.home5)
        iv_connect.setImageResource(R.drawable.home8)
    }

    private fun setConnectingView(connect: Boolean){
        tv_connect.text=if (connect) "Connecting" else "Stopping"
        iv_center.setImageResource(R.drawable.home4)
        iv_connect.setImageResource(R.drawable.home9)
        startConnectingAnimator()
    }

    private fun startConnectingAnimator(){
        connectingAnimator= ObjectAnimator.ofFloat(iv_connect, "rotation", 0f, 360f).apply {
            duration=1000L
            repeatCount= ValueAnimator.INFINITE
            repeatMode= ObjectAnimator.RESTART
            start()
        }
    }

    private fun stopConnectingAnimator(){
        iv_connect.rotation=0F
        connectingAnimator?.cancel()
        connectingAnimator=null
    }

    private fun setServerInfo(){
        tv_name.text=currentServer.country
        iv_logo.setImageResource(get0515ServerLogo(currentServer.country))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode==515){
            when(data?.getStringExtra("result")){
                "server_dis_0515"->{
                    clickConnectBtn()
                }
                "server_con_0515"->{
                    setServerInfo()
                    clickConnectBtn()
                }
            }
        }
    }

    override fun onServiceConnected() {
        setConnectedView()
        llc_connect.translationX=getTranslationX(0)
    }

    override fun disConnectSuccess() {
        if (canClick){
            setStoppedView()
        }
    }

    private fun hideGuide(){
        guide_view.showView(false)
        guide_lottie_view.showView(false)
    }


    override fun appToBackground() {
        if(jobTime in 0..2){
            jobTime=-1
            canClick=true
            stopConnectAnimator()
            stopConnectJob()
            stopConnectingAnimator()
            if(connect){
                Connect0515Util.appToBackgroundSetState(BaseService.State.Stopped)
                setStoppedView()
                llc_connect.translationX=getTranslationX(100)
            }else{
                Connect0515Util.appToBackgroundSetState(BaseService.State.Connected)
                setConnectedView()
                llc_connect.translationX=getTranslationX(0)
            }
        }
    }

    override fun onBackPressed() {
        if (guideShowing()){
            hideGuide()
            return
        }
        if(canClick){
            finish()
        }
        if (!connect&&Connect0515Util.isConnected()){
            appToBackground()
        }
    }

    private fun guideShowing()=guide_lottie_view.visibility==View.VISIBLE

    override fun onResume() {
        super.onResume()
        showHomeAd.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopConnectAnimator()
        stopConnectJob()
        stopConnectingAnimator()
        Connect0515Util.onDestroy()
        ConnectTime0515Util.setConnectTimeInterface(this)
        AppRegister.isHotLoad=true
        showHomeAd.endCheck()
        AppRegister.setIAppToBackgroundInterface(null)
        Limit0515Util.setRefreshBool(LoadAd0515Util.HOME,true)
    }

    override fun connectTimeCallback(time: Long) {
        tv_connect_time.text= transTime(time)
    }

}