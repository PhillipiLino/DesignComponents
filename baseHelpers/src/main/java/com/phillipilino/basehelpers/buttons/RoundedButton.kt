package com.phillipilino.basehelpers.buttons

import android.content.Context
import android.graphics.drawable.DrawableContainer
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import com.phillipilino.basehelpers.R
import com.phillipilino.basehelpers.ResourcesHelper

class RoundedButton(context: Context, attrs: AttributeSet): AppCompatButton(context, attrs, android.R.attr.borderlessButtonStyle)  {
    private val resHelper = ResourcesHelper(context)
    private var fillColor: Int
    private var strokeColor: Int
    private var strokeWidth: Int

    init {
        val defaultStrokeWidth = context.resources.getDimensionPixelSize(R.dimen.spacing_x0_5)
        val defaultColor = resHelper.getColorHelper(R.color.bamboo)

        val array = context.theme.obtainStyledAttributes(attrs, R.styleable.RoundedButton, 0, 0)
        fillColor = array.getColor(R.styleable.RoundedButton_fillColor, defaultColor)
        strokeColor = array.getColor(R.styleable.RoundedButton_strokeColor, defaultColor)
        strokeWidth = array.getDimensionPixelSize(R.styleable.RoundedButton_strokeWidth, defaultStrokeWidth)
        array.recycle()

        setBorderedButton()
    }

    private fun setBorderedButton() {
        setBackgroundResource(R.drawable.bg_rounded_btn)
        (background as StateListDrawable).apply {
            val children = (constantState as DrawableContainer.DrawableContainerState).children
            val selectedItem = children[0] as GradientDrawable

            selectedItem.setStroke(strokeWidth, strokeColor)
            selectedItem.setColor(fillColor)
        }
    }
}