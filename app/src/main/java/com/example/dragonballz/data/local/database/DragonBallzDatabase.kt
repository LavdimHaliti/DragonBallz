package com.example.dragonballz.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.dragonballz.data.local.converter.Converters
import com.example.dragonballz.data.local.dao.DragonBallzDao
import com.example.dragonballz.data.local.entity.CharacterEntity

@Database(entities = [CharacterEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class DragonBallzDatabase : RoomDatabase() {

    abstract fun dragonBallzDao(): DragonBallzDao
}