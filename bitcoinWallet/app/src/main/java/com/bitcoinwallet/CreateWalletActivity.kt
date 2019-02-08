package com.bitcoinwallet

import android.os.Bundle
import android.app.Activity
import android.os.Environment
import android.util.Log

import kotlinx.android.synthetic.main.activity_create_wallet.*
import org.bitcoinj.core.BlockChain
import org.bitcoinj.core.ECKey
import org.bitcoinj.core.NetworkParameters
import org.bitcoinj.core.PeerGroup
import org.bitcoinj.core.listeners.DownloadProgressTracker
import org.bitcoinj.kits.WalletAppKit
import org.bitcoinj.store.BlockStore
import org.bitcoinj.utils.BriefLogFormatter
import org.bitcoinj.wallet.Wallet
import java.io.File
import java.util.*

class CreateWalletActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_wallet)

        BriefLogFormatter.init()
        //val path  = Environment.getDataDirectory().toString() + "/wallet_file"
        val file = File(filesDir, "wallet_file")

        val walletAppKit = object : WalletAppKit(NetworkParameters.fromID(NetworkParameters.ID_TESTNET), file, "Test Wallet Name") {
            override fun onSetupCompleted() {
                if(wallet().importedKeys.size > 1) {
                    wallet().importKey(ECKey())
                }

                wallet().allowSpendingUnconfirmedTransactions()

                Log.d("Bitcoin wallet logs", "My address = " + wallet().freshReceiveAddress())
            }
        }

        walletAppKit.setDownloadListener(object : DownloadProgressTracker() {
            override fun progress(pct: Double, blocksSoFar: Int, date: Date?) {
                super.progress(pct, blocksSoFar, date)
                val percentage: Int = pct.toInt()
            }
        })

        walletAppKit.setBlockingStartup(false)
        walletAppKit.startAsync()
    }

}
