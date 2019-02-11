package com.bitcoinwallet.utilities

/**
 * Created by Ben Moore on 11/02/2019.
 */

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface

class InterfaceUtilities {
    companion object {
        fun showAlertDialog(context: Context, title: String, message: String,
                        positiveBtnTxt: String, positiveBtnListener: DialogInterface.OnClickListener,
                        negativeBtnTxt: String, negativeBtnListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(context)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveBtnTxt, positiveBtnListener)
            .setNegativeButton(negativeBtnTxt, negativeBtnListener)
            .show()
    }
    }
}