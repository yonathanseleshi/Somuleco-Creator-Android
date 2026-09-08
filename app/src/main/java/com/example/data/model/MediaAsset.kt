package com.example.data.model

enum class MediaType(val label: String) {
    IMAGE("Photo"),
    VIDEO("Video"),
    AUDIO("Audio"),
    DOCUMENT("Document")
}

data class MediaAsset(
    val id: String,
    val title: String,
    val fileName: String,
    val type: MediaType,
    val sizeText: String,
    val durationText: String? = null,
    val uploadDate: String,
    val channelName: String = "Photography Masterclass",
    val status: String = "READY", // "UPLOADING", "PROCESSING", "READY"
    val progress: Float = 1.0f,
    val thumbnailEmoji: String = "🖼️"
) {
    val name: String get() = title
    val size: String get() = sizeText
    val uploadedAt: String get() = uploadDate
}
