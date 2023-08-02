package com.qxtao.viewanalysis.ui.activity

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import com.google.gson.reflect.TypeToken
import com.qxtao.viewanalysis.R
import com.qxtao.viewanalysis.common.HierarchyNode
import com.qxtao.viewanalysis.constant.Constant.ACTION_FINISH_HIERARCHY_ACTIVITY
import com.qxtao.viewanalysis.service.FloatWindowService
import com.qxtao.viewanalysis.ui.widget.hierarchyview.HierarchyDetailView
import com.qxtao.viewanalysis.ui.widget.hierarchyview.HierarchyView
import com.qxtao.viewanalysis.ui.widget.layoutinfoview.LayoutInfoView
import com.qxtao.viewanalysis.ui.widget.layoutinfoview.OnNodeChangedListener
import com.qxtao.viewanalysis.utils.common.UiUtils
import com.qxtao.viewanalysis.utils.factory.JsonHelper


class HierarchyActivity : Activity(),
     OnNodeChangedListener {
    // define variable
    private var nodeList: ArrayList<HierarchyNode>? = null
    private var nodeMap: HashMap<Long, HierarchyNode>? = null
    private var showHierarchyView = false
    private lateinit var context: Context
    // define widget
    private lateinit var hierarchyView: HierarchyView
    private lateinit var hierarchyDetailView: HierarchyDetailView
    private lateinit var flHierarchy: FrameLayout

    companion object {
        fun start(
            context: Context?,
            node: ArrayList<HierarchyNode>?
        ) {
            val intent = Intent(context, HierarchyActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val bundle = Bundle()
            bundle.putParcelableArrayList("node", node)
            intent.putExtras(bundle)
            context?.startActivity(intent)
        }

        private var receiver = object : BroadcastReceiver() {
            private var reference: java.lang.ref.WeakReference<Activity>? = null
            fun setActivity(activity: Activity) {
                reference = java.lang.ref.WeakReference(activity)
            }

            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    ACTION_FINISH_HIERARCHY_ACTIVITY -> {
                        reference?.get()?.finish()
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hierarchy)
        context = this

        bindViews()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
        }
        flHierarchy.setPadding(0, UiUtils.getStatusHeight(),0,0)

        intent?.run {
            nodeList = getParcelableArrayListExtra("node")
            checkNodeList()
            val json = getStringExtra("nodeMap")
            nodeMap = JsonHelper.fromJson(
                json,
                object : TypeToken<HashMap<Long, HierarchyNode>>() {}.type
            )
        }
        val filter = IntentFilter(ACTION_FINISH_HIERARCHY_ACTIVITY)
        receiver.setActivity(this)
        registerReceiver(receiver, filter)
        showHierarchyView()
        FloatWindowService.setFloatButtonVisible(this, false)
    }

    private fun bindViews() {
        hierarchyView = findViewById(R.id.hierarchyView)
        hierarchyDetailView = findViewById(R.id.hierarchyDetailView)
        flHierarchy = findViewById(R.id.fl_hierarchy)
    }

    private fun checkNodeList() {
        nodeList ?: return
        if ((nodeList?.size ?: 0) <= 1) {
            return
        }
        var hierarchyNode: HierarchyNode? = null
        nodeList?.forEach {
            if (hierarchyNode == null) {
                hierarchyNode = it
            } else if (hierarchyNode?.screenBounds?.let { it1 -> it.screenBounds?.contains(it1) } == true) {
                hierarchyNode = it
            }
        }
        nodeList?.clear()
        hierarchyNode?.let {
            nodeList?.add(it)
        }
    }

    private fun showHierarchyView() {
        showHierarchyView = true
        hierarchyView.setHierarchyNodes(nodeList)
        hierarchyView.setOnHierarchyNodeClickListener(object :
            HierarchyView.OnHierarchyNodeClickListener {
            override fun onClick(node: HierarchyNode, parentNode: HierarchyNode?) {
                hierarchyDetailView.visibility = View.VISIBLE
                hierarchyDetailView.setNode(node, parentNode)
                val layoutInfoView =
                    LayoutInfoView(
                        context,
                        nodeList,
                        node
                    )
                layoutInfoView.setOnNodeChangedListener(this@HierarchyActivity)
                layoutInfoView.show()
            }

            override fun onSelectedNodeChanged(node: HierarchyNode, parentNode: HierarchyNode?) {
                hierarchyDetailView.visibility = View.VISIBLE
                hierarchyDetailView.setNode(node, parentNode)
            }
        })
    }

    override fun onChanged(node: HierarchyNode, parentNode: HierarchyNode?) {
        hierarchyDetailView.setNode(node, parentNode)
    }

    override fun onDestroy() {
        FloatWindowService.setFloatButtonVisible(this, true)
        unregisterReceiver(receiver)
        super.onDestroy()
    }

}