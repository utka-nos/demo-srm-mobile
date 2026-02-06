package com.example.diploma

data class Notification(
    val id: Long,
    val title: String,
    val text: String,
    val eventTime: Long,
    var isRead: Boolean
)
