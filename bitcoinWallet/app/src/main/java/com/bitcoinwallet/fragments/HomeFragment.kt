package com.bitcoinwallet.fragments

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
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

        Globals.kit?.wallet()?.addCoinsReceivedEventListener { _, _, prevBalance, newBalance ->
            Log.d(Globals.LOG_TAG, "Recieved tx")
            Log.d(Globals.LOG_TAG, "New Balance is" + newBalance.toFriendlyString())
            val CHANNEL_ID = "CHANNEL_TRANSACTION_RECEIVED"
            val NOTIFICATION_ID = getString(R.string.notifacation_received_id)
            val channelName = getString(R.string.notifacation_channel_name)
            val channelImportance = NotificationManager.IMPORTANCE_DEFAULT
            val amountRecieved = newBalance.subtract(prevBalance).toFriendlyString()
            val intent = Intent(context, HomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

            val channel = NotificationChannel(CHANNEL_ID, channelName, channelImportance).apply {
                description = getString(R.string.notifacation_channel_description)
            }

            var applicationBuilder = NotificationCompat.Builder(context!!, CHANNEL_ID)
                .setSmallIcon(R.drawable.icons8_bitcoin_24)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Transaction Received")
                .setStyle(NotificationCompat.BigTextStyle().bigText("You have recieved $amountRecieved"))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)

            val notificationManager : NotificationManager =
                context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

            with(NotificationManagerCompat.from(context!!)) {
                notify(1, applicationBuilder.build())
            }

            val fiatBalance = newBalance.multiply(currentPrice.toLong())
                ?.toFriendlyString()!!.split(" ")[0]

            setWalletbalance(newBalance.toFriendlyString())
            setFiatBalance(fiatBalance)
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
        val fiatBalance = Globals.kit?.wallet()?.balance?.multiply(currentPrice.toLong())
            ?.toFriendlyString()!!.split(" ")[0]
        setFiatBalance(fiatBalance)
    }

    inner class GetBalanceAsync : AsyncTask<Void, Int, String>() {
        override fun doInBackground(vararg params: Void?): String {
            balance = Globals.kit?.wallet()?.balance?.toFriendlyString()!!
            return balance
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            Log.d(Globals.LOG_TAG, "Balance: " + balance)
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
