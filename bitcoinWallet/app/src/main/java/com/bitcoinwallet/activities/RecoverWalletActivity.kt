package com.bitcoinwallet.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.bitcoinwallet.R
import com.bitcoinwallet.fragments.CustomDatePickerDialog
import com.bitcoinwallet.fragments.TimePickerSecondsDialog
import kotlinx.android.synthetic.main.activity_recover_wallet.*

class RecoverWalletActivity : AppCompatActivity(), TimePickerSecondsDialog.TimePickerDelegate, CustomDatePickerDialog.DatePickerDelegate {
    private var mYear = 0
    private var mMonth = 0
    private var mDay = 0
    private var mHour = 0
    private var mMin = 0
    private var mSec = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recover_wallet)

        txtTime.setOnClickListener {
            val timePickerSecondsDialog = TimePickerSecondsDialog()
            timePickerSecondsDialog.show(supportFragmentManager, "time picker")
        }

        txtDate.setOnClickListener {
            val datePickerDialog = CustomDatePickerDialog()
            datePickerDialog.show(supportFragmentManager, "date picker")
        }
    }

    override fun onSelectedTimeDone(hours: Int, minutes: Int, seconds: Int) {
        mHour = hours
        mMin = minutes
        mSec = seconds
    }

    override fun onSelectedDateDone(day: Int, month: Int, year: Int) {
        mDay = day
        mMonth = month
        mYear = year
    }
}
