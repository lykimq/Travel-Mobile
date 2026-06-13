package com.travelsync.core.data.local.reference.seed

import com.travelsync.core.data.local.reference.seed.catalog.ReferenceCities
import com.travelsync.core.data.local.reference.seed.catalog.ReferenceCountries
import com.travelsync.core.data.local.reference.seed.catalog.ReferenceCurrencies
import com.travelsync.core.data.local.reference.seed.catalog.ReferenceRegions

/**
 * Assembles bundled app reference data from modular catalog files.
 *
 * Extend coverage by editing files under [catalog]:
 * - [ReferenceRegions] regions list
 * - [ReferenceCurrencies] ISO currency codes
 * - [ReferenceCountries] countries linked to region + currency
 * - [ReferenceCities] cities linked to country + currency
 */
object ReferenceDataCatalog {

    object RegionCode {
        const val ASIA = "ASIA"
        const val EUROPE = "EUROPE"
        const val AMERICAS = "AMERICAS"
        const val OCEANIA = "OCEANIA"
        const val MIDDLE_EAST = "MIDDLE_EAST"
        const val AFRICA = "AFRICA"
    }

    data class ReferenceDataSnapshot(
        val currencies: List<com.travelsync.core.data.local.reference.entity.CurrencyEntity>,
        val regions: List<com.travelsync.core.data.local.reference.entity.RegionEntity>,
        val countries: List<com.travelsync.core.data.local.reference.entity.CountryEntity>,
        val cities: List<com.travelsync.core.data.local.reference.entity.CityEntity>
    )

    fun snapshot(): ReferenceDataSnapshot = ReferenceDataSnapshot(
        currencies = ReferenceCurrencies.all(),
        regions = ReferenceRegions.all(),
        countries = ReferenceCountries.all(),
        cities = ReferenceCities.all()
    )
}
