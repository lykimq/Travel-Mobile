package com.travelsync.core.data.local.reference.mapper

import com.travelsync.core.data.local.reference.entity.CityEntity
import com.travelsync.core.data.local.reference.entity.CountryEntity
import com.travelsync.core.data.local.reference.entity.CurrencyEntity
import com.travelsync.core.data.local.reference.entity.RegionEntity
import com.travelsync.core.domain.model.City
import com.travelsync.core.domain.model.Country
import com.travelsync.core.domain.model.Currency
import com.travelsync.core.domain.model.Region

fun RegionEntity.toDomain(): Region = Region(
    code = code,
    name = name
)

fun CurrencyEntity.toDomain(): Currency = Currency(
    code = code,
    name = name,
    symbol = symbol,
    decimalPlaces = decimalPlaces
)

fun CountryEntity.toDomain(): Country = Country(
    code = code,
    name = name,
    regionCode = regionCode,
    defaultCurrencyCode = defaultCurrencyCode
)

fun CityEntity.toDomain(): City = City(
    id = id,
    name = name,
    countryCode = countryCode,
    defaultCurrencyCode = defaultCurrencyCode,
    latitude = latitude,
    longitude = longitude,
    searchAliases = searchAliases.split(",").map { it.trim() }.filter { it.isNotEmpty() }
)
