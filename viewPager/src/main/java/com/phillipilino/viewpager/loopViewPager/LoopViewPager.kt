package com.phillipilino.viewpager.loopViewPager

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.annotation.LayoutRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_DRAGGING
import androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_IDLE
import com.phillipilino.basehelpers.adapters.BaseAdapter
import com.phillipilino.basehelpers.setVisible
import com.phillipilino.viewpager.R
import com.phillipilino.viewpager.extensions.setCurrentItem
import com.phillipilino.viewpager.indicatorView.IndicatorView


/**
 * Implementation of Loop View Pager
 *
 * @constructor
 * TODO
 *
 * @param context
 * @param attrs
 */
class LoopViewPager(context: Context, attrs: AttributeSet): ConstraintLayout(context, attrs) {

    companion object {
        @LayoutRes val VIEW_HOLDER_LAYOUT = R.layout.item_loop_view_pager
    }

    var indicatorView: IndicatorView? = null
    var items: List<LoopViewPagerItem> = listOf()
    var currentItem: LoopViewPagerItem? = null
    var currentPosition = 0

    private var infinityItems: MutableList<LoopViewPagerItem> = mutableListOf()
    private var adapter: BaseAdapter<LoopViewPagerItem>
    private var infinityPager = true
    private var viewPager: ViewPager2? = null

    var autoMoveStarted = false
    private var autoMoveInterval: Long = 0
    private var autoMoveVelocity: Long = 0
    private var sliderHandler = Handler()
    private val sliderRunnable = Runnable {
        viewPager?.setCurrentItem(viewPager?.currentItem?.plus(1) ?: 0, autoMoveVelocity)
    }

    var offscreenPageLimit
        get() = viewPager?.offscreenPageLimit
        set(value) {
            viewPager?.offscreenPageLimit = value ?: 0
        }

    var childOverScrollMode
        get() = viewPager?.getChildAt(0)?.overScrollMode
        set(value) {
            viewPager?.getChildAt(0)?.overScrollMode = value ?: 0
        }

    init {
        setPadding(0, 0, 0, 0)
        val array = context.theme.obtainStyledAttributes(attrs, R.styleable.LoopViewPager, 0, 0)
        val attrPadding = array.getDimensionPixelSize(R.styleable.LoopViewPager_android_padding, 0)
        val attrPaddingStart = array.getDimensionPixelSize(R.styleable.LoopViewPager_android_paddingStart, attrPadding)
        val attrPaddingTop = array.getDimensionPixelSize(R.styleable.LoopViewPager_android_paddingTop, attrPadding)
        val attrPaddingEnd = array.getDimensionPixelSize(R.styleable.LoopViewPager_android_paddingEnd, attrPadding)
        val attrPaddingBottom = array.getDimensionPixelSize(R.styleable.LoopViewPager_android_paddingBottom, attrPadding)
        val attrClipToPadding = array.getBoolean(R.styleable.LoopViewPager_android_clipToPadding, true)
        val attrClipChildren = array.getBoolean(R.styleable.LoopViewPager_android_clipChildren, true)
        val showIndicator = array.getBoolean(R.styleable.LoopViewPager_showIndicators, true)
        val selectedIdicatorColor = array.getColor(R.styleable.LoopViewPager_selectedIndicatorColor, context.resources.getColor(R.color.santas_gray))
        val unselectedIdicatorColor = array.getColor(R.styleable.LoopViewPager_unselectedIndicatorColor, context.resources.getColor(R.color.santas_gray))
        infinityPager = array.getBoolean(R.styleable.LoopViewPager_infinityPager, true)

        clipToPadding = true
        clipChildren = true
        viewPager?.clipToPadding = attrClipToPadding
        viewPager?.clipChildren = attrClipChildren
        viewPager?.setPadding(attrPaddingStart, attrPaddingTop, attrPaddingEnd, attrPaddingBottom)

        viewPager = ViewPager2(context, attrs)
        viewPager?.layoutParams = LayoutParams(MATCH_PARENT, 0)
        viewPager?.id = View.generateViewId()

        adapter = LoopViewPagerAdapter(context)
        viewPager?.adapter = adapter
        viewPager?.offscreenPageLimit = 1
        addView(viewPager)
        setOnPageChangeCallback()

        indicatorView = IndicatorView(context, attrs)
        indicatorView?.layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        indicatorView?.insertItems(10)
        indicatorView?.id = View.generateViewId()
        indicatorView?.setColors(selectedIdicatorColor, unselectedIdicatorColor)

        addView(indicatorView)

        val constraintSet = ConstraintSet()
        constraintSet.clone(this)
        constraintSet.connect(indicatorView!!.id, ConstraintSet.BOTTOM, this.id, ConstraintSet.BOTTOM, 0)
        constraintSet.connect(indicatorView!!.id, ConstraintSet.RIGHT, this.id, ConstraintSet.RIGHT, 0)
        constraintSet.connect(indicatorView!!.id, ConstraintSet.LEFT, this.id, ConstraintSet.LEFT, 0)
        constraintSet.connect(viewPager!!.id, ConstraintSet.TOP, this.id, ConstraintSet.TOP, 0)
        constraintSet.connect(viewPager!!.id, ConstraintSet.RIGHT, this.id, ConstraintSet.RIGHT, 0)
        constraintSet.connect(viewPager!!.id, ConstraintSet.LEFT, this.id, ConstraintSet.LEFT, 0)
        constraintSet.connect(viewPager!!.id, ConstraintSet.BOTTOM, indicatorView!!.id, ConstraintSet.TOP, 0)
        constraintSet.applyTo(this)

        indicatorView?.setVisible(showIndicator)
        array.recycle()
    }

    /**
     * Function to validate and set currentItem and currentPosition
     *
     * @param position - current viewPager position
     */
    private fun checkPosition(position: Int) {
        if (items.isEmpty()) return
        val realPosition = position % items.size
        val item = items[realPosition]
        if (item == currentItem) return
        indicatorView?.setSelectedItem(realPosition)
        currentPosition = position
        currentItem = item
    }

    /**
     * Function to set current position based on viewPager state when current item are first or last
     * to give de infinity sensation
     *
     * @param state - viewPager currentState
     */
    private fun setInfinityPositions(state: Int) {
        val lastInfinityItem = infinityItems.size - 1
        val lastRealItem = items.size - 1

        if (state == SCROLL_STATE_IDLE) {
            when(currentPosition) {
                0 -> viewPager?.setCurrentItem(items.size, false)
                items.size + 1 -> viewPager?.setCurrentItem(1, false)
                lastInfinityItem -> viewPager?.setCurrentItem(lastRealItem, false)
                else -> return
            }
        }

        if (state == SCROLL_STATE_DRAGGING && currentPosition == lastInfinityItem) {
            viewPager?.setCurrentItem(lastRealItem, false)
        }
    }

    /**
     * Function to set new adapter to viewPager
     *
     * @param adapter - adapter to be used on viewPager
     */
    fun setAdapter(adapter: BaseAdapter<LoopViewPagerItem>) {
        this.adapter = adapter
        viewPager?.adapter = adapter
    }

    /**
     * Function to load items on viewPager
     *
     * @param items - items to be loaded on viewPager
     * @param onItemPressed - callback called when press viewPager item
     */
    fun loadItems(items: List<LoopViewPagerItem>,
                  onItemPressed: ((LoopViewPagerItem, Int) -> Unit)?) {

        this.items = items
        currentItem = items.firstOrNull()
        indicatorView?.insertItems(items.size)
        indicatorView?.setSelectedItem(0)

        var itemsToAdapter = items
        if (infinityPager) {
            infinityItems = items.toMutableList()
            infinityItems.addAll(infinityItems)
            if (items.size < 3) infinityItems.addAll(infinityItems)
            itemsToAdapter = infinityItems
        }

        adapter.loadItems(itemsToAdapter) { item, position ->
            onItemPressed?.invoke(item, (position % items.size))
        }
    }

    /**
     * Function to set viewPager page change callbacks
     *
     * @param onPageScrollStateChanged - callback called when view pager scroll state change. Receive current state
     * @param onPageScrolled - callback called when view pager is scrolled. Receive current position, positionOffset and positionOffsetPixels
     * @param onPageSelected - callback called when view pager stop on item. Receive selected position
     */
    fun setOnPageChangeCallback(
        onPageScrollStateChanged: ((Int) -> Unit) = {},
        onPageScrolled: ((Int, Float, Int) -> Unit) = { _, _, _ -> },
        onPageSelected: ((Int) -> Unit) = {}) {

        viewPager?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                if (infinityPager) setInfinityPositions(state)
                onPageScrollStateChanged(state)
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                checkPosition(position)
                onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                onPageSelected(position)

                if (!autoMoveStarted) return
                renewSliderHandler()
            }
        })
    }

    /**
     * Function to set viewPager CompositePageTransformer
     *
     * @param transformer - CompositePageTransformer to set to viewPager
     */
    fun setPageTransformer(transformer: CompositePageTransformer) {
        viewPager?.setPageTransformer(transformer)
    }

    /**
     * When call this function the viewPager will slide automatically
     * Call on `onResume` of used activity or fragment
     *
     * @param interval - interval between automatic slides in milliseconds
     * @param velocity - automatic slide velocity in milliseconds
     */
    fun startAutoMove(interval: Long, velocity: Long = 2000) {
        autoMoveStarted = true
        autoMoveInterval = interval
        autoMoveVelocity = velocity
        renewSliderHandler()
    }

    /**
     * Function to stop viewPager automatically slider
     * Call on `onPause` of used activity or fragment
     *
     */
    fun stopAutoMove() {
        autoMoveStarted = false
        sliderHandler.removeCallbacks(sliderRunnable)
    }

    /**
     * Function to renew automatic slide handler
     *
     */
    private fun renewSliderHandler() {
        sliderHandler.removeCallbacks(sliderRunnable)
        sliderHandler.postDelayed(sliderRunnable, autoMoveInterval)
    }
}