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
import java.util.regex.Pattern

class HttpRequester(context: Context) {
    // TODO: Add functionality for switching currencies like USD
    private val COIN_API_KEY = "67544CAE-2A21-4718-A009-B6211B456707"
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

    private val delegate: HttpRequestDelegate by lazy {
        context as HttpRequestDelegate
    }

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
            }, Response.ErrorListener
            {
                delegate.onHttpError("Failed to get the current price")
            }
        )
        addToRequestQueue(stringReq)
    }

    fun requestHistoricPriceBetweenPeriods(start: String, end: String) {
        val url = ""
    }

    interface HttpRequestDelegate {
        fun onCurrentPriceReturned(price: Double)
        fun onHttpError(errorMessage: String)
    }
}