package com.example.supercalendar.parser
import android.content.Context
import android.provider.CalendarContract
import android.net.Uri


class CalendarParser {




    fun parseCalendarEvents(context: Context)
    {
        val uri: Uri = CalendarContract.Events.CONTENT_URI

        val projection = arrayOf(
            CalendarContract.Events._ID,
            CalendarContract.Events.TITLE,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.DTEND
        )

        val cursor = context.contentResolver.query(
            uri,
            projection,
            null,
            null,
            "${CalendarContract.Events.DTSTART} ASC"
        )

        // 4. Parse the results
        cursor?.use {
            val titleIndex = it.getColumnIndex(CalendarContract.Events.TITLE)
            val startIndex = it.getColumnIndex(CalendarContract.Events.DTSTART)

            while (it.moveToNext()) {
                val title = it.getString(titleIndex)
                val startTime = it.getLong(startIndex)

                // Do something with the parsed data
                println("Event: $title at $startTime")
            }
        }



    }

}