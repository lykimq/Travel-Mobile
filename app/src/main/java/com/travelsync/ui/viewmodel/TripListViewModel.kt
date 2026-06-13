package com.travelsync.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travelsync.core.domain.model.Trip
import com.travelsync.core.domain.repository.AuthRepository
import com.travelsync.core.domain.repository.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TripListViewModel @Inject constructor(
    authRepository: AuthRepository,
    private val tripRepository: TripRepository
) : ViewModel() {

    val currentUser = authRepository.currentUser.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    val trips: StateFlow<List<Trip>> = authRepository.currentUser.flatMapLatest { user ->
        if (user == null) flowOf(emptyList()) else tripRepository.getTrips(user.id)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    fun createTrip(trip: Trip, ownerUserId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            tripRepository.createTrip(trip.copy(ownerUserId = ownerUserId))
                .onSuccess { onSuccess() }
        }
    }

    fun updateTrip(trip: Trip, onSuccess: () -> Unit) {
        viewModelScope.launch {
            tripRepository.updateTrip(trip).onSuccess { onSuccess() }
        }
    }

    fun deleteTrip(tripId: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            tripRepository.deleteTrip(tripId).onSuccess { onSuccess() }
        }
    }
}
