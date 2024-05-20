package de.repeatuntil.yata.domain.todolist.valueobjects

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

data class Deadline(
    val dateTime: LocalDateTime,
    val timeZone: TimeZone,
    private val clock: Clock = Clock.System
) {

    fun isOverdue(timeZone: TimeZone = TimeZone.currentSystemDefault()): Boolean {
        val now = clock.now()
        val deadlineInstant = dateTime.toInstant(timeZone)
        return now >= deadlineInstant
    }

    companion object {
        val FAR_FUTURE = Deadline(
            dateTime = LocalDateTime(year = 10000, monthNumber = 1, dayOfMonth = 1, hour = 0, minute = 0, second = 0),
            timeZone = TimeZone.currentSystemDefault()
        )

        fun now(clock: Clock = Clock.System): Deadline {
            val timeZone = TimeZone.currentSystemDefault()
            return Deadline(
                dateTime = clock.now().toLocalDateTime(timeZone),
                timeZone = timeZone,
                clock = clock
            )
        }
    }
}