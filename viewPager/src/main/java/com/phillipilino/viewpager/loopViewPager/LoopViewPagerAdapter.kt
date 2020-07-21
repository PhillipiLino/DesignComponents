package com.phillipilino.viewpager.loopViewPager

import android.content.Context
import android.view.View
import com.phillipilino.basehelpers.adapters.BaseAdapter
import com.phillipilino.basehelpers.adapters.BaseVH

open class LoopViewPagerAdapter(val context: Context): BaseAdapter<LoopViewPagerItem>() {
    override fun getLayoutId(position: Int, obj: LoopViewPagerItem) = LoopViewPager.VIEW_HOLDER_LAYOUT
    override fun getViewHolder(view: View, viewType: Int): BaseVH<LoopViewPagerItem> = LoopViewPagerVH(context, view)
}