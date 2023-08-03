package com.qxtao.viewanalysis.utils.common

import android.util.Log
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.ShellUtils
import com.qxtao.viewanalysis.base.BaseApp
import com.qxtao.viewanalysis.service.ViewAnalysisAccessibilityService

object ShellUtils {
    private val packageName: String = BaseApp.instance.packageName
    private val accessibilityServiceName: String = ViewAnalysisAccessibilityService::class.java.canonicalName as String
    private val SHELL_OPEN_ACCESSIBILITY_SERVICE = "settings put secure enabled_accessibility_services $packageName/$accessibilityServiceName && settings put secure accessibility_enabled 1"
    private val SHELL_OPEN_SYSTEM_ALERT_WINDOW_SERVICE = "appops set $packageName SYSTEM_ALERT_WINDOW allow"
    private val SHELL_DUMPSYS_TOP_ACTIVITY = "dumpsys activity top | grep ACTIVITY | tail -n 1"

    fun openAccessibilityService(): Boolean {
        return ShellUtils.execCmd(SHELL_OPEN_ACCESSIBILITY_SERVICE, true).result == 0
    }

    fun openFloatWindowService(): Boolean {
        return ShellUtils.execCmd(SHELL_OPEN_SYSTEM_ALERT_WINDOW_SERVICE, true).result == 0
    }

    fun isRooted(): Boolean{
        return AppUtils.isAppRoot()
    }

    fun allowPermissionByRoot(): Boolean {
        val accessibilityServiceSuccess = openAccessibilityService()
        val floatWindowServiceSuccess = openFloatWindowService()
        return accessibilityServiceSuccess && floatWindowServiceSuccess
    }

    fun isManinActivityForeRun(): Boolean {
        val result = ShellUtils.execCmd(SHELL_DUMPSYS_TOP_ACTIVITY,true,true)
        if (result.result == 0) {
            val msg1 = "com.qxtao.viewanalysis/.ui.activity.MainActivity"
            val msg2 = result.successMsg
            return msg2.contains(msg1)
        }
        return false
    }

}
