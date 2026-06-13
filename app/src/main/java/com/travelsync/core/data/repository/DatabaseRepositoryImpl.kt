package com.travelsync.core.data.repository

import com.travelsync.BuildConfig
import com.travelsync.core.data.local.TravelSyncDatabase
import com.travelsync.core.data.local.entity.AppSessionEntity
import com.travelsync.core.data.local.seed.app.AppDatabaseSeeder
import com.travelsync.core.data.local.seed.demo.DemoDatabaseSeeder
import com.travelsync.core.domain.repository.DatabaseRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseRepositoryImpl @Inject constructor(
    private val database: TravelSyncDatabase
) : DatabaseRepository {

    override suspend fun clearAllData(): Result<Unit> = runCatching {
        database.clearAllTables()
        database.appSessionDao().saveSession(AppSessionEntity(currentUserId = null))
        AppDatabaseSeeder.seed(database)
        if (BuildConfig.SEED_DEMO_DATA) {
            DemoDatabaseSeeder.seedIfEmpty(database)
        }
    }
}
