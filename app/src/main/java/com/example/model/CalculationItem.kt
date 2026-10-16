package com.example.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class CalculationItem(
    val id: Long = System.currentTimeMillis(),
    val expression: String,
    val result: String,
    val timestamp: Long = System.currentTimeMillis()
) {
    val formattedTime: String
        get() {
            val sdf = SimpleDateFormat("HH:mm - dd MMM", Locale("id", "ID"))
            return sdf.format(Date(timestamp))
        }
}
