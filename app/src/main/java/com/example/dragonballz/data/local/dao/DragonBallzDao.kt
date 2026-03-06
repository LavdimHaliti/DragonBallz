package com.example.dragonballz.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.dragonballz.data.local.entity.CharacterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DragonBallzDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCharacter(character: CharacterEntity)

    @Update
    suspend fun updateCharacter(character: CharacterEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCharacters(characters: List<CharacterEntity>)

    @Query("SELECT * FROM character_table WHERE id = :id")
    suspend fun getCharacterById(id: Int): CharacterEntity?

    @Query("SELECT * FROM character_table WHERE id = :id")
    fun getCharacterByIdFlow(id: Int): Flow<CharacterEntity?>

    @Query("SELECT * FROM character_table WHERE isFavorite = 1 ORDER BY id")
    fun getFavoriteCharacters(): Flow<List<CharacterEntity>>

    @Query("SELECT * FROM character_table ORDER BY id")
    fun getCachedCharactersFlow(): Flow<List<CharacterEntity>>

    @Query("SELECT * FROM character_table ORDER BY id")
    suspend fun getCachedCharacters(): List<CharacterEntity>

    @Query("SELECT COUNT(*) FROM character_table")
    suspend fun getTotalCachedCharactersCount(): Int
}