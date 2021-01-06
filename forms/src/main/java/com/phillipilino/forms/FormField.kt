package com.phillipilino.forms

abstract class FormField<T>(
    open val objectClass: Class<out T>,
    open val id: String,
    open val title: String,
    open val marginStart: Int = 0,
    open val marginTop: Int = 0,
    open val marginEnd: Int = 0,
    open val marginBottom: Int = 0
)