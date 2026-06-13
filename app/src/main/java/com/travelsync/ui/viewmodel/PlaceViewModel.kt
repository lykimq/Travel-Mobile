package com.travelsync.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travelsync.core.domain.model.Place
import com.travelsync.core.domain.model.Trip
import com.travelsync.core.domain.model.TripDay
import com.travelsync.core.domain.repository.ItineraryRepository
import com.travelsync.core.domain.repository.PlaceRepository
import com.travelsync.core.domain.repository.TripRepository
import com.travelsync.core.domain.usecase.ActivityFromPlace
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaceViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    tripRepository: TripRepository,
    private val placeRepository: PlaceRepository,
    private val itineraryRepository: ItineraryRepository
) : ViewModel() {

    private val tripId: String = checkNotNull(savedStateHandle["tripId"])

    val trip: StateFlow<Trip?> = tripRepository.getTrip(tripId).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    val tripDays: StateFlow<List<TripDay>> = tripRepository.getDays(tripId).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    val places: StateFlow<List<Place>> = tripRepository.getTrip(tripId)
        .flatMapLatest { trip ->
            if (trip == null) {
                flowOf(emptyList())
            } else {
                placeRepository.getPlacesForTrip(tripId, trip.destination, trip.cityId)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun addPlace(place: Place, onSuccess: () -> Unit) {
        viewModelScope.launch {
            placeRepository.addPlace(place).onSuccess { onSuccess() }
        }
    }

    fun deletePlace(placeId: String) {
        viewModelScope.launch { placeRepository.deletePlace(placeId) }
    }

    fun addPlaceToItinerary(place: Place, tripDayId: String, userId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val activity = ActivityFromPlace.create(place, tripId, tripDayId, userId)
            itineraryRepository.addActivity(activity).onSuccess { onSuccess() }
        }
    }
}
