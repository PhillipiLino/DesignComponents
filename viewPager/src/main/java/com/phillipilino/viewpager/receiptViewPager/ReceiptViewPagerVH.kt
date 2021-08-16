package com.phillipilino.viewpager.receiptViewPager

import android.content.Context
import android.view.View
import com.phillipilino.viewpager.loopViewPager.LoopViewPagerItem
import com.phillipilino.viewpager.loopViewPager.LoopViewPagerVH

class ReceiptViewPagerVH(context: Context, view: View): LoopViewPagerVH(context, view) {
    override fun bind(item: LoopViewPagerItem, position: Int, onItemPressed: ((View, LoopViewPagerItem, Int) -> Unit)?) {
        super.bind(item, position, onItemPressed)
        setImageCornersRadius(topRight = 100f, bottomRight = 100f)
    }
}