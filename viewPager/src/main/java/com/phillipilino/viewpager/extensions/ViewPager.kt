package com.phillipilino.viewpager.extensions

import android.animation.TimeInterpolator
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.viewpager2.widget.ViewPager2
import com.phillipilino.basehelpers.extensions.createAnimation

fun ViewPager2.setCurrentItem(
    item: Int,
    duration: Long,
    interpolator: TimeInterpolator = AccelerateDecelerateInterpolator(),
    pagePxWidth: Int = width - paddingStart - paddingEnd) {

    val pxToDrag: Int = pagePxWidth * (item - currentItem)
    var previousValue = 0f

    createAnimation(0f, pxToDrag.toFloat(), duration, {
        val currentValue = it.animatedValue as Float
        val currentPxToDrag = (currentValue - previousValue)
        fakeDragBy(-currentPxToDrag)
        previousValue = currentValue
    }, onStart = { beginFakeDrag() }, onEnd = { endFakeDrag() }, interpolator = interpolator)
}