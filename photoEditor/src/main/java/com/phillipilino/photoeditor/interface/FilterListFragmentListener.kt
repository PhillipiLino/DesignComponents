package com.phillipilino.photoeditor.`interface`

import com.zomato.photofilters.imageprocessors.Filter

interface FilterListFragmentListener {
    fun onFilterSelected(filter: Filter)
}