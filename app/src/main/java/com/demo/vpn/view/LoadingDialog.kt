package com.demo.vpn.view

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.demo.vpn.R
import com.gyf.immersionbar.ImmersionBar
import kotlinx.android.synthetic.main.loading_dialog.*
import kotlinx.coroutines.*

class LoadingDialog(private val onDismiss:()->Unit):DialogFragment(){

    private lateinit var animator0515: ObjectAnimator

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return inflater.inflate(R.layout.loading_dialog,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setCancelable(false)
        imm()
        startAnimator()
        delayDismiss()
    }

    private fun imm(){
        ImmersionBar.with(this).apply {
            statusBarAlpha(0f)
            autoDarkModeEnable(true)
            statusBarDarkFont(false)
            init()
        }

    }
    private fun delayDismiss(){
        GlobalScope.launch {
            delay(2000L)
            withContext(Dispatchers.Main){
                dismiss()
                onDismiss.invoke()
            }
        }
    }

    private fun startAnimator(){
        animator0515= ObjectAnimator.ofFloat(iv_loading, "rotation", 0f, 360f).apply {
            duration=1000L
            repeatCount= ValueAnimator.INFINITE
            repeatMode= ObjectAnimator.RESTART
            start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        animator0515.removeAllUpdateListeners()
        animator0515.cancel()
    }
}