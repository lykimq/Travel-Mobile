package com.travelsync.core.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.travelsync.core.data.local.seed.app.AppDatabaseSeeder
import com.travelsync.core.data.local.seed.demo.DemoDatabaseSeeder

object TestDatabaseHelper {

    fun createEmptyInMemoryDatabase(context: Context = ApplicationProvider.getApplicationContext()): TravelSyncDatabase =
        Room.inMemoryDatabaseBuilder(context, TravelSyncDatabase::class.java)
            .allowMainThreadQueries()
            .build()

    suspend fun createAppSeededInMemoryDatabase(
        context: Context = ApplicationProvider.getApplicationContext()
    ): TravelSyncDatabase {
        val database = createEmptyInMemoryDatabase(context)
        database.appSessionDao().saveSession(
            com.travelsync.core.data.local.entity.AppSessionEntity(currentUserId = null)
        )
        AppDatabaseSeeder.seed(database)
        return database
    }

    suspend fun createDemoSeededInMemoryDatabase(
        context: Context = ApplicationProvider.getApplicationContext()
    ): TravelSyncDatabase {
        val database = createAppSeededInMemoryDatabase(context)
        DemoDatabaseSeeder.seed(database)
        return database
    }
}
