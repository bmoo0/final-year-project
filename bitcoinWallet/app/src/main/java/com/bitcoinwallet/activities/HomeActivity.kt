package com.bitcoinwallet.activities

import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.bitcoinwallet.R
import com.bitcoinwallet.fragments.HomeFragment
import com.bitcoinwallet.fragments.SendFragment
import com.bitcoinwallet.fragments.SettingsFragment
import com.bitcoinwallet.fragments.ShowQrDialog
import com.bitcoinwallet.utilities.Globals
import com.bitcoinwallet.utilities.HttpRequester
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity(), HttpRequester.HttpRequestDelegate {
    private lateinit var address: String
    lateinit var weeklyPrices: List<HttpRequester.PriceEntry>
    val httpRequester = HttpRequester.getInstance(this)
    private val homeFragment = HomeFragment.newInstance()
    private val sendFragment = SendFragment.newInstance()
    private val settingsFragment = SettingsFragment.newInstance()

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.homeItem -> {
                Log.d("BTC WALLET", "home pressed")
                supportActionBar?.title = "Home"
                openFragment(homeFragment)
                return@OnNavigationItemSelectedListener true
            }

            R.id.sendItem -> {
                Log.d("BTC WALLET", "send pressed")
                supportActionBar?.title = "Send"
                openFragment(sendFragment)
                return@OnNavigationItemSelectedListener true
            }

            R.id.setttingsItem -> {
                Log.d("BTC WALLET", "settings pressed")
                supportActionBar?.title = "Settings"
                openFragment(settingsFragment)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(homeToolbar)
        GetAddressAsync().execute()

        if (intent.getBooleanExtra("EXIT", false)) {
            finish()
        }

        supportActionBar?.title = "Home"

        httpRequester.requestCurrentPrice()
        httpRequester.requestWeeklyData()
        openFragment(homeFragment)
        homeNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home_toolbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
        R.id.item_show_qr -> {
            val qrDialog = ShowQrDialog(address)
            qrDialog.show(supportFragmentManager, "qr dialog")
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    // TODO: Fix back button on home screen so it closes app
    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onCurrentPriceReturned(price: Double) {
        Log.d(Globals.LOG_TAG, "Current price: " + price.toString())
    }

    override fun onHttpError(errorMessage: String) {
        Log.d(Globals.LOG_TAG, "Http request failed")
    }

    override fun onPriceRangeReturned(price: List<HttpRequester.PriceEntry>) {
        val priceDataReciever = homeFragment as PriceDataReceiver
        priceDataReciever.priceDataRecieved(price)
        weeklyPrices = price
    }

    inner class GetAddressAsync : AsyncTask<Void, Int, String>() {
        override fun doInBackground(vararg params: Void?): String {
            address = Globals.kit?.wallet()?.currentReceiveAddress().toString()
            return address
        }
    }

    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.homeScreenContainer, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    interface PriceDataReceiver {
        fun priceDataRecieved(prices: List<HttpRequester.PriceEntry>)
    }
}
