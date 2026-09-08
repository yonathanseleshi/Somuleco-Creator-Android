package com.example.data.model

enum class ProductType(val label: String) {
    DIGITAL_PRODUCT("Digital Guide & PDF"),
    CREATIVE_ASSETS("Preset & LUT Pack"),
    AUDIO_MEDIA("Audio & Sample Pack"),
    CODE_TEMPLATES("Code & Workflow"),
    COURSE_REFERENCE("Masterclass Course")
}

data class RightsRecord(
    val id: String,
    val registeredAt: String,
    val licenseName: String,
    val allowDownload: Boolean = true,
    val allowRedistribution: Boolean = false,
    val allowCommercialUse: Boolean = true,
    val allowModification: Boolean = true,
    val allowAiTraining: Boolean = false,
    val protectionStatus: String = "Protected & Registered"
)

data class CreatorProduct(
    val id: String,
    val creatorAccountId: String,
    val creatorName: String = "Elena Rostova",
    val channelId: String,
    val channelName: String,
    val title: String,
    val slug: String,
    val shortDescription: String,
    val fullDescription: String,
    val priceAmount: Double,
    val currency: String = "USD",
    val productType: ProductType,
    val fileFormat: String = "PDF & DNG Bundle",
    val fileSizeMb: Double = 84.5,
    val salesCount: Int = 342,
    val revenueTotal: Double = 13338.0,
    val status: String = "ACTIVE", // ACTIVE, DRAFT, ARCHIVED
    val isMarketplaceListed: Boolean = true,
    val iconEmoji: String = "📘",
    val rightsRecord: RightsRecord,
    val isPurchased: Boolean = false
)
