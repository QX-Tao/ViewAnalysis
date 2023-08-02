package com.qxtao.viewanalysis.utils.common

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.WindowManager
import com.blankj.utilcode.util.ScreenUtils


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
        val displayMetrics = DisplayMetrics()
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getRealMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

    /**
     * 获取屏幕宽度
     */
    fun getDeviceWidth(context: Context = CommonUtils.application): Int {
        val displayMetrics = DisplayMetrics()
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getRealMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }

    /**
     * 获取状态栏的高度
     */
    @SuppressLint("InternalInsetResource", "DiscouragedApi")
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

    /**
     * 获取屏幕方向
     */
    fun getScreenRotation(activity: Activity): Int{
        return ScreenUtils.getScreenRotation(activity)
    }

}