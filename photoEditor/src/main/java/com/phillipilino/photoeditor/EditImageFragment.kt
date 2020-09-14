package com.phillipilino.photoeditor

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.phillipilino.photoeditor.`interface`.EditImageFragmentListener
import kotlinx.android.synthetic.main.fragment_edit_image.view.*

class EditImageFragment : Fragment(), SeekBar.OnSeekBarChangeListener {

    private var listener: EditImageFragmentListener? = null
    private var viewFragment: View? = null


    fun resetControls() {
        viewFragment?.seekbar_brightness?.progress = 100
        viewFragment?.seekbar_constrant?.progress = 0
        viewFragment?.seekbar_brightness?.progress = 10
    }

    fun setListener(listener: EditImageFragmentListener) {
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
        viewFragment = inflater.inflate(R.layout.fragment_edit_image, container, false)

        viewFragment?.seekbar_brightness?.max = 200
        viewFragment?.seekbar_brightness?.progress = 100

        viewFragment?.seekbar_constrant?.max = 20
        viewFragment?.seekbar_constrant?.progress = 0

        viewFragment?.seekbar_saturation?.max = 30
        viewFragment?.seekbar_brightness?.progress = 10

        viewFragment?.seekbar_brightness?.setOnSeekBarChangeListener(this)
        viewFragment?.seekbar_constrant?.setOnSeekBarChangeListener(this)
        viewFragment?.seekbar_saturation?.setOnSeekBarChangeListener(this)

        return viewFragment
    }

    override fun onProgressChanged(seekbar: SeekBar?, progress: Int, fromUser: Boolean) {
        var progress = progress

        if (seekbar?.id == R.id.seekbar_brightness) {
            listener?.onBrightnessChanged(progress - 100)
        }

        if (seekbar?.id == R.id.seekbar_constrant) {
            progress += 10
            val floatVal = .1f * progress
            listener?.onConstrantChanged(floatVal)
        }

        if (seekbar?.id == R.id.seekbar_saturation) {
            progress += 10
            val floatVal = .1f * progress
            listener?.onSaturationChanged(floatVal)
        }
    }

    override fun onStartTrackingTouch(seekbar: SeekBar?) {
        listener?.onEditStarted()
    }

    override fun onStopTrackingTouch(seekbar: SeekBar?) {
        listener?.onEditComplete()
    }
}