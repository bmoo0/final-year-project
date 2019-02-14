package com.bitcoinwallet.activities

import android.os.Bundle
import android.app.Activity
import android.content.DialogInterface
import android.util.Log
import android.view.View
import com.bitcoinwallet.R
import com.bitcoinwallet.utilities.Globals
import com.bitcoinwallet.utilities.InterfaceUtilities
import com.bitcoinwallet.utilities.NetworkUtilities
import com.google.common.base.Joiner
import kotlinx.android.synthetic.main.activity_create_wallet.*
import org.bitcoinj.core.BlockChain

import org.bitcoinj.core.ECKey
import org.bitcoinj.core.NetworkParameters
import org.bitcoinj.core.PeerGroup
import org.bitcoinj.core.listeners.DownloadProgressTracker
import org.bitcoinj.kits.WalletAppKit
import org.bitcoinj.store.SPVBlockStore
import org.bitcoinj.utils.BriefLogFormatter
import org.bitcoinj.wallet.Wallet
import java.io.File
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*

class CreateWalletActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_wallet)
        BriefLogFormatter.init()
        val walletFile = File(filesDir, Globals.WALLET_NAME)
        //val progressDialog = ProgressDialog(this)
        var walletAppKit: WalletAppKit


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


        // this is all the wallet setup stoof
        val networkParams = NetworkParameters.fromID(NetworkParameters.ID_TESTNET) // should stay on testnet
        val wallet = Wallet(networkParams)
        val blockStore = SPVBlockStore(networkParams, walletFile)
        val blockChain = BlockChain(networkParams,wallet,blockStore)
        val peerGroup = PeerGroup(networkParams)

        val recoverySeed = wallet.keyChainSeed
        val mnemonicCode = recoverySeed.mnemonicCode
        textViewRecoverySeed.text = Joiner.on(" ").join(mnemonicCode)
        textViewRecoverySeedBirthday.text =
                DateTimeFormatter.ISO_INSTANT.format(Instant.ofEpochSecond(recoverySeed.creationTimeSeconds))

        peerGroup.addWallet(wallet)
        peerGroup.startAsync()



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
                    NetworkParameters.fromID(NetworkParameters.ID_TESTNET), walletFile,
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
}
