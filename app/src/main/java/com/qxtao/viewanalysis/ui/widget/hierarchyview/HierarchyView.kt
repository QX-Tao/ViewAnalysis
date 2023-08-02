package com.qxtao.viewanalysis.ui.widget.hierarchyview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.qxtao.viewanalysis.common.HierarchyNode
import com.qxtao.viewanalysis.service.ViewAnalysisAccessibilityService
import kotlin.math.pow

class HierarchyView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private val strokePaint = Paint()
    private val contentPaint = Paint()
    private var mHierarchyNodes = arrayListOf<HierarchyNode>()
    private val nodeMap = hashMapOf<Long, HierarchyNode>()
    private var onHierarchyNodeClickListener: OnHierarchyNodeClickListener? = null
    private var selectedNode: HierarchyNode? = null
    private var selectedParentNode: HierarchyNode? = null

    constructor(context: Context) : this(context, null)

    init {
        strokePaint.style = Paint.Style.STROKE
        strokePaint.strokeWidth = 2F
        strokePaint.color = Color.GREEN
        contentPaint.color = Color.argb(8, 0, 0, 0)
        setBackgroundColor(Color.TRANSPARENT)
    }

    fun setHierarchyNodes(
        hierarchyNodes: List<HierarchyNode>?
    ) {
        mHierarchyNodes.clear()
        hierarchyNodes?.let {
            mHierarchyNodes.addAll(it)
        }
        nodeMap.putAll(ViewAnalysisAccessibilityService.nodeMap)
        visibility = VISIBLE
        invalidate()
    }

    fun setOnHierarchyNodeClickListener(listener: OnHierarchyNodeClickListener) {
        onHierarchyNodeClickListener = listener
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                getSelectedNode(event.x, event.y)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                getSelectedNode(event.x, event.y)
                return false
            }
            MotionEvent.ACTION_UP -> {
                selectedNode?.let {
                    onHierarchyNodeClickListener?.onClick(it, selectedParentNode)
                }
            }
        }
        return super.onTouchEvent(event)
    }

    private fun getSelectedNode(x: Float, y: Float) {
        val hierarchyNode = getNode(x, y)
        if (hierarchyNode != null && onHierarchyNodeClickListener != null) {
            if (selectedNode == hierarchyNode.selectedNode) {
                return
            }
            selectedNode = hierarchyNode.selectedNode
            selectedParentNode = hierarchyNode.parentNode
            onHierarchyNodeClickListener?.onSelectedNodeChanged(hierarchyNode.selectedNode, hierarchyNode.parentNode)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        drawRect(canvas, mHierarchyNodes)
    }

    private fun drawRect(canvas: Canvas?, hierarchyNodes: List<HierarchyNode>) {
        for (hierarchyNode in hierarchyNodes) {
            drawWidget(canvas, hierarchyNode)
            drawRect(canvas, hierarchyNode.childId)
        }
    }

    private fun drawWidget(canvas: Canvas?, hierarchyNode: HierarchyNode) {
        hierarchyNode.screenBounds?.let {
            canvas?.drawRect(it, contentPaint)
            canvas?.drawRect(it, strokePaint)
        }

    }

    private fun getNode(x: Float, y: Float): SelectedNodeInfo? {
        val list = ArrayList<SelectedNodeInfo>()
        for (hierarchyNode in mHierarchyNodes) {
            getNode(x, y, hierarchyNode, list)
        }
        if (list.size == 1) {
            return list[0]
        }
        if (list.size == 0) return null
        var info = list[0]
        for (index in 1 until list.size) {
            if (info.contains(list[index])) {
                info = list[index]
            } else {
                var left = info.selectedNode.screenBounds?.left ?: 0
                var top = info.selectedNode.screenBounds?.top ?: 0
                val len = (x.toDouble() - left).pow(2.0) + (y.toDouble() - top).pow(2.0)
                left = list[index].selectedNode.screenBounds?.left ?: 0
                top = list[index].selectedNode.screenBounds?.top ?: 0
                val len1 = (x.toDouble() - left).pow(2.0) + (y.toDouble() - top).pow(2.0)
                if (len1 < len) {
                    info = list[index]
                }
            }
        }
        return info
    }

    private fun getNode(x: Float, y: Float, hierarchyNode: HierarchyNode, list: ArrayList<SelectedNodeInfo>) {
        val rect = hierarchyNode.screenBounds ?: return
        if (rect.contains(x.toInt(), y.toInt())) {
            if (hierarchyNode.childId.isNotEmpty()) {
                for (child in hierarchyNode.childId.reversed()) {
                    getNode(x, y, child, list)
                }
            }
            list.add(
                SelectedNodeInfo(
                    hierarchyNode,
                    nodeMap[hierarchyNode.parentId]
                )
            )
        }
    }

    interface OnHierarchyNodeClickListener {
        fun onClick(node: HierarchyNode, parentNode: HierarchyNode?)
        fun onSelectedNodeChanged(node: HierarchyNode, parentNode: HierarchyNode?)
    }
}