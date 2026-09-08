package com.example.data.repository

import com.example.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

object CreatorRepository {

    // Current User & Profile
    private val _currentUser = MutableStateFlow(
        UserReference(
            id = "usr_elena",
            displayName = "Elena Rostova",
            username = "elenarostova",
            email = "elena@rostovaphoto.com",
            isCreator = true
        )
    )
    val currentUser: StateFlow<UserReference> = _currentUser.asStateFlow()

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
    val creatorAccount: StateFlow<CreatorAccount> = _creatorAccount.asStateFlow()

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
    val creatorProfile: StateFlow<CreatorProfile> = _creatorProfile.asStateFlow()

    // Mode Switcher: True = Creator Mode, False = Consumer Mode
    private val _isCreatorMode = MutableStateFlow(true)
    val isCreatorMode: StateFlow<Boolean> = _isCreatorMode.asStateFlow()

    // Channel Context: null = All Channels
    private val _selectedChannelId = MutableStateFlow<String?>(null)
    val selectedChannelId: StateFlow<String?> = _selectedChannelId.asStateFlow()

    // Channels
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
    val channels: StateFlow<List<Channel>> = _channels.asStateFlow()

    // Content Items
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
                contentType = ContentType.VIDEO,
                title = "Live Studio Lighting Breakdown: 1-Light Editorial Portraits",
                summary = "Watch full 42-minute live studio walkthrough with lighting ratios and tethered capture previews.",
                body = "Full subscriber masterclass on single-source Octabox placement for cinematic contrast. Includes BTS camera angles, fill card adjustments, and color gel experiments.",
                mediaDuration = "42:15",
                coverEmoji = "💡",
                status = "PUBLISHED",
                accessType = AccessType.SUBSCRIBERS,
                publishedDate = "Yesterday",
                likesCount = 512,
                commentsCount = 68,
                isUnlocked = true
            ),
            ContentItem(
                id = "post_3",
                creatorAccountId = "acc_elena_01",
                channelId = "ch_2",
                channelName = "Beginner Photography",
                contentType = ContentType.POST,
                title = "Stop Buying Lenses Until You Understand Focal Length Compression",
                summary = "Quick visual guide: How 35mm vs 50mm vs 85mm alters face geometry and environment separation.",
                body = "Focal length isn't just about 'getting closer'. It dictates visual perspective and spatial compression between your subject and background.\n\n• 35mm: Environmental context, intimate feeling.\n• 50mm: True to human eye perception.\n• 85mm: Flattering facial compression, creamy bokeh separation.",
                mediaDuration = "Quick Tip",
                coverEmoji = "🔍",
                status = "PUBLISHED",
                accessType = AccessType.PUBLIC,
                publishedDate = "3 days ago",
                likesCount = 890,
                commentsCount = 114
            ),
            ContentItem(
                id = "post_4",
                creatorAccountId = "acc_elena_01",
                channelId = "ch_3",
                channelName = "Behind the Scenes & Studio",
                contentType = ContentType.IMAGE,
                title = "Raw Studio Diaries: Preparing the European Gallery Exhibition",
                summary = "Print proofs, frame selections, and exhibition curation notes for the upcoming gallery.",
                body = "A preview of the large-format matte cotton rag test prints for the Zurich gallery show. We are testing three weight stocks to check ink depth.",
                coverEmoji = "🖼️",
                status = "PUBLISHED",
                accessType = AccessType.PREMIUM_TIER,
                publishedDate = "5 days ago",
                likesCount = 230,
                commentsCount = 19,
                isUnlocked = true
            )
        )
    )
    val contentItems: StateFlow<List<ContentItem>> = _contentItems.asStateFlow()

    // Products
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
                priceAmount = 49.0,
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
                priceAmount = 29.0,
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
                priceAmount = 14.0,
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
    val products: StateFlow<List<CreatorProduct>> = _products.asStateFlow()

    // Subscription Plans
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
                    monthlyPrice = 0.0,
                    benefits = listOf("Weekly educational newsletter", "Public feed posts & tips", "Community discussions"),
                    subscriberCount = 11560,
                    badgeColorHex = 0xFF64748B
                ),
                MembershipTier(
                    id = "tier_insider",
                    name = "Creator Insider",
                    monthlyPrice = 9.0,
                    benefits = listOf("Subscriber-only video masterclasses", "Downloadable RAW files for practice", "Monthly live Q&A session", "20% discount on all digital store products"),
                    subscriberCount = 710,
                    isFeatured = true,
                    badgeColorHex = 0xFF7C3AED
                ),
                MembershipTier(
                    id = "tier_pro",
                    name = "Studio Pro Mentorship",
                    monthlyPrice = 39.0,
                    benefits = listOf("All Insider tier benefits", "Quarterly 1-on-1 portfolio video critique", "Direct Creator messaging access", "Free access to all new preset releases"),
                    subscriberCount = 210,
                    badgeColorHex = 0xFF2563EB
                )
            ),
            isSubscribed = true,
            activeTierId = "tier_insider"
        )
    )
    val subscriptionPlan: StateFlow<SubscriptionPlan> = _subscriptionPlan.asStateFlow()

    // Subscribers List
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

    // Analytics Overview
    private val _analytics = MutableStateFlow(AnalyticsOverview())
    val analytics: StateFlow<AnalyticsOverview> = _analytics.asStateFlow()

    // Top Content Performance
    private val _topContent = MutableStateFlow(
        listOf(
            TopContentPerformance("tc_1", "The 3 Golden Rules of Golden Hour", "Photography Masterclass", "34.8K views", "6.2% follow rate", 480, "Article"),
            TopContentPerformance("tc_2", "Stop Buying Lenses Until You Understand Focal Length", "Beginner Photography", "58.2K views", "8.9% follow rate", 820, "Quick Tip"),
            TopContentPerformance("tc_3", "1-Light Editorial Portrait Walkthrough", "Photography Masterclass", "22.1K views", "14.1% subscriber conversion", 140, "Video")
        )
    )
    val topContent: StateFlow<List<TopContentPerformance>> = _topContent.asStateFlow()

    // Transactions
    private val _transactions = MutableStateFlow(
        listOf(
            TransactionItem("tx_1", "Today, 14:20", "Liam O'Connor", "Wedding & Portrait Business Guide", 49.0, 2.45, 46.55, status = "COMPLETED", source = "Digital Product"),
            TransactionItem("tx_2", "Today, 11:05", "Sophia Rossi", "Studio Pro Mentorship (Renewal)", 39.0, 1.95, 37.05, status = "COMPLETED", source = "Subscription"),
            TransactionItem("tx_3", "Yesterday", "Hanna Schmidt", "Cinematic Daylight Lightroom Presets", 29.0, 1.45, 27.55, status = "COMPLETED", source = "Digital Product"),
            TransactionItem("tx_4", "Sep 05", "Kenji Sato", "Camera Settings Pocket Cards", 14.0, 0.70, 13.30, status = "COMPLETED", source = "Digital Product"),
            TransactionItem("tx_5", "Sep 04", "Marcus Vance", "Creator Insider (Renewal)", 9.0, 0.45, 8.55, status = "COMPLETED", source = "Subscription")
        )
    )
    val transactions: StateFlow<List<TransactionItem>> = _transactions.asStateFlow()

    // Creator AI Recommendations
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

    // Creator AI Messages
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

    // Notifications
    private val _notifications = MutableStateFlow(
        listOf(
            NotificationItem("notif_1", "New Studio Pro Subscriber!", "Sophia Rossi subscribed to Studio Pro Mentorship ($39/mo)", "20 mins ago", NotificationType.SUBSCRIBER),
            NotificationItem("notif_2", "Product Sale Confirmed", "Liam O'Connor purchased Complete Wedding & Portrait Business Guide ($49)", "2 hours ago", NotificationType.SALE),
            NotificationItem("notif_3", "Digital Rights Registration Complete", "Rights Record #r_103 successfully registered and protected in DPR", "1 day ago", NotificationType.RIGHTS),
            NotificationItem("notif_4", "Creator AI Recommendation", "New insight available: Convert high-performing beginner content to product", "2 days ago", NotificationType.AI_RECOMMENDATION)
        )
    )
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    // Media Assets
    private val _mediaAssets = MutableStateFlow(
        listOf(
            MediaAsset("med_1", "Golden Hour Sunburst Proof", "IMG_8921_raw.jpg", MediaType.IMAGE, "28.4 MB", uploadDate = "Today", channelName = "Photography Masterclass"),
            MediaAsset("med_2", "1-Light Studio Masterclass Reel", "studio_lighting_4k.mp4", MediaType.VIDEO, "1.4 GB", durationText = "42:15", uploadDate = "Yesterday", channelName = "Photography Masterclass"),
            MediaAsset("med_3", "Portrait Contract Template", "wedding_client_contract_v3.pdf", MediaType.DOCUMENT, "2.1 MB", uploadDate = "Sep 02", channelName = "Beginner Photography"),
            MediaAsset("med_4", "Studio Audio Podcast Ep 12", "creative_struggles_audio.wav", MediaType.AUDIO, "84.2 MB", durationText = "28:40", uploadDate = "Aug 29", channelName = "Behind the Scenes & Studio")
        )
    )
    val mediaAssets: StateFlow<List<MediaAsset>> = _mediaAssets.asStateFlow()
    val mediaItems: StateFlow<List<MediaAsset>> = _mediaAssets.asStateFlow()

    // Integrations
    private val _integrations = MutableStateFlow(
        listOf(
            CreatorIntegration("int_1", "Somuleco Passport", "ECOSYSTEM", "Portable creator identity and reputation verification across Somuleco.", "🛡️", true),
            CreatorIntegration("int_2", "Digital Product Rights Registry", "ECOSYSTEM", "Real-time copyright and licensing registration for products and media.", "📜", true),
            CreatorIntegration("int_3", "YouTube Channel Sync", "SOCIAL", "Auto-import video catalog and track viewer conversion.", "▶️", true),
            CreatorIntegration("int_4", "Instagram Creator API", "SOCIAL", "Sync reels and stories to your Behind the Scenes channel.", "📸", false),
            CreatorIntegration("int_5", "Dropbox Cloud Storage", "STORAGE", "Sync RAW photo catalogs and project deliveries.", "📦", true)
        )
    )
    val integrations: StateFlow<List<CreatorIntegration>> = _integrations.asStateFlow()

    // Standalone Comments for Post Discussions
    private val _comments = MutableStateFlow(
        listOf(
            ContentComment("c_1", "Marcus Vance", "🎧", "The rim light tip completely changed my setup today Elena!", "1 hour ago", 8),
            ContentComment("c_2", "Sarah Lin", "🎨", "Do you shoot this with spot metering or center-weighted?", "45 mins ago", 3),
            ContentComment("c_3", "David Kim", "📸", "Can't wait to test this on Saturday's outdoor portrait session!", "20 mins ago", 1)
        )
    )
    val comments: StateFlow<List<ContentComment>> = _comments.asStateFlow()

    // Goals
    private val _goals = MutableStateFlow(
        listOf(
            CreatorGoal("g_1", "Reach 1,000 Paid Subscribers", "1,000", "Dec 2026", isAchieved = false, progress = 0.84f),
            CreatorGoal("g_2", "Earn $25,000 Monthly Revenue", "$25K/mo", "Nov 2026", isAchieved = false, progress = 0.74f),
            CreatorGoal("g_3", "Publish 50 Masterclass Lessons", "50 Lessons", "Oct 2026", isAchieved = true, progress = 1.0f)
        )
    )
    val goals: StateFlow<List<CreatorGoal>> = _goals.asStateFlow()

    // --- Interactive Action Methods ---

    fun toggleCreatorMode() {
        _isCreatorMode.value = !_isCreatorMode.value
    }

    fun setCreatorMode(isCreator: Boolean) {
        _isCreatorMode.value = isCreator
    }

    fun selectChannelContext(channelId: String?) {
        _selectedChannelId.value = channelId
    }

    fun setSelectedChannel(channelId: String?) {
        selectChannelContext(channelId)
    }

    fun toggleIntegration(id: String) {
        _integrations.value = _integrations.value.map {
            if (it.id == id) it.copy(isConnected = !it.isConnected) else it
        }
    }

    fun uploadMediaItem(name: String, typeStr: String, sizeStr: String, channelName: String) {
        val mType = when (typeStr.uppercase()) {
            "VIDEO" -> MediaType.VIDEO
            "AUDIO" -> MediaType.AUDIO
            "DOCUMENT" -> MediaType.DOCUMENT
            else -> MediaType.IMAGE
        }
        val asset = MediaAsset(
            id = "med_${System.currentTimeMillis()}",
            title = name,
            fileName = name,
            type = mType,
            sizeText = sizeStr,
            uploadDate = "Just now",
            channelName = channelName
        )
        _mediaAssets.value = listOf(asset) + _mediaAssets.value
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

    fun toggleLike(contentId: String) {
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

    fun toggleSave(contentId: String) {
        _contentItems.value = _contentItems.value.map { item ->
            if (item.id == contentId) {
                item.copy(isSaved = !item.isSaved)
            } else item
        }
    }

    fun addComment(contentId: String, text: String) {
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
        // Update channel content count
        _channels.value = _channels.value.map { ch ->
            if (ch.id == channel.id) ch.copy(contentCount = ch.contentCount + 1) else ch
        }
        return newItem
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
            priceAmount = price,
            productType = productType,
            fileFormat = fileFormat,
            rightsRecord = newRights,
            iconEmoji = iconEmoji,
            status = "ACTIVE"
        )
        _products.value = listOf(newProd) + _products.value

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
                    revenueTotal = prod.revenueTotal + prod.priceAmount
                )
            } else prod
        }
        val product = _products.value.find { it.id == productId }
        if (product != null) {
            val newTx = TransactionItem(
                id = "tx_${System.currentTimeMillis()}",
                date = "Just now",
                customerName = _currentUser.value.displayName,
                itemTitle = product.title,
                grossAmount = product.priceAmount,
                feeAmount = product.priceAmount * 0.05,
                netAmount = product.priceAmount * 0.95,
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
        if (tier != null && tier.monthlyPrice > 0) {
            val newTx = TransactionItem(
                id = "tx_${System.currentTimeMillis()}",
                date = "Just now",
                customerName = _currentUser.value.displayName,
                itemTitle = "Subscription to ${tier.name}",
                grossAmount = tier.monthlyPrice,
                feeAmount = tier.monthlyPrice * 0.05,
                netAmount = tier.monthlyPrice * 0.95,
                status = "COMPLETED",
                source = "Subscription"
            )
            _transactions.value = listOf(newTx) + _transactions.value
        }
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

    fun sendAIMessage(prompt: String) {
        val userMsg = AIMessage(
            id = UUID.randomUUID().toString(),
            role = AIMessageRole.USER,
            content = prompt,
            timestamp = "Just now"
        )
        _aiMessages.value = _aiMessages.value + userMsg

        // Generate intelligent contextual response
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

    fun uploadMediaAsset(title: String, type: MediaType) {
        val newAsset = MediaAsset(
            id = "med_${System.currentTimeMillis()}",
            title = title,
            fileName = "${title.lowercase().replace(" ", "_")}.${if (type == MediaType.VIDEO) "mp4" else if (type == MediaType.AUDIO) "wav" else "jpg"}",
            type = type,
            sizeText = "18.2 MB",
            uploadDate = "Just now",
            status = "READY"
        )
        _mediaAssets.value = listOf(newAsset) + _mediaAssets.value
    }

    fun markNotificationRead(id: String) {
        _notifications.value = _notifications.value.map { notif ->
            if (notif.id == id) notif.copy(isRead = true) else notif
        }
    }

    fun markAllNotificationsRead() {
        _notifications.value = _notifications.value.map { it.copy(isRead = true) }
    }
}
