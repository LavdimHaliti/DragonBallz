package com.example.dragonballz.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.compose.AsyncImage
import com.example.dragonballz.R
import com.example.dragonballz.data.domain.CharacterDomain
import com.example.dragonballz.ui.states.CharacterListUiState
import com.example.dragonballz.viewmodel.CharacterListViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun CharacterListScreen(
    navigateToDetail: (Int) -> Unit,
    navigateToFavorites: () -> Unit,
    navigateToCompare: (Int) -> Unit,
    viewModel: CharacterListViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CharacterListContent(
        uiState = uiState,
        loadMoreCharacters = { viewModel.loadMoreCharacters() },
        updateSearchQuery = { query -> viewModel.updateSearchQuery(query) },
        refreshCharacters = { viewModel.refreshCharacters() },
        navigateToDetail = { id -> navigateToDetail(id) },
        navigateToFavorites = { navigateToFavorites() },
        navigateToCompare = { characters -> navigateToCompare(characters) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterListContent(
    uiState: CharacterListUiState,
    loadMoreCharacters: () -> Unit,
    updateSearchQuery: (String) -> Unit,
    refreshCharacters: () -> Unit,
    navigateToDetail: (Int) -> Unit,
    navigateToFavorites: () -> Unit,
    navigateToCompare: (Int) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Dragon Ball Characters")
                },
                actions = {
                    IconButton(onClick = { navigateToFavorites() }) {
                        Icon(Icons.Default.Favorite, contentDescription = "Favorite Characters")
                    }

                    IconButton(onClick = { navigateToCompare(uiState.characterList.size) }) {
                        Icon(painterResource(R.drawable.compare), contentDescription = "Compare")
                    }
                }
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PullToRefreshBox(
                isRefreshing = uiState.isRefreshing,
                onRefresh = { refreshCharacters() },
                modifier = Modifier.fillMaxWidth(),
                content = {
                    Column {
                        if (!uiState.isConnected) {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = MaterialTheme.colorScheme.errorContainer
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = "No Internet",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "No internet connection",
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            value = uiState.query,
                            onValueChange = {
                                updateSearchQuery(it)
                            },
                            placeholder = { Text(text = "Search Character...") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
                        )
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                8.dp
                            )
                        ) {
                            items(uiState.characterList, key = { it.id }) { character ->
                                CharacterItem(
                                    character = character,
                                    onClick = { navigateToDetail(character.id) }
                                )
                            }
                            if (uiState.query.isBlank()) {
                                item {
                                    LaunchedEffect(Unit) {
                                        loadMoreCharacters()
                                    }
                                }
                            }
                        }
                    }
                }
            )

        }
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.size(50.dp))
                }

                uiState.characterList.isEmpty() -> {
                    Text(text = "No characters found")
                }
            }
        }
    }
}

@Composable
fun CharacterItem(
    character: CharacterDomain,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clickable { onClick() },
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = character.image,
                contentDescription = character.name,
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = character.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Race: ${character.race}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (character.isFavorite) {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = "Favorite",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun CharacterListScreenPreview(
) {
    CharacterListContent(
        uiState = CharacterListUiState(),
        loadMoreCharacters = {},
        updateSearchQuery = {},
        refreshCharacters = {},
        navigateToDetail = {},
        navigateToFavorites = {},
        navigateToCompare = {}
    )
}