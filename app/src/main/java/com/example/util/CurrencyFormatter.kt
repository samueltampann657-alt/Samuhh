package com.example.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object CurrencyFormatter {
    private val localeId = Locale("in", "ID")
    private val currencyFormat = NumberFormat.getCurrencyInstance(localeId).apply {
        maximumFractionDigits = 0
    }

    fun formatRupiah(amount: Long): String {
        return try {
            val formatted = currencyFormat.format(amount)
            // standard Java locale might return "Rp12.500.000" or "IDR 12.500.000"
            // Make it clean: "Rp 12.500.000"
            formatted.replace("Rp", "Rp ").replace("IDR", "Rp ").trim()
        } catch (e: Exception) {
            "Rp $amount"
        }
    }

    fun formatCompactRupiah(amount: Long): String {
        return when {
            amount >= 1_000_000_000 -> String.format(Locale.US, "Rp %.1f M", amount / 1_000_000_000.0)
            amount >= 1_000_000 -> String.format(Locale.US, "Rp %.1f jt", amount / 1_000_000.0)
            amount >= 1_000 -> String.format(Locale.US, "Rp %.0f rb", amount / 1_000.0)
            else -> "Rp $amount"
        }
    }

    fun formatDate(timestamp: Long): String {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply { timeInMillis = timestamp }

        val isSameDay = now.get(Calendar.YEAR) == target.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) == target.get(Calendar.DAY_OF_YEAR)

        val isYesterday = now.get(Calendar.YEAR) == target.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) - target.get(Calendar.DAY_OF_YEAR) == 1

        val timeFormat = SimpleDateFormat("HH:mm", localeId)
        val timeString = timeFormat.format(Date(timestamp))

        return when {
            isSameDay -> "Hari ini, $timeString"
            isYesterday -> "Kemarin, $timeString"
            else -> {
                val fullDateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", localeId)
                fullDateFormat.format(Date(timestamp))
            }
        }
    }

    fun formatDateOnly(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", localeId)
        return dateFormat.format(Date(timestamp))
    }

    fun formatMonthYear(calendar: Calendar): String {
        val format = SimpleDateFormat("MMMM yyyy", localeId)
        return format.format(calendar.time)
    }
}
