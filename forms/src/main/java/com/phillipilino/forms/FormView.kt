package com.phillipilino.forms

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.core.view.children
import org.json.JSONObject

class FormView(context: Context, private val attrs: AttributeSet): LinearLayout(context, attrs) {
    init {
        orientation = VERTICAL
        layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        )
    }

    fun <T> addField(field: FormField<T>) {
        val fieldObject = generateObject(field.objectClass, context, attrs)
        val params = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        params.setMargins(field.marginStart, field.marginTop, field.marginEnd, field.marginBottom)
        (fieldObject as? View)?.layoutParams = params
        (fieldObject as? View)?.tag = field.id

        (field as? FormTextField)?.let { setupTextField(it, fieldObject as? EditText) }

        (field as? FormSwitchField)?.let { setupSwitchField(it, fieldObject as? QuestionSwitch) }
    }

    private fun setupTextField(field: FormTextField, editText: EditText?) {
        editText?.hint = field.hint
        editText?.textSize = field.textSize
        editText?.inputType = field.inputType
        editText?.setText(field.initText)
        addView(editText)
    }

    private fun setupSwitchField(field: FormSwitchField, switch: QuestionSwitch?) {
        switch?.setQuestion(title = field.title, negativeAnswer = field.negativeText, positiveAnswer = field.positiveText)
        switch?.switch?.isChecked = field.initialValue
        addView(switch)
    }

    override fun toString() = getFormInfo().toString()

    fun getJSON() = JSONObject(getFormInfo())

    fun getFormInfo() =
        children.fold(mutableMapOf<String, Any>()) { result, child ->
            val key = (child.tag as? String).orEmpty()

            (child as? EditText)?.let { result[key] = it.text?.toString().orEmpty() }
            (child as? QuestionSwitch)?.let { result[key] = it.result }

            result
        }.toMap()
}