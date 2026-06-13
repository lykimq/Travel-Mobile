package com.travelsync.core.data.local.reference.seed

import com.travelsync.core.data.local.TravelSyncDatabase
import com.travelsync.core.data.local.entity.PlaceEntity
import com.travelsync.core.domain.model.PlaceCategory

/**
 * Offline place catalog grouped by city.
 *
 * To extend later:
 * - append [CatalogPlaceEntry] rows here, or
 * - load city packs from assets/places/{cityId}.json via a dedicated importer.
 */
object PlaceCatalogSeed {

    data class CatalogPlaceEntry(
        val id: String,
        val cityId: String,
        val destinationTag: String,
        val name: String,
        val description: String,
        val address: String,
        val latitude: Double,
        val longitude: Double,
        val category: PlaceCategory,
        val priceNote: String,
        val rating: Float,
        val externalLink: String? = null
    )

    suspend fun seedIfEmpty(database: TravelSyncDatabase) {
        if (database.placeDao().countCatalogPlaces() > 0) return
        seed(database)
    }

    suspend fun seed(database: TravelSyncDatabase) {
        val now = System.currentTimeMillis()
        database.placeDao().insertAll(
            entries().map { entry ->
                PlaceEntity(
                    id = entry.id,
                    tripId = null,
                    cityId = entry.cityId,
                    ownerUserId = null,
                    destinationTag = entry.destinationTag,
                    name = entry.name,
                    description = entry.description,
                    address = entry.address,
                    latitude = entry.latitude,
                    longitude = entry.longitude,
                    category = entry.category.name,
                    priceMin = null,
                    priceMax = null,
                    priceNote = entry.priceNote,
                    currency = "",
                    rating = entry.rating,
                    externalLink = entry.externalLink,
                    phone = null,
                    isCatalog = true,
                    createdAt = now,
                    updatedAt = now
                )
            }
        )
    }

    private fun entries(): List<CatalogPlaceEntry> = listOf(
        CatalogPlaceEntry(
            id = "catalog_tokyo_sensoji",
            cityId = "city_tokyo",
            destinationTag = "Tokyo",
            name = "Senso-ji Temple",
            description = "Tokyo's oldest temple in Asakusa.",
            address = "2-3-1 Asakusa, Taito City, Tokyo",
            latitude = 35.714765,
            longitude = 139.796655,
            category = PlaceCategory.ATTRACTION,
            priceNote = "Free",
            rating = 4.8f
        ),
        CatalogPlaceEntry(
            id = "catalog_tokyo_shibuya",
            cityId = "city_tokyo",
            destinationTag = "Tokyo",
            name = "Shibuya Crossing",
            description = "Iconic scramble crossing and shopping district.",
            address = "Shibuya City, Tokyo",
            latitude = 35.6595,
            longitude = 139.7005,
            category = PlaceCategory.ATTRACTION,
            priceNote = "Free",
            rating = 4.6f
        ),
        CatalogPlaceEntry(
            id = "catalog_tokyo_ichiran",
            cityId = "city_tokyo",
            destinationTag = "Tokyo",
            name = "Ichiran Shibuya",
            description = "Popular tonkotsu ramen chain.",
            address = "1-22-7 Jinnan, Shibuya City, Tokyo",
            latitude = 35.660144,
            longitude = 139.701198,
            category = PlaceCategory.RESTAURANT,
            priceNote = "1500 JPY",
            rating = 4.5f
        ),
        CatalogPlaceEntry(
            id = "catalog_tokyo_hotel",
            cityId = "city_tokyo",
            destinationTag = "Tokyo",
            name = "Shibuya Stream Excel Hotel Tokyu",
            description = "Modern hotel near Shibuya Station.",
            address = "3-21-3 Shibuya, Shibuya City, Tokyo",
            latitude = 35.657579,
            longitude = 139.702951,
            category = PlaceCategory.HOTEL,
            priceNote = "25000 JPY/night",
            rating = 4.4f,
            externalLink = "https://www.tokyuhotels.co.jp/en/shibuya-stream/"
        ),
        CatalogPlaceEntry(
            id = "catalog_tokyo_parking",
            cityId = "city_tokyo",
            destinationTag = "Tokyo",
            name = "Shibuya Mark City Parking",
            description = "Paid parking garage near Shibuya Station.",
            address = "1-12-1 Dogenzaka, Shibuya City, Tokyo",
            latitude = 35.6580,
            longitude = 139.7016,
            category = PlaceCategory.PARKING,
            priceNote = "400 JPY/hour",
            rating = 4.0f
        ),
        CatalogPlaceEntry(
            id = "catalog_paris_louvre",
            cityId = "city_paris",
            destinationTag = "Paris",
            name = "Louvre Museum",
            description = "World-famous art museum at the former royal palace.",
            address = "Rue de Rivoli, 75001 Paris",
            latitude = 48.8606,
            longitude = 2.3376,
            category = PlaceCategory.ATTRACTION,
            priceNote = "22 EUR",
            rating = 4.7f
        ),
        CatalogPlaceEntry(
            id = "catalog_paris_hotel",
            cityId = "city_paris",
            destinationTag = "Paris",
            name = "Hotel Le Meurice",
            description = "Luxury hotel near the Tuileries Garden.",
            address = "228 Rue de Rivoli, 75001 Paris",
            latitude = 48.8653,
            longitude = 2.3280,
            category = PlaceCategory.HOTEL,
            priceNote = "From 900 EUR/night",
            rating = 4.8f
        ),
        CatalogPlaceEntry(
            id = "catalog_paris_bistro",
            cityId = "city_paris",
            destinationTag = "Paris",
            name = "Le Comptoir du Relais",
            description = "Classic Parisian bistro in Saint-Germain.",
            address = "9 Carrefour de l'Odéon, 75006 Paris",
            latitude = 48.8510,
            longitude = 2.3390,
            category = PlaceCategory.RESTAURANT,
            priceNote = "45 EUR",
            rating = 4.5f
        ),
        CatalogPlaceEntry(
            id = "catalog_london_tower",
            cityId = "city_london",
            destinationTag = "London",
            name = "Tower of London",
            description = "Historic castle and home of the Crown Jewels.",
            address = "London EC3N 4AB",
            latitude = 51.5081,
            longitude = -0.0759,
            category = PlaceCategory.ATTRACTION,
            priceNote = "34 GBP",
            rating = 4.6f
        ),
        CatalogPlaceEntry(
            id = "catalog_london_hotel",
            cityId = "city_london",
            destinationTag = "London",
            name = "The Ned London",
            description = "Boutique hotel in the City of London.",
            address = "27 Poultry, London EC2R 8AJ",
            latitude = 51.5135,
            longitude = -0.0909,
            category = PlaceCategory.HOTEL,
            priceNote = "From 350 GBP/night",
            rating = 4.7f
        ),
        CatalogPlaceEntry(
            id = "catalog_london_market",
            cityId = "city_london",
            destinationTag = "London",
            name = "Borough Market",
            description = "Historic food market near London Bridge.",
            address = "8 Southwark St, London SE1 1TL",
            latitude = 51.5054,
            longitude = -0.0910,
            category = PlaceCategory.RESTAURANT,
            priceNote = "Varies",
            rating = 4.6f
        ),
        CatalogPlaceEntry(
            id = "catalog_bangkok_palace",
            cityId = "city_bangkok",
            destinationTag = "Bangkok",
            name = "Grand Palace",
            description = "Former royal residence and temple complex.",
            address = "Na Phra Lan Rd, Phra Nakhon, Bangkok",
            latitude = 13.7500,
            longitude = 100.4913,
            category = PlaceCategory.ATTRACTION,
            priceNote = "500 THB",
            rating = 4.6f
        ),
        CatalogPlaceEntry(
            id = "catalog_bangkok_hotel",
            cityId = "city_bangkok",
            destinationTag = "Bangkok",
            name = "Mandarin Oriental Bangkok",
            description = "Historic riverside luxury hotel.",
            address = "48 Oriental Ave, Bang Rak, Bangkok",
            latitude = 13.7234,
            longitude = 100.5143,
            category = PlaceCategory.HOTEL,
            priceNote = "From 12000 THB/night",
            rating = 4.8f
        ),
        CatalogPlaceEntry(
            id = "catalog_bangkok_streetfood",
            cityId = "city_bangkok",
            destinationTag = "Bangkok",
            name = "Yaowarat Road",
            description = "Chinatown street food district.",
            address = "Yaowarat Rd, Samphanthawong, Bangkok",
            latitude = 13.7408,
            longitude = 100.5090,
            category = PlaceCategory.RESTAURANT,
            priceNote = "100-300 THB",
            rating = 4.5f
        )
    )
}
