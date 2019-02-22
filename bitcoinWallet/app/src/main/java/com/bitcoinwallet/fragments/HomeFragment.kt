package com.bitcoinwallet.fragments


import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContextWrapper
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat.getSystemService
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.bitcoinwallet.R
import com.bitcoinwallet.services.BlockchainDownloadService
import com.bitcoinwallet.utilities.BitcoinUtilities
import com.bitcoinwallet.utilities.Globals
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import org.bitcoinj.core.*
import org.bitcoinj.core.listeners.DownloadProgressTracker
import org.bitcoinj.core.listeners.PeerDataEventListener
import org.bitcoinj.kits.WalletAppKit
import org.bitcoinj.params.TestNet3Params
import org.bitcoinj.utils.BriefLogFormatter
import org.bitcoinj.utils.Threading
import org.bitcoinj.wallet.Wallet
import org.bitcoinj.wallet.listeners.WalletCoinsReceivedEventListener
import java.util.*
import java.util.concurrent.Executor

class HomeFragment : Fragment() {
    lateinit var balance: String
    lateinit var addr: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val handler: Handler = Handler()

        val runInUIThread = object : Executor {
            override fun execute(p0: Runnable?) {
                handler.post(p0)
            }
        }

        //Threading.USER_THREAD = runInUIThread

        val view = inflater.inflate(R.layout.fragment_home, container, false)


        BitcoinUtilities.setupWalletAppKit(context?.filesDir!!)
        Globals.kit?.startAsync()
        Globals.kit?.awaitRunning()
        //context?.startService(Intent(context, BlockchainDownloadService::class.java))
        GetBalanceAsync().execute()
        GetAddressAsync().execute()

        view.copyAddressBtn.setOnClickListener {
            Toast.makeText(context, "Address Copied", Toast.LENGTH_SHORT).show()
            val clipboardManager = getSystemService(context!!, ClipboardManager::class.java)
            val clip = ClipData.newPlainText("BTC ADDRESS", addr)
            clipboardManager?.primaryClip = clip
        }

        //Globals.peerGroup?.startAsync()
        Globals.kit?.wallet()?.addCoinsReceivedEventListener { wallet, tx, prevBalance, newBalance ->
            Log.d(Globals.LOG_TAG, "Recieved tx")
            Log.d(Globals.LOG_TAG, "New Balance is" + newBalance.toFriendlyString())
            setWalletbalance(newBalance.toFriendlyString())
        }


        /*
        val walletAppKit = object : WalletAppKit(TestNet3Params.get(), context!!.filesDir, "BTCWallet") {
            override fun onSetupCompleted() {
                super.onSetupCompleted()
                Log.d(Globals.LOG_TAG, "Setup complete")

                wallet().addCoinsReceivedEventListener { wallet, tx, prevBalance, newBalance ->
                    Log.d(Globals.LOG_TAG, "New balance is" + newBalance.toFriendlyString())
                }
            }
        }

        //walletAppKit.startAsync()
        //walletAppKit.awaitRunning()

        walletAppKit.setDownloadListener(object: DownloadProgressTracker() {
            override fun progress(pct: Double, blocksSoFar: Int, date: Date?) {
                super.progress(pct, blocksSoFar, date)
                Log.d(Globals.LOG_TAG, "Downloaded: " + blocksSoFar.toString() + " so far")
            }
            override fun doneDownload() {
                super.doneDownload()
                Log.d(Globals.LOG_TAG, "Download complete!")
            }

            override fun startDownload(blocks: Int) {
                super.startDownload(blocks)
                Log.d(Globals.LOG_TAG, "Download starting!")
            }
        })


        */
        //DownloadBlockchain().execute()

        /*
        if(!Globals.peerGroup?.isRunning!!) {
            Globals.peerGroup?.startAsync()
            //Globals.peerGroup?.downloadBlockChain()
        }
        */

        // Inflate the layout for this fragment
        return view
    }

    companion object {
        fun newInstance(): HomeFragment = HomeFragment()
    }

    private fun setWalletbalance(balance: String) {
        walletBalanceTxt.text = "₿" + balance

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
            //addr = Globals.wallet?.currentReceiveAddress().toString()
            addr = Globals.kit?.wallet()?.currentReceiveAddress().toString()
            return addr
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            addressTxt.text = addr
        }
    }
}
