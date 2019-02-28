package com.bitcoinwallet.utilities

import org.bitcoinj.kits.WalletAppKit
import java.io.File
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Created by Ben Moore on 15/02/2019.
 */

class BitcoinUtilities {
    companion object {
        fun setupWalletAppKit(dir: File, onComplete: () -> Unit) {
            Globals.kit = object: WalletAppKit(Globals.networkParams,
                dir,
                Globals.FILE_PREFIX
            ) {
                override fun onSetupCompleted() {
                    super.onSetupCompleted()
                    onComplete()
                }
            }
        }

        fun walletExists(dir: File): Boolean {
            val blockStore = File(dir, Globals.FILE_PREFIX + ".spvchain")
            val walletFile = File(dir, Globals.FILE_PREFIX + ".wallet")

            return (blockStore.exists() && walletFile.exists())
        }

        fun creationToFriendlyString(time: Long) : String {
            val formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy")
            return formatter.format(Instant.ofEpochSecond(time))
        }

        fun stringToEpochLong(dateTimeString: String) : Long {
            val formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy")
            val dt = LocalDateTime.parse(dateTimeString, formatter)
            return dt.atZone(ZoneId.systemDefault()).toInstant().epochSecond
        }
    }
}
