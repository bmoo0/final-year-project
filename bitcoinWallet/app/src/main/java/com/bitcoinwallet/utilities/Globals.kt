package com.bitcoinwallet.utilities

import org.bitcoinj.kits.WalletAppKit
import org.bitcoinj.params.TestNet3Params

/**
 * Created by Ben Moore on 29/01/2019.
 */

class Globals {
    companion object {
        val LOG_TAG = "BTC WALLET"
        val FILE_PREFIX = "btc_wallet"
        val networkParams: TestNet3Params
            get() = TestNet3Params.get()
        var kit: WalletAppKit? = null
    }
}