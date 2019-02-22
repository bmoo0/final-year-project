package com.bitcoinwallet.services

import android.arch.lifecycle.LifecycleService
import android.content.Context
import android.os.Handler
import android.os.PowerManager
import android.text.format.DateUtils
import android.util.Log
import com.bitcoinwallet.utilities.Globals
import org.bitcoinj.core.Block
import org.bitcoinj.core.FilteredBlock
import org.bitcoinj.core.Peer
import org.bitcoinj.core.listeners.AbstractPeerDataEventListener
import org.bitcoinj.kits.WalletAppKit
import org.bitcoinj.net.discovery.MultiplexingDiscovery
import org.bitcoinj.net.discovery.PeerDiscovery
import org.slf4j.LoggerFactory
import java.net.InetAddress
import java.net.InetSocketAddress
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong
import java.util.logging.Logger

class BlockchainDownloadService : LifecycleService() {
    private lateinit var wakeLock: PowerManager.WakeLock
    private var delayHandler = Handler()
    private val PREFS_KEY_TRUSTED_PEER = "trusted_peer"
    private val maxConnectedPeers = 4
    private val log = LoggerFactory.getLogger(BlockchainDownloadService::class.java)

    private var blockchainDownloadListener = object : AbstractPeerDataEventListener() {
        private val lastMessageTime = AtomicLong(0)

        private val runnable = object : Runnable {
            override fun run() {
                lastMessageTime.set(System.currentTimeMillis())
            }
        }

        override fun onBlocksDownloaded(
            peer: Peer?,
            block: Block?,
            filteredBlock: FilteredBlock?,
            blocksLeft: Int
        ) {
            super.onBlocksDownloaded(peer, block, filteredBlock, blocksLeft)

            delayHandler.removeCallbacksAndMessages(null)

            var now = System.currentTimeMillis()

            log.info("Block downloaded")
            Log.d(Globals.LOG_TAG, "Block downloaded, only " + blocksLeft.toString() + " left!")

            if (now - lastMessageTime.get() > DateUtils.SECOND_IN_MILLIS) {
                delayHandler.post(runnable)
            } else {
                delayHandler.postDelayed(runnable, DateUtils.SECOND_IN_MILLIS)
            }
        }

        override fun onChainDownloadStarted(peer: Peer?, blocksLeft: Int) {
            super.onChainDownloadStarted(peer, blocksLeft)
            Log.d(Globals.LOG_TAG, "Blockchain Download started")
        }
    }
    /*
    override fun onCreate() {
        super.onCreate()
        val powerManager = getSystemService(PowerManager::class.java) as PowerManager
        // TODO: Change this string to a constant also
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "BLOCKCHAIN DL SERVICE:")
        wakeLock.acquire()
        Log.d(Globals.LOG_TAG, "Blockchain Download service started")
        log.info("Blockchain Download started")
        Globals.peerGroup?.addWallet(Globals.wallet)
        Globals.peerGroup?.setDownloadTxDependencies(0)
        Globals.peerGroup?.setConnectTimeoutMillis(15 * 1000) // 15 seconds
        Globals.peerGroup?.setPeerDiscoveryTimeoutMillis(10 * DateUtils.SECOND_IN_MILLIS) // 10 seconds
        // TODO: Change these to constants
        Globals.peerGroup?.setUserAgent("BTC Wallet", "0.1")
        Globals.peerGroup?.maxConnections = maxConnectedPeers

        Globals.peerGroup?.addPeerDiscovery(object: PeerDiscovery {
            private val normalPeerDiscovery = MultiplexingDiscovery.forServices(Globals.networkParams, 0)
            override fun getPeers(
                services: Long,
                timeoutValue: Long,
                timeoutUnit: TimeUnit?
            ): Array<InetSocketAddress> {
                val peers = LinkedList<InetSocketAddress>()

                val addr = InetSocketAddress(PREFS_KEY_TRUSTED_PEER, Globals.networkParams.port)

                if (addr.address != null) {
                    peers.add(addr)
                }

                while (peers.size >= maxConnectedPeers) {
                    peers.removeAt(peers.size - 1)
                }

                return peers.toTypedArray()
            }

            override fun shutdown() {
                normalPeerDiscovery.shutdown()
            }
        })


        Globals.peerGroup?.startAsync()
        Globals.peerGroup?.startBlockChainDownload(blockchainDownloadListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        delayHandler.removeCallbacksAndMessages(null)
        if(wakeLock.isHeld) {
            wakeLock.release()
        }
        log.info("Download Service complete")

        if(Globals.peerGroup?.isRunning!!) {
            Globals.peerGroup?.stopAsync()
        }
    }
    */

    override fun onCreate() {
        super.onCreate()
        // try wallet app kit and is blocking startup(false)
        Log.d(Globals.LOG_TAG, "Blockchain Download service started")
        val powerManager = getSystemService(PowerManager::class.java) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "BLOCKCHAIN DL SERVICE:")
        wakeLock.acquire()
        Globals.kit?.peerGroup()?.setConnectTimeoutMillis(15 * 1000) // 15 seconds
        Globals.kit?.peerGroup()?.setPeerDiscoveryTimeoutMillis(10 * DateUtils.SECOND_IN_MILLIS) // 10 seconds
        Globals.kit?.peerGroup()?.setUserAgent("BTC Wallet", "0.1")
        Globals.kit?.peerGroup()?.maxConnections = 4
        Globals.kit?.startAsync()
        Globals.kit?.awaitRunning()
        wakeLock.release()
    }
}