package com.bitcoinwallet.utilities

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class HttpRequester(context: Context) {
    // TODO: Add functionality for switching currencies like USD
    private var TAG = "BTC WALLET REQUEST"
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
        val url = "https://api.coindesk.com/v1/bpi/currentprice/$fiatCurrency.json"
        val stringReq = StringRequest(
            Request.Method.GET, url,
            Response.Listener<String> { response ->
                val jsonObj = JSONObject(response)
                val price = jsonObj.getJSONObject("bpi").getJSONObject(fiatCurrency)
                    .getDouble("rate_float")
                delegate.onCurrentPriceReturned(price)
            }, Response.ErrorListener
            {
                delegate.onHttpError("Failed to get the current price")
            }
        )
        addToRequestQueue(stringReq)
    }

    interface HttpRequestDelegate {
        fun onCurrentPriceReturned(price: Double)
        fun onHttpError(errorMessage: String)
    }
}