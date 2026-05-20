package com.example.supercalendar.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calendar_events")
data class EventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    // Храним дату в формате "YYYY-MM-DD" (например, "2026-05-20")
    val date: String,
    val year: Int,
    val month: Int // 1 .. 12
)