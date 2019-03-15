package com.bitcoinwallet.utilities

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.beust.klaxon.Klaxon
import com.beust.klaxon.PathMatcher
import java.io.StringReader
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.regex.Pattern

class HttpRequester(context: Context) {
    // TODO: Add functionality for switching currencies like USD
    private val COIN_API_KEY = "67544CAE-2A21-4718-A009-B6211B456707"
    private val BACKUP_COIN_API_KEY = "3C0805F4-EA70-4F6A-8A70-DAE85236195E"
    private val TAG = "BTC WALLET REQUEST"
    private var fiatCurrency = "GBP"

    companion object {
        @Volatile
        private var INSTANCE: HttpRequester? = null

        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: HttpRequester(context).also {
                    INSTANCE = it
                }
            }
    }

    private val requestQueue: RequestQueue by lazy {
        // keep request queue live throughout application lifetime
        Volley.newRequestQueue(context.applicationContext)
    }

    private val delegate: HttpRequestDelegate by lazy { context as HttpRequestDelegate }

    private fun <T> addToRequestQueue(req: Request<T>) {
        req.tag = TAG
        requestQueue.add(req)
    }

    fun requestCurrentPrice() {
        val url = "https://api.coindesk.com/v1/bpi/currentprice.json"
        var price: Double = 0.00

        val pathMatcher = object : PathMatcher {
            override fun pathMatches(path: String): Boolean
                    = Pattern.matches(".*bpi.*$fiatCurrency.*", path)

            override fun onMatch(path: String, value: Any) {
                when(path) {
                        "$.bpi.GBP.rate_float" -> {
                            val strResponse = value.toString()
                            price = strResponse.toDouble()
                        }
                }
            }
        }

        val stringReq = StringRequest(
            Request.Method.GET, url,
            Response.Listener<String> { response ->
                Klaxon().pathMatcher(pathMatcher).parseJsonObject(StringReader(response))
                delegate.onCurrentPriceReturned(price)
            }, Response.ErrorListener {
                delegate.onHttpError("Failed to get the current price")
            }
        )
        addToRequestQueue(stringReq)
    }

    fun requestWeeklyData() {
        val time = LocalDateTime.now()
        val lastWeek = time.minusWeeks(1)

        requestHistoricPriceBetweenPeriods(
            lastWeek.format(DateTimeFormatter.ISO_DATE_TIME),
            time.format(DateTimeFormatter.ISO_DATE_TIME),
            "1DAY")
    }

    private fun requestHistoricPriceBetweenPeriods(start: String, end: String, timePeriod: String) {
        val url = "https://rest.coinapi.io/v1/ohlcv/BTC/" +
                "$fiatCurrency/history?period_id=$timePeriod&time_start=$start&time_end=$end"

        val result = ArrayList<PriceEntry>()

        val pathMatcher = object : PathMatcher {
            private lateinit var priceClose : String
            private lateinit var timeClose : String

            override fun pathMatches(path: String): Boolean
                = Pattern.matches("\\$\\[\\d\\].price_close|\\$\\[\\d\\].time_close", path)

            override fun onMatch(path: String, value: Any) {
                if(Pattern.matches("\\$\\[\\d\\].time_close", path)) {
                    timeClose = value.toString()
                }
                if(Pattern.matches("\\$\\[\\d\\].price_close", path)) {
                    priceClose = value.toString()
                    result.add(PriceEntry(priceClose.toDouble(),
                        DateTimeUtilities.isoStringToEpochLong(timeClose)))
                }
            }
        }

        val stringReq = object : StringRequest(
            Request.Method.GET, url,
            Response.Listener<String> { response ->
                Klaxon().pathMatcher(pathMatcher).parseJsonArray(StringReader(response))
                delegate.onPriceRangeReturned(result)
            }, Response.ErrorListener {
                delegate.onHttpError("Failed to get the price range $timePeriod between $start and $end")
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["X-CoinAPI-Key"] = BACKUP_COIN_API_KEY
                return headers
            }
        }

        addToRequestQueue(stringReq)
    }

    data class PriceEntry(val price: Double, val timestamp: Long)

    interface HttpRequestDelegate {
        fun onHttpError(errorMessage: String)
        fun onCurrentPriceReturned(price: Double)
        fun onPriceRangeReturned(price: List<PriceEntry>)
    }
}