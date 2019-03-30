package com.bitcoinwallet.utilities

/**
 * Created by Ben Moore on 11/02/2019.
 */

//import android.app.AlertDialog
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import com.bitcoinwallet.R

/**
 * All utilities related to working with the user interface
 */
class InterfaceUtilities {
    companion object {
        fun showAlertDialog(
            context: Context, title: String, message: String,
            positiveBtnTxt: String, positiveBtnListener: ((DialogInterface, Int) -> Unit),
            negativeBtnTxt: String, negativeBtnListener: ((DialogInterface, Int) -> Unit)
        ) {
            AlertDialog.Builder(context, R.style.MyCustomDialog)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveBtnTxt, positiveBtnListener)
                .setNegativeButton(negativeBtnTxt, negativeBtnListener)
                .show()
        }

        fun showErrorDialog(
            context: Context,
            title: String,
            message: String,
            buttonTxt: String,
            buttonListener: ((DialogInterface, Int) -> Unit)
        ) {
            showOneButtonDialog(context, title, message, android.R.drawable.ic_delete, buttonTxt, buttonListener)
        }

        fun showInfoDialog(
            context: Context,
            title: String,
            message: String,
            buttonTxt: String,
            buttonListener: ((DialogInterface, Int) -> Unit)
        ) {
            showOneButtonDialog(context, title, message, android.R.drawable.ic_dialog_info, buttonTxt, buttonListener)
        }

        private fun showOneButtonDialog(
            context: Context,
            title: String,
            message: String,
            icon: Int,
            buttonTxt: String,
            buttonListener: ((DialogInterface, Int) -> Unit)
        ) {
            AlertDialog.Builder(context, R.style.MyCustomDialog)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(buttonTxt, buttonListener)
                .show()
        }
    }
}