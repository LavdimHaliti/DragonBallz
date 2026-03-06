package com.example.dragonballz.navigation

sealed class Screen(val route: String) {

    object CharacterList : Screen("character_list")

    object CharacterDetail : Screen("character_detail/{id}") {
        fun createRoute(id: Int) = "character_detail/$id"
    }

    object FavoriteCharacters: Screen("favorite_characters")

    object CompareCharacters: Screen("compare_characters/{characters}") {
        fun createRoute(characters: Int) = "compare_characters/${characters}"
    }
}