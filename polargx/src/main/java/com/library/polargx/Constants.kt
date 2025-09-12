package com.library.polargx

import java.util.TimeZone

object Constants {

    const val FINGERPRINT = "AndroidSDK"

    object Koin {
        const val APP_HTTP_CLIENT = "PolarGXConstants.Koin.APP_HTTP_CLIENT"
    }

    object DateTime {
        val utcTimeZone = TimeZone.getTimeZone("GMT+00:00")

        const val DEFAULT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"
    }


    object Local {
        object Prefers {
            const val FIRST_TIME_KEY = "first_time"
            const val INSTALL_TIME_KEY = "install_time"

            object Event {
                const val EVENTS_KEY = "EVENTS_KEY"
            }

            object Link {
                const val LINK_DATA_KEY = "LINK_DATA_KEY"
            }
        }
    }

    object PolarEventKey {
        const val Email = "email"
        const val Name = "name"
        const val FirstName = "firstName"
        const val LastName = "lastName"
    }
}