package com.example.dragonballz.ui.states

import com.example.dragonballz.data.domain.CharacterDomain
import com.example.dragonballz.data.domain.Transformation

data class CharacterDetailsUiState(
    val character: CharacterDomain? = null,
    val isLoading: Boolean = false,
    val isFavorite: Boolean = false,
    val errorMessage: String = "",
    val isConnected: Boolean = true
)