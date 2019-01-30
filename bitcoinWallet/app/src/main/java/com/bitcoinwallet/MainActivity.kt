package com.bitcoinwallet

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import org.bitcoinj.core.NetworkParameters
import org.bitcoinj.wallet.Wallet

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
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
