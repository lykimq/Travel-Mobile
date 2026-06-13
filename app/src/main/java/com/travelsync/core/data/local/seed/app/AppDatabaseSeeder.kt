package com.travelsync.core.data.local.seed.app

import com.travelsync.core.data.local.TravelSyncDatabase
import com.travelsync.core.data.local.reference.seed.PlaceCatalogSeed
import com.travelsync.core.data.local.reference.seed.ReferenceDataSeeder

/**
 * Seeds bundled app data loaded on every install: regions, currencies, countries,
 * cities, and the offline place catalog.
 *
 * Extend by updating [ReferenceDataSeeder] / [PlaceCatalogSeed] catalogs, or by
 * plugging in asset-based loaders from this entry point later.
 */
object AppDatabaseSeeder {

    suspend fun isSeeded(database: TravelSyncDatabase): Boolean =
        database.currencyDao().count() > 0

    suspend fun seedIfEmpty(database: TravelSyncDatabase) {
        ReferenceDataSeeder.seedIfEmpty(database)
        PlaceCatalogSeed.seedIfEmpty(database)
    }

    suspend fun seed(database: TravelSyncDatabase) {
        ReferenceDataSeeder.seed(database)
        PlaceCatalogSeed.seed(database)
    }
}
