package com.bitcoinwallet.activities

import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.widget.Toast
import com.bitcoinwallet.R
import com.bitcoinwallet.animators.SendButtonOnClickListener
import com.bitcoinwallet.animators.SettingsButtonOnClickListener
import com.bitcoinwallet.fragments.HomeFragment
import com.bitcoinwallet.fragments.ShowQrDialog
import com.bitcoinwallet.utilities.Globals
import com.bitcoinwallet.utilities.HttpRequester
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity(), HttpRequester.HttpRequestDelegate {
    private lateinit var address: String
    lateinit var weeklyPrices: List<HttpRequester.PriceEntry>
    val httpRequester = HttpRequester.getInstance(this)
    private val homeFragment = HomeFragment.newInstance()
    var isSendScreenShown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(app_bar)
        //window.statusBarColor = getColor(R.color.offWhite)
        GetAddressAsync().execute()

        if (intent.getBooleanExtra("EXIT", false)) {
            finish()
        }

        send_btn_floating.setOnClickListener(SendButtonOnClickListener(this, send_screen))

        address_input.setOnTouchListener { v, event ->
            val DRAWABLE_RIGHT = 2

            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (address_input.right -
                            address_input.compoundDrawables[DRAWABLE_RIGHT].bounds.width())) {
                    scan_qr_code()
                    true
                }
            }
           false
        }

        httpRequester.requestPriceData()
        openFragment(homeFragment)
        app_bar.setNavigationOnClickListener(SettingsButtonOnClickListener(this, homeScreenContainer, send_screen,
            openIcon = getDrawable(R.drawable.btc_menu_icon), closeIcon = getDrawable(R.drawable.btc_menu_close_icon)))
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        val result = IntentIntegrator.parseActivityResult(requestCode,resultCode, data)

        if (result != null) {
            if(result.contents == null) {
                Toast.makeText(this, "QR scan cancelled", Toast.LENGTH_SHORT).show()
            } else {
                address_input.setText(result.contents)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onCurrentPriceReturned(price: Double) {
        val priceDataReciever = homeFragment as PriceDataReceiver
        priceDataReciever.currentPriceRecieved(price)
    }

    override fun onHttpError(errorMessage: String) {
        Log.d(Globals.LOG_TAG, "Http request failed")
    }

    override fun onPriceDataFound(data: HttpRequester.PriceData) {
        val priceDataReciever = homeFragment as PriceDataReceiver
        priceDataReciever.priceDataRecieved(data)
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

    private fun scan_qr_code() {
        try {
            val qrIntent = IntentIntegrator(this).initiateScan(IntentIntegrator.QR_CODE_TYPES)
        } catch(e: Exception) {
            Toast.makeText(this,"Error opening QR scaner", Toast.LENGTH_SHORT).show()
        }
    }

    interface PriceDataReceiver {
        fun priceDataRecieved(prices: HttpRequester.PriceData)
        fun currentPriceRecieved(price: Double)
    }
}
