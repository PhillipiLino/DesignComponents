package com.phillipilino.forms

import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.counter_field.view.*

class CounterField(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    var value: Int
        get() = edt_value.text.toString().toInt()
        set(value) = edt_value.setText(value.toString())

    var maxValue: Int = Int.MAX_VALUE
    var minValue: Int = 0

    init {
        inflate(context, R.layout.counter_field, this)
        val array = context.theme.obtainStyledAttributes(attrs, R.styleable.CounterField, 0, 0)
        val title = array.getString(R.styleable.CounterField_title).orEmpty()
        val minusText = array.getString(R.styleable.CounterField_minusButtonText) ?: "-"
        val pusText = array.getString(R.styleable.CounterField_plusButtonText) ?: "+"
        setProperties(title, minusText, pusText)
        array.recycle()

        btn_minus.setOnClickListener { onMinusButtonPressed() }
        btn_minus.setOnLongClickListener {
            autoDecrement = true
            handler.postDelayed(counterRunnable, counterDelay)
            return@setOnLongClickListener false
        }

        btn_minus.setOnTouchListener { v, event ->
            if (event.action in listOf(MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL) && autoDecrement) {
                autoDecrement = false
            }
            false
        }

        btn_plus.setOnTouchListener { v, event ->
            if (event.action in listOf(MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL) && autoIncrement) {
                autoIncrement = false
            }
            false
        }

        btn_plus.setOnClickListener { onPlusButtonPressed() }
        btn_plus.setOnLongClickListener {
            autoIncrement = true
            handler.postDelayed(counterRunnable, counterDelay)
            return@setOnLongClickListener false
        }
    }

    private var autoIncrement = false
    private var autoDecrement = false
    private var counterDelay: Long = 50
    internal val handler = Handler()

    private val counterRunnable = object : Runnable {
        override fun run() {
            if (autoIncrement) {
                onPlusButtonPressed()
                handler.postDelayed(this, counterDelay)
                return
            }

            if (autoDecrement) {
                onMinusButtonPressed()
                handler.postDelayed(this, counterDelay)
                return
            }
        }
    }

    private fun onMinusButtonPressed() {
        if (value == minValue) return
        value--
        setupButtonsStates()
        context.vibrate()
    }

    private fun onPlusButtonPressed() {
        if (value >= maxValue) return
        value++
        setupButtonsStates()
        context.vibrate()
    }

    private fun setupButtonsStates() {
//        btn_minus.isEnabled = value > 0
//        btn_plus.isEnabled = value < 100
    }

    fun setProperties(title: String,
                      minusButtonText: String,
                      plusButtonText: String) {
//        edt_value.text = title
        btn_minus.text = minusButtonText
        btn_plus.text = plusButtonText
    }
}

fun Context.vibrate(duration: Long = 100, amplitude: Int = 100) {
    val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val effect = VibrationEffect.createOneShot(duration, amplitude)
        return vibrator.vibrate(effect)
    }

    vibrator.vibrate(duration)
}