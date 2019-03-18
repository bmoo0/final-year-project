package com.bitcoinwallet.fragments

import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.bitcoinwallet.R
import com.bitcoinwallet.activities.HomeActivity
import com.bitcoinwallet.formatters.DateAxisValueFormatter
import com.bitcoinwallet.formatters.TimeAxisValueFormatter
import com.bitcoinwallet.utilities.Globals
import com.bitcoinwallet.utilities.HttpRequester
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment(), HomeActivity.PriceDataReceiver {
    lateinit var balance: String
    lateinit var addr: String
    private var isDateFromCache: Boolean = false
    private var referenceTimestamp: Long = 0
    private lateinit var entries: ArrayList<Entry>
    private lateinit var priceData: HttpRequester.PriceData
    private var currentPrice: Double = 0.00

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // don't start these until we know the wallet app kit has been initialised
        if (Globals.kit != null) {
            GetBalanceAsync().execute()
            GetAddressAsync().execute()
        }

        Globals.kit?.wallet()?.addCoinsReceivedEventListener { _, _, _, newBalance ->
            Log.d(Globals.LOG_TAG, "Recieved tx")
            Log.d(Globals.LOG_TAG, "New Balance is" + newBalance.toFriendlyString())
            setWalletbalance(newBalance.toFriendlyString())
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isDateFromCache) {
            drawGraph(priceData.hourlyPrice, true)
        }

        btnHourlyPrice.setOnClickListener {
            btnDailyPrice.isChecked = false
            btnWeeklyPrice.isChecked = false
            btnMonthlyPrice.isChecked = false
            drawGraph(priceData.hourlyPrice, true)
        }

        btnDailyPrice.setOnClickListener {
            btnHourlyPrice.isChecked = false
            btnWeeklyPrice.isChecked = false
            btnMonthlyPrice.isChecked = false
            drawGraph(priceData.dailyPrice)
        }

        btnWeeklyPrice.setOnClickListener {
            btnHourlyPrice.isChecked = false
            btnDailyPrice.isChecked = false
            btnMonthlyPrice.isChecked = false
            drawGraph(priceData.weeklyPrice)
        }

        btnMonthlyPrice.setOnClickListener {
            btnHourlyPrice.isChecked = false
            btnDailyPrice.isChecked = false
            btnWeeklyPrice.isChecked = false
            drawGraph(priceData.monthlyPrice)
        }
    }

    companion object {
        fun newInstance(): HomeFragment = HomeFragment()
    }

    private fun setWalletbalance(balance: String) {
        walletBalanceTxt.text = "₿" + balance

    }

    private fun setFiatBalance(balance: String) {
        walletBalanceFiatTxt.text = "£" + balance
    }

    private fun drawGraph(graphData: List<HttpRequester.PriceEntry>, isHourly: Boolean = false) {
        entries = ArrayList<Entry>()
        referenceTimestamp = graphData[0].timestamp
        //val gradientDrawable = ContextCompat.getDrawable(context!!, R.drawable.fade_blue)

        Log.d(Globals.LOG_TAG, "DRAWING")

        graphData.map {
            val graphTime = it.timestamp - referenceTimestamp
            entries.add(Entry(graphTime.toFloat(), it.price.toFloat()))
        }

        priceGraph.description.text = ""
        priceGraph.axisRight.setDrawLabels(false)
        priceGraph.xAxis.setDrawGridLines(false)
        priceGraph.legend.isEnabled = false

        // format dates correctely
        val axisValueFormatter = if(isHourly) TimeAxisValueFormatter(referenceTimestamp)
            else DateAxisValueFormatter(referenceTimestamp)

        val xAxis = priceGraph.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = axisValueFormatter

        val lineDataSet = LineDataSet(entries, "")
        //lineDataSet.fillDrawable = gradientDrawable
        lineDataSet.fillColor = R.color.colorPrimary
        lineDataSet.color = R.color.colorPrimary
        lineDataSet.setDrawFilled(true)
        lineDataSet.setDrawCircles(false)
        lineDataSet.fillAlpha = 168 //transparency 0 being completely transparrent
        lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER

        val lineData = LineData(lineDataSet)
        priceGraph.data = lineData
        priceGraph.invalidate()
    }

    override fun priceDataRecieved(prices: HttpRequester.PriceData) {
        isDateFromCache = prices.isFromCache
        priceData = prices
        if(!isDateFromCache) {
            drawGraph(prices.hourlyPrice, true)
        }
    }

    override fun currentPriceRecieved(price: Double) {
        currentPrice = price
    }

    inner class GetBalanceAsync : AsyncTask<Void, Int, String>() {
        override fun doInBackground(vararg params: Void?): String {
            balance = Globals.kit?.wallet()?.balance?.toFriendlyString()!!
            return balance
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            setWalletbalance(balance)
            val fiatBalance = currentPrice * balance.split(" ")[0].toDouble()
            setFiatBalance(fiatBalance.toString())
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
