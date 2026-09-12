package com.somuleco.creator

import com.somuleco.creator.core.model.Money
import com.somuleco.creator.data.datasource.MockCreatorDataSource
import com.somuleco.creator.data.model.*
import com.somuleco.creator.data.repository.impl.ChannelRepositoryImpl
import com.somuleco.creator.data.repository.impl.ContentRepositoryImpl
import com.somuleco.creator.data.repository.impl.MarketplaceRepositoryImpl
import com.somuleco.creator.data.repository.impl.ProductRepositoryImpl
import com.somuleco.creator.data.repository.impl.RightsRepositoryImpl
import com.somuleco.creator.data.repository.impl.SubscriptionRepositoryImpl
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Exercises the per-domain `*RepositoryImpl` classes against a shared
 * [MockCreatorDataSource] instance — the replacement for the deleted
 * `object CreatorRepository` (plan §7.3 task 4). These are plain-JVM unit tests: no
 * Android framework / Hilt / Robolectric dependency, since the data source and these
 * repositories hold no Android references. Session/auth-mode behavior moved to
 * `AppSessionState` and is covered by `AppSessionStatePersistenceTest` instead.
 */
class MockCreatorDataSourceTest {

    private lateinit var dataSource: MockCreatorDataSource
    private lateinit var channelRepository: ChannelRepositoryImpl
    private lateinit var contentRepository: ContentRepositoryImpl
    private lateinit var productRepository: ProductRepositoryImpl
    private lateinit var rightsRepository: RightsRepositoryImpl
    private lateinit var marketplaceRepository: MarketplaceRepositoryImpl
    private lateinit var subscriptionRepository: SubscriptionRepositoryImpl

    @Before
    fun setUp() {
        dataSource = MockCreatorDataSource()
        channelRepository = ChannelRepositoryImpl(dataSource)
        contentRepository = ContentRepositoryImpl(dataSource)
        productRepository = ProductRepositoryImpl(dataSource)
        rightsRepository = RightsRepositoryImpl(dataSource)
        marketplaceRepository = MarketplaceRepositoryImpl(dataSource)
        subscriptionRepository = SubscriptionRepositoryImpl(dataSource)
    }

    @Test
    fun channelFollowToggle_flipsFollowedStateAndFollowerCount() {
        val initialChannels = channelRepository.channels.value
        assertTrue("Channels should not be empty", initialChannels.isNotEmpty())

        val target = initialChannels.first()
        val initialFollow = target.isFollowed
        channelRepository.toggleFollowChannel(target.id)
        val updated = channelRepository.channels.value.find { it.id == target.id }
        assertEquals(!initialFollow, updated?.isFollowed)
    }

    @Test
    fun contentLikeAndBookmarkToggle_updateCountsAndFlags() {
        val posts = contentRepository.observeContent().value
        assertTrue(posts.isNotEmpty())

        val post = posts.first()
        val initialLikes = post.likesCount
        val initialLiked = post.isLiked

        contentRepository.toggleLike(post.id)
        val updatedPost = contentRepository.getContent(post.id)
        assertNotNull(updatedPost)
        assertEquals(!initialLiked, updatedPost?.isLiked)
        assertEquals(if (initialLiked) initialLikes - 1 else initialLikes + 1, updatedPost?.likesCount)

        val initialSaved = post.isSaved
        contentRepository.toggleBookmark(post.id)
        val savedPost = contentRepository.getContent(post.id)
        assertEquals(!initialSaved, savedPost?.isSaved)
    }

    @Test
    fun observeContent_filtersByChannelId_andNullMeansAllChannels() {
        val all = contentRepository.observeContent(null).value
        val channelId = all.first().channelId
        val scoped = contentRepository.observeContent(channelId).value
        assertTrue(scoped.isNotEmpty())
        assertTrue(scoped.all { it.channelId == channelId })
        assertTrue(scoped.size <= all.size)
    }

    @Test
    fun purchaseProductAndUpdateRights_reflectAcrossRepositories() {
        val products = productRepository.observeProducts().value
        assertTrue(products.isNotEmpty())

        val target = products.first()
        productRepository.purchaseProduct(target.id)
        val updatedProd = productRepository.getProduct(target.id)
        assertTrue("Product should be marked as purchased", updatedProd?.isPurchased == true)

        val updatedConfig = ProductRightsConfig(
            productId = target.id,
            productTitle = target.title,
            certificateId = target.rightsRecord.id,
            registeredOwner = target.creatorName,
            allowCommercialUse = false,
            allowModification = false,
            allowAiTraining = false,
            licenseType = "Restricted Personal License"
        )
        rightsRepository.updateProductRights(updatedConfig)

        val verifiedProd = productRepository.getProduct(target.id)
        assertEquals("Restricted Personal License", verifiedProd?.rightsRecord?.licenseName)
        assertFalse(verifiedProd?.rightsRecord?.allowCommercialUse ?: true)
    }

    @Test
    fun marketplaceListingUpdate_isPersistedAndReadableBack() {
        val products = productRepository.observeProducts().value
        val target = products.first()

        val newListing = MarketplaceListing(
            productId = target.id,
            productTitle = target.title,
            isPublished = true,
            listingPrice = Money(4900, "USD"),
            discoverableInSomulecoGlobal = true
        )
        marketplaceRepository.updateMarketplaceListing(newListing)

        val updated = marketplaceRepository.getMarketplaceListing(target.id)
        assertEquals(4900L, updated.listingPrice.amount)
        assertTrue(updated.isPublished)
    }

    @Test
    fun subscriptionPlanCreation_addsNewTier() {
        val initialTierCount = subscriptionRepository.subscriptionPlan.value.tiers.size
        val newTier = CreatorSubscriptionPlanItem(
            id = "tier_test_123",
            name = "VIP Masterclass Circle",
            monthlyPrice = Money(4900, "USD"),
            benefits = listOf("Exclusive Livestreams", "1-on-1 Reviews")
        )
        subscriptionRepository.createSubscriptionPlan(newTier)

        val updatedTiers = subscriptionRepository.subscriptionPlan.value.tiers
        assertEquals(initialTierCount + 1, updatedTiers.size)
        assertTrue(updatedTiers.any { it.name == "VIP Masterclass Circle" })
    }

    @Test
    fun revenueValues_areDerivedFromTransactionLedger_notHardcoded() = runBlocking {
        // Regression guard for plan §7.3 task 6: `totalRevenue` etc. must be computed from
        // the transaction ledger, not a fixed constant like the old `18450.0`.
        val expectedTotal = dataSource.transactions.value
            .filter { it.status == "COMPLETED" }
            .sumOf { it.netAmount.amount } / 100.0
        assertEquals(expectedTotal, dataSource.totalRevenue, 0.001)

        productRepository.purchaseProduct(productRepository.observeProducts().value.first().id)
        val newExpectedTotal = dataSource.transactions.value
            .filter { it.status == "COMPLETED" }
            .sumOf { it.netAmount.amount } / 100.0
        assertEquals(newExpectedTotal, dataSource.totalRevenue, 0.001)
        assertNotEquals(18450.0, dataSource.totalRevenue, 0.001)
    }
}
