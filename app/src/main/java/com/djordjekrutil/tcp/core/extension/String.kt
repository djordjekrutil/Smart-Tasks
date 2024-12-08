package com.djordjekrutil.tcp.core.extension

import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun String.toLocalDate(): LocalDate {
    return LocalDate.parse(this, DateTimeFormatter.ISO_DATE)
}