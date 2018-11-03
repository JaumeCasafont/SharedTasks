package com.jcr.sharedtasks.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

object TimeUtils {

    val now: Array<Int>
        get() {
            val calendar = Calendar.getInstance()
            return getSplitDate(calendar)
        }

    fun getDateFormatted(year: Int, month: Int, day: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)
        val simpleDateFormat = SimpleDateFormat("EEE, MMM d, ''yy")
        return simpleDateFormat.format(calendar.time)
    }

    fun getDateInMillis(year: Int, month: Int, day: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)
        return calendar.timeInMillis
    }

    fun getDateFormatted(timeInMillis: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMillis
        val simpleDateFormat = SimpleDateFormat("EEE, MMM d, ''yy")
        return simpleDateFormat.format(calendar.time)
    }

    fun getDateFormatted(dateToFormat: String): Long {
        var date = Date()
        try {
            date = SimpleDateFormat("EEE, MMM d, ''yy").parse(dateToFormat)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return date.time
    }

    fun getDateToCalendar(timeInMillis: Long): Array<Int> {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMillis
        return getSplitDate(calendar)
    }

    private fun getSplitDate(calendar: Calendar): Array<Int> {
        return arrayOf(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        )
    }
}
