package com.example.dragonballz.data.remote.dto

import com.example.dragonballz.data.domain.OriginPlanet
import com.example.dragonballz.data.domain.Transformation

data class CharacterDto(
    val id: Int,
    val affiliation: String,
    val deletedAt: Any,
    val description: String,
    val gender: String,
    val image: String,
    val ki: String,
    val maxKi: String,
    val name: String,
    val originPlanet: OriginPlanet?,
    val race: String,
    val transformations: List<Transformation>?
)
