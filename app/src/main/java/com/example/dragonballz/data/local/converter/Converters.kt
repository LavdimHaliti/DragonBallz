package com.example.dragonballz.data.local.converter

import androidx.room.TypeConverter
import com.example.dragonballz.data.domain.OriginPlanet
import com.example.dragonballz.data.domain.Transformation
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    @TypeConverter
    fun fromOriginPlanet(originPlanet: OriginPlanet): String {
        return Gson().toJson(originPlanet)
    }

    @TypeConverter
    fun toOriginPlanet(planetString: String): OriginPlanet {
        return Gson().fromJson(planetString, OriginPlanet::class.java)
    }

    @TypeConverter
    fun fromTransformationList(transformations: List<Transformation>?): String {
        return Gson().toJson(transformations)
    }

    @TypeConverter
    fun toTransformationList(transformation: String): List<Transformation>? {
        val type = object : TypeToken<List<Transformation>?>() {}.type

        return Gson().fromJson(transformation, type)
    }
}