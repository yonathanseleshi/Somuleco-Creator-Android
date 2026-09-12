package com.somuleco.creator.core.network

import com.somuleco.creator.data.model.ChannelSummary

/**
 * DTO -> UI-projection mappers for the Channel reference remote implementation
 * (plan §7.3 task 9). `core/network` previously defined its own DTOs with zero mappers
 * to any app-facing model. [ChannelDto] intentionally carries fewer fields than
 * [ChannelSummary] (no cosmetic emoji/color, no per-channel subscriber count) — those are
 * mock/presentation-only fields with no backend equivalent yet, so sensible defaults are
 * applied rather than inventing server fields that don't exist.
 */
fun ChannelDto.toChannelSummary(creatorAccountId: String): ChannelSummary = ChannelSummary(
    id = id,
    creatorAccountId = creatorAccountId,
    name = name,
    slug = name.lowercase().replace(" ", "-"),
    handle = handle,
    description = description,
    category = category,
    followersCount = followersCount,
    subscribersCount = 0,
    contentCount = contentCount,
    isDefault = false,
    iconEmoji = "📡",
    primaryColorHex = 0xFF2563EB,
    isFollowed = false
)

fun ChannelSummary.toCreateChannelRequest(): CreateChannelRequest = CreateChannelRequest(
    name = name,
    description = description,
    category = category,
    handle = handle,
    isPublic = true
)
