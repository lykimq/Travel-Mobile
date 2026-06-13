package com.travelsync.core.data.repository

import com.travelsync.core.data.local.TripDayGenerator
import com.travelsync.core.data.local.dao.TripDao
import com.travelsync.core.data.local.dao.TripDayDao
import com.travelsync.core.data.local.entity.TripEntity
import com.travelsync.core.data.local.mapper.toDomain
import com.travelsync.core.data.local.mapper.tripEntityFromDomain
import com.travelsync.core.domain.model.Trip
import com.travelsync.core.domain.model.TripDay
import com.travelsync.core.domain.model.TripStatus
import com.travelsync.core.domain.repository.TripRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TripRepositoryImpl @Inject constructor(
    private val tripDao: TripDao,
    private val tripDayDao: TripDayDao
) : TripRepository {

    override fun getTrips(userId: String): Flow<List<Trip>> =
        tripDao.getTripsForUser(userId).map { trips -> trips.map { it.toDomain() } }

    override fun getTrip(tripId: String): Flow<Trip?> =
        tripDao.getTrip(tripId).map { it?.toDomain() }

    override suspend fun createTrip(trip: Trip): Result<String> = runCatching {
        val id = trip.id.ifEmpty { UUID.randomUUID().toString() }
        val now = System.currentTimeMillis()
        tripDao.insertTrip(tripEntityFromDomain(trip.copy(id = id), now))
        tripDayDao.insertAll(
            TripDayGenerator.generateDays(id, trip.startDate.time, trip.endDate.time)
        )
        id
    }

    override suspend fun updateTrip(trip: Trip): Result<Unit> = runCatching {
        val existing = tripDao.getTripOnce(trip.id) ?: throw IllegalStateException("Trip not found")
        val now = System.currentTimeMillis()
        tripDao.insertTrip(
            existing.copy(
                cityId = trip.cityId,
                title = trip.title,
                destination = trip.destination,
                destinationCountry = trip.destinationCountry,
                startDate = trip.startDate.time,
                endDate = trip.endDate.time,
                currency = trip.currency,
                budgetAmount = trip.budgetAmount,
                notes = trip.notes,
                type = trip.type.name,
                status = trip.status.name,
                updatedAt = now
            )
        )
        syncTripDays(existing, trip)
    }

    override suspend fun deleteTrip(tripId: String): Result<Unit> = runCatching {
        tripDao.deleteTrip(tripId)
    }

    override suspend fun archiveTrip(tripId: String): Result<Unit> = runCatching {
        tripDao.updateStatus(tripId, TripStatus.ARCHIVED.name, System.currentTimeMillis())
    }

    override fun getDays(tripId: String): Flow<List<TripDay>> =
        tripDayDao.getDaysForTrip(tripId).map { list -> list.map { it.toDomain() } }

    private suspend fun syncTripDays(existing: TripEntity, trip: Trip) {
        val datesChanged = existing.startDate != trip.startDate.time ||
            existing.endDate != trip.endDate.time
        if (!datesChanged) return

        val currentDays = tripDayDao.getDaysForTripOnce(trip.id)
        if (currentDays.isEmpty()) {
            tripDayDao.insertAll(
                TripDayGenerator.generateDays(trip.id, trip.startDate.time, trip.endDate.time)
            )
            return
        }

        val generated = TripDayGenerator.generateDays(trip.id, trip.startDate.time, trip.endDate.time)
        if (generated.size == currentDays.size) return

        tripDayDao.deleteDaysForTrip(trip.id)
        tripDayDao.insertAll(generated)
    }
}
