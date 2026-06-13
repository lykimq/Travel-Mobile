package com.travelsync.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.travelsync.core.data.local.dao.*
import com.travelsync.core.data.local.entity.*
import com.travelsync.core.data.local.reference.dao.CityDao
import com.travelsync.core.data.local.reference.dao.CountryDao
import com.travelsync.core.data.local.reference.dao.CurrencyDao
import com.travelsync.core.data.local.reference.dao.RegionDao
import com.travelsync.core.data.local.reference.entity.CityEntity
import com.travelsync.core.data.local.reference.entity.CountryEntity
import com.travelsync.core.data.local.reference.entity.CurrencyEntity
import com.travelsync.core.data.local.reference.entity.RegionEntity

@Database(
    entities = [
        RegionEntity::class,
        CurrencyEntity::class,
        CountryEntity::class,
        CityEntity::class,
        UserEntity::class,
        AppSessionEntity::class,
        TripEntity::class,
        TripDayEntity::class,
        PlaceEntity::class,
        ActivityEntity::class,
        ExpenseEntity::class,
        PackingItemEntity::class
    ],
    version = 6,
    exportSchema = false
)
abstract class TravelSyncDatabase : RoomDatabase() {
    abstract fun regionDao(): RegionDao
    abstract fun currencyDao(): CurrencyDao
    abstract fun countryDao(): CountryDao
    abstract fun cityDao(): CityDao
    abstract fun userDao(): UserDao
    abstract fun appSessionDao(): AppSessionDao
    abstract fun tripDao(): TripDao
    abstract fun tripDayDao(): TripDayDao
    abstract fun placeDao(): PlaceDao
    abstract fun activityDao(): ActivityDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun packingDao(): PackingDao
}
