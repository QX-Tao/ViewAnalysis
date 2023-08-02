package com.qxtao.viewanalysis.utils.common

import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.ShellUtils
import com.qxtao.viewanalysis.base.BaseApp
import com.qxtao.viewanalysis.service.ViewAnalysisAccessibilityService

object ShellUtils {
    private val packageName: String = BaseApp.instance.packageName
    private val accessibilityServiceName: String = ViewAnalysisAccessibilityService::class.java.canonicalName as String
    private val SHELL_OPEN_ACCESSIBILITY_SERVICE = "settings put secure enabled_accessibility_services $packageName/$accessibilityServiceName && settings put secure accessibility_enabled 1"
    private val SHELL_OPEN_SYSTEM_ALERT_WINDOW_SERVICE = "appops set $packageName SYSTEM_ALERT_WINDOW allow"

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

}
