package com.travelsync.core.data.local

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.travelsync.core.data.local.seed.app.AppDatabaseSeeder
import com.travelsync.core.data.local.seed.demo.DemoSeedData
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppDatabaseSeederTest {

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
    fun seed_loadsReferenceDataAndPlaceCatalog() = runTest {
        AppDatabaseSeeder.seed(database)

        assertTrue(database.currencyDao().count() >= 90)
        assertTrue(database.regionDao().count() >= 6)
        assertTrue(database.countryDao().count() >= 130)
        assertTrue(database.cityDao().count() >= 270)
        assertTrue(database.placeDao().countCatalogPlaces() >= 3)
        assertNotNull(database.cityDao().getById(DemoSeedData.CITY_ID))
    }

    @Test
    fun seedIfEmpty_isIdempotent() = runTest {
        AppDatabaseSeeder.seedIfEmpty(database)
        val currencyCount = database.currencyDao().count()
        val catalogCount = database.placeDao().countCatalogPlaces()

        AppDatabaseSeeder.seedIfEmpty(database)

        assertEquals(currencyCount, database.currencyDao().count())
        assertEquals(catalogCount, database.placeDao().countCatalogPlaces())
    }

    @Test
    fun createAppSeededInMemoryDatabase_loadsAppSeedOnly() = runTest {
        database.close()
        database = TestDatabaseHelper.createAppSeededInMemoryDatabase()

        assertTrue(AppDatabaseSeeder.isSeeded(database))
        assertTrue(database.userDao().getUserByUsername(DemoSeedData.USERNAME) == null)
    }
}
