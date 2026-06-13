package com.travelsync.core.data.local.reference.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.travelsync.core.data.local.reference.entity.CityEntity
import com.travelsync.core.data.local.reference.entity.CountryEntity
import com.travelsync.core.data.local.reference.entity.CurrencyEntity
import com.travelsync.core.data.local.reference.entity.RegionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RegionDao {
    @Query("SELECT COUNT(*) FROM regions")
    suspend fun count(): Int

    @Query("SELECT * FROM regions WHERE isActive = 1 ORDER BY sortOrder ASC, name ASC")
    fun observeActive(): Flow<List<RegionEntity>>

    @Query(
        """
        SELECT DISTINCT r.* FROM regions r
        INNER JOIN countries c ON c.regionCode = r.code AND c.isActive = 1
        WHERE r.isActive = 1
        ORDER BY r.sortOrder ASC, r.name ASC
        """
    )
    fun observeActiveWithCountries(): Flow<List<RegionEntity>>

    @Query("SELECT * FROM regions WHERE code = :code LIMIT 1")
    suspend fun getByCode(code: String): RegionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<RegionEntity>)
}

@Dao
interface CurrencyDao {
    @Query("SELECT COUNT(*) FROM currencies")
    suspend fun count(): Int

    @Query("SELECT * FROM currencies WHERE isActive = 1 ORDER BY sortOrder ASC, name ASC")
    fun observeActive(): Flow<List<CurrencyEntity>>

    @Query("SELECT * FROM currencies WHERE code = :code LIMIT 1")
    suspend fun getByCode(code: String): CurrencyEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<CurrencyEntity>)
}

@Dao
interface CountryDao {
    @Query("SELECT COUNT(*) FROM countries")
    suspend fun count(): Int

    @Query("SELECT * FROM countries WHERE isActive = 1 ORDER BY sortOrder ASC, name ASC")
    fun observeActive(): Flow<List<CountryEntity>>

    @Query(
        """
        SELECT * FROM countries
        WHERE isActive = 1 AND regionCode = :regionCode
        ORDER BY sortOrder ASC, name ASC
        """
    )
    fun observeActiveByRegion(regionCode: String): Flow<List<CountryEntity>>

    @Query("SELECT * FROM countries WHERE code = :code LIMIT 1")
    suspend fun getByCode(code: String): CountryEntity?

    @Query("SELECT * FROM countries WHERE LOWER(name) = LOWER(:name) LIMIT 1")
    suspend fun findByName(name: String): CountryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<CountryEntity>)
}

@Dao
interface CityDao {
    @Query("SELECT COUNT(*) FROM cities")
    suspend fun count(): Int

    @Query(
        """
        SELECT * FROM cities
        WHERE isActive = 1 AND countryCode = :countryCode
        ORDER BY sortOrder ASC, name ASC
        """
    )
    fun observeActiveByCountry(countryCode: String): Flow<List<CityEntity>>

    @Query("SELECT * FROM cities WHERE id = :cityId LIMIT 1")
    suspend fun getById(cityId: String): CityEntity?

    @Query(
        """
        SELECT * FROM cities
        WHERE isActive = 1
          AND (
            LOWER(name) = LOWER(:name)
            OR LOWER(searchAliases) LIKE '%' || LOWER(:name) || '%'
          )
        LIMIT 1
        """
    )
    suspend fun findByName(name: String): CityEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<CityEntity>)
}
