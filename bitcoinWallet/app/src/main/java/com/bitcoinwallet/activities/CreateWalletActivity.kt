package com.bitcoinwallet.activities

import android.os.Bundle
import android.app.Activity
import android.content.DialogInterface
import android.os.Handler
import android.util.Log
import android.view.View
import com.bitcoinwallet.R
import com.bitcoinwallet.utilities.InterfaceUtilities
import com.bitcoinwallet.utilities.NetworkUtilities
import kotlinx.android.synthetic.main.activity_create_wallet.*

import org.bitcoinj.core.ECKey
import org.bitcoinj.core.NetworkParameters
import org.bitcoinj.core.listeners.DownloadProgressTracker
import org.bitcoinj.kits.WalletAppKit
import org.bitcoinj.utils.BriefLogFormatter
import org.bitcoinj.utils.Threading
import java.io.File
import java.util.*

class CreateWalletActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_wallet)
        BriefLogFormatter.init()
        val file = File(filesDir, "wallet_file")
        //val progressDialog = ProgressDialog(this)
        var walletAppKit: WalletAppKit

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


            if (password_input.text.toString() != password_input_confirm.text.toString()) {
                errorMessage.visibility = View.VISIBLE
            } else {
                errorMessage.visibility = View.GONE
                val walletPassword = password_input.text.toString()

                walletAppKit = object : WalletAppKit(
                    NetworkParameters.fromID(NetworkParameters.ID_TESTNET), file,
                    "Test Wallet Name"
                ) {
                    override fun onSetupCompleted() {
                        if (wallet().importedKeys.size > 1) {
                            wallet().importKey(ECKey())
                        }
                        wallet().allowSpendingUnconfirmedTransactions()

                        Log.d("BTC WALLET", "My address = " + wallet().freshReceiveAddress())
                    }
                }

                changeToLoadingScreen()
                //downloading_blockchain_progress_bar.isAnimating = true

                walletAppKit.setDownloadListener(object : DownloadProgressTracker() {
                    override fun progress(pct: Double, blocksSoFar: Int, date: Date?) {
                        super.progress(pct, blocksSoFar, date)
                        val percentage: Int = pct.toInt()
                        downloading_blockchain_progress_bar.setProgress(percentage, true)
                        downloading_blockchain_percentage.text = percentage.toString() + "%"
                    }

                    override fun doneDownload() {
                        super.doneDownload()
                        downloading_blockchain_progress_bar.setProgress(100,true)
                        walletAppKit.wallet().encrypt(walletPassword)
                    }
                })

                walletAppKit.setBlockingStartup(false)
                walletAppKit.startAsync()
            }
        }
    }

    fun changeToLoadingScreen() {
        if (create_password_form.visibility == View.VISIBLE) {
            create_password_form.visibility = View.GONE
            loading_screen.visibility = View.VISIBLE
        } else {
            create_password_form.visibility = View.VISIBLE
            loading_screen.visibility = View.GONE
        }
    }

    /*
    fun setBTCSDKThread() {
        var handler = Handler()
        Threading.USER_THREAD =
    }
    */
}
