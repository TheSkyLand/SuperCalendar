package com.example.supercalendar.data

import EventEntity
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Insert
    suspend fun insertEvent(event: EventEntity): Unit // Explicit :Unit prevents KSP signature bug

    @Query("SELECT * FROM events WHERE date = :date")
    suspend fun getEventsForDate(date: String): List<EventEntity>

    @Delete
    suspend fun deleteEvent(event: EventEntity);
}
