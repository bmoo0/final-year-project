package com.bitcoinwallet.utilities

import org.bitcoinj.core.BlockChain
import org.bitcoinj.core.PeerGroup
import org.bitcoinj.kits.WalletAppKit
import org.bitcoinj.store.SPVBlockStore
import org.bitcoinj.wallet.Wallet
import java.io.File

/**
 * Created by Ben Moore on 15/02/2019.
 */

class BitcoinUtilities {
    companion object {
        fun initialiseWallet(walletFile: File, blockStoreFile: File) {
            Globals.walletFile = walletFile
            Globals.wallet = Wallet(Globals.networkParams)
            Globals.blockStore = SPVBlockStore(Globals.networkParams, blockStoreFile)
            Globals.blockChain = BlockChain(
                Globals.networkParams,
                Globals.wallet, Globals.blockStore
            )
            Globals.peerGroup = PeerGroup(Globals.networkParams, Globals.blockChain)
            Globals.wallet!!.saveToFile(walletFile)
        }

        fun loadWalletFromFile(walletFile: File, blockStoreFile: File) {
            Globals.walletFile = walletFile
            Globals.wallet = Wallet.loadFromFile(walletFile)
            Globals.blockStore = SPVBlockStore(Globals.networkParams, blockStoreFile)
            Globals.blockChain = BlockChain(
                Globals.networkParams,
                Globals.wallet, Globals.blockStore
            )
            Globals.peerGroup = PeerGroup(Globals.networkParams, Globals.blockChain)
        }

        fun setupWalletAppKit(dir: File) {
            Globals.kit = WalletAppKit(Globals.networkParams,dir, "BTCWALLET")
        }
    }
}
