package com.phillipilino.basicviews

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.counter_info_view.view.*

class CounterInfoView(context: Context, attrs: AttributeSet): LinearLayout(context, attrs) {
    var title: String
        get() = lbl_title.text.toString()
        set(value) {
            lbl_title.text = value
        }

    var count: Int = 0
        set(value) {
            field = value
            lbl_count.text = count.toString()
        }

    init {
        inflate(context, R.layout.counter_info_view, this)

        val array = context.theme.obtainStyledAttributes(attrs, R.styleable.CounterInfoView, 0, 0)
        title = array.getString(R.styleable.CounterInfoView_title) ?: ""
        count = array.getInteger(R.styleable.CounterInfoView_count, 0)

        array.recycle()
    }
}