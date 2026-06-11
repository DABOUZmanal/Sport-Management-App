package com.example.playermanager.players

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object SubscriptionHelper {

    private val formats = listOf(
        SimpleDateFormat("dd/MM/yyyy", Locale.US),
        SimpleDateFormat("dd-MM-yyyy", Locale.US),
        SimpleDateFormat("yyyy-MM-dd", Locale.US)
    )

    fun parseDate(value: String): Date? {
        val trimmed = value.trim()
        if (trimmed.isEmpty()) return null
        for (format in formats) {
            try {
                return format.parse(trimmed)
            } catch (_: Exception) {
            }
        }
        return null
    }

    fun formatDate(date: Date): String =
        SimpleDateFormat("dd/MM/yyyy", Locale.US).format(date)

    /** Active when today is on or before the expiry date. */
    fun isSubscriptionActive(expiryDate: String): Boolean {
        val expiry = parseDate(expiryDate) ?: return false
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val expiryCal = Calendar.getInstance().apply {
            time = expiry
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return !today.after(expiryCal)
    }
}
