package com.travelsync.core.data.local

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.travelsync.core.data.local.seed.app.AppDatabaseSeeder
import com.travelsync.core.data.local.seed.demo.DemoDatabaseSeeder
import com.travelsync.core.data.local.seed.demo.DemoSeedData
import com.travelsync.core.security.PasswordHasher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DemoDatabaseSeederTest {

    private lateinit var database: TravelSyncDatabase

    @Before
    fun setUp() {
        database = TestDatabaseHelper.createEmptyInMemoryDatabase()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun seed_createsUserWithKnownCredentials() = runTest {
        AppDatabaseSeeder.seed(database)
        DemoDatabaseSeeder.seed(database)

        val user = database.userDao().getUserByUsername(DemoSeedData.USERNAME)
        assertNotNull(user)
        assertEquals(DemoSeedData.USER_ID, user?.id)
        assertEquals(DemoSeedData.DISPLAY_NAME, user?.displayName)
        assertTrue(
            PasswordHasher.verify(
                DemoSeedData.PASSWORD,
                user!!.passwordSalt,
                user.passwordHash
            )
        )
    }

    @Test
    fun seed_createsTripWithItineraryBudgetAndPacking() = runTest {
        AppDatabaseSeeder.seed(database)
        DemoDatabaseSeeder.seed(database)

        val trip = database.tripDao().getTripOnce(DemoSeedData.TRIP_ID)
        assertNotNull(trip)
        assertEquals(DemoSeedData.TRIP_TITLE, trip?.title)
        assertEquals(DemoSeedData.USER_ID, trip?.ownerUserId)

        val days = database.tripDayDao().getDaysForTripOnce(DemoSeedData.TRIP_ID)
        assertEquals(3, days.size)

        val activities = database.activityDao().getActivitiesForTrip(DemoSeedData.TRIP_ID).first()
        assertEquals(3, activities.size)

        val expenses = database.expenseDao().getExpensesForTrip(DemoSeedData.TRIP_ID).first()
        assertEquals(2, expenses.size)

        val packing = database.packingDao().getPackingItemsForTrip(DemoSeedData.TRIP_ID).first()
        assertEquals(3, packing.size)
    }

    @Test
    fun seedIfEmpty_isIdempotent() = runTest {
        AppDatabaseSeeder.seed(database)
        DemoDatabaseSeeder.seedIfEmpty(database)
        DemoDatabaseSeeder.seedIfEmpty(database)

        val user = database.userDao().getUserByUsername(DemoSeedData.USERNAME)
        assertNotNull(user)

        val trips = database.tripDao().getTripsForUser(DemoSeedData.USER_ID).first()
        assertEquals(1, trips.size)
    }

    @Test
    fun createDemoSeededInMemoryDatabase_loadsAppAndDemoSeed() = runTest {
        database.close()
        database = TestDatabaseHelper.createDemoSeededInMemoryDatabase()

        assertTrue(AppDatabaseSeeder.isSeeded(database))
        assertTrue(DemoDatabaseSeeder.isSeeded(database))
        assertNotNull(database.tripDao().getTripOnce(DemoSeedData.TRIP_ID))
    }
}
