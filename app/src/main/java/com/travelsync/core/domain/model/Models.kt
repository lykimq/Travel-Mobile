package com.travelsync.core.domain.model

import java.util.Date

data class User(
    val id: String = "",
    val username: String = "",
    val displayName: String = "",
    val email: String? = null,
    val bio: String? = null,
    val homeCurrency: String = "USD",
    val createdAt: Date = Date(),
    val lastLoginAt: Date? = null,
    val travelStats: TravelStats = TravelStats()
)

data class TravelStats(
    val totalTrips: Int = 0,
    val totalCountries: Int = 0,
    val totalSpent: Double = 0.0
)

data class Currency(
    val code: String = "",
    val name: String = "",
    val symbol: String = "",
    val decimalPlaces: Int = 2
) {
    val label: String get() = "$code - $name"
}

data class Region(
    val code: String = "",
    val name: String = ""
)

data class Country(
    val code: String = "",
    val name: String = "",
    val regionCode: String = "",
    val defaultCurrencyCode: String = "USD"
)

data class City(
    val id: String = "",
    val name: String = "",
    val countryCode: String = "",
    val defaultCurrencyCode: String = "USD",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val searchAliases: List<String> = emptyList()
)

enum class TripType {
    LEISURE, BUSINESS, ADVENTURE, ROAD_TRIP, FAMILY, ROMANTIC, OTHER
}

enum class TripStatus {
    PLANNED, ACTIVE, COMPLETED, ARCHIVED
}

data class Trip(
    val id: String = "",
    val ownerUserId: String = "",
    val cityId: String? = null,
    val title: String = "",
    val destination: String = "",
    val destinationCountry: String = "",
    val startDate: Date = Date(),
    val endDate: Date = Date(),
    val currency: String = "USD",
    val budgetAmount: Double? = null,
    val notes: String = "",
    val type: TripType = TripType.LEISURE,
    val status: TripStatus = TripStatus.PLANNED
)

data class TripDay(
    val id: String = "",
    val tripId: String = "",
    val dayNumber: Int = 1,
    val date: Date = Date(),
    val title: String = "",
    val notes: String = "",
    val sortOrder: Int = 0
) {
    val label: String get() = title.ifEmpty { "Day $dayNumber" }
}

enum class ActivityCategory {
    SIGHTSEEING, FOOD, TRANSPORT, ACCOMMODATION, LEISURE, FLIGHT, SHOPPING, OTHER
}

enum class TransportMode {
    WALKING, BICYCLE, CAR, TRANSIT, FLIGHT, OTHER
}

enum class ActivityStatus {
    PLANNED, CONFIRMED, DONE, CANCELLED
}

data class Activity(
    val id: String = "",
    val tripId: String = "",
    val tripDayId: String = "",
    val dayNumber: Int = 1,
    val dayLabel: String = "Day 1",
    val placeId: String? = null,
    val title: String = "",
    val description: String = "",
    val locationName: String = "",
    val address: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val startTime: Date = Date(),
    val endTime: Date = Date(),
    val category: ActivityCategory = ActivityCategory.SIGHTSEEING,
    val transportMode: TransportMode = TransportMode.WALKING,
    val estimatedCost: Double = 0.0,
    val actualCost: Double? = null,
    val bookingLink: String? = null,
    val bookingReference: String? = null,
    val status: ActivityStatus = ActivityStatus.PLANNED,
    val sortOrder: Int = 0,
    val notes: String = "",
    val createdByUserId: String = ""
)

enum class PlaceCategory {
    ATTRACTION, RESTAURANT, HOTEL, PARKING, SHOPPING, TRANSPORT, OTHER
}

data class Place(
    val id: String = "",
    val tripId: String? = null,
    val cityId: String? = null,
    val ownerUserId: String? = null,
    val destinationTag: String = "",
    val name: String = "",
    val description: String = "",
    val address: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val category: PlaceCategory = PlaceCategory.ATTRACTION,
    val priceMin: Double? = null,
    val priceMax: Double? = null,
    val priceNote: String = "",
    val currency: String = "",
    val rating: Float? = null,
    val externalLink: String? = null,
    val phone: String? = null,
    val isCatalog: Boolean = false
) {
    /** 0.0, 0.0 is the stored default for a place without coordinates. */
    val hasCoordinates: Boolean get() = latitude != 0.0 || longitude != 0.0
}

enum class ExpenseCategory {
    FOOD, TRANSPORT, ACCOMMODATION, ACTIVITIES, SHOPPING, EMERGENCY, OTHER
}

data class Expense(
    val id: String = "",
    val tripId: String = "",
    val title: String = "",
    val description: String = "",
    val amount: Double = 0.0,
    val currency: String = "USD",
    val category: ExpenseCategory = ExpenseCategory.OTHER,
    val expenseDate: Date = Date(),
    val createdByUserId: String = ""
)

data class PackingItem(
    val id: String = "",
    val tripId: String = "",
    val title: String = "",
    val category: String = "General",
    val quantity: Int = 1,
    val isPacked: Boolean = false,
    val sortOrder: Int = 0,
    val notes: String = ""
)
