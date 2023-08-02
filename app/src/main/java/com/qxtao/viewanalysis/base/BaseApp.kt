package com.qxtao.viewanalysis.base

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.qxtao.viewanalysis.utils.common.CommonUtils

class BaseApp : Application() {
    companion object {
        lateinit var instance: BaseApp
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        instance = this
    }

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        CommonUtils.register(this)
    }

}