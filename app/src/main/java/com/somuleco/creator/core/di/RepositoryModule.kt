package com.somuleco.creator.core.di

import com.somuleco.creator.data.repository.impl.AudienceRepositoryImpl
import com.somuleco.creator.data.repository.impl.ChannelRepositoryImpl
import com.somuleco.creator.data.repository.impl.ContentRepositoryImpl
import com.somuleco.creator.data.repository.impl.CreatorAIRepositoryImpl
import com.somuleco.creator.data.repository.impl.CreatorAccountRepositoryImpl
import com.somuleco.creator.data.repository.impl.CreatorProfileRepositoryImpl
import com.somuleco.creator.data.repository.impl.MarketplaceRepositoryImpl
import com.somuleco.creator.data.repository.impl.MediaRepositoryImpl
import com.somuleco.creator.data.repository.impl.NotificationRepositoryImpl
import com.somuleco.creator.data.repository.impl.ProductRepositoryImpl
import com.somuleco.creator.data.repository.impl.RevenueRepositoryImpl
import com.somuleco.creator.data.repository.impl.RightsRepositoryImpl
import com.somuleco.creator.data.repository.impl.SettingsRepositoryImpl
import com.somuleco.creator.data.repository.impl.SubscriptionRepositoryImpl
import com.somuleco.creator.data.repository.interfaces.AudienceRepository
import com.somuleco.creator.data.repository.interfaces.ChannelRepository
import com.somuleco.creator.data.repository.interfaces.ContentRepository
import com.somuleco.creator.data.repository.interfaces.CreatorAIRepository
import com.somuleco.creator.data.repository.interfaces.CreatorAccountRepository
import com.somuleco.creator.data.repository.interfaces.CreatorProfileRepository
import com.somuleco.creator.data.repository.interfaces.MarketplaceRepository
import com.somuleco.creator.data.repository.interfaces.MediaRepository
import com.somuleco.creator.data.repository.interfaces.NotificationRepository
import com.somuleco.creator.data.repository.interfaces.ProductRepository
import com.somuleco.creator.data.repository.interfaces.RevenueRepository
import com.somuleco.creator.data.repository.interfaces.RightsRepository
import com.somuleco.creator.data.repository.interfaces.SettingsRepository
import com.somuleco.creator.data.repository.interfaces.SubscriptionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Binds 13 of the 14 repository interfaces (`data/repository/interfaces/
 * DomainRepositories.kt`) to their per-domain implementations, so every migrated call
 * site is DI-supplied and typed against the interface (plan §7.3 tasks 1 & 4;
 * `v0.1-client-data-access.md` §9 substitutability requirement). A test module can
 * override any single `@Binds` here to substitute a fake — see
 * `RepositorySubstitutabilityTest`.
 *
 * `AuthRepository` is deliberately NOT bound here — see [AuthRepositoryModule] (Foundation
 * Wave 08) for why it needed to move into its own module.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindChannelRepository(impl: ChannelRepositoryImpl): ChannelRepository

    @Binds
    @Singleton
    abstract fun bindContentRepository(impl: ContentRepositoryImpl): ContentRepository

    @Binds
    @Singleton
    abstract fun bindProductRepository(impl: ProductRepositoryImpl): ProductRepository

    @Binds
    @Singleton
    abstract fun bindRightsRepository(impl: RightsRepositoryImpl): RightsRepository

    @Binds
    @Singleton
    abstract fun bindMarketplaceRepository(impl: MarketplaceRepositoryImpl): MarketplaceRepository

    @Binds
    @Singleton
    abstract fun bindSubscriptionRepository(impl: SubscriptionRepositoryImpl): SubscriptionRepository

    @Binds
    @Singleton
    abstract fun bindRevenueRepository(impl: RevenueRepositoryImpl): RevenueRepository

    @Binds
    @Singleton
    abstract fun bindAudienceRepository(impl: AudienceRepositoryImpl): AudienceRepository

    @Binds
    @Singleton
    abstract fun bindMediaRepository(impl: MediaRepositoryImpl): MediaRepository

    @Binds
    @Singleton
    abstract fun bindCreatorAIRepository(impl: CreatorAIRepositoryImpl): CreatorAIRepository

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(impl: NotificationRepositoryImpl): NotificationRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindCreatorProfileRepository(impl: CreatorProfileRepositoryImpl): CreatorProfileRepository

    // Foundation Wave 09 (plan §7.4 task 1): the first real, Retrofit-backed repository bound
    // here rather than a `MockCreatorDataSource`-backed one — Creator Account activation must
    // call the real API, per `v0.1-creator-authorization.md`, not a local mock.
    @Binds
    @Singleton
    abstract fun bindCreatorAccountRepository(impl: CreatorAccountRepositoryImpl): CreatorAccountRepository
}
