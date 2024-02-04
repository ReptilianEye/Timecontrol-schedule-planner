package com.example.timecontrol.utils

import androidx.compose.runtime.MutableState
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter



fun LocalDate.pretty(pattern: String = "dd.MM"): String =
    this.format(DateTimeFormatter.ofPattern(pattern))

fun LocalDate.toMillis() = this.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
fun Long.toLocalDate(): LocalDate = Instant.ofEpochMilli(this).atZone(ZoneOffset.UTC).toLocalDate()

fun Pair<String, String>.prettyTime() = "$first - $second"