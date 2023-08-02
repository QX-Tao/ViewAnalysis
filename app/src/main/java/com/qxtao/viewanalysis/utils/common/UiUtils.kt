package com.qxtao.viewanalysis.utils.common

import android.content.Context
import android.util.TypedValue

object UiUtils {
    private var statusBarHeight: Int = -1
    fun dp2px(context: Context = CommonUtils.application, dpVal: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dpVal,
            context.resources?.displayMetrics
        ).toInt()
    }

    fun sp2px(context: Context = CommonUtils.application, spVal: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spVal, context.resources?.displayMetrics).toInt()
    }

    fun px2dp(context: Context = CommonUtils.application, pxVal: Float): Float {
        val scale = context.resources?.displayMetrics?.density!!
        return pxVal / scale
    }

    fun px2sp(context: Context = CommonUtils.application, pxVal: Float): Float {
        return pxVal / context.resources?.displayMetrics?.scaledDensity!!
    }

    /**
     * 获取屏幕高度
     */
    fun getDeviceHeight(context: Context = CommonUtils.application): Int {
        val dm = context.resources?.displayMetrics
        return dm?.heightPixels ?: 0
    }

    /**
     * 获取屏幕宽度
     */
    fun getDeviceWidth(context: Context = CommonUtils.application): Int {
        val dm = context.resources?.displayMetrics
        return dm?.widthPixels ?: 0
    }

    /**
     * 获取状态栏的高度
     */
    fun getStatusHeight(context: Context = CommonUtils.application): Int {
        if (statusBarHeight != -1) {
            return statusBarHeight
        }
        var result: Int? = 10
        val resourceId =
            context.resources?.getIdentifier("status_bar_height", "dimen", "android") ?: 0
        if (resourceId > 0) {
            result = context.resources?.getDimensionPixelOffset(resourceId)
        }
        statusBarHeight = result ?: 0
        return statusBarHeight
    }
}