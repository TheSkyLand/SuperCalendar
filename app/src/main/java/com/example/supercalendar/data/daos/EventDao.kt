package com.example.supercalendar.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.supercalendar.data.entities.EventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    // Добавить или обновить событие
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: EventEntity)

    // Удалить событие
    @Delete
    suspend fun deleteEvent(event: EventEntity)

    // Получить ВСЕ события за конкретный день
    @Query("SELECT * FROM calendar_events WHERE date = :dateString")
    fun getEventsByDate(dateString: String): Flow<List<EventEntity>>

    // Получить ВСЕ события за конкретный месяц конкретного года (нужно для MonthlyView)
    @Query("SELECT * FROM calendar_events WHERE year = :year AND month = :month")
    fun getEventsByMonth(year: Int, month: Int): Flow<List<EventEntity>>
}
