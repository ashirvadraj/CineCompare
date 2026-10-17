package com.cinecompare.core.common

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun Int.toCurrencyString(): String {
    val rupees = this / 100
    val paise = this % 100
    return if (paise == 0) "₹$rupees" else "₹$rupees.$paise"
}

fun LocalDate.toDisplayString(): String =
    this.format(DateTimeFormatter.ofPattern("EEE, dd MMM"))

fun LocalDateTime.toTimeString(): String =
    this.format(DateTimeFormatter.ofPattern("h:mm a"))

fun Float.toDistanceString(): String = when {
    this < 1f -> "${(this * 1000).toInt()} m"
    else -> "${String.format("%.1f", this)} km"
}
