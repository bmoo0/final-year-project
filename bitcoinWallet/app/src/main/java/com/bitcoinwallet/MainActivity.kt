package com.bitcoinwallet

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.Window
import org.bitcoinj.core.NetworkParameters
import org.bitcoinj.wallet.Wallet

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val hasWallet: Boolean = resources.getBoolean(R.bool.wallet_present)

        if(!hasWallet) {
            // prompt to create or recover wallet
            val wallet: Wallet =  Wallet(NetworkParameters.fromID(NetworkParameters.ID_TESTNET))
            //val alertDialog:
        }
    }
}
