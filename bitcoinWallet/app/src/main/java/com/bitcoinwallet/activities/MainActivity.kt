package com.bitcoinwallet.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import com.bitcoinwallet.R
import com.bitcoinwallet.utilities.BitcoinUtilities
import com.bitcoinwallet.utilities.Globals
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        if (BitcoinUtilities.walletExists(filesDir)) {
            Log.d(Globals.LOG_TAG, "Setting up wallet")
            BitcoinUtilities.setupWalletAppKit(filesDir) {
                Log.d(Globals.LOG_TAG, "Wallet setup")
                val homeScreenIntent = Intent(this, HomeActivity::class.java)
                startActivity(homeScreenIntent)
            }
            Globals.kit?.startAsync()
        }

        createWalletButton.setOnClickListener {
            val createWalletIntent = Intent(this, CreateWalletActivity::class.java)
            startActivity(createWalletIntent)}
    }
}
