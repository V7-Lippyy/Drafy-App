// DrafyDatabase.kt
package com.example.drafy.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.drafy.data.local.dao.NoteDao
import com.example.drafy.data.local.entity.Note
import com.example.drafy.util.DateTimeConverter

@Database(entities = [Note::class], version = 1, exportSchema = false)
@TypeConverters(DateTimeConverter::class)
abstract class DrafyDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
}