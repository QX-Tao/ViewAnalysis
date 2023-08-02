package com.qxtao.viewanalysis.service

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import com.qxtao.viewanalysis.base.BaseApp
import com.qxtao.viewanalysis.constant.Constant
import com.qxtao.viewanalysis.utils.common.ShareUtils
import com.qxtao.viewanalysis.utils.common.ShellUtils

object AccessibilityManager {

    fun isAccessibilitySettingsOn(): Boolean {
        var accessibilityEnabled = 0
        val service =
            BaseApp.instance.applicationContext.packageName + "/" +
                    ViewAnalysisAccessibilityService::class.java.canonicalName
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                BaseApp.instance.applicationContext.contentResolver,
                Settings.Secure.ACCESSIBILITY_ENABLED
            )
        } catch (e: Settings.SettingNotFoundException) {
            Log.e(
                ContentValues.TAG,
                "Error finding setting, default accessibility to not found: " + e.message
            )
        }
        val mStringColonSplitter = TextUtils.SimpleStringSplitter(':')
        if (accessibilityEnabled == 1) {
            val settingValue = Settings.Secure.getString(
                BaseApp.instance.applicationContext.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue)
                while (mStringColonSplitter.hasNext()) {
                    val accessibilityService = mStringColonSplitter.next()
                    if (accessibilityService.equals(service, ignoreCase = true)) {
                        return true
                    }
                }
            }
        }
        return false
    }

    fun startService(context: Context? = BaseApp.instance.applicationContext) {
        context?.run {
            val shareUseRoot = ShareUtils.getBoolean(context, Constant.USE_ROOT,false)
            val isAppRoot = ShellUtils.isRooted()
            if (!isAppRoot) {
                ShareUtils.putBoolean(context, Constant.USE_ROOT, false)
            }
            if (shareUseRoot and isAppRoot) {
                ShellUtils.openAccessibilityService()
            } else {
                startAccessibilitySetting(context)
            }
        }
    }

    private fun startAccessibilitySetting(context: Context) {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

}