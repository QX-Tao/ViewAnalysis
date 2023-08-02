package com.qxtao.viewanalysis.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.view.Gravity
import com.lzf.easyfloat.EasyFloat
import com.lzf.easyfloat.enums.ShowPattern
import com.lzf.easyfloat.enums.SidePattern
import com.qxtao.viewanalysis.R
import com.qxtao.viewanalysis.constant.Constant
import com.qxtao.viewanalysis.utils.common.UiUtils

class FloatWindowService : Service() {
    companion object {
        private var isServiceRunning = false
        const val FLOAT_BUTTON = "floatButton"
        fun start(context: Context) {
            context.startService(Intent(context, FloatWindowService::class.java))
            isServiceRunning = true
        }
        fun stop(context: Context) {
            context.stopService(Intent(context, FloatWindowService::class.java))
            isServiceRunning = false
        }
        fun setFloatButtonVisible(context: Context, visible: Boolean) {
            val intent = Intent(Constant.ACTION_SET_FLOAT_BUTTON_VISIBLE)
            intent.putExtra("visible", visible)
            context.sendBroadcast(intent)
        }
        fun isServiceRunning(): Boolean {
            return isServiceRunning
        }
    }

    private val receiver = Receiver()
    override fun onCreate() {
        super.onCreate()
        initReceiver()
        EasyFloat
            .with(applicationContext)
            .setTag(FLOAT_BUTTON)
            .setShowPattern(ShowPattern.ALL_TIME)
            .setLayout(R.layout.layout_float_window_button){
                it.setOnClickListener {
                    if (!AccessibilityManager.isAccessibilitySettingsOn()) {
                        AccessibilityManager.startService()
                        return@setOnClickListener
                    }
                    sendBroadcast(Intent(Constant.ACTION_HIERARCHY_VIEW))
                }
            }
            .setGravity(Gravity.TOP or Gravity.START, 0,
                (UiUtils.getDeviceHeight(applicationContext)*0.3).toInt()
            )
            .setSidePattern(SidePattern.RESULT_SIDE)
            .setDragEnable(true)
            .show()
    }

    private fun showFloatButton() {
        EasyFloat.show(FLOAT_BUTTON)
    }

    private fun hideFloatButton() {
        EasyFloat.hide(FLOAT_BUTTON)
    }

    private fun initReceiver() {
        val filter = IntentFilter(Constant.ACTION_SET_FLOAT_BUTTON_VISIBLE)
        registerReceiver(receiver, filter)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        showFloatButton()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        try {
            EasyFloat.dismiss(FLOAT_BUTTON)
            unregisterReceiver(receiver)
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        } catch (_: Throwable) {
        }

        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder = null!!


    private inner class Receiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Constant.ACTION_SET_FLOAT_BUTTON_VISIBLE -> {
                    val visible = intent.getBooleanExtra("visible", false)
                    if (visible) {
                        showFloatButton()
                    } else {
                        hideFloatButton()
                    }
                }
            }
        }

    }

}