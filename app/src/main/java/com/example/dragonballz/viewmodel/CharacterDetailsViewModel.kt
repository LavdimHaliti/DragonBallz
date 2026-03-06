package com.example.dragonballz.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dragonballz.repository.CharacterRepository
import com.example.dragonballz.ui.states.CharacterDetailsUiState
import com.example.dragonballz.util.NetworkUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CharacterDetailsViewModel(
    private val id: Int,
    private val repository: CharacterRepository,
    private val context: Application
) : ViewModel() {

    private val _uiState = MutableStateFlow(CharacterDetailsUiState())
    val uiState: StateFlow<CharacterDetailsUiState> = _uiState.asStateFlow()

    init {
        checkConnectivity()
        getCharacterDetails()
        getCharacterFromDatabase()
    }

    private fun checkConnectivity() {
        viewModelScope.launch {
            NetworkUtils(context).isConnected.collect { connectStatus ->
                _uiState.update { it.copy(isConnected = connectStatus) }
            }

        }
    }

    private fun getCharacterFromDatabase() {
        viewModelScope.launch {
            repository.getCachedCharacterFlow(id)
                .collect { character ->
                    character?.let { characterData ->
                        _uiState.update {
                            it.copy(
                                character = characterData,
                                isLoading = false,
                                isFavorite = characterData.isFavorite
                            )
                        }
                    }
                }
        }
    }

    fun getCharacterDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val result = repository.fetchCharacterById(id)
            result.onFailure { exception ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Can't load character details"
                    )
                }
            }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            if (_uiState.value.isFavorite) {
                repository.deleteCharacterFromFavorites(id)
                _uiState.update { it.copy(isFavorite = false) }
            } else {
                repository.saveCharacterAsFavorite(id)
                _uiState.update { it.copy(isFavorite = true) }
            }
        }
    }

}