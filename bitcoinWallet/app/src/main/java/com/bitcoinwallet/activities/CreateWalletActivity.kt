package com.bitcoinwallet.activities

import android.os.Bundle
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.AsyncTask
import android.os.PowerManager
import android.util.Log
import android.view.View
import com.bitcoinwallet.R
import com.bitcoinwallet.utilities.BitcoinUtilities
import com.bitcoinwallet.utilities.Globals
import com.bitcoinwallet.utilities.InterfaceUtilities
import com.bitcoinwallet.utilities.NetworkUtilities
import kotlinx.android.synthetic.main.activity_create_wallet.*

import org.bitcoinj.core.listeners.DownloadProgressTracker
import org.bitcoinj.utils.BriefLogFormatter
import java.util.*

class CreateWalletActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_wallet)
        BriefLogFormatter.init()

        // this is all the wallet setup stoof
        BitcoinUtilities.setupWalletAppKit(filesDir) {
            Log.d(Globals.LOG_TAG, "Wallet created successfully")
        }


        confirmCreateWalletButton.setOnClickListener {

            if (NetworkUtilities.isUsingMobile(this)) {
                InterfaceUtilities.Companion.showAlertDialog(this, "Download Blockchain",
                    "Your mobile device is not on wifi, downloading the blockchain will be a large download," +
                            "are you sure you want to continue?",
                    "Yes",
                    DialogInterface.OnClickListener { _, _ ->
                        // continue
                    },
                    "No",
                    DialogInterface.OnClickListener { _, _ ->
                        finish() // close activity
                    }
                )
            }

            changeToLoadingScreen()

            Globals.kit?.setDownloadListener(object : DownloadProgressTracker() {
                override fun progress(pct: Double, blocksSoFar: Int, date: Date?) {
                    super.progress(pct, blocksSoFar, date)
                    val percentage: Int = pct.toInt()
                    downloading_blockchain_progress_bar.setProgress(percentage, true)
                    downloading_blockchain_percentage.text = percentage.toString() + "%"
                }

                override fun doneDownload() {
                    super.doneDownload()
                    downloading_blockchain_progress_bar.setProgress(100, true)
                    Log.d(Globals.LOG_TAG, "Download complete")

                    showRecoverySeed()
                }
            })

            DownloadBlockchain().execute()
        }
    }

    fun showRecoverySeed() {
        val displayRecoverySeedIntent = Intent(this, DisplayRecoverySeedActivity::class.java)
        startActivity(displayRecoverySeedIntent)
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

    private fun changeToLoadingScreen() {
        if (create_password_form.visibility == View.VISIBLE) {
            create_password_form.visibility = View.GONE
            loading_screen.visibility = View.VISIBLE
        } else {
            create_password_form.visibility = View.VISIBLE
            loading_screen.visibility = View.GONE
        }
    }
}
