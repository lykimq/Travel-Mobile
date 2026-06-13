package com.travelsync.core.data.local.reference.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "regions",
    indices = [Index("sortOrder"), Index("isActive")]
)
data class RegionEntity(
    @PrimaryKey val code: String,
    val name: String,
    val sortOrder: Int,
    val isActive: Boolean = true
)

@Entity(
    tableName = "currencies",
    indices = [Index("sortOrder"), Index("isActive")]
)
data class CurrencyEntity(
    @PrimaryKey val code: String,
    val name: String,
    val symbol: String,
    val decimalPlaces: Int,
    val sortOrder: Int,
    val isActive: Boolean = true
)

@Entity(
    tableName = "countries",
    foreignKeys = [
        ForeignKey(
            entity = RegionEntity::class,
            parentColumns = ["code"],
            childColumns = ["regionCode"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = CurrencyEntity::class,
            parentColumns = ["code"],
            childColumns = ["defaultCurrencyCode"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("regionCode"), Index("defaultCurrencyCode"), Index("sortOrder"), Index("isActive")]
)
data class CountryEntity(
    @PrimaryKey val code: String,
    val name: String,
    val regionCode: String,
    val defaultCurrencyCode: String,
    val sortOrder: Int,
    val isActive: Boolean = true
)

@Entity(
    tableName = "cities",
    foreignKeys = [
        ForeignKey(
            entity = CountryEntity::class,
            parentColumns = ["code"],
            childColumns = ["countryCode"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = CurrencyEntity::class,
            parentColumns = ["code"],
            childColumns = ["defaultCurrencyCode"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index("countryCode"),
        Index("defaultCurrencyCode"),
        Index("name"),
        Index("sortOrder"),
        Index("isActive")
    ]
)
data class CityEntity(
    @PrimaryKey val id: String,
    val name: String,
    val countryCode: String,
    val defaultCurrencyCode: String,
    val latitude: Double,
    val longitude: Double,
    val searchAliases: String,
    val sortOrder: Int,
    val isActive: Boolean = true
)
