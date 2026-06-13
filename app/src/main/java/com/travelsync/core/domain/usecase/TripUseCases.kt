package com.travelsync.core.domain.usecase

import com.travelsync.core.domain.model.Activity
import com.travelsync.core.domain.model.ActivityCategory
import com.travelsync.core.domain.model.Expense
import com.travelsync.core.domain.model.ExpenseCategory
import com.travelsync.core.domain.model.Place
import com.travelsync.core.domain.model.Trip
import java.util.Date

data class BudgetSummary(
    val totalSpent: Double,
    val budgetAmount: Double?,
    val remaining: Double?,
    val byCategory: Map<ExpenseCategory, Double>
)

object BudgetSummaryCalculator {
    fun calculate(trip: Trip, expenses: List<Expense>): BudgetSummary {
        val totalSpent = expenses.sumOf { it.amount }
        val remaining = trip.budgetAmount?.let { it - totalSpent }
        val byCategory = expenses
            .groupBy { it.category }
            .mapValues { (_, items) -> items.sumOf { it.amount } }
        return BudgetSummary(
            totalSpent = totalSpent,
            budgetAmount = trip.budgetAmount,
            remaining = remaining,
            byCategory = byCategory
        )
    }
}

object ActivityFromPlace {
    fun create(
        place: Place,
        tripId: String,
        tripDayId: String,
        userId: String,
        startTime: Date = Date(),
        endTime: Date = Date(System.currentTimeMillis() + 3_600_000)
    ): Activity = Activity(
        tripId = tripId,
        tripDayId = tripDayId,
        placeId = place.id,
        title = place.name,
        description = place.description,
        locationName = place.name,
        address = place.address,
        latitude = place.latitude,
        longitude = place.longitude,
        startTime = startTime,
        endTime = endTime,
        category = when (place.category) {
            com.travelsync.core.domain.model.PlaceCategory.RESTAURANT -> ActivityCategory.FOOD
            com.travelsync.core.domain.model.PlaceCategory.HOTEL -> ActivityCategory.ACCOMMODATION
            com.travelsync.core.domain.model.PlaceCategory.TRANSPORT -> ActivityCategory.TRANSPORT
            com.travelsync.core.domain.model.PlaceCategory.SHOPPING -> ActivityCategory.SHOPPING
            else -> ActivityCategory.SIGHTSEEING
        },
        bookingLink = place.externalLink,
        createdByUserId = userId
    )
}
