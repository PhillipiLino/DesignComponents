package com.phillipilino.forms

import android.text.InputType
import android.widget.EditText

data class FormCounterField(
    val value: Int,
    val maxValue: Int,
    val minValue: Int = 0,
    val onCounterChange: ((Int) -> Unit)? = null,
    override val objectClass: Class<out CounterView> = CounterView::class.java,
    override val id: String,
    override val title: String,
    override val marginStart: Int = 0,
    override val marginTop: Int = 0,
    override val marginEnd: Int = 0,
    override val marginBottom: Int = 0
): FormField<CounterView>(objectClass, id, title, marginStart, marginTop, marginEnd, marginBottom)