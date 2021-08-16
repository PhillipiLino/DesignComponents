package com.phillipilino.basicviews

import android.content.Context
import android.util.AttributeSet

class ContractsAutoCompleteTextView : androidx.appcompat.widget.AppCompatAutoCompleteTextView {
    constructor(context: Context) : super(context) {}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {}

    override fun onFilterComplete(count: Int) {
        super.onFilterComplete(count)
        onContractsAvailability?.contractsRetrieved(count)
    }

    override fun showDropDown() {
        super.showDropDown()
        onContractsAvailability?.onShowDropDown()
    }

    override fun dismissDropDown() {
        super.dismissDropDown()
        onContractsAvailability?.onDismissDropDown()
    }

    interface OnContractsAvailability {
        fun contractsRetrieved(count: Int)
        fun onShowDropDown()
        fun onDismissDropDown()
    }

    private var onContractsAvailability: OnContractsAvailability? =
        null

    fun setOnContractsAvailability(onContractsAvailability: OnContractsAvailability?) {
        this.onContractsAvailability = onContractsAvailability
    }
}