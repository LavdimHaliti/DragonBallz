package com.example.dragonballz.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dragonballz.repository.CharacterRepository
import com.example.dragonballz.ui.states.CharacterListUiState
import com.example.dragonballz.util.NetworkUtils
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for handling character list UI state and interactions.
 */
class CharacterListViewModel(
    private val repository: CharacterRepository,
    private val context: Application
) : ViewModel() {
    private val _uiState = MutableStateFlow(CharacterListUiState())
    val uiState: StateFlow<CharacterListUiState> = _uiState.asStateFlow()

    private var currentPage = 1
    private val pageLimit = 10
    private var job: Job? = null

    init {
        checkConnectivity()
        getCharactersFromDatabase()
        fetchCharacters()
    }

    // Retrieve cached characters from database
    private fun getCharactersFromDatabase() {
        viewModelScope.launch {
            repository.getCachedCharactersFlow()
                .collect { characters ->
                    _uiState.update {
                        it.copy(characterList = characters)
                    }
                }
        }
    }

    // Check network connectivity
    private fun checkConnectivity() {
        viewModelScope.launch {
            NetworkUtils(context).isConnected.collect { connectStatus ->
                _uiState.update { it.copy(isConnected = connectStatus) }
            }

        }
    }

    // Fetch characters from API
    private fun fetchCharacters() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            checkConnectivity()
            currentPage = 1
            val result = repository.fetchCharacters(currentPage, pageLimit)
            _uiState.update { it.copy(isLoading = false, isRefreshing = false) }
            result.onFailure { exception ->
                checkConnectivity()
                _uiState.update {
                    it.copy(
                        errorMessage = exception.message ?: "Failed to load characters",
                        isLoading = false,
                        isRefreshing = false
                    )
                }
            }
        }
    }

    // Load more characters from API
    fun loadMoreCharacters() {
        // Prevent multiple simultaneous loads
        if (_uiState.value.isLoadingMore || _uiState.value.query.isNotBlank()) {
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true) }
            checkConnectivity()
            currentPage++
            repository.fetchCharacters(currentPage, pageLimit)
            _uiState.update { it.copy(isLoadingMore = false) }
        }
    }

    // Search for a character
    private fun searchCharacter(query: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            checkConnectivity()
            currentPage = 1
            val result = repository.searchCharacter(query)
            result.onSuccess { characters ->
                _uiState.update {
                    it.copy(
                        characterList = characters,
                        query = query,
                        isLoading = false
                    )
                }
            }.onFailure { exception ->
                checkConnectivity()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isLoadingMore = false,
                        errorMessage = exception.message ?: "Could not find query"
                    )
                }
            }
        }
    }

    // Update search query and fetch characters
    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(query = query) }
        job?.cancel()
        if (query.isNotBlank()) {
            job = viewModelScope.launch {
                delay(500)
                searchCharacter(query)
            }
        } else {
            fetchCharacters()
        }
    }

    // Refresh characters from API
    fun refreshCharacters() {
        _uiState.update { it.copy(isRefreshing = true) }
        checkConnectivity()
        fetchCharacters()
    }
}

