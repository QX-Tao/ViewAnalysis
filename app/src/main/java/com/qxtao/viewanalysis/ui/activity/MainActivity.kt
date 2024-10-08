package com.qxtao.viewanalysis.ui.activity

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.RelativeLayout
import android.widget.ToggleButton
import androidx.core.view.WindowCompat
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.ScreenUtils
import com.google.android.material.materialswitch.MaterialSwitch
import com.qxtao.viewanalysis.R
import com.qxtao.viewanalysis.base.BaseActivity
import com.qxtao.viewanalysis.constant.Constant
import com.qxtao.viewanalysis.constant.Constant.USE_ROOT
import com.qxtao.viewanalysis.databinding.ActivityMainBinding
import com.qxtao.viewanalysis.service.FloatWindowService
import com.qxtao.viewanalysis.service.ViewAnalysisAccessibilityService
import com.qxtao.viewanalysis.utils.common.ShareUtils
import com.qxtao.viewanalysis.utils.common.ShellUtils
import com.qxtao.viewanalysis.utils.common.UiUtils
import java.lang.ref.WeakReference
import java.util.Timer
import kotlin.concurrent.schedule

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {
    // define widget
    private lateinit var rlAccessibilityPermission: RelativeLayout
    private lateinit var rlFloatWindowPermission: RelativeLayout
    private lateinit var rlRootPermission: RelativeLayout
    private lateinit var btServiceControl: ToggleButton
    private lateinit var stAccessibility: MaterialSwitch
    private lateinit var stFloatWindow: MaterialSwitch
    private lateinit var stRoot: MaterialSwitch
    // define variable
    private var isButtonClickable: Boolean = true

    companion object{
        private var receiver = object : BroadcastReceiver() {
            private var reference: WeakReference<Activity>? = null
            fun setActivity(activity: Activity) {
                reference = WeakReference(activity)
            }

            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    Constant.ACTION_FINISH_MAIN_ACTIVITY -> {
                        reference?.get()?.finish()
                    }
                }
            }
        }
    }

    override fun onCreate() {
        binding.includeTitleBarFirst.tvTitle.text = getString(R.string.app_name)
        binding.includeTitleBarFirst.ivMoreButton.setOnClickListener { showPopupMenu(binding.includeTitleBarFirst.ivMoreButton) }
        if (isUseRoot()){
            if (!getPermissionStatus().contentEquals(intArrayOf(1,1))){
                ShellUtils.allowPermissionByRoot()
                showShortToast(getString(R.string.allow_permission_by_root))
                refreshPermissionStatus()
            }
        }
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val filter = IntentFilter(Constant.ACTION_FINISH_MAIN_ACTIVITY)
        receiver.setActivity(this)
        registerReceiver(receiver, filter)
    }

    override fun bindViews() {
        rlAccessibilityPermission = binding.rlAccessibilityPermission
        rlFloatWindowPermission = binding.rlFloatWindowPermission
        rlRootPermission = binding.rlRootPermission
        btServiceControl = binding.btServiceControl
        stAccessibility = binding.stAccessibility
        stFloatWindow = binding.stFloatWindow
        stRoot = binding.stRoot
    }

    override fun initViews() {
        stAccessibility.isClickable = false
        stFloatWindow.isClickable = false
        stRoot.isClickable = false
    }

    override fun addListener() {
        rlAccessibilityPermission.setOnClickListener(this)
        rlFloatWindowPermission.setOnClickListener(this)
        rlRootPermission.setOnClickListener(this)
        btServiceControl.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when(view){
            rlAccessibilityPermission -> getAccessibilityPermission()
            rlFloatWindowPermission -> getFloatWindowPermission()
            rlRootPermission -> getRootPermission()
            btServiceControl -> if (isButtonClickable) serviceControl() else {
                showShortToast(getString(R.string.click_quickly))
                refreshServiceStatus()
            }
        }
    }

    /**
     * is accessibility service on
     *
     * 检查无障碍服务
     */
    private fun isAccessibilitySettingsOn(mContext: Context): Boolean {
        var accessibilityEnabled = 0
        val service = packageName + "/" + ViewAnalysisAccessibilityService::class.java.canonicalName
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                mContext.applicationContext.contentResolver,
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
                mContext.applicationContext.contentResolver,
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
    /**
     * check accessibility service
     *
     * 检查无障碍服务
     */
    private fun getAccessibilityPermission() {
        if (!isAccessibilitySettingsOn(this)) {
            if (ShellUtils.isRooted() and isUseRoot()){
                ShellUtils.openAccessibilityService()
                showShortToast(getString(R.string.allow_permission_by_root))
                refreshPermissionStatus()
            } else {
                showShortToast(getString(R.string.grant_accessibility_service))
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                startActivity(intent)
            }
        } else {
            showLongToast(getString(R.string.accessibility_service_on))
        }
    }

    /**
     * is float window permission on
     *
     * 检查悬浮窗权限
     */
    private fun isFloatWindowPermissionOn(mContext: Context) : Boolean {
        if (Settings.canDrawOverlays(mContext)) {
            return true
        }
        return false
    }
    /**
     * check float window permission
     *
     * 检查悬浮窗权限
     */
    private fun getFloatWindowPermission() {
        if (!isFloatWindowPermissionOn(this)) {
            if (ShellUtils.isRooted() and isUseRoot()){
                ShellUtils.openFloatWindowService()
                showShortToast(getString(R.string.allow_permission_by_root))
                refreshPermissionStatus()
            } else {
                showShortToast(getString(R.string.grant_float_window_permission))
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_OVERLAY_PERMISSION
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            }
        } else {
            showLongToast(getString(R.string.float_window_service_on))
        }
    }

    private fun serviceControl(){
        if (btServiceControl.isChecked){
            if (!isAccessibilitySettingsOn(mContext)) {
                showShortToast(getString(R.string.accessibility_service_off))
                btServiceControl.isChecked = false
                return
            }
            if (!isFloatWindowPermissionOn(mContext)) {
                showShortToast(getString(R.string.float_window_service_off))
                btServiceControl.isChecked = false
                return
            }
            FloatWindowService.start(this)
            if (FloatWindowService.isServiceRunning()) finish()
        } else {
            FloatWindowService.stop(this)
            isButtonClickable = false
            Timer().schedule(500){ isButtonClickable = true }
        }
        refreshServiceStatus()
    }


    private fun refreshServiceStatus(){
        btServiceControl.isChecked = FloatWindowService.isServiceRunning()
    }

    private fun refreshPermissionStatus(){
        stAccessibility.isChecked = isAccessibilitySettingsOn(mContext)
        stFloatWindow.isChecked = isFloatWindowPermissionOn(mContext)
        stRoot.isChecked = isUseRoot()
    }

    private fun isUseRoot(): Boolean{
        val shareUseRoot = ShareUtils.getBoolean(mContext,USE_ROOT,false)
        val isAppRoot = ShellUtils.isRooted()
        if (shareUseRoot and isAppRoot) return true
        if (!isAppRoot) ShareUtils.putBoolean(mContext, USE_ROOT, false)
        return false
    }

    private fun getRootPermission(){
        if (ShellUtils.isRooted() and !stRoot.isChecked){
            stRoot.isChecked = true
            ShareUtils.putBoolean(mContext, USE_ROOT, true)
            if (!getPermissionStatus().contentEquals(intArrayOf(1,1))){
                ShellUtils.allowPermissionByRoot()
                showShortToast(getString(R.string.allow_permission_by_root))
                refreshPermissionStatus()
            }
        } else {
            stRoot.isChecked = false
            ShareUtils.putBoolean(mContext, USE_ROOT, false)
        }
    }

    private fun getPermissionStatus(): IntArray {
        val accessPermission = if(isAccessibilitySettingsOn(mContext)) 1 else 0
        val overlayPermission = if(isFloatWindowPermissionOn(mContext)) 1 else 0
        return intArrayOf(overlayPermission,accessPermission)
    }

    /**
     * show popup menu
     * 弹出菜单
     */
    private fun showPopupMenu(anchorView: View) {
        val popupMenu = PopupMenu(this, anchorView)
        popupMenu.menuInflater.inflate(R.menu.menu_popup, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.exit -> {
                    FloatWindowService.stop(mContext)
                    finish()
                }
            }
            true
        }
        popupMenu.show()
    }

    override fun onResume() {
        super.onResume()
        refreshPermissionStatus()
        refreshServiceStatus()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

}