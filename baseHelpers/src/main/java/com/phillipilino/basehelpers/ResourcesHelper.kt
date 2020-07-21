package com.phillipilino.basehelpers

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.DisplayMetrics
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.DimenRes
import androidx.annotation.StyleRes


@Suppress("DEPRECATION")
class ResourcesHelper(context: Context?) : ContextWrapper(context) {
    fun getColorHelper(id: Int): Int =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) resources.getColor(id, null)
        else resources.getColor(id)

    fun getDrawableHelper(id: Int): Drawable =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) resources.getDrawable(id, null)
        else resources.getDrawable(id)

    fun setTextAppearanceHelper(view: EditText, @StyleRes style: Int) =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) view.setTextAppearance(style)
        else view.setTextAppearance(this, style)

    fun getNullableDimen(@DimenRes dimen: Int?): Int? {
        return resources.getDimensionPixelSize(dimen ?: return null)
    }
}

fun Context.convertDpToPixel(dp: Float): Float {
    return dp * (resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

fun Activity.hideSoftKeyboard() {
    val view = currentFocus ?: View(this)
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager
    inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
}