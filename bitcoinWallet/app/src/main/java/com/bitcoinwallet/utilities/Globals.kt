package com.bitcoinwallet.utilities

import org.bitcoinj.core.BlockChain
import org.bitcoinj.core.NetworkParameters
import org.bitcoinj.core.PeerGroup
import org.bitcoinj.kits.WalletAppKit
import org.bitcoinj.params.TestNet3Params
import org.bitcoinj.store.SPVBlockStore
import org.bitcoinj.wallet.Wallet
import java.io.File

/**
 * Created by Ben Moore on 29/01/2019.
 */

class Globals {
    companion object {
        val LOG_TAG = "BTC WALLET"
        val FILE_PREFIX = "btc_wallet"
        val IS_PRODUCTION = false
        val networkParams: TestNet3Params
            get() = TestNet3Params.get()

        var wallet : Wallet? = null
        var walletFile: File? = null
        var blockStore: SPVBlockStore? = null
        var blockChain: BlockChain? = null
        var peerGroup: PeerGroup? = null
        var kit: WalletAppKit? = null
    }
}