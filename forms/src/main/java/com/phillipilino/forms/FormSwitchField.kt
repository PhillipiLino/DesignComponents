package com.phillipilino.forms

data class FormSwitchField(
    val positiveText: String = "Sim",
    val negativeText: String = "Não",
    val initialValue: Boolean = false,
    override val objectClass: Class<out QuestionSwitch> = QuestionSwitch::class.java,
    override val id: String,
    override val title: String,
    override val marginStart: Int = 0,
    override val marginTop: Int = 0,
    override val marginEnd: Int = 0,
    override val marginBottom: Int = 0
): FormField<QuestionSwitch>(objectClass, id, title, marginStart, marginTop, marginEnd, marginBottom)