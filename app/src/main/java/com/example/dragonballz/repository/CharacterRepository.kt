package com.example.dragonballz.repository

import com.example.dragonballz.data.domain.CharacterDomain
import kotlinx.coroutines.flow.Flow

interface CharacterRepository {

    suspend fun searchCharacter(query: String): Result<List<CharacterDomain>>
    suspend fun saveCharacterAsFavorite(characterId: Int)

    suspend fun deleteCharacterFromFavorites(characterId: Int)

    suspend fun fetchCharacterById(id: Int): Result<Unit>

    fun getCachedCharacterFlow(id: Int): Flow<CharacterDomain?>

    fun getCachedCharactersFlow(): Flow<List<CharacterDomain>>

    suspend fun fetchCharacters(page: Int, limit: Int): Result<Unit>

    fun getFavoriteCharacters(): Flow<List<CharacterDomain>>



}