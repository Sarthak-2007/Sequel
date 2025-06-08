package com.personalplanner.utils

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DateUtils @Inject constructor() {

    private val isoDateFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE // YYYY-MM-DD

    fun toIsoDateString(date: LocalDate): String {
        return date.format(isoDateFormatter)
    }

    fun fromIsoDateString(dateString: String): LocalDate {
        return LocalDate.parse(dateString, isoDateFormatter)
    }

    fun getCurrentDate(): LocalDate {
        return LocalDate.now()
    }

    fun getDayDisplayName(date: LocalDate, style: TextStyle = TextStyle.FULL, locale: Locale = Locale.getDefault()): String {
        return date.dayOfWeek.getDisplayName(style, locale)
    }

    fun getMonthDisplayName(date: LocalDate, style: TextStyle = TextStyle.FULL, locale: Locale = Locale.getDefault()): String {
        return date.month.getDisplayName(style, locale)
    }

    fun getYearMonthDisplayName(date: LocalDate, pattern: String = "MMMM yyyy", locale: Locale = Locale.getDefault()): String {
        val formatter = DateTimeFormatter.ofPattern(pattern, locale)
        return date.format(formatter)
    }

    fun getWeekDays(referenceDate: LocalDate): List<LocalDate> {
        val startOfWeek = referenceDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        return (0..6).map { startOfWeek.plusDays(it.toLong()) }
    }

    fun getPreviousWeek(referenceDate: LocalDate): LocalDate {
        return referenceDate.minusWeeks(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    }

    fun getNextWeek(referenceDate: LocalDate): LocalDate {
        return referenceDate.plusWeeks(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    }

    fun isSameDay(date1: LocalDate, date2: LocalDate): Boolean {
        return date1.isEqual(date2)
    }

    fun getFormattedDateShort(date: LocalDate, locale: Locale = Locale.getDefault()): String {
        // Example: "Mon, Feb 26"
        val formatter = DateTimeFormatter.ofPattern("EEE, MMM d", locale)
        return date.format(formatter)
    }

    fun getFormattedDateFull(date: LocalDate, locale: Locale = Locale.getDefault()): String {
        // Example: "Monday, February 26, 2024"
        val formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", locale)
        return date.format(formatter)
    }

    fun getDaysInMonth(yearMonth: YearMonth): List<LocalDate> {
        return (1..yearMonth.lengthOfMonth()).map { day ->
            yearMonth.atDay(day)
        }
    }
}
