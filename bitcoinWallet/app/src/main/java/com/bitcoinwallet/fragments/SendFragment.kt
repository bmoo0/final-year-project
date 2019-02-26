package com.bitcoinwallet.fragments


import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.bitcoinwallet.R
import com.bitcoinwallet.utilities.Globals
import com.bitcoinwallet.utilities.InterfaceUtilities
import kotlinx.android.synthetic.main.fragment_send.*
import kotlinx.android.synthetic.main.fragment_send.view.*
import org.bitcoinj.core.Address
import org.bitcoinj.core.AddressFormatException
import org.bitcoinj.core.Coin
import org.bitcoinj.core.InsufficientMoneyException
import org.bitcoinj.wallet.Wallet

/**
 * A simple [Fragment] subclass.
 *
 */
class SendFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_send, container, false)

        view.sendBtn.setOnClickListener {
            // TODO: Some error checking for this badboi
            val value = Coin.parseCoin(amount_input.text.toString())
            var toAddr: Address
            var sendResult : Wallet.SendResult

            try {
                toAddr = Address.fromBase58(Globals.networkParams, address_input.text.toString())
            } catch (e :AddressFormatException) {
                InterfaceUtilities.showErrorDialog(context!!,
                    "Invalid Address",
                    "Address not found, are you sure you have written your address correctly",
                    "Ok",
                    DialogInterface.OnClickListener { dialogInterface, _ ->
                        // do nothing
                        dialogInterface.dismiss()
                    })
                return@setOnClickListener
            }

            InterfaceUtilities.showAlertDialog(context!!,
                "Confirm Transaction",
                "Are you sure you want to send " + value.toFriendlyString() + "BTC to " + toAddr.toBase58() + "?",
                "Yes", DialogInterface.OnClickListener { dialogInterface, _ ->
                    dialogInterface.dismiss()
                    // send transaction
                    try {
                        sendResult = Globals.kit?.wallet()!!.sendCoins(Globals.kit?.peerGroup(), toAddr, value)
                        Log.d(Globals.LOG_TAG, "Transaction sent in block " + sendResult.tx.toString())
                        InterfaceUtilities.showInfoDialog(context!!,
                            "Transaction Sent",
                            "You have successfully sent " + value.toFriendlyString() + " to " + toAddr.toBase58(),
                            "Ok",
                            DialogInterface.OnClickListener { dialogInterface, _ ->
                                dialogInterface.dismiss()
                                address_input.setText("")
                                amount_input.setText("")
                            })
                    } catch (e : InsufficientMoneyException) {
                        InterfaceUtilities.showErrorDialog(
                            context!!,
                            "Insufficient Funds",
                            "You have isufficient funds to make this transaction",
                            "Ok",
                            DialogInterface.OnClickListener { dialogInterface, _ ->
                                dialogInterface.dismiss()
                                return@OnClickListener
                            })
                    }
                },
                "No", DialogInterface.OnClickListener { dialogInterface, _ ->
                    dialogInterface.dismiss()
                })
        }


        return view
    }

    companion object {
        fun newInstance(): SendFragment = SendFragment()
    }
}
