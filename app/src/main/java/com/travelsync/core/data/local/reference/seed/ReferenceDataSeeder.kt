package com.travelsync.core.data.local.reference.seed

import com.travelsync.core.data.local.TravelSyncDatabase

object ReferenceDataSeeder {

    suspend fun seedIfEmpty(database: TravelSyncDatabase) {
        if (database.currencyDao().count() > 0) return
        seed(database)
    }

    suspend fun seed(database: TravelSyncDatabase) {
        val snapshot = ReferenceDataCatalog.snapshot()
        database.currencyDao().insertAll(snapshot.currencies)
        database.regionDao().insertAll(snapshot.regions)
        database.countryDao().insertAll(snapshot.countries)
        database.cityDao().insertAll(snapshot.cities)
    }
}
