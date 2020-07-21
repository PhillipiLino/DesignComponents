package com.phillipilino.viewpager.receiptViewPager

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_DRAGGING
import androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_IDLE
import com.phillipilino.viewpager.R
import com.phillipilino.viewpager.loopViewPager.LoopViewPagerItem
import kotlinx.android.synthetic.main.receipt_view_pager.view.*
import kotlin.math.abs

class ReceiptViewPager(context: Context, attrs: AttributeSet): ConstraintLayout(context, attrs) {

    companion object {
        const val SLIDE_PAGER_TO_LEFT = -1
        const val SLIDE_PAGER_TO_RIGHT = 1
        const val PAGER_TEXT_DEFAULT_TRANSLATION = 100f
        const val PAGER_TEXT_ANIM_DURATION: Long = 300
    }

    private var textIsVisible = true
    private var slideDirection = 0
    private var positionWithOffset = 0.0f
    private var currentState = 0
    private var positionOnChangeState = 0
    private var infinityPager = true
    private var firstSelect = true

    private var adapter = ReceiptViewPagerAdapter(context)

    init {
        inflate(context, R.layout.receipt_view_pager, this)
        val array = context.theme.obtainStyledAttributes(attrs, R.styleable.ReceiptViewPager, 0, 0)
        infinityPager = array.getBoolean(R.styleable.ReceiptViewPager_infinityPager, true)
        array.recycle()

        loop_view_pager.offscreenPageLimit = 3
        loop_view_pager.childOverScrollMode = View.OVER_SCROLL_NEVER
        loop_view_pager.setAdapter(adapter)

        val pageTransformer = CompositePageTransformer()
        pageTransformer.addTransformer(MarginPageTransformer(0))
        pageTransformer.addTransformer { page, position ->
            if (position < 0) {
                page.scaleY = 1f
                page.scaleX = 1f
                page.alpha = 1f
                page.translationZ = 0f
                return@addTransformer
            }

            val radius = 1 - abs(position)
            page.scaleY = 0.85f + radius * 0.15f
            page.scaleX = 1f - 0.05f * position;
            page.translationX = (-page.width + 50.0f) * position;
            page.translationZ = -position
            page.alpha = 0.8f + radius * 0.3f
        }

        loop_view_pager.setPageTransformer(pageTransformer)

        loop_view_pager.setOnPageChangeCallback(onPageScrollStateChanged = { state ->
            checkState(state)
            Handler().postDelayed({ animateText(state) }, 100)
        }, onPageScrolled = { position, positionOffset, _ ->
            slideDirection = when {
                (position + positionOffset > positionWithOffset) -> SLIDE_PAGER_TO_LEFT
                else -> SLIDE_PAGER_TO_RIGHT
            }

            positionWithOffset = position + positionOffset
        }, onPageSelected = {
            if (firstSelect) {
                firstSelect = false
                return@setOnPageChangeCallback
            }

            if (!loop_view_pager.autoMoveStarted) return@setOnPageChangeCallback
            slideDirection = SLIDE_PAGER_TO_LEFT
            animateText(SCROLL_STATE_DRAGGING)
        })
    }

    private fun checkState(state: Int) {
        if (state == currentState) return
        positionOnChangeState = loop_view_pager.currentPosition
        currentState = state
    }

    private fun animateText(state: Int) {
        when {
            state == SCROLL_STATE_DRAGGING && textIsVisible -> {
                val validator = positionOnChangeState == loop_view_pager.currentPosition && loop_view_pager.currentPosition == loop_view_pager.items.size - 1
                if (validator && !infinityPager) return

                val translation = PAGER_TEXT_DEFAULT_TRANSLATION * slideDirection
                setTextAnimation(0f,
                    translation,
                    duration = 200,
                    showText = false)
            }

            state == SCROLL_STATE_IDLE && !textIsVisible ->{
                setTextAnimation(1f,
                    0f,
                    500,
                    onStart = { pager_text.text = loop_view_pager.currentItem?.name })
            }
        }
    }

    private fun setTextAnimation(
        alpha: Float,
        translate: Float,
        delay: Long = 0,
        duration: Long = PAGER_TEXT_ANIM_DURATION,
        showText: Boolean = true,
        onStart: () -> Unit = {}) {

        textIsVisible = showText
        pager_text.animate()
            .alpha(alpha)
            .translationX(translate)
            .setStartDelay(delay)
            .setDuration(duration)
            .withStartAction { onStart.invoke() }
            .start()
    }

    fun loadItems(items: List<LoopViewPagerItem>, onItemPressed: ((LoopViewPagerItem, Int) -> Unit)?) {
        loop_view_pager.loadItems(items, onItemPressed)
        pager_text.text = loop_view_pager.currentItem?.name
    }

    fun startAutoMove(interval: Long) { loop_view_pager?.startAutoMove(interval) }

    fun stopAutoMove() { loop_view_pager?.stopAutoMove() }
}