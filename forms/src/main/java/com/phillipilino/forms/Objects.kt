package com.phillipilino.forms

import android.content.Context
import android.util.AttributeSet

fun <T>generateObject(objectClass: Class<T>, context: Context, attrs: AttributeSet): T? {
    val classToLoad: Class<T> = objectClass

    val argsList = arrayOfNulls<Class<*>?>(2)
    argsList[0] = Context::class.java
    argsList[1] = AttributeSet::class.java

    return classToLoad.getDeclaredConstructor(*argsList).newInstance(context, attrs)
}