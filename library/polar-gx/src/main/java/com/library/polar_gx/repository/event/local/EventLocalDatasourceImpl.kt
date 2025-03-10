package com.library.polar_gx.repository.event.local

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import com.library.polar_gx.PolarGXConstants
import com.library.polar_gx.repository.event.local.model.EventEntity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.TimeUnit

class EventLocalDatasourceImpl(
    private val sf: SharedPreferences
) : EventLocalDatasource {

    override suspend fun setCacheEventList(events: List<EventEntity>?) {
        val jsonStr = try {
            Json.encodeToString(events)
        } catch (ex: Throwable) {
            null
        }
        sf.edit()
            .putString(
                PolarGXConstants.Local.Prefers.Event.EVENTS_KEY,
                jsonStr
            )
            .apply()
    }

    override suspend fun getAllCacheEventList(): List<EventEntity>? {
        try {
            val jsonStr = sf.getString(
                PolarGXConstants.Local.Prefers.Event.EVENTS_KEY,
                null
            ) ?: return null
            return Json.decodeFromString<List<EventEntity>>(jsonStr)
        } catch (ex: Exception) {
            return null
        }
    }

//    override fun getEvents(): TreeMap<Long, List<EventEntity>?>? {
//        try {
//            val jsonStr = sf.getString(
//                LinkAttributionConstants.Local.Prefers.Event.EVENTS_KEY,
//                null
//            ) ?: return null
//            return Json.decodeFromString<TreeMap<Long, List<EventEntity>?>>(jsonStr)
//        } catch (ex: Exception) {
//            return null
//        }
//    }
//
//    override fun setEvents(events: TreeMap<Long, List<EventEntity>?>?) {
//        val jsonStr = try {
//            Json.encodeToString(events)
//        } catch (ex: Throwable) {
//            null
//        }
//        sf.edit()
//            .putString(
//                LinkAttributionConstants.Local.Prefers.Event.EVENTS_KEY,
//                jsonStr
//            )
//            .apply()
//    }

    override suspend fun isFirstTimeLaunch(context: Context?, nowInMillis: Long): Boolean {
        if (context == null) return false
        val firstTime = sf.getBoolean(PolarGXConstants.Local.Prefers.FIRST_TIME_KEY, true)
        if (!firstTime) return false // Already marked as not first time

        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            val installTimeMillis = packageInfo.firstInstallTime

            // Check if install time is stored. If not, store it.
            val storedInstallTime = sf.getLong(PolarGXConstants.Local.Prefers.INSTALL_TIME_KEY, 0L)
            if (storedInstallTime == 0L) {
                sf.edit().putLong(PolarGXConstants.Local.Prefers.INSTALL_TIME_KEY, installTimeMillis).apply()
            }

            val timeDifferenceMillis = nowInMillis - installTimeMillis
            val timeDifferenceSeconds = TimeUnit.MILLISECONDS.toSeconds(timeDifferenceMillis)

            // If it's a very recent install (adjust threshold), it's the first launch.
            if (timeDifferenceSeconds < 60) { // Adjust threshold as needed
                sf.edit().putBoolean(PolarGXConstants.Local.Prefers.FIRST_TIME_KEY, false).apply() // Mark as not first time
                return true
            } else {
                //If the time difference is greater than the threshold, and the app was reinstalled, it is not the first time
                sf.edit().putBoolean(PolarGXConstants.Local.Prefers.FIRST_TIME_KEY, false).apply()
                return false
            }

        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            return false // Handle error as not first time
        }
    }

}
