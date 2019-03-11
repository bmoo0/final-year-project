package com.bitcoinwallet.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bitcoinwallet.R
import com.bitcoinwallet.utilities.DateTimeUtilities
import com.bitcoinwallet.utilities.Globals
import com.bitcoinwallet.utilities.InterfaceUtilities
import com.google.common.base.Joiner
import kotlinx.android.synthetic.main.activity_display_recovery_seed.*

class DisplayRecoverySeedActivity : AppCompatActivity() {
    private val title = "Have you written down your recovery seed?"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_recovery_seed)

        val isFromSettings = intent.getBooleanExtra("IS_FROM_SETTINGS", false)

        val recoverySeed = Globals.kit?.wallet()?.keyChainSeed
        val mnemonicCode = recoverySeed?.mnemonicCode
        textViewRecoverySeed.text = Joiner.on(" ").join(mnemonicCode)


        textViewRecoverySeedBirthday.text =
            DateTimeUtilities.epochTimeToFriendlyString(recoverySeed?.creationTimeSeconds!!)

        confirmStoredSeedBtn.setOnClickListener {
            if (isFromSettings) {
                InterfaceUtilities.showInfoDialog(
                    this, title,
                    "Make sure you have written down your recovery seed and creation time",
                    "OK"
                ) { dialog, _ ->
                    dialog.dismiss()
                    finish()
                }
            } else {
                InterfaceUtilities.showAlertDialog(
                    this, title,
                    "Please confirm that you have written down your recovery seed and creation time, " +
                            "without it you could potentially lose your funds",
                    "Yes",
                    { _, _ ->
                        val homeActivityIntent = Intent(this, HomeActivity::class.java)
                        startActivity(homeActivityIntent)
                    },
                    "No",
                    { _, _ ->
                        // do nothing
                    }
                )
            }
        }
    }
}
