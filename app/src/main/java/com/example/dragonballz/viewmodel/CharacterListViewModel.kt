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

    private fun checkConnectivity() {
        viewModelScope.launch {
            NetworkUtils(context).isConnected.collect { connectStatus ->
                _uiState.update { it.copy(isConnected = connectStatus) }
            }

        }
    }

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

    fun loadMoreCharacters() {
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

    fun refreshCharacters() {
        _uiState.update { it.copy(isRefreshing = true) }
        checkConnectivity()
        fetchCharacters()
    }
}

