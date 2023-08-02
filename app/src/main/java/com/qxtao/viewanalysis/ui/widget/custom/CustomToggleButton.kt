package com.qxtao.viewanalysis.ui.widget.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatToggleButton
import com.qxtao.viewanalysis.R

class CustomToggleButton(context: Context, attrs: AttributeSet) : AppCompatToggleButton(context, attrs) {

    private val checkedBackgroundColor = context.getColor(R.color.theme_color_mar)
    private val uncheckedBackgroundColor = context.getColor(R.color.theme_color_gap)
    private val cornerRadius = resources.getDimensionPixelSize(R.dimen.toggleButtonCornerRadius).toFloat()
    private val shape: GradientDrawable = GradientDrawable()

    init {
        shape.cornerRadius = cornerRadius
    }

    override fun onDraw(canvas: Canvas) {
        val backgroundColor = if (isChecked) checkedBackgroundColor else uncheckedBackgroundColor
        shape.setColor(backgroundColor)
        shape.setBounds(0, 0, width, height)
        shape.draw(canvas)
        super.onDraw(canvas)
    }
}