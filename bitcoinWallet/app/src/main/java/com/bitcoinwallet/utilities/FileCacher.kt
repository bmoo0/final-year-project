package com.bitcoinwallet.utilities

import android.util.Log
import java.io.File
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.reflect.full.memberProperties

/*
    This class saves the price data gotten from the API to a local file so that it can lower the amount of HTTP calls
    needed, if the date in the file is < 24 hours old the cacher will extract the local data, if it is older it will
    pull the date from online.
 */
class FileCacher(private val mFile: File) {
    private val delimiter = ";"

    fun writeFile(prices: HttpRequester.PriceData) {
        val now = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)

        val timeString = "time_data_cached: " + now.toString()

        if (mFile.exists()) {
            mFile.delete()
            mFile.createNewFile()
        }

        mFile.appendText(timeString)
        mFile.appendText("\n")

        val clazz = prices.javaClass.kotlin

        clazz.memberProperties.forEach {// I am your god now
            if(it.name != "isFromCache") {
                val rowString = "${it.name}: ${listToString(it.get(prices) as List<HttpRequester.PriceEntry>)}"
                mFile.appendText(rowString)
                mFile.appendText("\n")
                Log.d(Globals.LOG_TAG, rowString)
            }
        }
    }

    fun readFile(): HttpRequester.PriceData {
        var lines = mFile.readLines()
        lines = lines.drop(1) // skip timestamp

        var hourlyPrice = ArrayList<HttpRequester.PriceEntry>()
        var dailyPrice = ArrayList<HttpRequester.PriceEntry>()
        var weeklyPrice = ArrayList<HttpRequester.PriceEntry>()
        var monthlyPrice = ArrayList<HttpRequester.PriceEntry>()


        lines.forEach { line ->
            if ("^hourlyPrice:".toRegex().containsMatchIn(line)) {
                hourlyPrice = stringToList(line)
            }

            if ("^dailyPrice:".toRegex().containsMatchIn(line)) {
                dailyPrice = stringToList(line)
            }

            if ("^weeklyPrice:".toRegex().containsMatchIn(line)) {
                weeklyPrice = stringToList(line)
            }

            if ("^monthlyPrice:".toRegex().containsMatchIn(line)) {
                monthlyPrice = stringToList(line)
            }
        }

        return HttpRequester.PriceData(true, hourlyPrice, dailyPrice, weeklyPrice, monthlyPrice)
    }

    fun isCacheValid(): Boolean {
        if (!mFile.exists()) {
            return false
        }

        val lines = mFile.readLines()
        val timeLine = lines[0]

        val regex = """^.*: (\d*)""".toRegex()
        val matchResult = regex.find(timeLine)
        val (timestamp) = matchResult?.destructured!!

        val cacheTime = LocalDateTime.ofEpochSecond(timestamp.toLong(), 0, ZoneOffset.UTC)
        val now = LocalDateTime.now()

        if (cacheTime.isBefore(now.minusDays(1))) {
            return false
        }

        return true
    }

    private fun stringToList(line: String): ArrayList<HttpRequester.PriceEntry> {
        val withTitle = line.split(":")
        val lst = withTitle[1].split(delimiter)
        val result = ArrayList<HttpRequester.PriceEntry>()
        val regex = """price=(\d*\.\d*), timestamp=(\d*)""".toRegex()

        lst.forEach {
            val matchResult = regex.find(it)
            val (price, timestamp) = matchResult?.destructured!!
            result.add(HttpRequester.PriceEntry(price.toDouble(), timestamp.toLong()))
        }

        return result
    }

    private fun <T> listToString(lst: List<T>): String {
        var result = ""

        lst.map { result += it.toString() + delimiter }
        return result.dropLast(1)
    }
}