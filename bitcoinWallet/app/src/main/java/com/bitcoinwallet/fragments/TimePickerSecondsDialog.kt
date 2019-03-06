package com.bitcoinwallet.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bitcoinwallet.R
import kotlinx.android.synthetic.main.dialog_time_picker_seconds_layout.*

class TimePickerSecondsDialog : ThreePickerDialog() {
    private var mHours = 0
    private var mMinutes = 0
    private var mSeconds = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_time_picker_seconds_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val hourVals = createPickerRanges(to = 24)
        val minVals = createPickerRanges(to = 59)
        val secVals = createPickerRanges(to = 59)

        setupPicker(hourPicker, hourVals) { _, _, newVal -> mHours = newVal }
        setupPicker(minPicker, minVals) { _, _, newVal -> mMinutes = newVal }
        setupPicker(secondPicker, secVals) { _, _, newVal -> mSeconds = newVal }

        cancelBtn.setOnClickListener { dismiss() }

        okBtn.setOnClickListener {
            val delegate = activity as? TimePickerDelegate
            delegate!!.onSelectedTimeDone(mHours, mMinutes, mSeconds)
            dismiss()
        }
    }

    interface TimePickerDelegate {
        fun onSelectedTimeDone(hours: Int, minutes: Int, seconds: Int)
    }
}