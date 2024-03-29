package com.phillipilino.basehelpers.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<T>: RecyclerView.Adapter<BaseVH<T>>() {
    var items: List<T> = listOf()
    var onItemPressed: ((View, T, Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseVH<T> =
        getViewHolder(LayoutInflater.from(parent.context).inflate(viewType, parent, false),
            viewType)

    override fun onBindViewHolder(holder: BaseVH<T>, position: Int) =
        holder.bind(items[position], position, onItemPressed)

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int =
        getLayoutId(position, items[position])

    protected abstract fun getLayoutId(position: Int, obj: T): Int

    abstract fun getViewHolder(view: View, viewType: Int): BaseVH<T>

    fun loadItems(items: List<T>, onItemPressed: ((View, T, Int) -> Unit)?) {
        this.items = items
        this.onItemPressed = onItemPressed
        notifyDataSetChanged()
    }
}