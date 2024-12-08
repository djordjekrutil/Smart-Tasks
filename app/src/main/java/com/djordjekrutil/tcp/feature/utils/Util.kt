package com.djordjekrutil.tcp.feature.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

fun calculateDaysLeft(dueDate: String): Int {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val dueDateLocalDate = LocalDate.parse(dueDate, formatter)
    val today = LocalDate.now()

    return ChronoUnit.DAYS.between(today, dueDateLocalDate).toInt()
}