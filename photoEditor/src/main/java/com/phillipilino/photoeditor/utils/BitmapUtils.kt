package com.phillipilino.photoeditor.utils

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.contentValuesOf
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method


object BitmapUtils {
    fun getBitmapFromAssets(context: Context, fileName: String, width: Int, height: Int): Bitmap? {
        val assetManager = context.assets

        val inputStream: InputStream
        val bitmap: Bitmap? = null
        try {
           val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            inputStream = assetManager.open(fileName)

            options.inSampleSize = calculateSampleSize(options, width, height)
            options.inJustDecodeBounds = false
            return BitmapFactory.decodeStream(inputStream, null, options)
        } catch (e: IOException) {
            Log.e("DEBUG", e.message ?: "")
        }

        return null
    }

    fun getBitmapFromGallery(context: Context, path: Uri, width: Int, height: Int): Bitmap {
        val imageStream = context.contentResolver.openInputStream(path)

        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        var selectedImage = BitmapFactory.decodeStream(imageStream, null, options)
        options.inSampleSize = calculateSampleSize(options, width, height)
        options.inJustDecodeBounds = false

        selectedImage = BitmapFactory.decodeStream(context.contentResolver.openInputStream(path),
            null, options)
        return rotateBitmap(path.path, selectedImage!!)!!
    }

    fun insertImage(contentResolver: ContentResolver, source: Bitmap?, title: String, description: String): String? {
        val values = contentValuesOf()
        values.put(MediaStore.Images.Media.TITLE, title)
        values.put(MediaStore.Images.Media.DISPLAY_NAME, title)
        values.put(MediaStore.Images.Media.DESCRIPTION, description)
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis())
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())

        var url: Uri? = null
        var stringUrl: String? = null

        try {
            url = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            if (source != null) {
                val imageOut = contentResolver.openOutputStream(url!!)
                try {
                    source.compress(Bitmap.CompressFormat.JPEG, 50, imageOut)
                } finally {
                    imageOut!!.close()
                }

                val id = ContentUris.parseId(url)
                val miniThumb = MediaStore.Images.Thumbnails.getThumbnail(contentResolver,
                id,
                MediaStore.Images.Thumbnails.MINI_KIND,
                null)

                val THUMBSIZE = 128

                val thumbImage =
                    ThumbnailUtils.extractThumbnail(source, THUMBSIZE, THUMBSIZE)


//                storeThumbnail(contentResolver, thumbImage, id, 50f, 50f, MediaStore.Images.Thumbnails.MICRO_KIND)
            } else {
                contentResolver.delete(url!!, null, null)
                url = null
            }
        } catch (e: Exception) {
            if (url != null) {
                contentResolver.delete(url, null, null)
                url = null
            }

            e.printStackTrace()
        }

        if (url != null) {
            stringUrl = url.toString()
        }

        return stringUrl
    }

    private fun storeThumbnail(contentResolver: ContentResolver, source: Bitmap, id: Long, width: Float, height: Float, micro: Int): Bitmap? {
        val matrix = Matrix()
        val scaleX = width / source.width
        val scaleY= width / source.height

        matrix.setScale(scaleX, scaleY)

        val thumb = Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)

        val values = ContentValues(4)
        values.put(MediaStore.Images.Thumbnails.KIND, micro)
        values.put(MediaStore.Images.Thumbnails.IMAGE_ID, id.toInt())
        values.put(MediaStore.Images.Thumbnails.HEIGHT, thumb.height)
        values.put(MediaStore.Images.Thumbnails.WIDTH, thumb.width)

        val url = contentResolver.insert(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, values)

        try {
            val thumbOut = contentResolver.openOutputStream(url!!)
            thumb.compress(Bitmap.CompressFormat.JPEG, 100, thumbOut)
            thumbOut!!.close()
            return thumb
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            return null
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }

    private fun calculateSampleSize(options: BitmapFactory.Options, regWidth: Int, regHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth

        var inSampleSize = 1

        if (height > regHeight || width > regWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2
            while (halfHeight / inSampleSize >= regHeight && halfWidth / inSampleSize >= regHeight)
                inSampleSize *= 2
        }

        return inSampleSize
    }

    fun rotateBitmap(src: String?, bitmap: Bitmap): Bitmap? {
        try {
            val orientation = getExifOrientation(src!!)
            if (orientation == 1) {
                return bitmap
            }
            val matrix = Matrix()
            when (orientation) {
                2 -> matrix.setScale(-1f, 1f)
                3 -> matrix.setRotate(180f)
                4 -> {
                    matrix.setRotate(180f)
                    matrix.postScale(-1f, 1f)
                }
                5 -> {
                    matrix.setRotate(90f)
                    matrix.postScale(-1f, 1f)
                }
                6 -> matrix.setRotate(90f)
                7 -> {
                    matrix.setRotate(-90f)
                    matrix.postScale(-1f, 1f)
                }
                8 -> matrix.setRotate(-90f)
                else -> return bitmap
            }
            return try {
                val oriented = Bitmap.createBitmap(
                    bitmap, 0, 0,
                    bitmap.width, bitmap.height, matrix, true
                )
                bitmap.recycle()
                oriented
            } catch (e: OutOfMemoryError) {
                e.printStackTrace()
                bitmap
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bitmap
    }

    @Throws(IOException::class)
    private fun getExifOrientation(src: String): Int {
        var orientation = 1
        try {
            /**
             * if your are targeting only api level >= 5 ExifInterface exif =
             * new ExifInterface(src); orientation =
             * exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
             */
            if (Build.VERSION.SDK_INT >= 5) {
                val exifClass = Class
                    .forName("android.media.ExifInterface")
                val exifConstructor: Constructor<*> = exifClass
                    .getConstructor(*arrayOf<Class<*>>(String::class.java))
                val exifInstance: Any = exifConstructor
                    .newInstance(arrayOf<Any>(src))
                val getAttributeInt: Method = exifClass.getMethod(
                    "getAttributeInt",
                    *arrayOf(
                        String::class.java,
                        Int::class.javaPrimitiveType
                    )
                )
                val tagOrientationField: Field = exifClass.getField("TAG_ORIENTATION")
                val tagOrientation = tagOrientationField.get(null) as String
                orientation = getAttributeInt(exifInstance, arrayOf(tagOrientation, 1)) as Int
            }
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: SecurityException) {
            e.printStackTrace()
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: InstantiationException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        }
        return orientation
    }
}