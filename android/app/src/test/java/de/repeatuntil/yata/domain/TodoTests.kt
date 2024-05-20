package de.repeatuntil.yata.domain

import de.repeatuntil.yata.domain.todolist.entities.Todo
import de.repeatuntil.yata.domain.todolist.valueobjects.Deadline
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class TodoTests {

    @Test
    fun `new to-do has a random unique id`() {
        // Given
        val todo1 = Todo()
        val todo2 = Todo()

        // Then
        assert(todo1.id != todo2.id)
    }

    @Test
    fun `new to-do has no deadline`() {
        // Given
        val todo = Todo()

        // Then
        assert(!todo.hasDeadline())
    }

    @Test
    fun `new to-do has empty title and description`() {
        // Given
        val todo = Todo()

        // Then
        assertEquals("", todo.title)
        assertEquals("", todo.description)
    }

    @Test
    fun `new to-do is not completed`() {
        // Given
        val todo = Todo()

        // Then
        assertFalse(todo.isCompleted)
    }

    @Test
    fun `remove deadline`() {
        // Given
        val todo = Todo(deadline = Deadline.now())

        // When
        assert(todo.hasDeadline())
        val todoWithoutDeadline = todo.removeDeadline()

        // Then
        assert(!todoWithoutDeadline.hasDeadline())
    }

    @Test
    fun `is overdue when deadline has passed`() {
        // Given
        val now = Deadline.now()
        val dateTimeNow = now.dateTime
        val instantNow = dateTimeNow.toInstant(now.timeZone)
        val dateTimeYesterday = instantNow.minus(1, DateTimeUnit.DAY, now.timeZone)
            .toLocalDateTime(now.timeZone)

        val todo = Todo(deadline = Deadline(dateTimeYesterday, now.timeZone))

        // Then
        assert(todo.deadline.isOverdue())
    }

    @Test
    fun `is overdue when deadline is now`() {
        // Given
        val instant = Clock.System.now()
        val clock = object : Clock {
            override fun now(): Instant {
                return instant
            }
        }

        val todo = Todo(deadline = Deadline.now(clock))

        // Then
        assert(todo.deadline.isOverdue())
    }

    @Test
    fun `is overdue when current time zone is ahead of deadline's time zone`() {
        // Given
        val deadlineTimeZone = TimeZone.of("UTC+1")

        val todo = Todo(
            deadline = Deadline(
                dateTime = Clock.System.now().toLocalDateTime(deadlineTimeZone),
                timeZone = deadlineTimeZone
            )
        )

        // Given
        val currentTimeZone = TimeZone.of("UTC+2")

        // Then
        assert(todo.deadline.isOverdue(currentTimeZone))
    }
}