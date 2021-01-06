package com.phillipilino.forms

import android.text.InputType
import android.widget.EditText

data class FormTextField(
    val hint: String,
    val textSize: Float,
    val initText: String = "",
    val inputType: Int = InputType.TYPE_CLASS_TEXT,
    override val objectClass: Class<out EditText> = EditText::class.java,
    override val id: String,
    override val title: String,
    override val marginStart: Int = 0,
    override val marginTop: Int = 0,
    override val marginEnd: Int = 0,
    override val marginBottom: Int = 0
): FormField<EditText>(objectClass, id, title, marginStart, marginTop, marginEnd, marginBottom)