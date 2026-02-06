package com.example.diploma

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object NotificationDateTime {

    fun format(epochMillis: Long): String {
        val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        return formatter.format(Date(epochMillis))
    }

    fun parse(rawValue: String?): Long {
        if (rawValue.isNullOrBlank()) {
            return System.currentTimeMillis()
        }

        val asLong = rawValue.toLongOrNull()
        if (asLong != null) {
            return if (asLong < 1000000000000L) asLong * 1000 else asLong
        }

        val formats = listOf(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd HH:mm:ss",
            "dd.MM.yyyy HH:mm"
        )

        formats.forEach { pattern ->
            val formatter = SimpleDateFormat(pattern, Locale.getDefault())
            try {
                val parsedDate = formatter.parse(rawValue)
                if (parsedDate != null) {
                    return parsedDate.time
                }
            } catch (_: ParseException) {
                // Try next format.
            }
        }

        return System.currentTimeMillis()
    }
}
