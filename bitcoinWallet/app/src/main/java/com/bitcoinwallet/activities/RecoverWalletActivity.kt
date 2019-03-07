package com.bitcoinwallet.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.bitcoinwallet.R
import com.bitcoinwallet.fragments.CustomDatePickerDialog
import com.bitcoinwallet.fragments.TimePickerSecondsDialog
import com.bitcoinwallet.utilities.BitcoinUtilities
import com.bitcoinwallet.utilities.DateTimeUtilities
import com.bitcoinwallet.utilities.Globals
import kotlinx.android.synthetic.main.activity_recover_wallet.*
import org.bitcoinj.wallet.DeterministicSeed

class RecoverWalletActivity : AppCompatActivity(), TimePickerSecondsDialog.TimePickerDelegate, CustomDatePickerDialog.DatePickerDelegate {
    private var mYear = 0
    private var mMonth = 0
    private var mDay = 0
    private var mHour = 0
    private var mMin = 0
    private var mSec = 0
    private var isDateSet = false
    private var isTimeSet = false

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

        recoverWalletBtn.setOnClickListener {
            if (!isDateSet || !isTimeSet) {
                // show error message
                return@setOnClickListener
            }
            val creationTime = DateTimeUtilities.numbersToEpochLong(mHour,mMin,mSec,mDay,mMonth,mYear)

            // TODO: Add password functionality
            // if no password
            val deterministicSeed = DeterministicSeed(recoverySeedTxt.text.toString(),null, "", creationTime)

            BitcoinUtilities.setupWalletAppKit(filesDir) {
                Globals.kit?.restoreWalletFromSeed(deterministicSeed)
            }

            // TODO: in order to finish this feature
            // turn back to loading screen
            // remove files(if they exist), redownload blockchain and go to home screen
        }
    }

    override fun onSelectedTimeDone(hours: Int, minutes: Int, seconds: Int) {
        mHour = hours
        mMin = minutes
        mSec = seconds
        txtTime.text = DateTimeUtilities.numbersToTimeString(mHour,mMin,mSec)
        isTimeSet = true
    }

    override fun onSelectedDateDone(day: Int, month: Int, year: Int) {
        mDay = day
        mMonth = month
        mYear = year
        txtDate.text = DateTimeUtilities.numbersToDateString(mDay,mMonth,mYear)
        isDateSet = true
    }
}
