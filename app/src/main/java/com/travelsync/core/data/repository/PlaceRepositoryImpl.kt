package com.travelsync.core.data.repository

import com.travelsync.core.data.local.dao.PlaceDao
import com.travelsync.core.data.local.mapper.placeEntityFromDomain
import com.travelsync.core.data.local.mapper.toDomain
import com.travelsync.core.domain.model.Place
import com.travelsync.core.domain.repository.PlaceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaceRepositoryImpl @Inject constructor(
    private val placeDao: PlaceDao
) : PlaceRepository {

    override fun getPlacesForTrip(tripId: String, destination: String, cityId: String?): Flow<List<Place>> {
        val tag = destination.split(",").first().trim()
        return placeDao.getPlacesForTrip(tripId, tag, cityId).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getPlace(placeId: String): Place? =
        placeDao.getPlaceOnce(placeId)?.toDomain()

    override suspend fun addPlace(place: Place): Result<String> = runCatching {
        val id = place.id.ifEmpty { UUID.randomUUID().toString() }
        placeDao.insertPlace(placeEntityFromDomain(place.copy(id = id)))
        id
    }

    override suspend fun updatePlace(place: Place): Result<Unit> = runCatching {
        placeDao.insertPlace(placeEntityFromDomain(place))
    }

    override suspend fun deletePlace(placeId: String): Result<Unit> = runCatching {
        placeDao.deleteUserPlace(placeId)
    }
}
