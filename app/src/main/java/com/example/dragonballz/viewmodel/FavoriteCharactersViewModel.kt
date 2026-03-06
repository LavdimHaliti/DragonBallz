package com.example.dragonballz.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dragonballz.data.domain.CharacterDomain
import com.example.dragonballz.repository.CharacterRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class FavoriteCharactersViewModel(
    private val repository: CharacterRepository
) : ViewModel() {

    val favoriteCharacters: StateFlow<List<CharacterDomain>> = repository.getFavoriteCharacters().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

}