package com.phillipilino.photoeditor.`interface`

interface EditImageFragmentListener {
    fun onBrightnessChanged(brightness: Int)
    fun onSaturationChanged(saturation: Float)
    fun onConstrantChanged(constrant: Float)
    fun onEditStarted()
    fun onEditComplete()
}