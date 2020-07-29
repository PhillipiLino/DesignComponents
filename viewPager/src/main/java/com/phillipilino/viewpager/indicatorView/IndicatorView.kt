package com.phillipilino.viewpager.indicatorView

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity.CENTER
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.animation.BounceInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.view.children
import com.phillipilino.basehelpers.extensions.animateColor
import com.phillipilino.viewpager.R

class IndicatorView(context: Context, attrs: AttributeSet): LinearLayout(context, attrs) {

    companion object {
        @DrawableRes val  ITEM_DRAWABLE: Int = R.drawable.indicator_loop_view_pager
    }

    private var selectedItem: Int? = null
    private var selectedItemColor: Int
    private var unselectedItemColor: Int

    init {
        val array = context.theme.obtainStyledAttributes(attrs, R.styleable.IndicatorView, 0, 0)
        selectedItemColor = array.getColor(R.styleable.IndicatorView_selectedItemColor, context.resources.getColor(R.color.santas_gray))
        unselectedItemColor = array.getColor(R.styleable.IndicatorView_unselectedItemColor, context.resources.getColor(R.color.santas_gray))

        orientation = HORIZONTAL
        gravity = CENTER
        dividerDrawable = resources.getDrawable(R.drawable.divider_linear_layout)
        showDividers = SHOW_DIVIDER_MIDDLE
        clipToPadding = false
        setPadding(50, 50, 50, 50)
    }

    fun insertItems(quantity: Int) {
        removeAllViews()
        for (index in 1..quantity) {
            val item = ImageView(context)
            item.layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
            item.setImageDrawable(context.resources.getDrawable(ITEM_DRAWABLE))
            item.setColorFilter(unselectedItemColor)
            addView(item)
        }
    }

    fun setSelectedItem(position: Int) {
        animateItem(position)
        changeItemColor(position)
        selectedItem?.let {
            animateItem(it, false)
            changeItemColor(it, false)
        }
        selectedItem = position
    }

    private fun animateItem(position: Int, isSelected: Boolean = true) {
        val scale = if (isSelected) 1.5f else 1.0f
        getChildAt(position)
            ?.animate()
            ?.scaleX(scale)
            ?.scaleY(scale)
            ?.setDuration(500)
            ?.setInterpolator(BounceInterpolator())
            ?.start()
    }

    private fun changeItemColor(position: Int, isSelected: Boolean = true) {
        val item: ImageView = getChildAt(position) as ImageView? ?: return
        val color = if (isSelected) selectedItemColor else unselectedItemColor
        val oldColor = if (isSelected) unselectedItemColor else selectedItemColor
        item.animateColor(oldColor, color)
    }

    fun setColors(@ColorInt selectedColor: Int, @ColorInt unselectedColor: Int) {
        selectedItemColor = selectedColor
        unselectedItemColor = unselectedColor
        children.forEachIndexed { index, _ -> changeItemColor(index, index == selectedItem) }
    }
}