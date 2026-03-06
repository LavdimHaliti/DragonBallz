package com.example.dragonballz.ui.states

import com.example.dragonballz.data.domain.CharacterDomain

data class CharacterListUiState(
    val characterList: List<CharacterDomain> = emptyList(),
    val query: String = "",
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String = "",
    val isConnected: Boolean = true,
    )
