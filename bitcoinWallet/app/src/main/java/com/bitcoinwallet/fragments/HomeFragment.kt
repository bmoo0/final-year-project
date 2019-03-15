package com.bitcoinwallet.fragments


import android.content.ClipData
import android.content.ClipboardManager
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat.getSystemService
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.bitcoinwallet.R
import com.bitcoinwallet.utilities.Globals
import com.github.mikephil.charting.data.Entry
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeFragment : Fragment() {
    lateinit var balance: String
    lateinit var addr: String
    private lateinit var entries : List<Entry>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // don't start these until we know the wallet app kit has been initialised
        if(Globals.kit != null) {
            GetBalanceAsync().execute()
            GetAddressAsync().execute()
        }

        // copy address
        view.copyAddressBtn.setOnClickListener {
            Toast.makeText(context, "Address Copied", Toast.LENGTH_SHORT).show()
            val clipboardManager = getSystemService(context!!, ClipboardManager::class.java)
            val clip = ClipData.newPlainText("BTC ADDRESS", addr)
            clipboardManager?.primaryClip = clip
        }

        Globals.kit?.wallet()?.addCoinsReceivedEventListener { _, _, _, newBalance ->
            Log.d(Globals.LOG_TAG, "Recieved tx")
            Log.d(Globals.LOG_TAG, "New Balance is" + newBalance.toFriendlyString())
            setWalletbalance(newBalance.toFriendlyString())
        }

        // setup graph

        return view
    }

    companion object {
        fun newInstance(): HomeFragment = HomeFragment()
    }

    private fun setWalletbalance(balance: String) {
        walletBalanceTxt.text = "₿" + balance

    }

    private fun setupGraph(graphData: List<Double>) {
        entries = ArrayList<Entry>()

        graphData.map {
            entries.add()
        }
    }

    inner class GetBalanceAsync : AsyncTask<Void, Int, String>() {
        override fun doInBackground(vararg params: Void?): String {
            balance = Globals.kit?.wallet()?.balance?.toFriendlyString()!!
            return balance
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            Log.d("BTC WALLET", "Balance = " + balance)
            setWalletbalance(balance)
        }
    }

    inner class GetAddressAsync : AsyncTask<Void, Int, String>() {
        override fun doInBackground(vararg params: Void?): String {
            addr = Globals.kit?.wallet()?.currentReceiveAddress().toString()
            return addr
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            addressTxt.text = addr
        }
    }
}
