package com.phillipilino.viewpager.loopViewPager

import android.content.Context
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import androidx.annotation.DimenRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.CornerFamily
import com.phillipilino.basehelpers.ResourcesHelper
import com.phillipilino.basehelpers.adapters.BaseVH
import com.phillipilino.viewpager.R

open class LoopViewPagerVH(val context: Context, view: View): BaseVH<LoopViewPagerItem>(view) {
    private var itemImageView: ShapeableImageView

    init {
        val layout = view.findViewById<ConstraintLayout>(R.id.layout_item)
        itemImageView = ShapeableImageView(context)
        itemImageView.layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        layout.addView(itemImageView)
    }

    override fun bind(
        item: LoopViewPagerItem,
        position: Int,
        onItemPressed: ((LoopViewPagerItem, Int) -> Unit)?) {
        super.bind(item, position, onItemPressed)
        if (item.imageResId != null) {
            itemImageView.setImageResource(item.imageResId)
            return
        }

        Glide
            .with(context)
            .load(item.url)
            .centerCrop()
            .into(itemImageView)
    }

    fun setImageCornersRadius(
        @DimenRes topLeft: Int? = null,
        @DimenRes topRight: Int? = null,
        @DimenRes bottomLeft: Int? = null,
        @DimenRes bottomRight: Int? = null) {

        val resHelper = ResourcesHelper(context)

        val topLeftDimen = resHelper.getNullableDimen(topLeft)?.toFloat() ?: 0f
        val topRightDimen = resHelper.getNullableDimen(topRight)?.toFloat() ?: 0f
        val bottomLeftDimen = resHelper.getNullableDimen(bottomLeft)?.toFloat() ?: 0f
        val bottomRightDimen = resHelper.getNullableDimen(bottomRight)?.toFloat() ?: 0f
        setImageCornersRadius(topLeftDimen, topRightDimen, bottomLeftDimen, bottomRightDimen)
    }

    fun setImageCornersRadius(
        topLeft: Float = 0f,
        topRight: Float = 0f,
        bottomLeft: Float = 0f,
        bottomRight: Float = 0f) {

        val shapeAppearance = itemImageView.shapeAppearanceModel
            .toBuilder()
            .setTopLeftCorner(CornerFamily.ROUNDED, topLeft)
            .setTopRightCorner(CornerFamily.ROUNDED, topRight)
            .setBottomLeftCorner(CornerFamily.ROUNDED, bottomLeft)
            .setBottomRightCorner(CornerFamily.ROUNDED, bottomRight)
            .build()

        itemImageView.shapeAppearanceModel = shapeAppearance
    }
}