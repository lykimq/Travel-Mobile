package com.travelsync.core.data.local.reference.seed.catalog

import com.travelsync.core.data.local.reference.entity.RegionEntity
import com.travelsync.core.data.local.reference.seed.ReferenceDataCatalog

internal object ReferenceRegions {

    fun all(): List<RegionEntity> = listOf(
        region(ReferenceDataCatalog.RegionCode.ASIA, "Asia", 1),
        region(ReferenceDataCatalog.RegionCode.EUROPE, "Europe", 2),
        region(ReferenceDataCatalog.RegionCode.AMERICAS, "Americas", 3),
        region(ReferenceDataCatalog.RegionCode.OCEANIA, "Oceania", 4),
        region(ReferenceDataCatalog.RegionCode.MIDDLE_EAST, "Middle East", 5),
        region(ReferenceDataCatalog.RegionCode.AFRICA, "Africa", 6)
    )
}
