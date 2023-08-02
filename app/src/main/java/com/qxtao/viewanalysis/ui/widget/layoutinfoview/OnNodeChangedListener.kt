package com.qxtao.viewanalysis.ui.widget.layoutinfoview

import com.qxtao.viewanalysis.common.HierarchyNode

interface OnNodeChangedListener {
    fun onChanged(node: HierarchyNode, parentNode: HierarchyNode?)
}