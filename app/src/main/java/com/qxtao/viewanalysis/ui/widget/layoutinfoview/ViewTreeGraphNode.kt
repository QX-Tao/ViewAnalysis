package com.qxtao.viewanalysis.ui.widget.layoutinfoview

import com.qxtao.viewanalysis.common.HierarchyNode

class ViewTreeGraphNode(val node: HierarchyNode) {
    var selected: Boolean = false
    var childSelected = false
        set(value) {
            if (field != value) {
                field = value
                parent?.childSelected = value
            }

        }
    var parent: ViewTreeGraphNode? = null
    var shortName = true

    fun isParentSelected(): Boolean {
        if (parent?.selected == true) {
            return true
        }
        return parent?.isParentSelected() ?: false
    }
}