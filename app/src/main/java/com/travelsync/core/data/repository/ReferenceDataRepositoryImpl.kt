package com.travelsync.core.data.repository

import com.travelsync.core.data.local.reference.dao.CityDao
import com.travelsync.core.data.local.reference.dao.CountryDao
import com.travelsync.core.data.local.reference.dao.CurrencyDao
import com.travelsync.core.data.local.reference.dao.RegionDao
import com.travelsync.core.data.local.reference.mapper.toDomain
import com.travelsync.core.domain.model.City
import com.travelsync.core.domain.model.Country
import com.travelsync.core.domain.model.Currency
import com.travelsync.core.domain.model.Region
import com.travelsync.core.domain.repository.ReferenceDataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReferenceDataRepositoryImpl @Inject constructor(
    private val regionDao: RegionDao,
    private val currencyDao: CurrencyDao,
    private val countryDao: CountryDao,
    private val cityDao: CityDao
) : ReferenceDataRepository {

    override fun observeRegions(): Flow<List<Region>> =
        regionDao.observeActiveWithCountries().map { items -> items.map { it.toDomain() } }

    override fun observeCurrencies(): Flow<List<Currency>> =
        currencyDao.observeActive().map { items -> items.map { it.toDomain() } }

    override fun observeCountriesByRegion(regionCode: String): Flow<List<Country>> =
        countryDao.observeActiveByRegion(regionCode).map { items -> items.map { it.toDomain() } }

    override fun observeCitiesByCountry(countryCode: String): Flow<List<City>> =
        cityDao.observeActiveByCountry(countryCode).map { items -> items.map { it.toDomain() } }

    override suspend fun getRegion(code: String): Region? =
        regionDao.getByCode(code)?.toDomain()

    override suspend fun getCountry(code: String): Country? =
        countryDao.getByCode(code)?.toDomain()

    override suspend fun findCountryByName(name: String): Country? =
        countryDao.findByName(name.trim())?.toDomain()

    override suspend fun getCity(cityId: String): City? =
        cityDao.getById(cityId)?.toDomain()

    override suspend fun findCityByName(name: String): City? =
        cityDao.findByName(name.trim())?.toDomain()
}
