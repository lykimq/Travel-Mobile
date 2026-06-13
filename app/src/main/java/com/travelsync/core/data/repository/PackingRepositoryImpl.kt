package com.travelsync.core.data.repository

import com.travelsync.core.data.local.dao.PackingDao
import com.travelsync.core.data.local.entity.PackingItemEntity
import com.travelsync.core.data.local.mapper.toDomain
import com.travelsync.core.domain.model.PackingItem
import com.travelsync.core.domain.repository.PackingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PackingRepositoryImpl @Inject constructor(
    private val packingDao: PackingDao
) : PackingRepository {

    override fun getPackingItems(tripId: String): Flow<List<PackingItem>> =
        packingDao.getPackingItemsForTrip(tripId).map { list -> list.map { it.toDomain() } }

    override suspend fun addPackingItem(item: PackingItem): Result<String> = runCatching {
        val id = item.id.ifEmpty { UUID.randomUUID().toString() }
        packingDao.insertPackingItem(
            PackingItemEntity(
                id = id,
                tripId = item.tripId,
                title = item.title,
                category = item.category,
                quantity = item.quantity,
                isPacked = item.isPacked,
                sortOrder = item.sortOrder,
                notes = item.notes,
                createdAt = System.currentTimeMillis()
            )
        )
        id
    }

    override suspend fun updatePackingItem(item: PackingItem): Result<Unit> = runCatching {
        packingDao.insertPackingItem(
            PackingItemEntity(
                id = item.id,
                tripId = item.tripId,
                title = item.title,
                category = item.category,
                quantity = item.quantity,
                isPacked = item.isPacked,
                sortOrder = item.sortOrder,
                notes = item.notes,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    override suspend fun deletePackingItem(itemId: String): Result<Unit> = runCatching {
        packingDao.deletePackingItem(itemId)
    }
}
