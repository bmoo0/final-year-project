package com.bitcoinwallet.fragments

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bitcoinwallet.R
import kotlinx.android.synthetic.main.custom_confirm_dialog.*

/**
 * Created by Ben Moore on 27/03/2019.
 */

class CustomConfimDialog(
    private val title: String,
    private val message: String,
    private val confirmOnClickListener: View.OnClickListener,
    private val cancelOnClickListener: View.OnClickListener
) : DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.custom_confirm_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        txt_alert_title.setText(title)
        txt_alert_message.setText(message)
        btn_confim.setOnClickListener(confirmOnClickListener)
        btn_cancel.setOnClickListener(cancelOnClickListener)
    }
}