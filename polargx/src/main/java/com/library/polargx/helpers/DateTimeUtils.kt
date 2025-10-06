package com.library.polargx.helpers

import com.library.polargx.Constants
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object DateTimeUtils {

    fun calendarToString(
        source: Calendar,
        format: String = Constants.DateTime.DEFAULT_DATE_FORMAT,
        timeZone: TimeZone? = TimeZone.getDefault(),
        locale: Locale = Locale.US
    ): String {
        return dateToString(source.time, format, timeZone, locale)
    }

    fun dateToString(
        source: Date,
        format: String = Constants.DateTime.DEFAULT_DATE_FORMAT,
        timeZone: TimeZone? = TimeZone.getDefault(),
        locale: Locale = Locale.US
    ): String {
        val df = SimpleDateFormat(format, locale)
        df.timeZone = timeZone ?: TimeZone.getDefault()
        return df.format(source)
    }
}