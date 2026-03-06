package com.example.dragonballz.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dragonballz.data.domain.CharacterDomain
import com.example.dragonballz.viewmodel.FavoriteCharactersViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun FavoriteCharactersScreen(
    navigateBack: () -> Unit,
    navigateToDetails: (Int) -> Unit,
    viewModel: FavoriteCharactersViewModel = koinViewModel()
) {
    val favoriteCharacters by viewModel.favoriteCharacters.collectAsStateWithLifecycle()

    FavoriteCharactersContent(
        favoriteCharacters = favoriteCharacters,
        navigateBack = { navigateBack() },
        navigateToDetails = { characterId -> navigateToDetails(characterId) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteCharactersContent(
    favoriteCharacters: List<CharacterDomain>,
    navigateBack: () -> Unit,
    navigateToDetails: (Int) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favorites") },
                navigationIcon = {
                    IconButton(onClick = { navigateBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { paddingValues ->
        if (favoriteCharacters.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No Favorite Characters.")
            }
        } else {
            LazyColumn(modifier = Modifier.padding(paddingValues)) {
                items(favoriteCharacters, key = { it.id }) { character ->
                    CharacterItem(
                        character,
                        onClick = { navigateToDetails(character.id) }
                    )
                }
            }
        }
    }
}