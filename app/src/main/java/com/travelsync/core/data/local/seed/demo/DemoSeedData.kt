package com.travelsync.core.data.local.seed.demo

import java.time.Instant

/**
 * Fixed identifiers and credentials for the demo database seed.
 * Login with [USERNAME] / [PASSWORD] after [DemoDatabaseSeeder] runs.
 *
 * Demo data references app catalog ids (e.g. [CATALOG_SENSOJI_ID]) defined in
 * [com.travelsync.core.data.local.reference.seed.PlaceCatalogSeed].
 */
object DemoSeedData {

    const val USERNAME = "demo"
    const val PASSWORD = "demo123456"
    const val USER_ID = "seed-user-001"
    const val DISPLAY_NAME = "Demo Traveler"
    const val EMAIL = "demo@travelsync.local"
    const val HOME_CURRENCY = "USD"

    const val CITY_ID = "city_tokyo"
    const val TRIP_ID = "seed-trip-tokyo"
    const val TRIP_TITLE = "Tokyo Adventure"
    const val TRIP_DESTINATION = "Tokyo"
    const val TRIP_COUNTRY = "Japan"
    const val TRIP_CURRENCY = "JPY"
    const val TRIP_BUDGET = 150_000.0

    const val DAY_1_ID = "seed-day-1"
    const val DAY_2_ID = "seed-day-2"
    const val DAY_3_ID = "seed-day-3"

    const val ACTIVITY_SENSOJI_ID = "seed-activity-sensoji"
    const val ACTIVITY_ICHIRAN_ID = "seed-activity-ichiran"
    const val ACTIVITY_SHIBUYA_ID = "seed-activity-shibuya"

    const val EXPENSE_RAMEN_ID = "seed-expense-ramen"
    const val EXPENSE_METRO_ID = "seed-expense-metro"

    const val PACKING_PASSPORT_ID = "seed-packing-passport"
    const val PACKING_ADAPTER_ID = "seed-packing-adapter"
    const val PACKING_JACKET_ID = "seed-packing-jacket"

    const val CATALOG_SENSOJI_ID = "catalog_tokyo_sensoji"
    const val CATALOG_SHIBUYA_ID = "catalog_tokyo_shibuya"
    const val CATALOG_ICHIRAN_ID = "catalog_tokyo_ichiran"

    private fun utcMillis(isoInstant: String): Long =
        Instant.parse(isoInstant).toEpochMilli()

    val tripStartMillis: Long = utcMillis("2026-06-01T00:00:00Z")
    val tripEndMillis: Long = utcMillis("2026-06-03T00:00:00Z")

    val day1Millis: Long = tripStartMillis
    val day2Millis: Long = utcMillis("2026-06-02T00:00:00Z")
    val day3Millis: Long = tripEndMillis

    fun dayTimeMillis(dayOffset: Int, hour: Int, minute: Int = 0): Long =
        Instant.parse("2026-06-0${dayOffset + 1}T${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}:00Z")
            .toEpochMilli()

    val seedCreatedAtMillis: Long = utcMillis("2026-01-01T12:00:00Z")
}
