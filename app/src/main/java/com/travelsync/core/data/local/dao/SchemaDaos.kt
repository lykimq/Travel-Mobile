package com.travelsync.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.travelsync.core.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :userId")
    fun observeUser(userId: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: String): UserEntity?

    @Query("SELECT * FROM users WHERE username = :username COLLATE NOCASE LIMIT 1")
    suspend fun getUserByUsername(username: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("SELECT COUNT(*) FROM trips WHERE ownerUserId = :userId AND status != 'ARCHIVED'")
    suspend fun countActiveTrips(userId: String): Int
}

@Dao
interface AppSessionDao {
    @Query("SELECT * FROM app_session WHERE id = 1")
    fun observeSession(): Flow<AppSessionEntity?>

    @Query("SELECT * FROM app_session WHERE id = 1")
    suspend fun getSession(): AppSessionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSession(session: AppSessionEntity)

    @Query("UPDATE app_session SET currentUserId = NULL WHERE id = 1")
    suspend fun clearSession()
}

@Dao
interface TripDao {
    @Query("SELECT * FROM trips WHERE ownerUserId = :userId AND status != 'ARCHIVED' ORDER BY startDate ASC")
    fun getTripsForUser(userId: String): Flow<List<TripEntity>>

    @Query("SELECT * FROM trips WHERE id = :tripId")
    fun getTrip(tripId: String): Flow<TripEntity?>

    @Query("SELECT * FROM trips WHERE id = :tripId")
    suspend fun getTripOnce(tripId: String): TripEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: TripEntity)

    @Query("DELETE FROM trips WHERE id = :tripId")
    suspend fun deleteTrip(tripId: String)

    @Query("UPDATE trips SET status = :status, updatedAt = :updatedAt WHERE id = :tripId")
    suspend fun updateStatus(tripId: String, status: String, updatedAt: Long)
}

@Dao
interface TripDayDao {
    @Query("SELECT * FROM trip_days WHERE tripId = :tripId ORDER BY sortOrder ASC, dayNumber ASC")
    fun getDaysForTrip(tripId: String): Flow<List<TripDayEntity>>

    @Query("SELECT * FROM trip_days WHERE tripId = :tripId ORDER BY sortOrder ASC, dayNumber ASC")
    suspend fun getDaysForTripOnce(tripId: String): List<TripDayEntity>

    @Query("SELECT * FROM trip_days WHERE id = :dayId")
    suspend fun getDayById(dayId: String): TripDayEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(days: List<TripDayEntity>)

    @Query("DELETE FROM trip_days WHERE tripId = :tripId")
    suspend fun deleteDaysForTrip(tripId: String)
}

@Dao
interface ActivityDao {
    @Query("SELECT * FROM activities WHERE tripId = :tripId ORDER BY startTime ASC")
    fun getActivitiesForTrip(tripId: String): Flow<List<ActivityEntity>>

    @Query("SELECT * FROM activities WHERE tripDayId = :tripDayId ORDER BY sortOrder ASC, startTime ASC")
    fun getActivitiesForDay(tripDayId: String): Flow<List<ActivityEntity>>

    @Query("SELECT * FROM activities WHERE id = :activityId")
    fun observeActivity(activityId: String): Flow<ActivityEntity?>

    @Query("SELECT * FROM activities WHERE id = :activityId")
    suspend fun getActivityOnce(activityId: String): ActivityEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: ActivityEntity)

    @Query("DELETE FROM activities WHERE id = :activityId")
    suspend fun deleteActivity(activityId: String)
}

@Dao
interface PlaceDao {
    @Query(
        """
        SELECT * FROM places
        WHERE tripId = :tripId
           OR (isCatalog = 1 AND (
               (:cityId IS NOT NULL AND cityId = :cityId)
               OR LOWER(destinationTag) = LOWER(:destinationTag)
               OR LOWER(:destinationTag) LIKE '%' || LOWER(destinationTag) || '%'
           ))
        ORDER BY category ASC, name ASC
        """
    )
    fun getPlacesForTrip(tripId: String, destinationTag: String, cityId: String?): Flow<List<PlaceEntity>>

    @Query("SELECT * FROM places WHERE id = :placeId")
    suspend fun getPlaceOnce(placeId: String): PlaceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlace(place: PlaceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(places: List<PlaceEntity>)

    @Query("DELETE FROM places WHERE id = :placeId AND isCatalog = 0")
    suspend fun deleteUserPlace(placeId: String)

    @Query("SELECT COUNT(*) FROM places WHERE isCatalog = 1")
    suspend fun countCatalogPlaces(): Int
}

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses WHERE tripId = :tripId ORDER BY expenseDate DESC")
    fun getExpensesForTrip(tripId: String): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE id = :expenseId")
    suspend fun getExpenseOnce(expenseId: String): ExpenseEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity)

    @Query("DELETE FROM expenses WHERE id = :expenseId")
    suspend fun deleteExpense(expenseId: String)
}

@Dao
interface PackingDao {
    @Query("SELECT * FROM packing_items WHERE tripId = :tripId ORDER BY sortOrder ASC, category ASC")
    fun getPackingItemsForTrip(tripId: String): Flow<List<PackingItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPackingItem(item: PackingItemEntity)

    @Query("DELETE FROM packing_items WHERE id = :itemId")
    suspend fun deletePackingItem(itemId: String)
}
