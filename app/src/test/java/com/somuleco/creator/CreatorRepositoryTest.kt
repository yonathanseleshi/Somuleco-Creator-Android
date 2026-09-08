package com.somuleco.creator

import com.somuleco.creator.data.model.*
import com.somuleco.creator.data.repository.CreatorRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class CreatorRepositoryTest {

    @Test
    fun testAuthenticationAndModeSwitching() = runBlocking {
        // Initial state
        val authResult = CreatorRepository.login("elena@example.com", "password123")
        assertTrue(authResult.isSuccess)
        assertEquals("Elena Rostova", authResult.getOrNull()?.displayName)

        // Creator mode toggle
        CreatorRepository.setCreatorMode(true)
        assertTrue(CreatorRepository.isCreatorMode.value)

        CreatorRepository.toggleCreatorMode()
        assertFalse(CreatorRepository.isCreatorMode.value)

        CreatorRepository.setCreatorMode(true)
        assertTrue(CreatorRepository.isCreatorMode.value)
    }

    @Test
    fun testChannelSelectionAndContent() {
        val initialChannels = CreatorRepository.channels.value
        assertTrue("Channels should not be empty", initialChannels.isNotEmpty())

        val targetChannel = initialChannels.first()
        CreatorRepository.selectChannel(targetChannel.id)
        assertEquals(targetChannel.id, CreatorRepository.selectedChannelId.value)

        // Toggle follow channel
        val initialFollow = targetChannel.isFollowed
        CreatorRepository.toggleFollowChannel(targetChannel.id)
        val updatedChannel = CreatorRepository.channels.value.find { it.id == targetChannel.id }
        assertEquals(!initialFollow, updatedChannel?.isFollowed)
    }

    @Test
    fun testContentInteractionAndPublishing() {
        val posts = CreatorRepository.contentItems.value
        assertTrue(posts.isNotEmpty())

        val post = posts.first()
        val initialLikes = post.likesCount
        val initialLiked = post.isLiked

        CreatorRepository.toggleLike(post.id)
        val updatedPost = CreatorRepository.contentItems.value.find { it.id == post.id }
        assertNotNull(updatedPost)
        assertEquals(!initialLiked, updatedPost?.isLiked)
        assertEquals(if (initialLiked) initialLikes - 1 else initialLikes + 1, updatedPost?.likesCount)

        // Toggle Bookmark / Save
        val initialSaved = post.isSaved
        CreatorRepository.toggleBookmark(post.id)
        val savedPost = CreatorRepository.contentItems.value.find { it.id == post.id }
        assertEquals(!initialSaved, savedPost?.isSaved)
    }

    @Test
    fun testDigitalProductPurchaseAndRights() {
        val products = CreatorRepository.products.value
        assertTrue(products.isNotEmpty())

        val targetProd = products.first()
        CreatorRepository.purchaseProduct(targetProd.id)

        val updatedProd = CreatorRepository.products.value.find { it.id == targetProd.id }
        assertTrue("Product should be marked as purchased", updatedProd?.isPurchased == true)

        // DPR Rights update
        val updatedConfig = ProductRightsConfig(
            productId = targetProd.id,
            productTitle = targetProd.title,
            certificateId = targetProd.rightsRecord.id,
            registeredOwner = targetProd.creatorName,
            allowCommercialUse = false,
            allowModification = false,
            allowAiTraining = false,
            licenseType = "Restricted Personal License"
        )
        CreatorRepository.updateProductRights(updatedConfig)

        val verifiedProd = CreatorRepository.products.value.find { it.id == targetProd.id }
        assertEquals("Restricted Personal License", verifiedProd?.rightsRecord?.licenseName)
        assertFalse(verifiedProd?.rightsRecord?.allowCommercialUse ?: true)
    }

    @Test
    fun testMarketplaceListingUpdate() {
        val products = CreatorRepository.products.value
        val targetProd = products.first()

        val newListing = MarketplaceListing(
            productId = targetProd.id,
            productTitle = targetProd.title,
            isPublished = true,
            listingPrice = 49.00,
            discoverableInSomulecoGlobal = true
        )
        CreatorRepository.updateMarketplaceListing(newListing)

        val updated = CreatorRepository.products.value.find { it.id == targetProd.id }
        assertEquals(49.00, updated?.priceAmount ?: 0.0, 0.01)
        assertTrue(updated?.isMarketplaceListed == true)
    }

    @Test
    fun testSubscriptionTierCreation() {
        val initialPlansCount = CreatorRepository.subscriptionPlan.value.tiers.size
        val newTier = CreatorSubscriptionPlanItem(
            id = "tier_test_123",
            name = "VIP Masterclass Circle",
            monthlyPrice = 49.0,
            benefits = listOf("Exclusive Livestreams", "1-on-1 Reviews")
        )
        CreatorRepository.createSubscriptionPlan(newTier)

        val updatedPlans = CreatorRepository.subscriptionPlan.value.tiers
        assertEquals(initialPlansCount + 1, updatedPlans.size)
        assertTrue(updatedPlans.any { it.name == "VIP Masterclass Circle" })
    }
}
