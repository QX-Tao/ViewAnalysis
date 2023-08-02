package com.qxtao.viewanalysis.service

import android.accessibilityservice.AccessibilityService
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Rect
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.qxtao.viewanalysis.common.HierarchyNode
import com.qxtao.viewanalysis.constant.Constant.ACTION_ACCESSIBILITY_SERVICE_STATUS_CHANGED
import com.qxtao.viewanalysis.constant.Constant.ACTION_HIERARCHY_VIEW
import com.qxtao.viewanalysis.ui.activity.HierarchyActivity
import com.qxtao.viewanalysis.utils.common.UiUtils

class ViewAnalysisAccessibilityService : AccessibilityService() {
    private val receiver = ViewAnalysisAccessibilityReceiver()
    private var nodeId = 0L
    private var activity: String? = null

    companion object {
        internal var serviceRunning = false
        val nodeMap: HashMap<Long, HierarchyNode> = hashMapOf()
    }

    override fun onInterrupt() {
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val source = event!!.source
        if (source != null) {
            if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                val componentName = ComponentName(
                    event.packageName.toString(),
                    event.className.toString()
                )
                try {
                    var activityName = packageManager.getActivityInfo(componentName, 0).toString()
                    activityName = activityName.substring(activityName.indexOf(" "), activityName.indexOf("}"))
                    activity = activityName.trim()
                } catch (e: PackageManager.NameNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun readNode(): ArrayList<HierarchyNode> {
        val hierarchyNodes = arrayListOf<HierarchyNode>()
        nodeMap.clear()
        if (rootInActiveWindow != null) {
            readNodeInfo(hierarchyNodes, rootInActiveWindow, null)
//            val node = getDecorViewNode(rootInActiveWindow)
//            readNodeInfo(hierarchyNodes, node ?: rootInActiveWindow, null)
        }
        return hierarchyNodes
    }

    private fun getDecorViewNode(node: AccessibilityNodeInfo): AccessibilityNodeInfo? {
        for (index in 0 until node.childCount) {
            val child = node.getChild(index)
            if (child.viewIdResourceName == "android:id/content") {
                return child
            }
            val decorViewNode = getDecorViewNode(child)
            if (decorViewNode != null) {
                return decorViewNode
            }
        }
        return null
    }

    override fun onCreate() {
        super.onCreate()
        val filter = IntentFilter()
        filter.addAction(ACTION_HIERARCHY_VIEW)
        registerReceiver(receiver, filter)
        sendStatusBroadcast(true)
        serviceRunning = true
        nodeMap.clear()
    }



    override fun onDestroy() {
        super.onDestroy()
        nodeMap.clear()
        serviceRunning = false
        unregisterReceiver(receiver)
        sendStatusBroadcast(false)
    }

    private fun setNodeId(node: HierarchyNode) {
        node.id = nodeId
        nodeId++
    }

    private fun sendStatusBroadcast(running: Boolean) {
        val intent = Intent(ACTION_ACCESSIBILITY_SERVICE_STATUS_CHANGED)
        intent.putExtra("status", running)
        sendBroadcast(intent)
    }

    private fun readNodeInfo(
        hierarchyNodes: ArrayList<HierarchyNode>,
        accessibilityNodeInfo: AccessibilityNodeInfo,
        parentNode: HierarchyNode?
    ) {
        if (accessibilityNodeInfo.childCount == 0) {
            return
        }
        for (index in 0 until accessibilityNodeInfo.childCount) {
            val child = accessibilityNodeInfo.getChild(index)
            if (child?.isVisibleToUser != true) {
                continue
            }
            val screenRect = Rect()
            val parentRect = Rect()
            child.getBoundsInScreen(screenRect)
            child.getBoundsInParent(parentRect)
            screenRect.offset(0, -UiUtils.getStatusHeight())
            val node = HierarchyNode()
            setNodeId(node)
            if (parentNode != null) {
                parentNode.childId.add(node)
                nodeMap[node.id] = node
                node.parentId = parentNode.id
            } else {
                hierarchyNodes.add(node)
                nodeMap[node.id] = node
            }
            child.viewIdResourceName?.let {
                node.resourceId = it
            }
            var text = ""
            if (child.text != null) {
                text = child.text.toString()
            }
            node.text = text
            node.screenBounds = screenRect
            node.parentBounds = parentRect
            node.checkable = child.isCheckable
            node.checked = child.isChecked
            node.isImportantForAccessibility = child.isImportantForAccessibility
            node.widget = if (child.className == null) {
                ""
            } else {
                child.className.toString()
            }
            node.activity = activity.toString()
            node.clickable = child.isClickable
            node.contentDesc = if (child.contentDescription == null) {
                ""
            } else {
                child.contentDescription.toString()
            }
            node.enabled = child.isEnabled
            node.focusable = child.isFocusable
            node.focused = child.isFocused
            node.longClickable = child.isLongClickable
            node.packageName = if (child.packageName == null) {
                ""
            } else {
                child.packageName.toString()
            }
            node.password = child.isPassword
            node.scrollable = child.isScrollable
            node.selected = child.isSelected
            readNodeInfo(hierarchyNodes, child, node)
        }
    }


    inner class ViewAnalysisAccessibilityReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, data: Intent?) {
            val nodesInfo = readNode()
            HierarchyActivity.start(context, nodesInfo)
        }
    }
}