package com.example.diploma

data class PushPayload(
    val externalId: String?,
    val title: String,
    val text: String,
    val eventTime: Long
)
