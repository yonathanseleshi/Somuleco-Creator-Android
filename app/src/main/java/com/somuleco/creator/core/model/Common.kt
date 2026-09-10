package com.somuleco.creator.core.model

import com.squareup.moshi.JsonClass

/**
 * Canonical domain model types shared by all 25 Wave 03 foundational objects.
 *
 * These types mirror `GRD/Contracts/v0.1-domain-contracts.md` in the
 * Somuleco-Creator-Dev-Docs repository field-for-field. That document is the
 * single source of truth; do not diverge from it here without updating it
 * first (and only the contract's own owners should do that).
 *
 * Conventions applied throughout this package (see contract §2):
 * - All `id` fields are strings (UUID v4 in v0.1, or an opaque provider id).
 * - All timestamps are ISO 8601 UTC strings with millisecond precision
 *   (`"2026-09-10T14:32:00.000Z"`), never epoch integers. Field names end in
 *   `At`.
 * - Field names are camelCase, matching the wire (JSON) contract directly -
 *   Kotlin's natural convention already matches, so no mapping is needed.
 * - Enum wire values are lowercase snake_case strings; each enum constant
 *   below carries an explicit `@Json(name = ...)` so Moshi serializes/
 *   deserializes the exact contract string rather than deriving it from the
 *   Kotlin constant name.
 * - A field that can legitimately be absent is nullable (`T?`), never
 *   omitted, whenever the underlying concept is "no value yet".
 * - Money is always the `{ amount, currency }` shape below, never a bare
 *   float - `amount` is an integer in minor currency units (cents).
 */

/**
 * Contract §2.7 — a monetary amount. `amount` is an integer in minor
 * currency units (e.g. cents) to avoid floating-point error; `currency` is
 * an ISO 4217 code (e.g. "USD").
 */
@JsonClass(generateAdapter = true)
data class Money(
    val amount: Long,
    val currency: String
)

/** Contract §2.6 — pagination metadata attached to list envelopes. */
@JsonClass(generateAdapter = true)
data class ListMeta(
    val page: Int,
    val pageSize: Int,
    val total: Int
)

/** Contract §2.6 — the envelope every list endpoint returns: `{ data, meta }`. */
@JsonClass(generateAdapter = true)
data class ListResponse<T>(
    val data: List<T>,
    val meta: ListMeta
)

/**
 * Contract §2.6 — the envelope every single-resource endpoint returns.
 * `meta` is `{}` for single-resource responses per the contract.
 */
@JsonClass(generateAdapter = true)
data class SingleResponse<T>(
    val data: T,
    val meta: Map<String, Any> = emptyMap()
)
