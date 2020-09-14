package com.phillipilino.photoeditor

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.LENGTH_SHORT
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.phillipilino.photoeditor.`interface`.EditImageFragmentListener
import com.phillipilino.photoeditor.`interface`.FilterListFragmentListener
import com.phillipilino.photoeditor.adapter.ViewPagerAdapter
import com.phillipilino.photoeditor.utils.BitmapUtils
import com.phillipilino.photoeditor.utils.NonSwipeableViewPager
import com.zomato.photofilters.imageprocessors.Filter
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter
import kotlinx.android.synthetic.main.activity_main_editor.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity(), FilterListFragmentListener, EditImageFragmentListener {

    init {
        System.loadLibrary("NativeImageProcessor")
    }

    object Main {
        val IMAGE_NAME = "spider.jpeg"
    }

    val SELECT_GALLERY_PERMISSION = 1000

    var originalImage: Bitmap? = null
    lateinit var filteredImage: Bitmap
    lateinit var finalImage: Bitmap

    lateinit var filterListFragment: FilterListFragment
    lateinit var editImageFragment: EditImageFragment

    var brightnessFinal = 0
    var saturationFinal = 1.0f
    var constrantFinal = 1.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_editor)

        setSupportActionBar(toolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Instagram Filter"

        loadImage()
        setupViewPager(viewPager)
        tabs.setupWithViewPager(viewPager)
    }

    private fun setupViewPager(viewPager: NonSwipeableViewPager?) {
        val adapter = ViewPagerAdapter(supportFragmentManager)

        filterListFragment = FilterListFragment()
        filterListFragment.setListener(this)

        editImageFragment = EditImageFragment()
        editImageFragment.setListener(this)

        adapter.addFragment(filterListFragment, "FILTERS")
        adapter.addFragment(editImageFragment, "EDIT")

        viewPager?.adapter = adapter
    }

    private fun loadImage() {
        originalImage = BitmapUtils.getBitmapFromAssets(this, Main.IMAGE_NAME, 300, 300)
        filteredImage = originalImage!!.copy(Bitmap.Config.ARGB_8888, true)
        finalImage = originalImage!!.copy(Bitmap.Config.ARGB_8888, true)
        image_preview.setImageBitmap(originalImage)
    }

    override fun onFilterSelected(filter: Filter) {
        resetControls()
        filteredImage = originalImage!!.copy(Bitmap.Config.ARGB_8888, true)
        image_preview.setImageBitmap(filter.processFilter(filteredImage))
        finalImage = filteredImage!!.copy(Bitmap.Config.ARGB_8888, true)
    }

    private fun resetControls() {
        editImageFragment?.resetControls()

        brightnessFinal = 0
        saturationFinal = 1.0f
        constrantFinal = 1.0f
    }

    override fun onBrightnessChanged(brightness: Int) {
        brightnessFinal = brightness
        val myFilter = Filter()
        myFilter.addSubFilter(BrightnessSubFilter(brightness))
        val image = finalImage.copy(Bitmap.Config.ARGB_8888, true)
        image_preview.setImageBitmap(myFilter.processFilter(image))
    }

    override fun onSaturationChanged(saturation: Float) {
        saturationFinal = saturation
        val myFilter = Filter()
        myFilter.addSubFilter(SaturationSubfilter(saturation))
        val image = finalImage.copy(Bitmap.Config.ARGB_8888, true)
        image_preview.setImageBitmap(myFilter.processFilter(image))
    }

    override fun onConstrantChanged(constrant: Float) {
        constrantFinal = constrant
        val myFilter = Filter()
        myFilter.addSubFilter(ContrastSubFilter(constrant))
        val image = finalImage.copy(Bitmap.Config.ARGB_8888, true)
        image_preview.setImageBitmap(myFilter.processFilter(image))
    }

    override fun onEditStarted() {

    }

    override fun onEditComplete() {
        val bitmap = filteredImage.copy(Bitmap.Config.ARGB_8888, true)
        val myFilter = Filter()

        myFilter.addSubFilter(ContrastSubFilter(constrantFinal))
        myFilter.addSubFilter(SaturationSubfilter(saturationFinal))
        myFilter.addSubFilter(BrightnessSubFilter(brightnessFinal))
        finalImage = myFilter.processFilter(bitmap)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_open) {
            openImageFromGallery()
            return true
        }

        if (id == R.id.action_save) {
            saveImageToGallery()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun saveImageToGallery() {
        val withListener = Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report!!.areAllPermissionsGranted()) {
                        val path = BitmapUtils.insertImage(contentResolver,
                            finalImage,
                            System.currentTimeMillis().toString()+"_profile.jpg", "")

                        if (!TextUtils.isEmpty(path)) {
                            val snackbar = Snackbar.make(coordinator, "Image saved to gallery", Snackbar.LENGTH_SHORT)
                                .setAction("OPEN") {
                                    openImage(path!!)
                                }

                            snackbar.show()
                        } else {
                            val snackbar = Snackbar.make(coordinator, "Unable to saved image", Snackbar.LENGTH_SHORT)
                            snackbar.show()
                        }
                    } else
                        Toast.makeText(applicationContext, "Permission denied", Toast.LENGTH_SHORT).show()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token!!.continuePermissionRequest()
                }

            }).check()
    }

    fun openImage(path: String) {
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.setDataAndType(Uri.parse(path), "image/*")
        startActivity(intent)
    }

    private fun openImageFromGallery() {
        val withListener = Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report?.areAllPermissionsGranted() == true) {
                        val intent = Intent(Intent.ACTION_PICK)
                        intent.type = "image/*"
                        startActivityForResult(intent, SELECT_GALLERY_PERMISSION)
                    } else
                        Toast.makeText(applicationContext, "Permission denied", Toast.LENGTH_SHORT).show()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token!!.continuePermissionRequest()
                }

            }).check()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == SELECT_GALLERY_PERMISSION) {
            val bitmap = BitmapUtils.getBitmapFromGallery(this, data!!.data!!, 800, 800)

//            originalImage?.recycle()
//            finalImage?.recycle()
//            filteredImage?.recycle()

            originalImage = bitmap.copy(Bitmap.Config.ARGB_8888, true)
            filteredImage = originalImage!!.copy(Bitmap.Config.ARGB_8888, true)
            finalImage = originalImage!!.copy(Bitmap.Config.ARGB_8888, true)
            image_preview.setImageBitmap(bitmap)

//            bitmap.recycle()

            filterListFragment.displayImage(bitmap)
        }
    }
}