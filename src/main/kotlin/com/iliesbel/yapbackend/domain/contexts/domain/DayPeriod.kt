package com.iliesbel.yapbackend.domain.contexts.domain

import java.time.LocalTime

/**
 * Represents different time contexts throughout a 24-hour day.
 *
 * Time periods are divided as follows:
 *
 * <pre>
 * 00:00                   06:00          12:00          17:00          21:00    24:00
 *   |---------------------|--------------|--------------|--------------|--------|
 *   |       NIGHT         |   MORNING    |  AFTERNOON   |   EVENING    | NIGHT  |
 *   |---------------------|--------------|--------------|--------------|--------|
 *
 * Timeline representation:
 * ========================================================================================
 * 00  01  02  03  04  05  06  07  08  09  10  11  12  13  14  15  16  17  18  19  20  21  22  23
 * [--------NIGHT---------][----MORNING----][---AFTERNOON---][---EVENING---][---NIGHT---]
 * ========================================================================================
 *
 * Details:
 * - NIGHT:     21:00 - 06:00 (9 hours, spans midnight)
 * - MORNING:   06:00 - 12:00 (6 hours)
 * - AFTERNOON: 12:00 - 17:00 (5 hours)
 * - EVENING:   17:00 - 21:00 (4 hours)
 * </pre>
 *
 * @property displayName Human-readable name for the time context
 * @property startTime Starting time of the period (inclusive)
 * @property endTime Ending time of the period (exclusive)
 */
enum class DayPeriod(val displayName: String, val startTime: LocalTime, val endTime: LocalTime) {
    NIGHT("Night", LocalTime.of(21, 0), LocalTime.of(6, 0)),
    MORNING("Morning", LocalTime.of(6, 0), LocalTime.of(12, 0)),
    AFTERNOON("Afternoon", LocalTime.of(12, 0), LocalTime.of(17, 0)),
    EVENING("Evening", LocalTime.of(17, 0), LocalTime.of(21, 0));

    companion object {
        /**
         * Determines the current time context based on the system's current time.
         *
         * @return The TimeContext that matches the current time
         */
        fun getCurrent(): DayPeriod {
            val currentTime = LocalTime.now()

            // Special handling for NIGHT since it spans across midnight
            if (currentTime >= NIGHT.startTime || currentTime < NIGHT.endTime) {
                return NIGHT
            }

            // Check other time periods
            return entries.find { timeContext ->
                timeContext != NIGHT &&
                        currentTime >= timeContext.startTime &&
                        currentTime < timeContext.endTime
            } ?: NIGHT
        }

        /**
         * Finds a TimeContext by its display name (case-insensitive).
         *
         * @param name The display name to search for
         * @return The matching TimeContext, or null if not found
         */
        fun fromContextName(name: String): DayPeriod? {
            return entries.find { it.displayName.equals(name, ignoreCase = true) }
        }
    }
}