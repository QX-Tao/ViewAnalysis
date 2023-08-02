package com.qxtao.viewanalysis.ui.widget.layoutinfoview

import android.content.Context
import android.view.LayoutInflater
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.qxtao.viewanalysis.common.HierarchyNode
import com.qxtao.viewanalysis.databinding.ViewLayoutInfoBinding
import com.qxtao.viewanalysis.utils.common.UiUtils


class LayoutInfoView(
    context: Context,
    nodeList: List<HierarchyNode>?,
    hierarchyNode: HierarchyNode
) : BottomSheetDialog(context) {
    private lateinit var binding: ViewLayoutInfoBinding
    private val adapter = LayoutInfoViewPagerAdapter(
        context,
        nodeList,
        hierarchyNode
    )

    init {
        init()
    }

    fun setOnNodeChangedListener(listener: OnNodeChangedListener) {
        adapter.setOnNodeChangedListener(listener)
    }

    private fun init() {
        binding = ViewLayoutInfoBinding.inflate(LayoutInflater.from(context), null,false)
        setContentView(binding.root)
        val layoutParams = binding.layoutInfoContainer.layoutParams
        layoutParams.height = UiUtils.getDeviceHeight(context) / 2
        binding.layoutInfoContainer.layoutParams = layoutParams
        initViewpager()
    }

    private fun initViewpager() {
        binding.viewPager.adapter = adapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)
    }
}
