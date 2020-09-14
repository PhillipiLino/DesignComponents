package com.phillipilino.photoeditor

import android.graphics.Bitmap
import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.LinearLayout.HORIZONTAL
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.phillipilino.photoeditor.MainActivity.Main.IMAGE_NAME
import com.phillipilino.photoeditor.`interface`.FilterListFragmentListener
import com.phillipilino.photoeditor.adapter.ThumbnailAdapter
import com.phillipilino.photoeditor.utils.BitmapUtils
import com.phillipilino.photoeditor.utils.SpaceItemDecoration
import com.zomato.photofilters.FilterPack
import com.zomato.photofilters.imageprocessors.Filter
import com.zomato.photofilters.utils.ThumbnailItem
import com.zomato.photofilters.utils.ThumbnailsManager
import kotlinx.android.synthetic.main.fragment_filter_list.view.*

class FilterListFragment : Fragment(), FilterListFragmentListener {

    private var listener: FilterListFragmentListener? = null
    private var itemList = mutableListOf<ThumbnailItem>()
    private lateinit var adapter: ThumbnailAdapter
    var viewFragment: View? = null

    fun setListener(listener: FilterListFragmentListener) {
        this.listener = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewFragment = inflater.inflate(R.layout.fragment_filter_list, container, false)

        adapter = ThumbnailAdapter(activity!!, itemList, this)

        viewFragment?.recycler_view?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        viewFragment?.recycler_view?.itemAnimator = DefaultItemAnimator()

        val space = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, resources.displayMetrics).toInt()
        viewFragment?.recycler_view?.addItemDecoration(SpaceItemDecoration(space))
        viewFragment?.recycler_view?.adapter = adapter

        displayImage(null)

        return viewFragment
    }

    fun displayImage(bitmap: Bitmap?) {
        val r = Runnable {
            val thumb: Bitmap?
            if (bitmap == null)
                thumb = BitmapUtils.getBitmapFromAssets(activity!!, MainActivity.Main.IMAGE_NAME, 100, 100)
            else
                thumb = Bitmap.createScaledBitmap(bitmap, 100, 100, false)

            if (thumb == null) return@Runnable

            ThumbnailsManager.clearThumbs()
            itemList.clear()

            val item = ThumbnailItem()
            item.image = thumb
            item.filterName = "Normal"
            ThumbnailsManager.addThumb(item)

            val filters = FilterPack.getFilterPack(activity!!)

            for (filter in filters) {
                val newItem = ThumbnailItem()
                newItem.image = thumb
                newItem.filter = filter
                newItem.filterName = filter.name
                ThumbnailsManager.addThumb(newItem)
            }

            val teste = ThumbnailsManager.processThumbs(activity!!)
            itemList.addAll(teste)
            activity?.runOnUiThread {
                adapter.notifyDataSetChanged()
            }
        }

        Thread(r).start()
    }

    override fun onFilterSelected(filter: Filter) {
        listener?.onFilterSelected(filter)
    }


}