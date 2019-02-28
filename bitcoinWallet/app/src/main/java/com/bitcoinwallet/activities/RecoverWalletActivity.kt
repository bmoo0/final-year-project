package com.bitcoinwallet.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.DatePicker
import com.bitcoinwallet.R
import kotlinx.android.synthetic.main.activity_recover_wallet.*
import java.util.*

class RecoverWalletActivity : AppCompatActivity() {
    private val cal = Calendar.getInstance()
    private var mYear = cal.get(Calendar.YEAR)
    private var mMonth = cal.get(Calendar.MONTH)
    private var mDay = cal.get(Calendar.DAY_OF_MONTH)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recover_wallet)
        var selectedYear : Int
        var selectedMonth : Int
        var selectedDay : Int
        var selectedHour : Int
        var selectedMin : Int
        var selectedSecond : Int

        creationTimeTxt.setOnClickListener {
            val datePicker = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { picker, year, month, day ->
                selectedYear = year
                selectedMonth = month
                selectedDay = day

            },mYear, mMonth,mDay).show()
        }
    }
}
