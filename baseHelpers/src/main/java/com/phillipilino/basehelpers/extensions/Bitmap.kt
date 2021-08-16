package com.phillipilino.basehelpers.extensions

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

fun Bitmap.resize(maxWidth: Int, maxHeight: Int): Bitmap? {
    var image: Bitmap = this
    return if (maxHeight > 0 && maxWidth > 0) {
        val width: Int = image.width
        val height: Int = image.height
        val ratioBitmap = width.toFloat() / height.toFloat()
        val ratioMax = maxWidth.toFloat() / maxHeight.toFloat()
        var finalWidth = maxWidth
        var finalHeight = maxHeight
        if (ratioMax > ratioBitmap) {
            finalWidth = (maxHeight.toFloat() * ratioBitmap).toInt()
        } else {
            finalHeight = (maxWidth.toFloat() / ratioBitmap).toInt()
        }
        image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true)
        image
    } else {
        image
    }
}

fun Bitmap.compressImage(quality: Int = 100): Bitmap {
    val out = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.PNG, quality, out)
    return BitmapFactory.decodeStream(ByteArrayInputStream(out.toByteArray()))
}