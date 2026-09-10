package com.somuleco.creator.data.repository

import com.somuleco.creator.core.model.Money
import com.somuleco.creator.data.model.*
import com.somuleco.creator.data.repository.interfaces.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

object CreatorRepository :
    AuthRepository,
    ChannelRepository,
    ContentRepository,
    ProductRepository,
    RightsRepository,
    MarketplaceRepository,
    SubscriptionRepository,
    RevenueRepository,
    AudienceRepository,
    MediaRepository,
    CreatorAIRepository,
    NotificationRepository,
    SettingsRepository,
    CreatorProfileRepository {

    // -------------------------------------------------------------------------
    // Auth & Session State
    // -------------------------------------------------------------------------
    private val _currentUser = MutableStateFlow(
        UserReference(
            id = "usr_elena",
            displayName = "Elena Rostova",
            username = "elenarostova",
            email = "elena@rostovaphoto.com",
            isCreator = true
        )
    )
    override val currentUser: StateFlow<UserReference> = _currentUser.asStateFlow()

    private val _authState = MutableStateFlow<AuthState>(
        AuthState.Authenticated(
            user = _currentUser.value,
            isCreator = true,
            hasCompletedOnboarding = true
        )
    )
    override val authState: StateFlow<AuthState> = _authState.asStateFlow()

    // Mode Switcher: True = Creator Mode, False = Consumer Mode
    private val _isCreatorMode = MutableStateFlow(true)
    override val isCreatorMode: StateFlow<Boolean> = _isCreatorMode.asStateFlow()

    // -------------------------------------------------------------------------
    // Creator Account & Profile
    // -------------------------------------------------------------------------
    private val _creatorAccount = MutableStateFlow(
        CreatorAccount(
            id = "acc_elena_01",
            userId = "usr_elena",
            status = CreatorAccountStatus.ACTIVE,
            creatorType = "Visual Arts & Photography",
            primaryCategory = "Photography & Lighting",
            defaultChannelId = "ch_1",
            onboardingCompleted = true
        )
    )
    override val creatorAccount: StateFlow<CreatorAccount> = _creatorAccount.asStateFlow()

    private val _creatorProfile = MutableStateFlow(
        CreatorProfile(
            id = "prof_elena",
            creatorAccountId = "acc_elena_01",
            handle = "@elenarostova",
            displayName = "Elena Rostova",
            bio = "Visual storyteller, educator & photographer. Helping creators craft authentic visual narratives and sustainable creative businesses.",
            headline = "Natural Light & Editorial Photography Educator",
            followerCount = 14280,
            subscriberCount = 920
        )
    )
    override val creatorProfile: StateFlow<CreatorProfile> = _creatorProfile.asStateFlow()

    // -------------------------------------------------------------------------
    // Channel Context & Channels
    // -------------------------------------------------------------------------
    private val _selectedChannelId = MutableStateFlow<String?>(null)
    override val selectedChannelId: StateFlow<String?> = _selectedChannelId.asStateFlow()

    private val _channels = MutableStateFlow(
        listOf(
            Channel(
                id = "ch_1",
                creatorAccountId = "acc_elena_01",
                name = "Photography Masterclass",
                slug = "photography-masterclass",
                handle = "@elena/masterclass",
                description = "Deep dive into natural light portraits, color science, and client workflow systems.",
                category = "Photography",
                followersCount = 8940,
                subscribersCount = 610,
                contentCount = 48,
                isDefault = true,
                iconEmoji = "📸",
                primaryColorHex = 0xFF2563EB,
                isFollowed = true
            ),
            Channel(
                id = "ch_2",
                creatorAccountId = "acc_elena_01",
                name = "Beginner Photography",
                slug = "beginner-photography",
                handle = "@elena/beginners",
                description = "Foundational concepts, composition breakdowns, and gear guides without the jargon.",
                category = "Education",
                followersCount = 4220,
                subscribersCount = 240,
                contentCount = 29,
                iconEmoji = "🎯",
                primaryColorHex = 0xFF7C3AED,
                isFollowed = true
            ),
            Channel(
                id = "ch_3",
                creatorAccountId = "acc_elena_01",
                name = "Behind the Scenes & Studio",
                slug = "behind-the-scenes",
                handle = "@elena/bts",
                description = "Unfiltered studio sessions, creative struggles, business insights, and gear experiments.",
                category = "Lifestyle & Studio",
                followersCount = 2120,
                subscribersCount = 90,
                contentCount = 17,
                iconEmoji = "🎬",
                primaryColorHex = 0xFFEC4899,
                isFollowed = false
            )
        )
    )
    override val channels: StateFlow<List<Channel>> = _channels.asStateFlow()

    // -------------------------------------------------------------------------
    // Content Items & Preservation Drafts
    // -------------------------------------------------------------------------
    override var draftContentTitle: String = ""
    override var draftContentBody: String = ""
    override var draftContentType: ContentType = ContentType.ARTICLE
    override var draftContentAccessType: AccessType = AccessType.PUBLIC

    private val _contentItems = MutableStateFlow(
        listOf(
            ContentItem(
                id = "post_1",
                creatorAccountId = "acc_elena_01",
                channelId = "ch_1",
                channelName = "Photography Masterclass",
                contentType = ContentType.ARTICLE,
                title = "The 3 Golden Rules of Golden Hour Composition",
                summary = "Why standard framing rules break down when shooting directly into backlight and how to rescue dynamic range.",
                body = "When working with late afternoon backlight, traditional metering frequently crushes shadows or blows out subject skin tones. In this breakdown, we explore: 1) Negative space framing against high-contrast sky, 2) Using subtle ambient bounce from pavement or sandstone, and 3) Exposing for skin highlight preservation before post-processing in Lightroom.\n\nKey takeaway: Never position your key subject directly in front of the sun without a secondary reflective surface or intentional silhouette motive.",
                mediaDuration = "6 min read",
                coverEmoji = "🌅",
                status = "PUBLISHED",
                accessType = AccessType.PUBLIC,
                publishedDate = "2 hours ago",
                likesCount = 348,
                commentsCount = 42,
                isLiked = true,
                isSaved = true,
                comments = listOf(
                    ContentComment("c_1", "Marcus Vance", "🎧", "The rim light tip completely changed my setup today Elena!", "1 hour ago", 8),
                    ContentComment("c_2", "Sarah Lin", "🎨", "Do you shoot this with spot metering or center-weighted?", "45 mins ago", 3)
                )
            ),
            ContentItem(
                id = "post_2",
                creatorAccountId = "acc_elena_01",
                channelId = "ch_1",
                channelName = "Photography Masterclass",
                contentType = ContentType.VIDEO_POST,
                title = "Live Studio Lighting Breakdown: 1-Light Editorial Portraits",
                summary = "Watch full 42-minute live studio walkthrough with lighting ratios and tethered capture previews.",
                body = "Full subscriber masterclass on single-source Octabox placement for cinematic contrast. Includes BTS camera angles, fill card adjustments, and color gel experiments.",
                mediaDuration = "42:15",
                coverEmoji = "💡",
                status = "PUBLISHED",
                accessType = AccessType.PAID_SUBSCRIBERS,
                publishedDate = "Yesterday",
                likesCount = 512,
                commentsCount = 68,
                isUnlocked = true,
                isSaved = false
            ),
            ContentItem(
                id = "post_3",
                creatorAccountId = "acc_elena_01",
                channelId = "ch_2",
                channelName = "Beginner Photography",
                contentType = ContentType.TEXT_POST,
                title = "Stop Buying Lenses Until You Understand Focal Length Compression",
                summary = "Quick visual guide: How 35mm vs 50mm vs 85mm alters face geometry and environment separation.",
                body = "Focal length isn't just about 'getting closer'. It dictates visual perspective and spatial compression between your subject and background.\n\n• 35mm: Environmental context, intimate feeling.\n• 50mm: True to human eye perception.\n• 85mm: Flattering facial compression, creamy bokeh separation.",
                mediaDuration = "Quick Tip",
                coverEmoji = "🔍",
                status = "PUBLISHED",
                accessType = AccessType.PUBLIC,
                publishedDate = "3 days ago",
                likesCount = 890,
                commentsCount = 114,
                isSaved = true
            ),
            ContentItem(
                id = "post_4",
                creatorAccountId = "acc_elena_01",
                channelId = "ch_3",
                channelName = "Behind the Scenes & Studio",
                contentType = ContentType.IMAGE_POST,
                title = "Raw Studio Diaries: Preparing the European Gallery Exhibition",
                summary = "Print proofs, frame selections, and exhibition curation notes for the upcoming gallery.",
                body = "A preview of the large-format matte cotton rag test prints for the Zurich gallery show. We are testing three weight stocks to check ink depth.",
                coverEmoji = "🖼️",
                status = "PUBLISHED",
                accessType = AccessType.MEMBERSHIP_TIER,
                publishedDate = "5 days ago",
                likesCount = 230,
                commentsCount = 19,
                isUnlocked = true,
                isSaved = false
            )
        )
    )
    override val contentItems: StateFlow<List<ContentItem>> = _contentItems.asStateFlow()

    // -------------------------------------------------------------------------
    // Products & Product Drafts
    // -------------------------------------------------------------------------
    override var draftProductTitle: String = ""
    override var draftProductDescription: String = ""
    override var draftProductPrice: String = "29.00"
    override var draftProductCategory: String = "Photography & Lighting"
    override var draftProductType: ProductType = ProductType.DIGITAL_PRODUCT
    override var draftProductDeliverables: String = "PDF Guide, Preset Bundle"

    private val _products = MutableStateFlow(
        listOf(
            CreatorProduct(
                id = "prod_1",
                creatorAccountId = "acc_elena_01",
                channelId = "ch_1",
                channelName = "Photography Masterclass",
                title = "Complete Wedding & Portrait Business Guide",
                slug = "wedding-portrait-business-guide",
                shortDescription = "Comprehensive 140-page roadmap for client booking, contract systems, and pricing strategy.",
                fullDescription = "The battle-tested playbook used to scale portrait bookings to six figures. Includes client questionnaire templates, email sequences, pricing calculator spreadsheet, and legal contract guidance verified by Digital Product Rights.",
                price = Money(4900, "USD"),
                productType = ProductType.DIGITAL_PRODUCT,
                fileFormat = "PDF & Notion Template",
                fileSizeMb = 34.2,
                salesCount = 412,
                revenueTotal = 20188.0,
                iconEmoji = "📘",
                rightsRecord = RightsRecord(
                    id = "r_101",
                    registeredAt = "2026-03-15",
                    licenseName = "Somuleco Commercial Pro License",
                    allowDownload = true,
                    allowRedistribution = false,
                    allowCommercialUse = true,
                    allowModification = true,
                    allowAiTraining = false,
                    protectionStatus = "Protected & Verified in DPR"
                ),
                isPurchased = true
            ),
            CreatorProduct(
                id = "prod_2",
                creatorAccountId = "acc_elena_01",
                channelId = "ch_1",
                channelName = "Photography Masterclass",
                title = "Cinematic Daylight Lightroom & Capture One Presets",
                slug = "cinematic-daylight-presets",
                shortDescription = "18 refined color profiles crafted for natural skin tones and organic golden warmth.",
                fullDescription = "Hand-tailored color gradings crafted across 200+ commercial shoots. Includes presets for Lightroom Classic, CC Mobile, and Capture One Styles with custom curve adjustment guides.",
                price = Money(2900, "USD"),
                productType = ProductType.CREATIVE_ASSETS,
                fileFormat = "XMP, DNG & COSTYLE",
                fileSizeMb = 14.8,
                salesCount = 890,
                revenueTotal = 25810.0,
                iconEmoji = "🎨",
                rightsRecord = RightsRecord(
                    id = "r_102",
                    registeredAt = "2026-04-10",
                    licenseName = "Standard Creator Asset License",
                    allowDownload = true,
                    allowRedistribution = false,
                    allowCommercialUse = true,
                    allowModification = false,
                    allowAiTraining = false,
                    protectionStatus = "Protected in DPR"
                ),
                isPurchased = false
            ),
            CreatorProduct(
                id = "prod_3",
                creatorAccountId = "acc_elena_01",
                channelId = "ch_2",
                channelName = "Beginner Photography",
                title = "Camera Settings Cheat Sheets & Pocket Cards",
                slug = "camera-settings-pocket-cards",
                shortDescription = "Printable & phone-ready field reference cards for exposure triangle and lighting setups.",
                fullDescription = "Pocket-friendly visual charts covering Aperture, Shutter Speed, ISO, and metering modes for sports, low-light, portraits, and landscapes.",
                price = Money(1400, "USD"),
                productType = ProductType.DIGITAL_PRODUCT,
                fileFormat = "PDF & Mobile Wallpapers",
                fileSizeMb = 8.5,
                salesCount = 630,
                revenueTotal = 8820.0,
                iconEmoji = "📑",
                rightsRecord = RightsRecord(
                    id = "r_103",
                    registeredAt = "2026-05-01",
                    licenseName = "Personal Learning License",
                    allowDownload = true,
                    allowRedistribution = false,
                    allowCommercialUse = false,
                    allowModification = false,
                    allowAiTraining = false,
                    protectionStatus = "Protected in DPR"
                ),
                isPurchased = false
            )
        )
    )
    override val products: StateFlow<List<CreatorProduct>> = _products.asStateFlow()

    // -------------------------------------------------------------------------
    // Digital Product Rights (DPR)
    // -------------------------------------------------------------------------
    private val _rightsRecords = MutableStateFlow(
        listOf(
            RightsRecord("r_101", "2026-03-15", "Somuleco Commercial Pro License", allowDownload = true, allowCommercialUse = true),
            RightsRecord("r_102", "2026-04-10", "Standard Creator Asset License", allowDownload = true, allowCommercialUse = true),
            RightsRecord("r_103", "2026-05-01", "Personal Learning License", allowDownload = true, allowCommercialUse = false)
        )
    )
    override val rightsRecords: StateFlow<List<RightsRecord>> = _rightsRecords.asStateFlow()

    private val _productRightsMap = MutableStateFlow<Map<String, ProductRightsConfig>>(
        mapOf(
            "prod_1" to ProductRightsConfig(
                productId = "prod_1",
                productTitle = "Complete Wedding & Portrait Business Guide",
                certificateId = "DPR-CERT-8842-PROD1",
                registeredOwner = "Elena Rostova (Somuleco Passport #9921)",
                allowCommercialUse = true,
                allowRedistribution = false,
                allowModification = true,
                licenseType = "Somuleco Commercial Pro License v2.4",
                registrationTimestamp = "2026-03-15T14:30:00Z"
            ),
            "prod_2" to ProductRightsConfig(
                productId = "prod_2",
                productTitle = "Cinematic Daylight Lightroom & Capture One Presets",
                certificateId = "DPR-CERT-9104-PROD2",
                registeredOwner = "Elena Rostova (Somuleco Passport #9921)",
                allowCommercialUse = true,
                allowRedistribution = false,
                allowModification = false,
                licenseType = "Standard Creator Asset License v1.2",
                registrationTimestamp = "2026-04-10T09:15:00Z"
            ),
            "prod_3" to ProductRightsConfig(
                productId = "prod_3",
                productTitle = "Camera Settings Cheat Sheets & Pocket Cards",
                certificateId = "DPR-CERT-9452-PROD3",
                registeredOwner = "Elena Rostova (Somuleco Passport #9921)",
                allowCommercialUse = false,
                allowRedistribution = false,
                allowModification = false,
                licenseType = "Personal Learning Non-Commercial License",
                registrationTimestamp = "2026-05-01T16:45:00Z"
            )
        )
    )

    override fun getProductRights(productId: String): ProductRightsConfig? {
        return _productRightsMap.value[productId] ?: _products.value.find { it.id == productId }?.let {
            ProductRightsConfig(
                productId = it.id,
                productTitle = it.title,
                certificateId = "DPR-CERT-${it.id.uppercase()}",
                registeredOwner = _creatorProfile.value.displayName
            )
        }
    }

    override fun updateProductRights(config: ProductRightsConfig) {
        _productRightsMap.value = _productRightsMap.value + (config.productId to config)
        _products.value = _products.value.map { prod ->
            if (prod.id == config.productId) {
                prod.copy(
                    rightsRecord = prod.rightsRecord.copy(
                        licenseName = config.licenseType,
                        allowCommercialUse = config.allowCommercialUse,
                        allowModification = config.allowModification,
                        allowAiTraining = config.allowAiTraining
                    )
                )
            } else prod
        }
    }

    // -------------------------------------------------------------------------
    // Marketplace Publishing
    // -------------------------------------------------------------------------
    private val _marketplaceMap = MutableStateFlow<Map<String, MarketplaceListing>>(
        mapOf(
            "prod_1" to MarketplaceListing("prod_1", "Complete Wedding & Portrait Business Guide", true, Money(4900, "USD")),
            "prod_2" to MarketplaceListing("prod_2", "Cinematic Daylight Lightroom & Capture One Presets", true, Money(2900, "USD")),
            "prod_3" to MarketplaceListing("prod_3", "Camera Settings Cheat Sheets & Pocket Cards", false, Money(1400, "USD"))
        )
    )

    override fun getMarketplaceListing(productId: String): MarketplaceListing {
        return _marketplaceMap.value[productId] ?: run {
            val prod = getProduct(productId)
            MarketplaceListing(
                productId = productId,
                productTitle = prod?.title ?: "Digital Product",
                listingPrice = prod?.price ?: Money(2900, "USD")
            )
        }
    }

    override fun updateMarketplaceListing(listing: MarketplaceListing) {
        _marketplaceMap.value = _marketplaceMap.value + (listing.productId to listing)
    }

    override fun togglePublishMarketplace(productId: String): MarketplaceListing {
        val current = getMarketplaceListing(productId)
        val updated = current.copy(
            isPublished = !current.isPublished,
            lastSyncedAt = "Just now"
        )
        updateMarketplaceListing(updated)
        return updated
    }

    // -------------------------------------------------------------------------
    // Subscriptions & Plans
    // -------------------------------------------------------------------------
    private val _subscriptionPlan = MutableStateFlow(
        SubscriptionPlan(
            id = "plan_elena",
            creatorAccountId = "acc_elena_01",
            channelId = "ch_1",
            name = "Elena's Creator Circle",
            description = "Support ongoing creative education, unlock deep-dive masterclasses, raw session files, and private critiques.",
            tiers = listOf(
                MembershipTier(
                    id = "tier_free",
                    name = "Free Community Follower",
                    price = Money(0, "USD"),
                    benefits = listOf("Weekly educational newsletter", "Public feed posts & tips", "Community discussions"),
                    subscriberCount = 11560,
                    badgeColorHex = 0xFF64748B
                ),
                MembershipTier(
                    id = "tier_insider",
                    name = "Creator Insider",
                    price = Money(900, "USD"),
                    benefits = listOf("Subscriber-only video masterclasses", "Downloadable RAW files for practice", "Monthly live Q&A session", "20% discount on all digital store products"),
                    subscriberCount = 710,
                    isFeatured = true,
                    badgeColorHex = 0xFF7C3AED
                ),
                MembershipTier(
                    id = "tier_pro",
                    name = "Studio Pro Mentorship",
                    price = Money(3900, "USD"),
                    benefits = listOf("All Insider tier benefits", "Quarterly 1-on-1 portfolio video critique", "Direct Creator messaging access", "Free access to all new preset releases"),
                    subscriberCount = 210,
                    badgeColorHex = 0xFF2563EB
                )
            ),
            isSubscribed = true,
            activeTierId = "tier_insider"
        )
    )
    override val subscriptionPlan: StateFlow<SubscriptionPlan> = _subscriptionPlan.asStateFlow()

    private val _creatorSubscriptionPlans = MutableStateFlow(
        listOf(
            CreatorSubscriptionPlanItem(
                id = "plan_tier_free",
                name = "Free Community Access",
                monthlyPrice = Money(0, "USD"),
                isFree = true,
                benefits = listOf("Public channel posts", "Community questions", "Weekly highlights"),
                subscriberCount = 11560,
                badgeColorHex = 0xFF64748B
            ),
            CreatorSubscriptionPlanItem(
                id = "plan_tier_insider",
                name = "Creator Insider Tier",
                monthlyPrice = Money(900, "USD"),
                benefits = listOf("Full masterclasses", "Downloadable practice files", "Monthly AMA live", "20% Store discount"),
                subscriberCount = 710,
                isActive = true,
                badgeColorHex = 0xFF7C3AED
            ),
            CreatorSubscriptionPlanItem(
                id = "plan_tier_pro",
                name = "Studio Pro Mentorship",
                monthlyPrice = Money(3900, "USD"),
                benefits = listOf("All Insider benefits", "Quarterly portfolio video review", "Direct message priority", "Free new releases"),
                subscriberCount = 210,
                isActive = true,
                badgeColorHex = 0xFF2563EB
            )
        )
    )
    override val creatorSubscriptionPlans: StateFlow<List<CreatorSubscriptionPlanItem>> = _creatorSubscriptionPlans.asStateFlow()

    private val _userSubscriptions = MutableStateFlow(
        listOf(
            UserSubscription(
                id = "usub_1",
                creatorId = "usr_elena",
                creatorName = "Elena Rostova",
                creatorHandle = "@elenarostova",
                avatarEmoji = "📸",
                tierName = "Creator Insider",
                monthlyPrice = Money(900, "USD"),
                nextBillingDate = "Oct 01, 2026",
                status = "ACTIVE",
                benefitsSummary = "Masterclass tutorials, RAW practice downloads & community perks"
            ),
            UserSubscription(
                id = "usub_2",
                creatorId = "usr_marcus",
                creatorName = "Marcus Vance",
                creatorHandle = "@marcusvance",
                avatarEmoji = "🎧",
                tierName = "Sound Designer Pro",
                monthlyPrice = Money(1400, "USD"),
                nextBillingDate = "Oct 12, 2026",
                status = "ACTIVE",
                benefitsSummary = "Monthly audio stems, Ableton templates & sound design presets"
            )
        )
    )
    override val userSubscriptions: StateFlow<List<UserSubscription>> = _userSubscriptions.asStateFlow()

    override fun createSubscriptionPlan(plan: CreatorSubscriptionPlanItem) {
        _creatorSubscriptionPlans.value = _creatorSubscriptionPlans.value + plan
        val newTier = MembershipTier(
            id = plan.id,
            name = plan.name,
            price = plan.monthlyPrice,
            description = plan.benefits.joinToString(", ").ifBlank { "Member exclusive benefits" },
            perks = plan.benefits
        )
        _subscriptionPlan.value = _subscriptionPlan.value.copy(
            tiers = _subscriptionPlan.value.tiers + newTier
        )
    }

    override fun updateSubscriptionPlan(plan: CreatorSubscriptionPlanItem) {
        _creatorSubscriptionPlans.value = _creatorSubscriptionPlans.value.map {
            if (it.id == plan.id) plan else it
        }
    }

    override fun cancelUserSubscription(id: String) {
        _userSubscriptions.value = _userSubscriptions.value.filterNot { it.id == id }
    }

    private val _subscribers = MutableStateFlow(
        listOf(
            SubscriberMember("sub_1", "Marcus Vance", "@marcusvance", "🎧", "Studio Pro Mentorship", 39.0, "Joined 4 months ago"),
            SubscriberMember("sub_2", "Aria Chen", "@ariachen", "💻", "Creator Insider", 9.0, "Joined 3 months ago"),
            SubscriberMember("sub_3", "Liam O'Connor", "@liamphoto", "📷", "Creator Insider", 9.0, "Joined 2 months ago"),
            SubscriberMember("sub_4", "Sophia Rossi", "@sophiar", "🌸", "Studio Pro Mentorship", 39.0, "Joined 1 month ago"),
            SubscriberMember("sub_5", "David K.", "@davidk", "🎨", "Creator Insider", 9.0, "Joined 2 weeks ago")
        )
    )
    val subscribers: StateFlow<List<SubscriberMember>> = _subscribers.asStateFlow()
    override val subscriberMembers: StateFlow<List<SubscriberMember>> = _subscribers.asStateFlow()

    // -------------------------------------------------------------------------
    // Revenue & Transactions
    // -------------------------------------------------------------------------
    override val totalRevenue: Double get() = 18450.0
    override val availableBalance: Double get() = 7820.0
    override val pendingBalance: Double get() = 1430.0
    override val monthlyRecurringRevenue: Double get() = 8190.0
    override val productSalesRevenue: Double get() = 10260.0

    private val _transactions = MutableStateFlow(
        listOf(
            TransactionItem("tx_1", "Today, 14:20", "Liam O'Connor", "Wedding & Portrait Business Guide", Money(4900, "USD"), Money(245, "USD"), Money(4655, "USD"), status = "COMPLETED", source = "Digital Product"),
            TransactionItem("tx_2", "Today, 11:05", "Sophia Rossi", "Studio Pro Mentorship (Renewal)", Money(3900, "USD"), Money(195, "USD"), Money(3705, "USD"), status = "COMPLETED", source = "Subscription"),
            TransactionItem("tx_3", "Yesterday", "Hanna Schmidt", "Cinematic Daylight Lightroom Presets", Money(2900, "USD"), Money(145, "USD"), Money(2755, "USD"), status = "COMPLETED", source = "Digital Product"),
            TransactionItem("tx_4", "Sep 05", "Kenji Sato", "Camera Settings Pocket Cards", Money(1400, "USD"), Money(70, "USD"), Money(1330, "USD"), status = "COMPLETED", source = "Digital Product"),
            TransactionItem("tx_5", "Sep 04", "Marcus Vance", "Creator Insider (Renewal)", Money(900, "USD"), Money(45, "USD"), Money(855, "USD"), status = "COMPLETED", source = "Subscription")
        )
    )
    override val transactions: StateFlow<List<RevenueTransaction>> = _transactions.asStateFlow()

    override fun getTransaction(id: String): RevenueTransaction? {
        return _transactions.value.find { it.id == id }
    }

    // -------------------------------------------------------------------------
    // Audience & Relationships
    // -------------------------------------------------------------------------
    private val _analytics = MutableStateFlow(AnalyticsOverview())
    val analytics: StateFlow<AnalyticsOverview> = _analytics.asStateFlow()

    private val _topContent = MutableStateFlow(
        listOf(
            TopContentPerformance("tc_1", "The 3 Golden Rules of Golden Hour", "Photography Masterclass", "34.8K views", "6.2% follow rate", 480, "Article"),
            TopContentPerformance("tc_2", "Stop Buying Lenses Until You Understand Focal Length", "Beginner Photography", "58.2K views", "8.9% follow rate", 820, "Quick Tip"),
            TopContentPerformance("tc_3", "1-Light Editorial Portrait Walkthrough", "Photography Masterclass", "22.1K views", "14.1% subscriber conversion", 140, "Video")
        )
    )
    val topContent: StateFlow<List<TopContentPerformance>> = _topContent.asStateFlow()

    private val _audienceMetrics = MutableStateFlow(AudienceMetrics())
    override val audienceMetrics: StateFlow<AudienceMetrics> = _audienceMetrics.asStateFlow()

    private val _topReferrers = MutableStateFlow(
        listOf(
            TrafficSource("Somuleco Discover", "44%", "8,120 visitors", "🌐"),
            TrafficSource("Creator Profile Link", "28%", "5,180 visitors", "🔗"),
            TrafficSource("YouTube Video Description", "18%", "3,320 visitors", "▶️"),
            TrafficSource("Direct & Email Newsletter", "10%", "1,840 visitors", "✉️")
        )
    )
    override val topReferrers: StateFlow<List<TrafficSource>> = _topReferrers.asStateFlow()

    private val _segments = MutableStateFlow(
        listOf(
            AudienceSegment("seg_1", "Highly Engaged Learners", 420, "Consistently read articles & bookmark tips", "18.2%"),
            AudienceSegment("seg_2", "Gear & Equipment Enthusiasts", 890, "Interact primarily with focal length and camera cheat sheets", "12.4%"),
            AudienceSegment("seg_3", "Active Masterclass Subscribers", 920, "Paying monthly recurring members with high retention", "94.2%"),
            AudienceSegment("seg_4", "Casual Discovery Visitors", 12480, "Free followers browsing public feed content", "4.8%")
        )
    )
    override val segments: StateFlow<List<AudienceSegment>> = _segments.asStateFlow()

    // -------------------------------------------------------------------------
    // Media Library
    // -------------------------------------------------------------------------
    private val _mediaAssets = MutableStateFlow(
        listOf(
            MediaAsset("med_1", "Golden Hour Sunburst Proof", "IMG_8921_raw.jpg", MediaType.IMAGE, "28.4 MB", uploadDate = "Today", channelName = "Photography Masterclass"),
            MediaAsset("med_2", "1-Light Studio Masterclass Reel", "studio_lighting_4k.mp4", MediaType.VIDEO, "1.4 GB", durationText = "42:15", uploadDate = "Yesterday", channelName = "Photography Masterclass"),
            MediaAsset("med_3", "Portrait Contract Template", "wedding_client_contract_v3.pdf", MediaType.DOCUMENT, "2.1 MB", uploadDate = "Sep 02", channelName = "Beginner Photography"),
            MediaAsset("med_4", "Studio Audio Podcast Ep 12", "creative_struggles_audio.wav", MediaType.AUDIO, "84.2 MB", durationText = "28:40", uploadDate = "Aug 29", channelName = "Behind the Scenes & Studio")
        )
    )
    override val mediaAssets: StateFlow<List<MediaAsset>> = _mediaAssets.asStateFlow()
    val mediaItems: StateFlow<List<MediaAsset>> = _mediaAssets.asStateFlow()

    override fun uploadMedia(name: String, type: MediaType, size: String, channelName: String): MediaAsset {
        val asset = MediaAsset(
            id = "med_${System.currentTimeMillis()}",
            title = name,
            fileName = name,
            type = type,
            sizeText = size,
            uploadDate = "Just now",
            channelName = channelName,
            status = "READY"
        )
        _mediaAssets.value = listOf(asset) + _mediaAssets.value
        return asset
    }

    override fun deleteMedia(id: String) {
        _mediaAssets.value = _mediaAssets.value.filterNot { it.id == id }
    }

    // -------------------------------------------------------------------------
    // Creator AI
    // -------------------------------------------------------------------------
    private val _recommendations = MutableStateFlow(
        listOf(
            CreatorRecommendation(
                id = "rec_1",
                title = "Convert High-Performing Beginner Content into a Mini-Course",
                reason = "Your 'Focal Length Compression' post generated 820 new followers this week with a 74% completion rate.",
                supportingMetric = "+37% higher viewer-to-follower ratio than your channel benchmark",
                actionLabel = "Generate Course Outline",
                actionCategory = "PRODUCT"
            ),
            CreatorRecommendation(
                id = "rec_2",
                title = "Launch a Subscriber Q&A Live Session",
                reason = "42 new Insider subscribers joined this month and have not yet attended an onboarding session.",
                supportingMetric = "Subscribers who attend a live session retain 3.2x longer",
                actionLabel = "Schedule Live Session",
                actionCategory = "COMMUNITY"
            ),
            CreatorRecommendation(
                id = "rec_3",
                title = "Register Autumn Editorial Preset Pack in Digital Product Rights",
                reason = "You have 3 unpublished preset drafts in your media library ready for monetization.",
                supportingMetric = "Preset sales peak by 48% between September and November",
                actionLabel = "Start Product Wizard",
                actionCategory = "PRODUCT"
            )
        )
    )
    val recommendations: StateFlow<List<CreatorRecommendation>> = _recommendations.asStateFlow()

    private val _aiMessages = MutableStateFlow(
        listOf(
            AIMessage(
                id = "msg_1",
                role = AIMessageRole.SYSTEM,
                content = "Welcome to Creator AI. I understand your channels, audience analytics, and Digital Product Rights status. How can I help you grow today?",
                timestamp = "10:00 AM"
            ),
            AIMessage(
                id = "msg_2",
                role = AIMessageRole.USER,
                content = "What topic should I create my next Photography Masterclass post on?",
                timestamp = "10:01 AM"
            ),
            AIMessage(
                id = "msg_3",
                role = AIMessageRole.ASSISTANT,
                content = "Based on your recent engagement metrics, your audience is asking repeatedly about handling harsh overhead midday sun. Here is an actionable post concept with high conversion potential.",
                timestamp = "10:01 AM",
                artifactTitle = "Post Draft: 4 Ways to Master Harsh Midday Sunlight",
                artifactType = "POST_DRAFT",
                artifactContent = "Title: 4 Ways to Master Harsh Midday Sunlight\n\nMost photographers pack their gear away between 11 AM and 3 PM. Here is how editorial pros use harsh overhead light instead:\n\n1. Find Deep Architectural Shadows: Use the hard cutoff line as a dramatic graphic element.\n2. Backlight + Diffuser: Turn your subject away from the sun and hold a 1-stop diffusion scrim directly above.\n3. Emphasize Specular Highlights: Don't fear high contrast—embrace deep blacks and vivid skin glints."
            )
        )
    )
    val aiMessages: StateFlow<List<AIMessage>> = _aiMessages.asStateFlow()
    override val chatMessages: StateFlow<List<AIMessage>> = _aiMessages.asStateFlow()

    override val suggestedPrompts: List<String> = listOf(
        "Analyze this week's revenue and churn risks",
        "Generate a lesson outline for Photography Masterclass",
        "Draft a commercial license for my preset bundle",
        "Suggest ideas to convert free followers to paid subscribers"
    )

    override suspend fun sendMessage(userText: String): String {
        sendAIMessage(userText)
        return _aiMessages.value.lastOrNull()?.content ?: "Response generated."
    }

    // -------------------------------------------------------------------------
    // Notifications
    // -------------------------------------------------------------------------
    private val _notifications = MutableStateFlow(
        listOf(
            NotificationItem("notif_1", "New Studio Pro Subscriber!", "Sophia Rossi subscribed to Studio Pro Mentorship ($39/mo)", "20 mins ago", NotificationType.SUBSCRIBER),
            NotificationItem("notif_2", "Product Sale Confirmed", "Liam O'Connor purchased Complete Wedding & Portrait Business Guide ($49)", "2 hours ago", NotificationType.SALE),
            NotificationItem("notif_3", "Digital Rights Registration Complete", "Rights Record #r_103 successfully registered and protected in DPR", "1 day ago", NotificationType.RIGHTS),
            NotificationItem("notif_4", "Creator AI Recommendation", "New insight available: Convert high-performing beginner content to product", "2 days ago", NotificationType.AI_RECOMMENDATION)
        )
    )
    override val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    override fun markAsRead(id: String) {
        _notifications.value = _notifications.value.map { notif ->
            if (notif.id == id) notif.copy(isRead = true) else notif
        }
    }

    override fun markAllAsRead() {
        _notifications.value = _notifications.value.map { it.copy(isRead = true) }
    }

    fun markNotificationRead(id: String) = markAsRead(id)
    fun markAllNotificationsRead() = markAllAsRead()

    // -------------------------------------------------------------------------
    // Settings
    // -------------------------------------------------------------------------
    private val _settings = MutableStateFlow(CreatorSettingsData())
    override val settings: StateFlow<CreatorSettingsData> = _settings.asStateFlow()

    override fun updateSettings(settings: CreatorSettingsData) {
        _settings.value = settings
    }

    // -------------------------------------------------------------------------
    // Integrations & Goals
    // -------------------------------------------------------------------------
    private val _integrations = MutableStateFlow(
        listOf(
            CreatorIntegration("int_1", "Somuleco Passport", "ECOSYSTEM", "Portable creator identity and reputation verification across Somuleco.", "🛡️", true),
            CreatorIntegration("int_2", "Digital Product Rights Registry", "ECOSYSTEM", "Real-time copyright and licensing registration for products and media.", "📜", true),
            CreatorIntegration("int_3", "YouTube Channel Sync", "SOCIAL", "Auto-import video catalog and track viewer conversion.", "▶️", true),
            CreatorIntegration("int_4", "Instagram Creator API", "SOCIAL", "Sync reels and stories to your Behind the Scenes channel.", "📸", false),
            CreatorIntegration("int_5", "Dropbox Cloud Storage", "STORAGE", "Sync RAW photo catalogs and project deliveries.", "📦", true)
        )
    )
    override val integrations: StateFlow<List<CreatorIntegration>> = _integrations.asStateFlow()

    private val _goals = MutableStateFlow(
        listOf(
            CreatorGoal("g_1", "Reach 1,000 Paid Subscribers", "1,000", "Dec 2026", isAchieved = false, progress = 0.84f),
            CreatorGoal("g_2", "Earn $25,000 Monthly Revenue", "$25K/mo", "Nov 2026", isAchieved = false, progress = 0.74f),
            CreatorGoal("g_3", "Publish 50 Masterclass Lessons", "50 Lessons", "Oct 2026", isAchieved = true, progress = 1.0f)
        )
    )
    override val goals: StateFlow<List<CreatorGoal>> = _goals.asStateFlow()

    override fun updateProfile(profile: CreatorProfile) {
        _creatorProfile.value = profile
    }

    override fun toggleIntegration(id: String) {
        _integrations.value = _integrations.value.map {
            if (it.id == id) it.copy(isConnected = !it.isConnected) else it
        }
    }

    // -------------------------------------------------------------------------
    // Standalone Comments
    // -------------------------------------------------------------------------
    private val _comments = MutableStateFlow(
        listOf(
            ContentComment("c_1", "Marcus Vance", "🎧", "The rim light tip completely changed my setup today Elena!", "1 hour ago", 8),
            ContentComment("c_2", "Sarah Lin", "🎨", "Do you shoot this with spot metering or center-weighted?", "45 mins ago", 3),
            ContentComment("c_3", "David Kim", "📸", "Can't wait to test this on Saturday's outdoor portrait session!", "20 mins ago", 1)
        )
    )
    val comments: StateFlow<List<ContentComment>> = _comments.asStateFlow()

    // -------------------------------------------------------------------------
    // AuthRepository Implementation
    // -------------------------------------------------------------------------
    override suspend fun login(email: String, password: String): Result<UserReference> {
        val user = _currentUser.value
        _authState.value = AuthState.Authenticated(
            user = user,
            isCreator = user.isCreator,
            hasCompletedOnboarding = true
        )
        return Result.success(user)
    }

    override suspend fun signup(name: String, email: String, password: String): Result<UserReference> {
        val newUser = UserReference(
            id = "usr_${System.currentTimeMillis()}",
            displayName = name,
            username = name.lowercase().replace(" ", "_"),
            email = email,
            isCreator = true
        )
        _currentUser.value = newUser
        _authState.value = AuthState.Authenticated(
            user = newUser,
            isCreator = true,
            hasCompletedOnboarding = false
        )
        return Result.success(newUser)
    }

    override suspend fun verifyEmail(code: String): Result<Boolean> = Result.success(true)

    override suspend fun forgotPassword(email: String): Result<Boolean> = Result.success(true)

    override suspend fun resetPassword(token: String, newPassword: String): Result<Boolean> = Result.success(true)

    override suspend fun logout() {
        _authState.value = AuthState.Unauthenticated
    }

    override fun setCreatorMode(enabled: Boolean) {
        _isCreatorMode.value = enabled
    }

    override fun toggleCreatorMode() {
        _isCreatorMode.value = !_isCreatorMode.value
    }

    override fun completeOnboarding() {
        val current = _authState.value
        if (current is AuthState.Authenticated) {
            _authState.value = current.copy(hasCompletedOnboarding = true)
        }
    }

    // -------------------------------------------------------------------------
    // ChannelRepository Implementation
    // -------------------------------------------------------------------------
    override fun selectChannel(id: String?) {
        _selectedChannelId.value = id
    }

    fun selectChannelContext(channelId: String?) = selectChannel(channelId)
    fun setSelectedChannel(channelId: String?) = selectChannel(channelId)

    override fun getChannel(id: String): Channel? {
        return _channels.value.find { it.id == id }
    }

    override fun createChannel(name: String, description: String, category: String, handle: String): Channel {
        return createChannel(name, description, category, handle, "✨")
    }

    fun createChannel(name: String, description: String, category: String, handle: String, iconEmoji: String = "✨"): Channel {
        val newChannel = Channel(
            id = "ch_${System.currentTimeMillis()}",
            creatorAccountId = _creatorAccount.value.id,
            name = name,
            slug = name.lowercase().replace(" ", "-"),
            handle = if (handle.startsWith("@")) handle else "@$handle",
            description = description,
            category = category,
            followersCount = 1,
            subscribersCount = 0,
            contentCount = 0,
            iconEmoji = iconEmoji,
            primaryColorHex = 0xFF7C3AED,
            isFollowed = true
        )
        _channels.value = _channels.value + newChannel
        return newChannel
    }

    override fun toggleFollowChannel(channelId: String) {
        followChannel(channelId)
    }

    fun followChannel(channelId: String) {
        _channels.value = _channels.value.map { ch ->
            if (ch.id == channelId) {
                val newFollow = !ch.isFollowed
                ch.copy(
                    isFollowed = newFollow,
                    followersCount = if (newFollow) ch.followersCount + 1 else ch.followersCount - 1
                )
            } else ch
        }
    }

    // -------------------------------------------------------------------------
    // ContentRepository Implementation
    // -------------------------------------------------------------------------
    override fun getContent(id: String): ContentItem? {
        return _contentItems.value.find { it.id == id }
    }

    override fun publishContent(
        title: String,
        body: String,
        type: ContentType,
        access: AccessType,
        channelId: String?,
        tags: List<String>
    ): ContentItem {
        val targetChannelId = channelId ?: _channels.value.firstOrNull()?.id ?: "ch_1"
        val summary = if (body.length > 120) body.take(120) + "..." else body
        return createContentItem(
            title = title,
            body = body,
            summary = summary,
            channelId = targetChannelId,
            contentType = type,
            accessType = access
        )
    }

    fun createContentItem(
        title: String,
        body: String,
        summary: String,
        channelId: String,
        contentType: ContentType,
        accessType: AccessType,
        coverEmoji: String = "✨"
    ): ContentItem {
        val channel = _channels.value.find { it.id == channelId } ?: _channels.value.first()
        val newItem = ContentItem(
            id = "post_${System.currentTimeMillis()}",
            creatorAccountId = _creatorAccount.value.id,
            creatorName = _creatorProfile.value.displayName,
            channelId = channel.id,
            channelName = channel.name,
            contentType = contentType,
            title = title,
            summary = summary,
            body = body,
            coverEmoji = coverEmoji,
            status = "PUBLISHED",
            accessType = accessType,
            publishedDate = "Just now",
            likesCount = 1,
            commentsCount = 0
        )
        _contentItems.value = listOf(newItem) + _contentItems.value
        _channels.value = _channels.value.map { ch ->
            if (ch.id == channel.id) ch.copy(contentCount = ch.contentCount + 1) else ch
        }
        return newItem
    }

    override fun toggleLike(contentId: String) {
        _contentItems.value = _contentItems.value.map { item ->
            if (item.id == contentId) {
                val newLiked = !item.isLiked
                item.copy(
                    isLiked = newLiked,
                    likesCount = if (newLiked) item.likesCount + 1 else item.likesCount - 1
                )
            } else item
        }
    }

    override fun toggleBookmark(contentId: String) {
        toggleSave(contentId)
    }

    fun toggleSave(contentId: String) {
        _contentItems.value = _contentItems.value.map { item ->
            if (item.id == contentId) {
                item.copy(isSaved = !item.isSaved)
            } else item
        }
    }

    override fun addComment(contentId: String, text: String) {
        val newComment = ContentComment(
            id = UUID.randomUUID().toString(),
            authorName = _currentUser.value.displayName,
            authorEmoji = "✨",
            text = text,
            timestamp = "Just now"
        )
        _contentItems.value = _contentItems.value.map { item ->
            if (item.id == contentId) {
                item.copy(
                    commentsCount = item.commentsCount + 1,
                    comments = item.comments + newComment
                )
            } else item
        }
    }

    fun addComment(text: String) {
        val newComment = ContentComment(
            id = UUID.randomUUID().toString(),
            authorName = _currentUser.value.displayName,
            authorEmoji = "✨",
            text = text,
            timestamp = "Just now"
        )
        _comments.value = _comments.value + newComment
    }

    // -------------------------------------------------------------------------
    // ProductRepository Implementation
    // -------------------------------------------------------------------------
    override fun getProduct(id: String): CreatorProduct? {
        return _products.value.find { it.id == id }
    }

    override fun createProduct(
        title: String,
        description: String,
        price: Double,
        category: String,
        channelId: String?,
        productType: ProductType,
        deliverables: List<String>,
        isDprProtected: Boolean
    ): CreatorProduct {
        val targetChannelId = channelId ?: _channels.value.firstOrNull()?.id ?: "ch_1"
        val shortDesc = if (description.length > 100) description.take(100) + "..." else description
        return createProduct(
            title = title,
            shortDescription = shortDesc,
            fullDescription = description,
            price = price,
            channelId = targetChannelId,
            productType = productType,
            fileFormat = deliverables.joinToString(", ").ifBlank { "Digital Download" },
            licenseName = if (isDprProtected) "Somuleco Verified DPR License" else "Standard License",
            allowDownload = true,
            allowCommercial = true
        )
    }

    fun createProduct(
        title: String,
        shortDescription: String,
        fullDescription: String,
        price: Double,
        channelId: String,
        productType: ProductType,
        fileFormat: String,
        licenseName: String,
        allowDownload: Boolean,
        allowCommercial: Boolean,
        iconEmoji: String = "📦"
    ): CreatorProduct {
        val channel = _channels.value.find { it.id == channelId } ?: _channels.value.first()
        val newRights = RightsRecord(
            id = "r_${System.currentTimeMillis()}",
            registeredAt = "Today",
            licenseName = licenseName,
            allowDownload = allowDownload,
            allowCommercialUse = allowCommercial,
            protectionStatus = "Protected & Registered in DPR"
        )
        val newProd = CreatorProduct(
            id = "prod_${System.currentTimeMillis()}",
            creatorAccountId = _creatorAccount.value.id,
            channelId = channel.id,
            channelName = channel.name,
            title = title,
            slug = title.lowercase().replace(" ", "-"),
            shortDescription = shortDescription,
            fullDescription = fullDescription,
            price = Money(Math.round(price * 100), "USD"),
            productType = productType,
            fileFormat = fileFormat,
            rightsRecord = newRights,
            iconEmoji = iconEmoji,
            status = "ACTIVE"
        )
        _products.value = listOf(newProd) + _products.value

        // Seed DPR config
        _productRightsMap.value = _productRightsMap.value + (newProd.id to ProductRightsConfig(
            productId = newProd.id,
            productTitle = newProd.title,
            certificateId = "DPR-CERT-${newProd.id.uppercase()}",
            registeredOwner = _creatorProfile.value.displayName,
            allowCommercialUse = allowCommercial
        ))

        // Notify
        _notifications.value = listOf(
            NotificationItem(
                id = UUID.randomUUID().toString(),
                title = "New Product Published",
                message = "$title is now available in your store and registered in DPR",
                timestamp = "Just now",
                type = NotificationType.SALE
            )
        ) + _notifications.value

        return newProd
    }

    fun purchaseProduct(productId: String) {
        _products.value = _products.value.map { prod ->
            if (prod.id == productId) {
                prod.copy(
                    isPurchased = true,
                    salesCount = prod.salesCount + 1,
                    revenueTotal = prod.revenueTotal + prod.price.amount / 100.0
                )
            } else prod
        }
        val product = _products.value.find { it.id == productId }
        if (product != null) {
            val grossCents = product.price.amount
            val feeCents = Math.round(grossCents * 0.05)
            val newTx = TransactionItem(
                id = "tx_${System.currentTimeMillis()}",
                date = "Just now",
                customerName = _currentUser.value.displayName,
                itemTitle = product.title,
                grossAmount = Money(grossCents, product.price.currency),
                feeAmount = Money(feeCents, product.price.currency),
                netAmount = Money(grossCents - feeCents, product.price.currency),
                status = "COMPLETED",
                source = "Digital Product"
            )
            _transactions.value = listOf(newTx) + _transactions.value

            _notifications.value = listOf(
                NotificationItem(
                    id = UUID.randomUUID().toString(),
                    title = "Purchase Confirmed!",
                    message = "You purchased ${product.title}. Access is now unlocked in your Library.",
                    timestamp = "Just now",
                    type = NotificationType.SALE
                )
            ) + _notifications.value
        }
    }

    fun subscribeTier(tierId: String) {
        val currentPlan = _subscriptionPlan.value
        val tier = currentPlan.tiers.find { it.id == tierId }
        _subscriptionPlan.value = currentPlan.copy(
            isSubscribed = true,
            activeTierId = tierId
        )
        if (tier != null && tier.price.amount > 0) {
            val grossCents = tier.price.amount
            val feeCents = Math.round(grossCents * 0.05)
            val newTx = TransactionItem(
                id = "tx_${System.currentTimeMillis()}",
                date = "Just now",
                customerName = _currentUser.value.displayName,
                itemTitle = "Subscription to ${tier.name}",
                grossAmount = Money(grossCents, tier.price.currency),
                feeAmount = Money(feeCents, tier.price.currency),
                netAmount = Money(grossCents - feeCents, tier.price.currency),
                status = "COMPLETED",
                source = "Subscription"
            )
            _transactions.value = listOf(newTx) + _transactions.value
        }
    }

    // -------------------------------------------------------------------------
    // AI Actions & Helpers
    // -------------------------------------------------------------------------
    fun sendAIMessage(prompt: String) {
        val userMsg = AIMessage(
            id = UUID.randomUUID().toString(),
            role = AIMessageRole.USER,
            content = prompt,
            timestamp = "Just now"
        )
        _aiMessages.value = _aiMessages.value + userMsg

        val assistantResponse = generateContextualAIResponse(prompt)
        _aiMessages.value = _aiMessages.value + assistantResponse
    }

    private fun generateContextualAIResponse(prompt: String): AIMessage {
        val lower = prompt.lowercase()
        return when {
            lower.contains("create") || lower.contains("post") || lower.contains("draft") || lower.contains("next") -> {
                AIMessage(
                    id = UUID.randomUUID().toString(),
                    role = AIMessageRole.ASSISTANT,
                    content = "Here is a high-impact content draft crafted for your Photography Masterclass audience based on current engagement trends:",
                    timestamp = "Just now",
                    artifactTitle = "Draft: Mastering Backlit Editorial Portraits",
                    artifactType = "POST_DRAFT",
                    artifactContent = "Title: 3 Backlight Adjustments That Eliminate Blown Highlights\n\nWhen positioning subjects against sunset light:\n1. Feather your key light 45° across the face instead of shooting head-on.\n2. Use a black flag on the non-key side to preserve deep contrast.\n3. Meter specifically for the skin tones on the cheekbone.\n\nTry this on your next golden hour shoot!"
                )
            }
            lower.contains("grow") || lower.contains("audience") -> {
                AIMessage(
                    id = UUID.randomUUID().toString(),
                    role = AIMessageRole.ASSISTANT,
                    content = "Audience Growth Strategy for Elena Rostova:\n\n• Your 'Beginner Photography' channel is currently growing 2.4x faster than Masterclass. Creating 2 weekly quick tips will accelerate your top-of-funnel discovery.\n• Cross-post key insights from your subscriber tutorials as 60-second summary cards.\n• Host a monthly live portfolio review to convert free followers into paid subscribers.",
                    timestamp = "Just now"
                )
            }
            lower.contains("product") || lower.contains("monetize") || lower.contains("sell") -> {
                AIMessage(
                    id = UUID.randomUUID().toString(),
                    role = AIMessageRole.ASSISTANT,
                    content = "Digital Product Opportunity Identified:\n\nYour audience engages heavily with your lighting breakdowns. Here is a recommended product concept ready for Digital Product Rights protection:",
                    timestamp = "Just now",
                    artifactTitle = "Product Concept: Studio Lighting Blueprint & Ratio Cards",
                    artifactType = "PRODUCT_CONCEPT",
                    artifactContent = "Format: 25 High-Res Digital Blueprint Cards + Behind-the-Scenes Video Commentary\nRecommended Price: $34.00\nTarget Audience: Portrait and commercial photographers upgrading their home studio setups."
                )
            }
            else -> {
                AIMessage(
                    id = UUID.randomUUID().toString(),
                    role = AIMessageRole.ASSISTANT,
                    content = "I have analyzed your creator ecosystem ($18,450 revenue, 840 subscribers, 3 active channels). Your strongest leverage point this week is publishing a new subscriber masterclass to reduce month-end churn.",
                    timestamp = "Just now"
                )
            }
        }
    }

    fun dismissRecommendation(recId: String) {
        _recommendations.value = _recommendations.value.map { rec ->
            if (rec.id == recId) rec.copy(isDismissed = true) else rec
        }
    }

    fun uploadMediaItem(name: String, typeStr: String, sizeStr: String, channelName: String) {
        val mType = when (typeStr.uppercase()) {
            "VIDEO" -> MediaType.VIDEO
            "AUDIO" -> MediaType.AUDIO
            "DOCUMENT" -> MediaType.DOCUMENT
            else -> MediaType.IMAGE
        }
        uploadMedia(name, mType, sizeStr, channelName)
    }

    fun uploadMediaAsset(title: String, type: MediaType) {
        uploadMedia(
            name = title,
            type = type,
            size = "18.2 MB",
            channelName = _channels.value.firstOrNull()?.name ?: "Photography Masterclass"
        )
    }
}
