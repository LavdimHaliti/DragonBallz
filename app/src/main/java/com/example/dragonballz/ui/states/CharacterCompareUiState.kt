package com.example.dragonballz.ui.states

import com.example.dragonballz.data.domain.CharacterDomain

data class CharacterCompareUiState(
    val characters: List<CharacterDomain> = emptyList(),
    val isLoading: Boolean = false,
    val firstCharacter: CharacterDomain? = null,
    val secondCharacter: CharacterDomain? = null,
    val strongerCharacter: CharacterDomain? = null,
    val compareResult: String? = "",
    val errorMessage: String = ""
)
