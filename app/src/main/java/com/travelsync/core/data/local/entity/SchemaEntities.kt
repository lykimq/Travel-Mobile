package com.travelsync.core.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["username"], unique = true)]
)
data class UserEntity(
    @PrimaryKey val id: String,
    val username: String,
    val passwordHash: String,
    val passwordSalt: String,
    val displayName: String,
    val email: String?,
    val bio: String?,
    val homeCurrency: String,
    val createdAt: Long,
    val updatedAt: Long,
    val lastLoginAt: Long?
)

@Entity(tableName = "app_session")
data class AppSessionEntity(
    @PrimaryKey val id: Int = 1,
    val currentUserId: String?
)

@Entity(
    tableName = "trips",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["ownerUserId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("ownerUserId"), Index("status"), Index("cityId")]
)
data class TripEntity(
    @PrimaryKey val id: String,
    val ownerUserId: String,
    val cityId: String?,
    val title: String,
    val destination: String,
    val destinationCountry: String,
    val startDate: Long,
    val endDate: Long,
    val currency: String,
    val budgetAmount: Double?,
    val notes: String,
    val type: String,
    val status: String,
    val createdAt: Long,
    val updatedAt: Long
)

@Entity(
    tableName = "trip_days",
    foreignKeys = [
        ForeignKey(
            entity = TripEntity::class,
            parentColumns = ["id"],
            childColumns = ["tripId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("tripId")]
)
data class TripDayEntity(
    @PrimaryKey val id: String,
    val tripId: String,
    val dayNumber: Int,
    val date: Long,
    val title: String,
    val notes: String,
    val sortOrder: Int
)

@Entity(
    tableName = "places",
    foreignKeys = [
        ForeignKey(
            entity = TripEntity::class,
            parentColumns = ["id"],
            childColumns = ["tripId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["ownerUserId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("tripId"), Index("cityId"), Index("destinationTag"), Index("isCatalog")]
)
data class PlaceEntity(
    @PrimaryKey val id: String,
    val tripId: String?,
    val cityId: String?,
    val ownerUserId: String?,
    val destinationTag: String,
    val name: String,
    val description: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val category: String,
    val priceMin: Double?,
    val priceMax: Double?,
    val priceNote: String,
    val currency: String,
    val rating: Float?,
    val externalLink: String?,
    val phone: String?,
    val isCatalog: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)

@Entity(
    tableName = "activities",
    foreignKeys = [
        ForeignKey(
            entity = TripEntity::class,
            parentColumns = ["id"],
            childColumns = ["tripId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TripDayEntity::class,
            parentColumns = ["id"],
            childColumns = ["tripDayId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = PlaceEntity::class,
            parentColumns = ["id"],
            childColumns = ["placeId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["createdByUserId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("tripId"), Index("tripDayId"), Index("placeId")]
)
data class ActivityEntity(
    @PrimaryKey val id: String,
    val tripId: String,
    val tripDayId: String,
    val placeId: String?,
    val title: String,
    val description: String,
    val locationName: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val startTime: Long,
    val endTime: Long,
    val category: String,
    val transportMode: String,
    val estimatedCost: Double,
    val actualCost: Double?,
    val bookingLink: String?,
    val bookingReference: String?,
    val status: String,
    val sortOrder: Int,
    val notes: String,
    val createdByUserId: String?,
    val createdAt: Long,
    val updatedAt: Long
)

@Entity(
    tableName = "expenses",
    foreignKeys = [
        ForeignKey(
            entity = TripEntity::class,
            parentColumns = ["id"],
            childColumns = ["tripId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["createdByUserId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("tripId")]
)
data class ExpenseEntity(
    @PrimaryKey val id: String,
    val tripId: String,
    val title: String,
    val description: String,
    val amount: Double,
    val currency: String,
    val category: String,
    val expenseDate: Long,
    val createdByUserId: String?,
    val createdAt: Long,
    val updatedAt: Long
)

@Entity(
    tableName = "packing_items",
    foreignKeys = [
        ForeignKey(
            entity = TripEntity::class,
            parentColumns = ["id"],
            childColumns = ["tripId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("tripId")]
)
data class PackingItemEntity(
    @PrimaryKey val id: String,
    val tripId: String,
    val title: String,
    val category: String,
    val quantity: Int,
    val isPacked: Boolean,
    val sortOrder: Int,
    val notes: String,
    val createdAt: Long
)
