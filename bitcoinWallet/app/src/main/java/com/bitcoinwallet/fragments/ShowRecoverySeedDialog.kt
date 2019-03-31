package com.bitcoinwallet.fragments

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bitcoinwallet.R
import com.bitcoinwallet.utilities.DateTimeUtilities
import com.bitcoinwallet.utilities.Globals
import com.google.common.base.Joiner
import kotlinx.android.synthetic.main.dialog_show_recovery_seed.*

class ShowRecoverySeedDialog : DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_show_recovery_seed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog.window.setBackgroundDrawable(ColorDrawable(0))
        closeBtn.setOnClickListener { dismiss() }

        val recoverySeed = Globals.kit?.wallet()?.keyChainSeed
        val mnemonicCode = recoverySeed?.mnemonicCode
        textViewRecoverySeed.text = Joiner.on(" ").join(mnemonicCode)

        textViewRecoverySeedBirthday.text =
                DateTimeUtilities.epochTimeToFriendlyString(recoverySeed?.creationTimeSeconds!!)
    }
}