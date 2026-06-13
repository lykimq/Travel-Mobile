package com.travelsync.core.data.local.seed.demo

import com.travelsync.core.data.local.TravelSyncDatabase
import com.travelsync.core.data.local.entity.*
import com.travelsync.core.security.PasswordHasher

/**
 * Seeds demo user content for local development and instrumented tests.
 *
 * Requires [com.travelsync.core.data.local.seed.app.AppDatabaseSeeder] to run first
 * so reference data and catalog place ids exist.
 */
object DemoDatabaseSeeder {

    suspend fun isSeeded(database: TravelSyncDatabase): Boolean =
        database.userDao().getUserByUsername(DemoSeedData.USERNAME) != null

    suspend fun seedIfEmpty(database: TravelSyncDatabase) {
        if (isSeeded(database)) return
        seed(database)
    }

    suspend fun seed(database: TravelSyncDatabase) {
        val now = DemoSeedData.seedCreatedAtMillis
        val password = PasswordHasher.hash(DemoSeedData.PASSWORD)
        database.userDao().insertUser(
            UserEntity(
                id = DemoSeedData.USER_ID,
                username = DemoSeedData.USERNAME,
                passwordHash = password.hash,
                passwordSalt = password.salt,
                displayName = DemoSeedData.DISPLAY_NAME,
                email = DemoSeedData.EMAIL,
                bio = "Seed account for local development and tests.",
                homeCurrency = DemoSeedData.HOME_CURRENCY,
                createdAt = now,
                updatedAt = now,
                lastLoginAt = null
            )
        )

        database.tripDao().insertTrip(
            TripEntity(
                id = DemoSeedData.TRIP_ID,
                ownerUserId = DemoSeedData.USER_ID,
                cityId = DemoSeedData.CITY_ID,
                title = DemoSeedData.TRIP_TITLE,
                destination = DemoSeedData.TRIP_DESTINATION,
                destinationCountry = DemoSeedData.TRIP_COUNTRY,
                startDate = DemoSeedData.tripStartMillis,
                endDate = DemoSeedData.tripEndMillis,
                currency = DemoSeedData.TRIP_CURRENCY,
                budgetAmount = DemoSeedData.TRIP_BUDGET,
                notes = "Sample trip seeded for development and instrumented tests.",
                type = "LEISURE",
                status = "PLANNED",
                createdAt = now,
                updatedAt = now
            )
        )

        database.tripDayDao().insertAll(
            listOf(
                TripDayEntity(
                    id = DemoSeedData.DAY_1_ID,
                    tripId = DemoSeedData.TRIP_ID,
                    dayNumber = 1,
                    date = DemoSeedData.day1Millis,
                    title = "Arrival and Asakusa",
                    notes = "",
                    sortOrder = 1
                ),
                TripDayEntity(
                    id = DemoSeedData.DAY_2_ID,
                    tripId = DemoSeedData.TRIP_ID,
                    dayNumber = 2,
                    date = DemoSeedData.day2Millis,
                    title = "Shibuya and shopping",
                    notes = "",
                    sortOrder = 2
                ),
                TripDayEntity(
                    id = DemoSeedData.DAY_3_ID,
                    tripId = DemoSeedData.TRIP_ID,
                    dayNumber = 3,
                    date = DemoSeedData.day3Millis,
                    title = "Departure day",
                    notes = "",
                    sortOrder = 3
                )
            )
        )

        database.activityDao().insertActivity(
            ActivityEntity(
                id = DemoSeedData.ACTIVITY_SENSOJI_ID,
                tripId = DemoSeedData.TRIP_ID,
                tripDayId = DemoSeedData.DAY_1_ID,
                placeId = DemoSeedData.CATALOG_SENSOJI_ID,
                title = "Senso-ji Temple",
                description = "Morning visit to Asakusa.",
                locationName = "Senso-ji Temple",
                address = "2-3-1 Asakusa, Taito City, Tokyo",
                latitude = 35.714765,
                longitude = 139.796655,
                startTime = DemoSeedData.dayTimeMillis(dayOffset = 0, hour = 9),
                endTime = DemoSeedData.dayTimeMillis(dayOffset = 0, hour = 11),
                category = "SIGHTSEEING",
                transportMode = "TRANSIT",
                estimatedCost = 500.0,
                actualCost = null,
                bookingLink = null,
                bookingReference = null,
                status = "PLANNED",
                sortOrder = 1,
                notes = "",
                createdByUserId = DemoSeedData.USER_ID,
                createdAt = now,
                updatedAt = now
            )
        )

        database.activityDao().insertActivity(
            ActivityEntity(
                id = DemoSeedData.ACTIVITY_ICHIRAN_ID,
                tripId = DemoSeedData.TRIP_ID,
                tripDayId = DemoSeedData.DAY_1_ID,
                placeId = DemoSeedData.CATALOG_ICHIRAN_ID,
                title = "Ichiran Shibuya",
                description = "Lunch ramen.",
                locationName = "Ichiran Shibuya",
                address = "1-22-7 Jinnan, Shibuya City, Tokyo",
                latitude = 35.660144,
                longitude = 139.701198,
                startTime = DemoSeedData.dayTimeMillis(dayOffset = 0, hour = 12, minute = 30),
                endTime = DemoSeedData.dayTimeMillis(dayOffset = 0, hour = 13, minute = 30),
                category = "FOOD",
                transportMode = "WALKING",
                estimatedCost = 1500.0,
                actualCost = 1500.0,
                bookingLink = null,
                bookingReference = null,
                status = "CONFIRMED",
                sortOrder = 2,
                notes = "",
                createdByUserId = DemoSeedData.USER_ID,
                createdAt = now,
                updatedAt = now
            )
        )

        database.activityDao().insertActivity(
            ActivityEntity(
                id = DemoSeedData.ACTIVITY_SHIBUYA_ID,
                tripId = DemoSeedData.TRIP_ID,
                tripDayId = DemoSeedData.DAY_2_ID,
                placeId = DemoSeedData.CATALOG_SHIBUYA_ID,
                title = "Shibuya Crossing",
                description = "Evening walk around Shibuya.",
                locationName = "Shibuya Crossing",
                address = "Shibuya City, Tokyo",
                latitude = 35.6595,
                longitude = 139.7005,
                startTime = DemoSeedData.dayTimeMillis(dayOffset = 1, hour = 18),
                endTime = DemoSeedData.dayTimeMillis(dayOffset = 1, hour = 20),
                category = "SIGHTSEEING",
                transportMode = "WALKING",
                estimatedCost = 0.0,
                actualCost = null,
                bookingLink = null,
                bookingReference = null,
                status = "PLANNED",
                sortOrder = 1,
                notes = "",
                createdByUserId = DemoSeedData.USER_ID,
                createdAt = now,
                updatedAt = now
            )
        )

        database.expenseDao().insertExpense(
            ExpenseEntity(
                id = DemoSeedData.EXPENSE_RAMEN_ID,
                tripId = DemoSeedData.TRIP_ID,
                title = "Ichiran lunch",
                description = "Tonkotsu ramen",
                amount = 1500.0,
                currency = DemoSeedData.TRIP_CURRENCY,
                category = "FOOD",
                expenseDate = DemoSeedData.dayTimeMillis(dayOffset = 0, hour = 13),
                createdByUserId = DemoSeedData.USER_ID,
                createdAt = now,
                updatedAt = now
            )
        )

        database.expenseDao().insertExpense(
            ExpenseEntity(
                id = DemoSeedData.EXPENSE_METRO_ID,
                tripId = DemoSeedData.TRIP_ID,
                title = "Metro day pass",
                description = "Tokyo Metro 24h ticket",
                amount = 800.0,
                currency = DemoSeedData.TRIP_CURRENCY,
                category = "TRANSPORT",
                expenseDate = DemoSeedData.dayTimeMillis(dayOffset = 0, hour = 8),
                createdByUserId = DemoSeedData.USER_ID,
                createdAt = now,
                updatedAt = now
            )
        )

        database.packingDao().insertPackingItem(
            PackingItemEntity(
                id = DemoSeedData.PACKING_PASSPORT_ID,
                tripId = DemoSeedData.TRIP_ID,
                title = "Passport",
                category = "Documents",
                quantity = 1,
                isPacked = true,
                sortOrder = 1,
                notes = "",
                createdAt = now
            )
        )

        database.packingDao().insertPackingItem(
            PackingItemEntity(
                id = DemoSeedData.PACKING_ADAPTER_ID,
                tripId = DemoSeedData.TRIP_ID,
                title = "Power adapter",
                category = "Electronics",
                quantity = 1,
                isPacked = false,
                sortOrder = 2,
                notes = "Type A for Japan",
                createdAt = now
            )
        )

        database.packingDao().insertPackingItem(
            PackingItemEntity(
                id = DemoSeedData.PACKING_JACKET_ID,
                tripId = DemoSeedData.TRIP_ID,
                title = "Light jacket",
                category = "Clothing",
                quantity = 1,
                isPacked = false,
                sortOrder = 3,
                notes = "",
                createdAt = now
            )
        )
    }
}
