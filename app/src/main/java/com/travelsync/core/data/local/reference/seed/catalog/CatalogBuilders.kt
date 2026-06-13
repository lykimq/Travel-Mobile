package com.travelsync.core.data.local.reference.seed.catalog

import com.travelsync.core.data.local.reference.entity.CityEntity
import com.travelsync.core.data.local.reference.entity.CountryEntity
import com.travelsync.core.data.local.reference.entity.CurrencyEntity
import com.travelsync.core.data.local.reference.entity.RegionEntity

internal fun region(code: String, name: String, sortOrder: Int) = RegionEntity(
    code = code,
    name = name,
    sortOrder = sortOrder
)

internal fun currency(
    code: String,
    name: String,
    symbol: String,
    decimalPlaces: Int,
    sortOrder: Int
) = CurrencyEntity(
    code = code,
    name = name,
    symbol = symbol,
    decimalPlaces = decimalPlaces,
    sortOrder = sortOrder
)

internal fun country(
    code: String,
    name: String,
    regionCode: String,
    defaultCurrencyCode: String,
    sortOrder: Int
) = CountryEntity(
    code = code,
    name = name,
    regionCode = regionCode,
    defaultCurrencyCode = defaultCurrencyCode,
    sortOrder = sortOrder
)

internal fun city(
    id: String,
    name: String,
    countryCode: String,
    defaultCurrencyCode: String,
    latitude: Double,
    longitude: Double,
    searchAliases: String = "",
    sortOrder: Int
) = CityEntity(
    id = id,
    name = name,
    countryCode = countryCode,
    defaultCurrencyCode = defaultCurrencyCode,
    latitude = latitude,
    longitude = longitude,
    searchAliases = searchAliases,
    sortOrder = sortOrder
)
