package com.travelsync.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travelsync.core.domain.model.Activity
import com.travelsync.core.domain.model.Trip
import com.travelsync.core.domain.model.TripDay
import com.travelsync.core.domain.repository.ItineraryRepository
import com.travelsync.core.domain.repository.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ItineraryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val tripRepository: TripRepository,
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

    val activities: StateFlow<List<Activity>> = itineraryRepository.getActivities(tripId).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    fun deleteActivity(activityId: String) {
        viewModelScope.launch { itineraryRepository.deleteActivity(activityId) }
    }
}
