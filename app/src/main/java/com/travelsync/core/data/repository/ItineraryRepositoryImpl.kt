package com.travelsync.core.data.repository

import com.travelsync.core.data.local.dao.ActivityDao
import com.travelsync.core.data.local.dao.TripDayDao
import com.travelsync.core.data.local.mapper.activityEntityFromDomain
import com.travelsync.core.data.local.mapper.toDomain
import com.travelsync.core.domain.model.Activity
import com.travelsync.core.domain.repository.ItineraryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ItineraryRepositoryImpl @Inject constructor(
    private val activityDao: ActivityDao,
    private val tripDayDao: TripDayDao
) : ItineraryRepository {

    override fun getActivities(tripId: String): Flow<List<Activity>> =
        activityDao.getActivitiesForTrip(tripId).mapLatest { activities ->
            val days = tripDayDao.getDaysForTripOnce(tripId).associateBy { it.id }
            activities.map { it.toDomain(days[it.tripDayId]) }
        }

    override fun observeActivity(activityId: String): Flow<Activity?> =
        activityDao.observeActivity(activityId).mapLatest { entity ->
            entity?.let {
                val day = tripDayDao.getDayById(it.tripDayId)
                it.toDomain(day)
            }
        }

    override suspend fun addActivity(activity: Activity): Result<String> = runCatching {
        val id = activity.id.ifEmpty { UUID.randomUUID().toString() }
        val now = System.currentTimeMillis()
        activityDao.insertActivity(activityEntityFromDomain(activity.copy(id = id), now))
        id
    }

    override suspend fun updateActivity(activity: Activity): Result<Unit> = runCatching {
        val existing = activityDao.getActivityOnce(activity.id)
            ?: throw IllegalStateException("Activity not found")
        activityDao.insertActivity(
            existing.copy(
                tripDayId = activity.tripDayId,
                placeId = activity.placeId,
                title = activity.title,
                description = activity.description,
                locationName = activity.locationName,
                address = activity.address,
                latitude = activity.latitude,
                longitude = activity.longitude,
                startTime = activity.startTime.time,
                endTime = activity.endTime.time,
                category = activity.category.name,
                transportMode = activity.transportMode.name,
                estimatedCost = activity.estimatedCost,
                actualCost = activity.actualCost,
                bookingLink = activity.bookingLink,
                bookingReference = activity.bookingReference,
                status = activity.status.name,
                sortOrder = activity.sortOrder,
                notes = activity.notes,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    override suspend fun deleteActivity(activityId: String): Result<Unit> = runCatching {
        activityDao.deleteActivity(activityId)
    }
}
