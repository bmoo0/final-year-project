package com.bitcoinwallet.activities

import android.os.Bundle
import android.app.Activity
import android.content.DialogInterface
import android.util.Log
import com.bitcoinwallet.R
import com.bitcoinwallet.utilities.InterfaceUtilities
import com.bitcoinwallet.utilities.NetworkUtilities

import org.bitcoinj.core.ECKey
import org.bitcoinj.core.NetworkParameters
import org.bitcoinj.core.listeners.DownloadProgressTracker
import org.bitcoinj.kits.WalletAppKit
import org.bitcoinj.utils.BriefLogFormatter
import java.io.File
import java.util.*

class CreateWalletActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_wallet)
        BriefLogFormatter.init()
        val file = File(filesDir, "wallet_file")
        //val progressDialog = ProgressDialog(this)

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

        val walletAppKit = object : WalletAppKit(
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

        walletAppKit.setDownloadListener(object : DownloadProgressTracker() {
            override fun progress(pct: Double, blocksSoFar: Int, date: Date?) {
                super.progress(pct, blocksSoFar, date)
                val percentage: Int = pct.toInt()
            }
        })

        //walletAppKit.wallet().encrypt()

        //walletAppKit.setBlockingStartup(false)
        //walletAppKit.startAsync()
    }
}
