package com.somuleco.creator.data.model

enum class NotificationType(val label: String, val iconEmoji: String) {
    FOLLOWER("Follower", "👤"),
    SUBSCRIBER("Subscriber", "⭐"),
    SALE("Product Sale", "💰"),
    RIGHTS("Digital Rights", "🛡️"),
    AI_RECOMMENDATION("Creator AI", "✨"),
    CONTENT("New Content", "📢")
}

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: String,
    val type: NotificationType,
    val isRead: Boolean = false,
    val targetRoute: String? = null
) {
    val iconEmoji: String get() = type.iconEmoji
}
