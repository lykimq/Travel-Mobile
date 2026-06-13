package com.travelsync.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travelsync.core.domain.model.Activity
import com.travelsync.core.domain.model.Place
import com.travelsync.core.domain.repository.ItineraryRepository
import com.travelsync.core.domain.repository.PlaceRepository
import com.travelsync.core.domain.usecase.ActivityFromPlace
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActivityEditorViewModel @Inject constructor(
    private val itineraryRepository: ItineraryRepository,
    private val placeRepository: PlaceRepository
) : ViewModel() {

    fun observeActivity(activityId: String): Flow<Activity?> =
        itineraryRepository.observeActivity(activityId)

    suspend fun activityFromPlace(
        placeId: String,
        tripId: String,
        tripDayId: String,
        userId: String
    ): Activity? {
        val place = placeRepository.getPlace(placeId) ?: return null
        return ActivityFromPlace.create(place, tripId, tripDayId, userId)
    }

    fun saveActivity(activity: Activity, userId: String, onComplete: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val payload = activity.copy(createdByUserId = userId)
            val result = if (payload.id.isEmpty()) {
                itineraryRepository.addActivity(payload).map { }
            } else {
                itineraryRepository.updateActivity(payload)
            }
            onComplete(result)
        }
    }

    fun deleteActivity(activityId: String, onComplete: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            onComplete(itineraryRepository.deleteActivity(activityId))
        }
    }
}
