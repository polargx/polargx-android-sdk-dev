package com.library.link_attribution

import java.util.TimeZone

object LinkAttributionConstants {
    object Configuration {
        const val DOMAIN_SUFFIX = ".makelabs.ai"
    }

    object DateTime {
        val utcTimeZone = TimeZone.getTimeZone("GMT+00:00")

        const val DEFAULT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"
        const val YYYY_MM_DD_FORMAT = "yyyy-MM-dd"
        const val YYYY_MM_DD_hhTmm_ss_SSSZ_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        const val ISO_8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
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
}