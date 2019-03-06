package com.bitcoinwallet.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bitcoinwallet.R
import kotlinx.android.synthetic.main.dialog_custom_date_picker.*

class CustomDatePickerDialog : ThreePickerDialog() {
    private val startYear = 2009
    private var mDay = 1
    private var mMonth = 1
    private var mYear = startYear

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_custom_date_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dayVals = createPickerRanges(1, 31)
        val monthVals = createPickerRanges(1, 12)
        val yearVals = createPickerRanges(startYear, 2050)

        setupPicker(dayPicker, dayVals) { _, _, newVal -> mDay = newVal+1}
        setupPicker(monthPicker, monthVals) { _, _, newVal -> mMonth = newVal+1}
        setupPicker(yearPicker, yearVals) { _, _, newVal -> mYear = yearVals[newVal].toInt() }

        cancelBtn.setOnClickListener { dismiss() }

        okBtn.setOnClickListener {
            val delegate = activity as? DatePickerDelegate
            delegate!!.onSelectedDateDone(mDay, mMonth, mYear)
            dismiss()
        }
    }

    interface DatePickerDelegate {
        fun onSelectedDateDone(day: Int, month: Int, year: Int)
    }
}