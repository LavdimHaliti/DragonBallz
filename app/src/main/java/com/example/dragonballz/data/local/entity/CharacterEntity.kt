package com.example.dragonballz.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.dragonballz.data.domain.OriginPlanet
import com.example.dragonballz.data.domain.Transformation

@Entity(tableName = "character_table")
data class CharacterEntity(
    @PrimaryKey
    val id: Int,

    val affiliation: String,
    val description: String,
    val gender: String,
    val image: String,
    val ki: String,
    val maxKi: String,
    val name: String,
    val originPlanet: OriginPlanet?,
    val race: String,
    val transformations: List<Transformation>?,
    val isFavorite: Boolean
)
