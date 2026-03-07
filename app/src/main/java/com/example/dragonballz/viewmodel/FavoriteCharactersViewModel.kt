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

    // Expose the favorite characters as a StateFlow
    val favoriteCharacters: StateFlow<List<CharacterDomain>> =
        // Use stateIn to expose the flow as a StateFlow
        repository.getFavoriteCharacters().stateIn(
        scope = viewModelScope, // The scope in which the flow should be kept alive
        started = SharingStarted.WhileSubscribed(5000), // The flow should be kept alive while there are subscribers. If no subscribers are present for 5 seconds, the flow will be stopped
        initialValue = emptyList() // The initial value of the StateFlow
    )

}