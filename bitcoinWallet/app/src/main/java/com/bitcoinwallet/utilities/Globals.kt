package com.bitcoinwallet.utilities

import org.bitcoinj.core.BlockChain
import org.bitcoinj.core.NetworkParameters
import org.bitcoinj.core.PeerGroup
import org.bitcoinj.store.SPVBlockStore
import org.bitcoinj.wallet.Wallet
import java.io.File

/**
 * Created by Ben Moore on 29/01/2019.
 */

class Globals {
    companion object {
        val WALLET_NAME = "wallet_file.dat"
        val BLOCK_STORE_NAME = "block_store_file.dat"
        val IS_PRODUCTION = false
        var networkParams = if (IS_PRODUCTION) NetworkParameters.fromID(NetworkParameters.ID_MAINNET)
        else NetworkParameters.fromID(NetworkParameters.ID_TESTNET)

        var wallet : Wallet? = null
        var walletFile: File? = null
        var blockStore: SPVBlockStore? = null
        var blockChain: BlockChain? = null
        var peerGroup: PeerGroup? = null
    }
}