package com.bitcoinwallet

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val hasWallet: Boolean = getString(R.string.wallet_present).toBoolean()

        if(!hasWallet) {
            // prompt to create or recover wallet
        }
    }
}
