package com.phillipilino.forms

import android.content.Context
import android.util.AttributeSet
import android.view.View.inflate
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.question_switch.view.*

class QuestionSwitch(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {
    var result = false
        get() = switch_question.isChecked
        private set

    var switch: SwitchCompat? = null
        get() = switch_question
        private set

    init {
        inflate(context, R.layout.question_switch, this)
        val array = context.theme.obtainStyledAttributes(attrs, R.styleable.QuestionSwitch, 0, 0)
        val title = array.getString(R.styleable.QuestionSwitch_title).orEmpty()
        val negativeAnswer = array.getString(R.styleable.QuestionSwitch_negativeAnswer) ?: "Não"
        val positiveAnswer = array.getString(R.styleable.QuestionSwitch_positiveAnswer) ?: "Sim"

        setQuestion(title, negativeAnswer, positiveAnswer)
        array.recycle()
    }

    fun setQuestion(title: String,
                    negativeAnswer: String,
                    positiveAnswer: String) {
        lbl_title.text = title
        lbl_negative_text.text = negativeAnswer
        lbl_positive_text.text = positiveAnswer
    }
}