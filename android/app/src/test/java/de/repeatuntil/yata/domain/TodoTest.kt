package de.repeatuntil.yata.domain

import de.repeatuntil.yata.domain.todolist.entities.Todo
import de.repeatuntil.yata.domain.todolist.valueobjects.Deadline
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.equals.shouldNotBeEqual
import io.kotest.matchers.string.shouldBeEmpty
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.junit.Test

class TodoTest {

    @Test
    fun `new to-do has a random unique id`() {
        // Given
        val todo1 = Todo()
        val todo2 = Todo()

        // Then
        todo1.id shouldNotBeEqual todo2.id
    }

    @Test
    fun `new to-do has no deadline`() {
        // Given
        val todo = Todo()

        // Then
        todo.hasDeadline().shouldBeFalse()
    }

    @Test
    fun `new to-do has empty title and description`() {
        // Given
        val todo = Todo()

        // Then
        todo.title.shouldBeEmpty()
        todo.description.shouldBeEmpty()
    }

    @Test
    fun `new to-do is not completed`() {
        // Given
        val todo = Todo()

        // Then
        todo.isCompleted.shouldBeFalse()
    }

    @Test
    fun `remove deadline`() {
        // Given
        val todo = Todo(deadline = Deadline.now())

        // When
        assert(todo.hasDeadline())
        val todoWithoutDeadline = todo.removeDeadline()

        // Then
        todoWithoutDeadline.hasDeadline().shouldBeFalse()
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
        todo.deadline.isOverdue().shouldBeTrue()
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
        todo.deadline.isOverdue().shouldBeTrue()
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
        todo.deadline.isOverdue(currentTimeZone).shouldBeTrue()
    }
}