package com.example.dragonballz.data.mapper

import android.util.Printer
import com.example.dragonballz.data.domain.CharacterDomain
import com.example.dragonballz.data.domain.Transformation
import com.example.dragonballz.data.local.entity.CharacterEntity
import com.example.dragonballz.data.remote.dto.CharacterDto

fun CharacterEntity.toCharacterDomain(): CharacterDomain =
    CharacterDomain(
        id = id,
        name = name,
        image = image,
        originPlanet = originPlanet,
        affiliation = affiliation,
        description = description,
        gender = gender,
        race = race,
        ki = ki,
        maxKi = maxKi,
        transformations = transformations,
        isFavorite = isFavorite
    )

fun CharacterDomain.toCharacterEntity(): CharacterEntity =
    CharacterEntity(
        id = id,
        name = name,
        image = image,
        originPlanet = originPlanet,
        affiliation = affiliation,
        description = description,
        gender = gender,
        race = race,
        ki = ki,
        maxKi = maxKi,
        transformations = transformations,
        isFavorite = isFavorite
    )

fun CharacterDto.toCharacterDomain(): CharacterDomain =
    CharacterDomain(
        id = id,
        name = name,
        image = image,
        originPlanet = originPlanet,
        affiliation = affiliation,
        description = description,
        gender = gender,
        race = race,
        ki = ki,
        maxKi = maxKi,
        transformations = transformations,
        isFavorite = false
    )