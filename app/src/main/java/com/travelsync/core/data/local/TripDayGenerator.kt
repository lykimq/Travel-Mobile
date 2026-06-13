package com.travelsync.core.data.local

import com.travelsync.core.data.local.entity.TripDayEntity
import java.util.Calendar
import java.util.UUID
import java.util.concurrent.TimeUnit

object TripDayGenerator {

    fun generateDays(tripId: String, startMillis: Long, endMillis: Long): List<TripDayEntity> {
        if (endMillis < startMillis) return emptyList()
        val days = mutableListOf<TripDayEntity>()
        var dayNumber = 1
        var cursor = startOfDay(startMillis)
        val end = startOfDay(endMillis)
        while (cursor <= end) {
            days.add(
                TripDayEntity(
                    id = UUID.randomUUID().toString(),
                    tripId = tripId,
                    dayNumber = dayNumber,
                    date = cursor,
                    title = "",
                    notes = "",
                    sortOrder = dayNumber
                )
            )
            cursor += TimeUnit.DAYS.toMillis(1)
            dayNumber++
        }
        return days
    }

    private fun startOfDay(millis: Long): Long {
        val cal = Calendar.getInstance().apply {
            timeInMillis = millis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }
}
