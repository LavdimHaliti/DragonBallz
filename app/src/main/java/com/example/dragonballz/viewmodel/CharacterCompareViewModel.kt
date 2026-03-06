package com.example.dragonballz.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dragonballz.data.domain.CharacterDomain
import com.example.dragonballz.repository.CharacterRepository
import com.example.dragonballz.ui.states.CharacterCompareUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CharacterCompareViewModel(
    private val characters: Int,
    private val repository: CharacterRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(CharacterCompareUiState())
    val uiState: StateFlow<CharacterCompareUiState> = _uiState.asStateFlow()

    private var firstCharacter: CharacterDomain? = null
    private var secondCharacter: CharacterDomain? = null

    init {
        getCharactersFromDatabase()
    }

    private fun getCharactersFromDatabase() {
        viewModelScope.launch {
            repository.getCachedCharactersFlow()
                .collect { characters ->
                    _uiState.update {
                        it.copy(characters = characters)
                    }
                }
        }
    }

//    private fun getCharactersFromDatabase() {
//        viewModelScope.launch {
//            _uiState.update { it.copy(isLoading = true) }
//
//            val characterCount = repository.getTotalCachedCharacters()
//            val result = repository.fetchCharacters(1, characterCount)
//
//            result.onSuccess { characterDomains ->
//                _uiState.update {
//                    it.copy(
//                        isLoading = false,
//                        characters = characterDomains,
//                        firstCharacter = firstCharacter,
//                        secondCharacter = secondCharacter
//                    )
//                }
//            }.onFailure { exception ->
//                _uiState.update {
//                    it.copy(
//                        isLoading = false,
//                        errorMessage = exception.message ?: "Can't load characters"
//                    )
//                }
//            }
//        }
//    }

    fun selectFirstCharacter(character: CharacterDomain) {
        firstCharacter = character
        _uiState.update { it.copy(firstCharacter = firstCharacter) }
        updateSelection()
    }

    fun selectSecondCharacter(character: CharacterDomain) {
        secondCharacter = character
        _uiState.update { it.copy(secondCharacter = secondCharacter) }
        updateSelection()
    }

    private fun updateSelection() {
        val first = firstCharacter
        val second = secondCharacter
        val stronger = when {
            first == null || second == null -> null
            extractNumericKi(first.ki) > extractNumericKi(second.ki) -> first
            extractNumericKi(second.ki) > extractNumericKi(first.ki) -> second
            else -> null
        }
        _uiState.update {
            it.copy(
                compareResult = compareCharacters(),
                strongerCharacter = stronger
            )
        }
    }

    fun compareCharacters(): String? {
        val first = firstCharacter ?: return null
        val second = secondCharacter ?: return null
        val firstKi = extractNumericKi(first.ki)
        val secondKi = extractNumericKi(second.ki)

        return when {
            firstKi > secondKi -> "${first.name} is stronger!"
            secondKi > firstKi -> "${second.name} is stronger!"
            else -> "They are equally strong!"
        }
    }

    private fun extractNumericKi(ki: String?): Long {
        return ki?.replace(",", "")?.replace(".", "")?.toLongOrNull() ?: 0
    }
}