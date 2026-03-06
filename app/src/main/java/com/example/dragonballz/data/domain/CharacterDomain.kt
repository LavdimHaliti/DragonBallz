package com.example.dragonballz.data.domain

data class CharacterDomain(
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