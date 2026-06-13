package com.travelsync.core.data.local.mapper

import com.travelsync.core.data.local.entity.*
import com.travelsync.core.domain.model.*
import java.util.Date

fun UserEntity.toDomain(stats: TravelStats = TravelStats()): User = User(
    id = id,
    username = username,
    displayName = displayName,
    email = email,
    bio = bio,
    homeCurrency = homeCurrency,
    createdAt = Date(createdAt),
    lastLoginAt = lastLoginAt?.let { Date(it) },
    travelStats = stats
)

fun tripEntityFromDomain(trip: Trip, now: Long = System.currentTimeMillis()): TripEntity = TripEntity(
    id = trip.id,
    ownerUserId = trip.ownerUserId,
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
    createdAt = now,
    updatedAt = now
)

fun TripEntity.toDomain(): Trip = Trip(
    id = id,
    ownerUserId = ownerUserId,
    cityId = cityId,
    title = title,
    destination = destination,
    destinationCountry = destinationCountry,
    startDate = Date(startDate),
    endDate = Date(endDate),
    currency = currency,
    budgetAmount = budgetAmount,
    notes = notes,
    type = TripType.valueOf(type),
    status = TripStatus.valueOf(status)
)

fun TripDayEntity.toDomain(): TripDay = TripDay(
    id = id,
    tripId = tripId,
    dayNumber = dayNumber,
    date = Date(date),
    title = title,
    notes = notes,
    sortOrder = sortOrder
)

fun ActivityEntity.toDomain(day: TripDayEntity?): Activity {
    val dayNumber = day?.dayNumber ?: 1
    val dayLabel = day?.title?.takeIf { it.isNotEmpty() } ?: "Day $dayNumber"
    return Activity(
        id = id,
        tripId = tripId,
        tripDayId = tripDayId,
        dayNumber = dayNumber,
        dayLabel = dayLabel,
        placeId = placeId,
        title = title,
        description = description,
        locationName = locationName,
        address = address,
        latitude = latitude,
        longitude = longitude,
        startTime = Date(startTime),
        endTime = Date(endTime),
        category = ActivityCategory.valueOf(category),
        transportMode = TransportMode.valueOf(transportMode),
        estimatedCost = estimatedCost,
        actualCost = actualCost,
        bookingLink = bookingLink,
        bookingReference = bookingReference,
        status = ActivityStatus.valueOf(status),
        sortOrder = sortOrder,
        notes = notes,
        createdByUserId = createdByUserId.orEmpty()
    )
}

fun activityEntityFromDomain(activity: Activity, now: Long = System.currentTimeMillis()): ActivityEntity =
    ActivityEntity(
        id = activity.id,
        tripId = activity.tripId,
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
        createdByUserId = activity.createdByUserId.ifEmpty { null },
        createdAt = now,
        updatedAt = now
    )

fun PlaceEntity.toDomain(): Place = Place(
    id = id,
    tripId = tripId,
    cityId = cityId,
    ownerUserId = ownerUserId,
    destinationTag = destinationTag,
    name = name,
    description = description,
    address = address,
    latitude = latitude,
    longitude = longitude,
    category = PlaceCategory.valueOf(category),
    priceMin = priceMin,
    priceMax = priceMax,
    priceNote = priceNote,
    currency = currency,
    rating = rating,
    externalLink = externalLink,
    phone = phone,
    isCatalog = isCatalog
)

fun ExpenseEntity.toDomain(): Expense = Expense(
    id = id,
    tripId = tripId,
    title = title,
    description = description,
    amount = amount,
    currency = currency,
    category = ExpenseCategory.valueOf(category),
    expenseDate = Date(expenseDate),
    createdByUserId = createdByUserId.orEmpty()
)

fun expenseEntityFromDomain(expense: Expense, now: Long = System.currentTimeMillis()): ExpenseEntity =
    ExpenseEntity(
        id = expense.id,
        tripId = expense.tripId,
        title = expense.title,
        description = expense.description,
        amount = expense.amount,
        currency = expense.currency,
        category = expense.category.name,
        expenseDate = expense.expenseDate.time,
        createdByUserId = expense.createdByUserId.ifEmpty { null },
        createdAt = now,
        updatedAt = now
    )

fun PackingItemEntity.toDomain(): PackingItem = PackingItem(
    id = id,
    tripId = tripId,
    title = title,
    category = category,
    quantity = quantity,
    isPacked = isPacked,
    sortOrder = sortOrder,
    notes = notes
)

fun placeEntityFromDomain(place: Place, now: Long = System.currentTimeMillis()): PlaceEntity = PlaceEntity(
    id = place.id,
    tripId = place.tripId,
    cityId = place.cityId,
    ownerUserId = place.ownerUserId,
    destinationTag = place.destinationTag,
    name = place.name,
    description = place.description,
    address = place.address,
    latitude = place.latitude,
    longitude = place.longitude,
    category = place.category.name,
    priceMin = place.priceMin,
    priceMax = place.priceMax,
    priceNote = place.priceNote,
    currency = place.currency,
    rating = place.rating,
    externalLink = place.externalLink,
    phone = place.phone,
    isCatalog = place.isCatalog,
    createdAt = now,
    updatedAt = now
)
