package com.travelsync.core.domain.repository

import com.travelsync.core.domain.model.*
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<User?>
    suspend fun isLoggedIn(): Boolean
    suspend fun register(
        username: String,
        password: String,
        displayName: String,
        email: String? = null
    ): Result<User>
    suspend fun login(username: String, password: String): Result<User>
    suspend fun logout(): Result<Unit>
    suspend fun updateProfile(
        displayName: String,
        email: String?,
        bio: String?,
        homeCurrency: String
    ): Result<User>
}

interface TripRepository {
    fun getTrips(userId: String): Flow<List<Trip>>
    fun getTrip(tripId: String): Flow<Trip?>
    suspend fun createTrip(trip: Trip): Result<String>
    suspend fun updateTrip(trip: Trip): Result<Unit>
    suspend fun deleteTrip(tripId: String): Result<Unit>
    suspend fun archiveTrip(tripId: String): Result<Unit>
    fun getDays(tripId: String): Flow<List<TripDay>>
}

interface ItineraryRepository {
    fun getActivities(tripId: String): Flow<List<Activity>>
    fun observeActivity(activityId: String): Flow<Activity?>
    suspend fun addActivity(activity: Activity): Result<String>
    suspend fun updateActivity(activity: Activity): Result<Unit>
    suspend fun deleteActivity(activityId: String): Result<Unit>
}

interface PlaceRepository {
    fun getPlacesForTrip(tripId: String, destination: String, cityId: String? = null): Flow<List<Place>>
    suspend fun getPlace(placeId: String): Place?
    suspend fun addPlace(place: Place): Result<String>
    suspend fun updatePlace(place: Place): Result<Unit>
    suspend fun deletePlace(placeId: String): Result<Unit>
}

interface ExpenseRepository {
    fun getExpenses(tripId: String): Flow<List<Expense>>
    suspend fun addExpense(expense: Expense): Result<String>
    suspend fun updateExpense(expense: Expense): Result<Unit>
    suspend fun deleteExpense(expenseId: String): Result<Unit>
}

interface PackingRepository {
    fun getPackingItems(tripId: String): Flow<List<PackingItem>>
    suspend fun addPackingItem(item: PackingItem): Result<String>
    suspend fun updatePackingItem(item: PackingItem): Result<Unit>
    suspend fun deletePackingItem(itemId: String): Result<Unit>
}

interface DatabaseRepository {
    suspend fun clearAllData(): Result<Unit>
}

interface ReferenceDataRepository {
    fun observeRegions(): Flow<List<Region>>
    fun observeCurrencies(): Flow<List<Currency>>
    fun observeCountriesByRegion(regionCode: String): Flow<List<Country>>
    fun observeCitiesByCountry(countryCode: String): Flow<List<City>>
    suspend fun getRegion(code: String): Region?
    suspend fun getCountry(code: String): Country?
    suspend fun findCountryByName(name: String): Country?
    suspend fun getCity(cityId: String): City?
    suspend fun findCityByName(name: String): City?
}
