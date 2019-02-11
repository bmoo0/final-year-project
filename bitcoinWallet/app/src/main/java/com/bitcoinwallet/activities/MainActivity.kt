package com.bitcoinwallet.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Button
import com.bitcoinwallet.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val hasWallet: Boolean = resources.getBoolean(R.bool.wallet_present)

        if(hasWallet) { // transition to home screen
            // prompt to create or recover wallet
            //val wallet: Wallet =  Wallet(NetworkParameters.fromID(NetworkParameters.ID_TESTNET))
            //val alertDialog:
        }

        val createWalletButton = findViewById<Button>(R.id.createWalletButton)

        createWalletButton.setOnClickListener {
            val createWalletIntent = Intent(this, CreateWalletActivity::class.java)
            startActivity(createWalletIntent)}
    }
}
