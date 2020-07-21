package com.phillipilino.viewpager.receiptViewPager

import android.content.Context
import android.view.View
import com.phillipilino.viewpager.loopViewPager.LoopViewPagerAdapter

open class ReceiptViewPagerAdapter(context: Context): LoopViewPagerAdapter(context) {
    override fun getViewHolder(view: View, viewType: Int) = ReceiptViewPagerVH(context, view)
}