package com.example.dragonballz.repository

import com.example.dragonballz.data.domain.CharacterDomain
import com.example.dragonballz.data.domain.Transformation
import com.example.dragonballz.data.local.dao.DragonBallzDao
import com.example.dragonballz.data.mapper.toCharacterDomain
import com.example.dragonballz.data.mapper.toCharacterEntity
import com.example.dragonballz.data.remote.api.DragonBallzApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map

class CharacterRepositoryImpl(
    private val api: DragonBallzApi,
    private val dao: DragonBallzDao
) : CharacterRepository {
    override suspend fun searchCharacter(query: String): Result<List<CharacterDomain>> {
        return try {
            val response = api.searchCharacter(query)
            if (response.isSuccessful) {
                val apiResponse = response.body()
                val characters = apiResponse!!.map { it.toCharacterDomain() }
                Result.success(characters)
            } else {
                Result.failure(Exception("Couldn't find character"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveCharacterAsFavorite(characterId: Int) {
        val existingCharacter = dao.getCharacterById(characterId)
        if (existingCharacter != null) {
            val updatedCharacter = existingCharacter.copy(isFavorite = true)
            dao.saveCharacter(updatedCharacter)
        }
    }

    override suspend fun deleteCharacterFromFavorites(characterId: Int) {
        val existingCharacter = dao.getCharacterById(characterId)
        if (existingCharacter != null) {
            val updatedCharacter = existingCharacter.copy(isFavorite = false)
            dao.saveCharacter(updatedCharacter)
        }
    }

    override suspend fun fetchCharacters(page: Int, limit: Int): Result<Unit> {
        return try {
            val response = api.getCharacters(page, limit)
            if (response.isSuccessful) {
                val apiResponse = response.body()

                val characters = apiResponse!!.items.map { it.toCharacterDomain() }

                val charactersEntity = characters.map { characterDomain ->
                    val existingCharacter = dao.getCharacterById(characterDomain.id)
                    characterDomain.toCharacterEntity()
                        .copy(isFavorite = existingCharacter?.isFavorite ?: false)
                }
                dao.saveCharacters(charactersEntity)

                Result.success(Unit)
            } else {
                Result.failure(Exception("Server Error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getFavoriteCharacters(): Flow<List<CharacterDomain>> {
        return dao.getFavoriteCharacters().map { characters -> characters.map { it.toCharacterDomain() } }
    }

    override fun getCachedCharactersFlow(): Flow<List<CharacterDomain>> {
        return dao.getCachedCharactersFlow().map { characters -> characters.map { it.toCharacterDomain() } }
    }

    override suspend fun fetchCharacterById(id: Int): Result<Unit> {
        return try {
            val response = api.getCharacter(id)
            if (response.isSuccessful) {
                val apiResponse = response.body()
                val character = apiResponse!!.toCharacterDomain()

                val existingCharacter = dao.getCharacterById(id)
                val updatedCharacter = character.copy(isFavorite = existingCharacter?.isFavorite ?: false)

                dao.updateCharacter(updatedCharacter.toCharacterEntity())

                Result.success(Unit)
            } else {
                Result.failure(Exception("Couldn't find character from API"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getCachedCharacterFlow(id: Int): Flow<CharacterDomain?> {
        return dao.getCharacterByIdFlow(id).map { characterEntity ->
            val character = characterEntity?.toCharacterDomain()
            character?.copy(transformations = getCachedCharacterTransformations(character.transformations))
        }
    }

    private fun getCachedCharacterTransformations(transformations: List<Transformation>?): List<Transformation> {
        return if (transformations?.isNotEmpty() == true) {
            transformations
        } else {
            emptyList()
        }
    }

}