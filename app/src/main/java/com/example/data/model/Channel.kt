package com.example.data.model

data class Channel(
    val id: String,
    val creatorAccountId: String,
    val name: String,
    val slug: String,
    val handle: String,
    val description: String,
    val category: String,
    val followersCount: Int,
    val subscribersCount: Int,
    val contentCount: Int,
    val isDefault: Boolean = false,
    val iconEmoji: String = "📷",
    val primaryColorHex: Long = 0xFF2563EB,
    val isSubscribed: Boolean = false,
    val isFollowed: Boolean = false
)
