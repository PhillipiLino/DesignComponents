package com.phillipilino.viewpager.loopViewPager

import androidx.annotation.DrawableRes

data class LoopViewPagerItem(
    @DrawableRes val imageResId: Int?,
    val url: String?,
    val name: String) {
    constructor(@DrawableRes imageResId: Int, name: String): this(imageResId, null, name)
    constructor(imageUrl: String, name: String): this(null, imageUrl, name)
}