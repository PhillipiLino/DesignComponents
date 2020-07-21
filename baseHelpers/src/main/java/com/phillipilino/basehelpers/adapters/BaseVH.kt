package com.phillipilino.basehelpers.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BaseVH<T>(val view: View): RecyclerView.ViewHolder(view) {
    open fun bind(item: T, position: Int, onItemPressed: ((T, Int) -> Unit)?) {
        view.setOnClickListener { onItemPressed?.invoke(item, position) }
    }
}