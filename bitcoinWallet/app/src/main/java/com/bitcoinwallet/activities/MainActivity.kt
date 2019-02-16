package com.bitcoinwallet.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import com.bitcoinwallet.R
import com.bitcoinwallet.utilities.BitcoinUtilities
import com.bitcoinwallet.utilities.Globals
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val walletFile = File(filesDir, Globals.WALLET_NAME)
        val blockStoreFile = File(filesDir, Globals.BLOCK_STORE_NAME)

        if (walletFile.exists() && blockStoreFile.exists()) {
            BitcoinUtilities.loadWalletFromFile(walletFile,blockStoreFile)
            val homeScreenIntent = Intent(this, HomeActivity::class.java)
            startActivity(homeScreenIntent)
        }


        createWalletButton.setOnClickListener {
            val createWalletIntent = Intent(this, CreateWalletActivity::class.java)
            startActivity(createWalletIntent)}
    }
}
