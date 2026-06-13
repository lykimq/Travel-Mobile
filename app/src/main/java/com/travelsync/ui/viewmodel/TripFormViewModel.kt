package com.travelsync.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travelsync.core.domain.model.City
import com.travelsync.core.domain.model.Trip
import com.travelsync.core.domain.repository.ReferenceDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TripFormViewModel @Inject constructor(
    private val referenceDataRepository: ReferenceDataRepository
) : ViewModel() {

    val regions = referenceDataRepository.observeRegions().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    val currencies = referenceDataRepository.observeCurrencies().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    private val _selectedRegionCode = MutableStateFlow<String?>(null)
    val selectedRegionCode: StateFlow<String?> = _selectedRegionCode.asStateFlow()

    private val _selectedCountryCode = MutableStateFlow<String?>(null)
    val selectedCountryCode: StateFlow<String?> = _selectedCountryCode.asStateFlow()

    private val _selectedCityId = MutableStateFlow<String?>(null)
    val selectedCityId: StateFlow<String?> = _selectedCityId.asStateFlow()

    private val _selectedCurrencyCode = MutableStateFlow("USD")
    val selectedCurrencyCode: StateFlow<String> = _selectedCurrencyCode.asStateFlow()

    val countries: StateFlow<List<com.travelsync.core.domain.model.Country>> =
        _selectedRegionCode.flatMapLatest { regionCode ->
            if (regionCode.isNullOrBlank()) {
                flowOf(emptyList())
            } else {
                referenceDataRepository.observeCountriesByRegion(regionCode)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val cities: StateFlow<List<City>> = _selectedCountryCode.flatMapLatest { countryCode ->
        if (countryCode.isNullOrBlank()) {
            flowOf(emptyList())
        } else {
            referenceDataRepository.observeCitiesByCountry(countryCode)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    fun initializeFromTrip(trip: Trip?) {
        if (trip == null) return
        viewModelScope.launch {
            _selectedCurrencyCode.value = trip.currency.ifBlank { "USD" }
            val city = when {
                !trip.cityId.isNullOrBlank() -> referenceDataRepository.getCity(trip.cityId!!)
                trip.destination.isNotBlank() -> referenceDataRepository.findCityByName(trip.destination)
                else -> null
            }
            if (city != null) {
                val country = referenceDataRepository.getCountry(city.countryCode)
                _selectedRegionCode.value = country?.regionCode
                _selectedCountryCode.value = city.countryCode
                _selectedCityId.value = city.id
                if (trip.currency.isBlank()) {
                    _selectedCurrencyCode.value = city.defaultCurrencyCode
                }
            } else if (trip.destinationCountry.isNotBlank()) {
                val country = referenceDataRepository.findCountryByName(trip.destinationCountry)
                _selectedRegionCode.value = country?.regionCode
                _selectedCountryCode.value = country?.code
            }
        }
    }

    fun onRegionSelected(regionCode: String) {
        _selectedRegionCode.value = regionCode
        _selectedCountryCode.value = null
        _selectedCityId.value = null
    }

    fun onCountrySelected(countryCode: String) {
        _selectedCountryCode.value = countryCode
        _selectedCityId.value = null
        viewModelScope.launch {
            referenceDataRepository.getCountry(countryCode)?.let { country ->
                _selectedCurrencyCode.value = country.defaultCurrencyCode
            }
        }
    }

    fun onCitySelected(city: City) {
        _selectedCityId.value = city.id
        _selectedCurrencyCode.value = city.defaultCurrencyCode
    }

    fun onCurrencySelected(currencyCode: String) {
        _selectedCurrencyCode.value = currencyCode
    }

    suspend fun resolveSelection(): TripLocationSelection? {
        val cityId = _selectedCityId.value ?: return null
        val city = referenceDataRepository.getCity(cityId) ?: return null
        val country = referenceDataRepository.getCountry(city.countryCode) ?: return null
        return TripLocationSelection(
            cityId = city.id,
            destination = city.name,
            destinationCountry = country.name,
            currency = _selectedCurrencyCode.value
        )
    }

    data class TripLocationSelection(
        val cityId: String,
        val destination: String,
        val destinationCountry: String,
        val currency: String
    )
}
