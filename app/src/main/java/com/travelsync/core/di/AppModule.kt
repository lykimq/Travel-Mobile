package com.travelsync.core.di

import android.content.Context
import androidx.room.Room
import com.travelsync.BuildConfig
import com.travelsync.core.data.local.seed.app.AppDatabaseSeeder
import com.travelsync.core.data.local.seed.demo.DemoDatabaseSeeder
import com.travelsync.core.data.local.TravelSyncDatabase
import com.travelsync.core.data.local.dao.*
import com.travelsync.core.data.local.reference.dao.CityDao
import com.travelsync.core.data.local.reference.dao.CountryDao
import com.travelsync.core.data.local.reference.dao.CurrencyDao
import com.travelsync.core.data.local.reference.dao.RegionDao
import com.travelsync.core.data.local.entity.AppSessionEntity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TravelSyncDatabase {
        val db = Room.databaseBuilder(
            context,
            TravelSyncDatabase::class.java,
            "travelsync_db"
        )
            .fallbackToDestructiveMigration()
            .build()

        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            if (db.appSessionDao().getSession() == null) {
                db.appSessionDao().saveSession(AppSessionEntity(currentUserId = null))
            }
            AppDatabaseSeeder.seedIfEmpty(db)
            if (BuildConfig.SEED_DEMO_DATA) {
                DemoDatabaseSeeder.seedIfEmpty(db)
            }
        }
        return db
    }

    @Provides fun provideRegionDao(db: TravelSyncDatabase): RegionDao = db.regionDao()
    @Provides fun provideCurrencyDao(db: TravelSyncDatabase): CurrencyDao = db.currencyDao()
    @Provides fun provideCountryDao(db: TravelSyncDatabase): CountryDao = db.countryDao()
    @Provides fun provideCityDao(db: TravelSyncDatabase): CityDao = db.cityDao()
    @Provides fun provideUserDao(db: TravelSyncDatabase): UserDao = db.userDao()
    @Provides fun provideAppSessionDao(db: TravelSyncDatabase): AppSessionDao = db.appSessionDao()
    @Provides fun provideTripDao(db: TravelSyncDatabase): TripDao = db.tripDao()
    @Provides fun provideTripDayDao(db: TravelSyncDatabase): TripDayDao = db.tripDayDao()
    @Provides fun provideActivityDao(db: TravelSyncDatabase): ActivityDao = db.activityDao()
    @Provides fun providePlaceDao(db: TravelSyncDatabase): PlaceDao = db.placeDao()
    @Provides fun provideExpenseDao(db: TravelSyncDatabase): ExpenseDao = db.expenseDao()
    @Provides fun providePackingDao(db: TravelSyncDatabase): PackingDao = db.packingDao()
}
