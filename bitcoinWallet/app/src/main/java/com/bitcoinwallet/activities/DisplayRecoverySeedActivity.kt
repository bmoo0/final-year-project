package com.bitcoinwallet.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.bitcoinwallet.R
import com.bitcoinwallet.utilities.DateTimeUtilities
import com.bitcoinwallet.utilities.Globals
import com.bitcoinwallet.utilities.InterfaceUtilities
import com.google.common.base.Joiner
import kotlinx.android.synthetic.main.activity_display_recovery_seed.*

class DisplayRecoverySeedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_recovery_seed)

        val recoverySeed = Globals.kit?.wallet()?.keyChainSeed
        val mnemonicCode = recoverySeed?.mnemonicCode
        textViewRecoverySeed.text = Joiner.on(" ").join(mnemonicCode)

        textViewRecoverySeedBirthday.text =
            DateTimeUtilities.epochTimeToFriendlyString(recoverySeed?.creationTimeSeconds!!)

        confirmStoredSeedBtn.setOnClickListener {
            InterfaceUtilities.showAlertDialog(
                this, "Have you written down your recovery seed?",
                "Please confirm that you have written down your recovery seed, " +
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
