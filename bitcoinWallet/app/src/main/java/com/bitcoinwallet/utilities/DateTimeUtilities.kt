package com.bitcoinwallet.utilities

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class DateTimeUtilities {
    companion object {
        private const val dateTimeFormat = "HH:mm:ss dd-MM-yyyy"

        fun epochTimeToFriendlyString(time: Long) : String {
            val formatter = DateTimeFormatter.ofPattern(dateTimeFormat)
            return formatter.format(LocalDateTime.ofEpochSecond(time, 0, ZoneOffset.UTC))
        }

        fun stringToEpochLong(dateTimeString: String) : Long {
            val formatter = DateTimeFormatter.ofPattern(dateTimeFormat)
            val dt = LocalDateTime.parse(dateTimeString, formatter)
            return dt.atZone(ZoneId.of("UTC")).toInstant().epochSecond
        }

        fun numbersToEpochLong(hour: Int, minute: Int, second: Int,
                              day: Int, month: Int, year: Int) : Long {
            val time =
                "${if (hour < 10) "0$hour" else hour.toString()}:${if (minute < 10) "0$minute" else minute.toString()}:${if(second < 10) "0$second" else second.toString()}"

            val date = "${if (day < 10) "0$day" else day.toString()}-${if (month < 10) "0$month" else month.toString()}-$year"

            val dtString = "$time $date"

            return stringToEpochLong(dtString)
        }

        fun numbersToTimeString(hour: Int, minute: Int, second: Int) : String {
            return "${if (hour < 10) "0$hour" else hour.toString()}:${if (minute < 10) "0$minute" else minute.toString()}:${if(second < 10) "0$second" else second.toString()}"
        }

        fun numbersToDateString(day: Int, month: Int, year: Int) : String {
            return "${if (day < 10) "0$day" else day.toString()}-${if (month < 10) "0$month" else month.toString()}-$year"
        }
    }
}