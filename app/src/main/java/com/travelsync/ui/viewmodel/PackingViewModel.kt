package com.travelsync.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travelsync.core.domain.model.PackingItem
import com.travelsync.core.domain.model.Trip
import com.travelsync.core.domain.repository.PackingRepository
import com.travelsync.core.domain.repository.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PackingViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    tripRepository: TripRepository,
    private val packingRepository: PackingRepository
) : ViewModel() {

    private val tripId: String = checkNotNull(savedStateHandle["tripId"])

    val trip: StateFlow<Trip?> = tripRepository.getTrip(tripId).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    val items: StateFlow<List<PackingItem>> = packingRepository.getPackingItems(tripId).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    fun toggleItem(item: PackingItem) {
        viewModelScope.launch {
            packingRepository.updatePackingItem(item.copy(isPacked = !item.isPacked))
        }
    }

    fun addItem(title: String, category: String) {
        viewModelScope.launch {
            packingRepository.addPackingItem(
                PackingItem(tripId = tripId, title = title, category = category)
            )
        }
    }

    fun deleteItem(itemId: String) {
        viewModelScope.launch { packingRepository.deletePackingItem(itemId) }
    }
}
