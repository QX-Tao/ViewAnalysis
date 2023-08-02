package com.qxtao.viewanalysis.ui.widget.layoutinfoview.infopage

import android.view.View

class ItemInfo @JvmOverloads constructor(
    var title: String = "",
    var content: Any = "",
    listener: View.OnClickListener? = null
) {
    var id = title
    var clickListener: View.OnClickListener? = listener
        private set

    private fun setOnClickListener(listener: View.OnClickListener) {
        clickListener = listener
    }

    fun setOnClickListener(onclick: View?.() -> Unit) {
        setOnClickListener { v -> v.onclick() }
    }
}
