package com.payclip.design.extensions

import java.math.BigDecimal
import java.math.RoundingMode
import java.util.concurrent.TimeUnit

fun Float.toTime(): String {
    val minutes = TimeUnit.SECONDS.toMinutes(this.toLong()).round(2)
    val seconds = (this % 60).toInt()

    val mins = if (minutes.toString().length > 1) "$minutes" else "0$minutes"
    val secs = if (seconds.toString().length > 1) "$seconds" else "0$seconds"

    return "$mins:$secs"
}

fun Long.round(decimals: Int): Long = BigDecimal(this).setScale(decimals, RoundingMode.HALF_EVEN).toLong()