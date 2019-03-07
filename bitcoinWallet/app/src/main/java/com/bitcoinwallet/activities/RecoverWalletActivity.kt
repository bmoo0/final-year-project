package com.bitcoinwallet.activities

import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.bitcoinwallet.R
import com.bitcoinwallet.fragments.CustomDatePickerDialog
import com.bitcoinwallet.fragments.TimePickerSecondsDialog
import com.bitcoinwallet.utilities.BitcoinUtilities
import com.bitcoinwallet.utilities.DateTimeUtilities
import com.bitcoinwallet.utilities.Globals
import kotlinx.android.synthetic.main.activity_recover_wallet.*
import org.bitcoinj.core.listeners.DownloadProgressTracker
import org.bitcoinj.wallet.DeterministicSeed
import java.util.*

class RecoverWalletActivity : AppCompatActivity(), TimePickerSecondsDialog.TimePickerDelegate,
    CustomDatePickerDialog.DatePickerDelegate {
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
            val creationTime = DateTimeUtilities.numbersToEpochLong(mHour, mMin, mSec, mDay, mMonth, mYear)

            // TODO: Add password functionality
            // if no password
            val deterministicSeed = DeterministicSeed(recoverySeedTxt.text.toString(), null, "", creationTime)

            BitcoinUtilities.setupWalletAppKit(filesDir) {
                Globals.kit?.restoreWalletFromSeed(deterministicSeed)
            }

            changeToLoadingScreen()
            if (BitcoinUtilities.walletExists(filesDir)) {
                BitcoinUtilities.removeWalletFiles(filesDir)
            }

            Globals.kit?.setDownloadListener(object : DownloadProgressTracker() {
                override fun progress(pct: Double, blocksSoFar: Int, date: Date?) {
                    super.progress(pct, blocksSoFar, date)
                    val percentage: Int = pct.toInt()
                    downloading_blockchain_progress_bar.setProgress(percentage, true)
                    downloading_blockchain_percentage.text = "${percentage.toString()}%"
                }

                override fun doneDownload() {
                    super.doneDownload()
                    downloading_blockchain_progress_bar.setProgress(100, true)
                    Log.d(Globals.LOG_TAG, "Download complete")
                }
            })

            DownloadBlockchain().execute()
        }
    }

    private fun changeToLoadingScreen() {
        if (recoverWalletScreen.visibility == View.VISIBLE) {
            recoverWalletScreen.visibility = View.GONE
            loading_screen.visibility = View.VISIBLE
        } else {
            recoverWalletScreen.visibility = View.VISIBLE
            loading_screen.visibility = View.GONE
        }
    }

    override fun onSelectedTimeDone(hours: Int, minutes: Int, seconds: Int) {
        mHour = hours
        mMin = minutes
        mSec = seconds
        txtTime.text = DateTimeUtilities.numbersToTimeString(mHour, mMin, mSec)
        isTimeSet = true
    }

    override fun onSelectedDateDone(day: Int, month: Int, year: Int) {
        mDay = day
        mMonth = month
        mYear = year
        txtDate.text = DateTimeUtilities.numbersToDateString(mDay, mMonth, mYear)
        isDateSet = true
    }

    inner class DownloadBlockchain : AsyncTask<Void, Int, String>() {
        override fun doInBackground(vararg p1: Void?): String {
            Globals.kit?.setBlockingStartup(false)
            Globals.kit?.setAutoSave(true)
            Globals.kit?.startAsync()
            Globals.kit?.awaitRunning()
            return "complete"
        }
    }
}
