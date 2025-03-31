package com.library.polargx.helpers

import com.library.polargx.Constants
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object DateTimeUtils {

    fun isSameDate(calendar1: Calendar?, calendar2: Calendar?): Boolean {
        return calendar1?.get(Calendar.DAY_OF_MONTH) == calendar2?.get(Calendar.DAY_OF_MONTH)
                && calendar1?.get(Calendar.MONTH) == calendar2?.get(Calendar.MONTH)
                && calendar1?.get(Calendar.YEAR) == calendar2?.get(Calendar.YEAR)
    }

    fun isSameMonthYear(calendar1: Calendar?, calendar2: Calendar?): Boolean {
        return calendar1?.get(Calendar.MONTH) == calendar2?.get(Calendar.MONTH)
                && calendar1?.get(Calendar.YEAR) == calendar2?.get(Calendar.YEAR)
    }

    fun stringToCalendar(
        source: String?,
        format: String = Constants.DateTime.DEFAULT_DATE_FORMAT,
        timeZone: TimeZone? = TimeZone.getDefault(),
        locale: Locale = Locale.US
    ): Calendar? {
        val date = stringToDate(source, format, timeZone, locale) ?: return null
        return Calendar.getInstance().apply {
            this.time = date
        }
    }

    fun calendarToString(
        source: Calendar?,
        format: String = Constants.DateTime.DEFAULT_DATE_FORMAT,
        timeZone: TimeZone? = TimeZone.getDefault(),
        locale: Locale = Locale.US
    ): String? {
        return dateToString(source?.time, format, timeZone, locale)
    }

    fun stringToDate(
        source: String?,
        format: String = Constants.DateTime.DEFAULT_DATE_FORMAT,
        timeZone: TimeZone? = TimeZone.getDefault(),
        locale: Locale = Locale.US
    ): Date? {
        if (source == null) return null
        val df = SimpleDateFormat(format, locale)
        df.timeZone = timeZone ?: TimeZone.getDefault()
        return try {
            df.parse(source)
        } catch (e: ParseException) {
            null
        }
    }

    fun dateToString(
        source: Date?,
        format: String = Constants.DateTime.DEFAULT_DATE_FORMAT,
        timeZone: TimeZone? = TimeZone.getDefault(),
        locale: Locale = Locale.US
    ): String? {
        if (source == null) return null
        val df = SimpleDateFormat(format, locale)
        df.timeZone = timeZone ?: TimeZone.getDefault()
        return try {
            df.format(source)
        } catch (e: ParseException) {
            null
        }
    }
}