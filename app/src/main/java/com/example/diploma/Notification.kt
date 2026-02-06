package com.example.diploma

import java.io.Serializable

data class Notification(
    val id: String,
    val title: String,
    val dateTime: String,
    val text: String,
    var isRead: Boolean
) : Serializable
