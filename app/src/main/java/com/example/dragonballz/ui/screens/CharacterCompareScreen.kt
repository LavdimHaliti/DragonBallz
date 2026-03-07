package com.example.dragonballz.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.dragonballz.data.domain.CharacterDomain
import com.example.dragonballz.ui.states.CharacterCompareUiState
import com.example.dragonballz.viewmodel.CharacterCompareViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun CharacterCompareScreen(
    navigateBack: () -> Unit,
    viewModel: CharacterCompareViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CharacterCompareScreenContent(
        uiState = uiState,
        navigateBack = navigateBack,
        onSelectFirst = { character -> viewModel.selectFirstCharacter(character) },
        onSelectSecond = { character -> viewModel.selectSecondCharacter(character) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterCompareScreenContent(
    uiState: CharacterCompareUiState,
    navigateBack: () -> Unit,
    onSelectFirst: (CharacterDomain) -> Unit,
    onSelectSecond: (CharacterDomain) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Character Compare") },
                navigationIcon = {
                    IconButton(onClick = { navigateBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                CharacterSelector(
                    label = "First character",
                    selected = uiState.firstCharacter,
                    characters = uiState.characters,
                    onSelect = onSelectFirst
                )
                Spacer(modifier = Modifier.height(8.dp))
                CharacterSelector(
                    label = "Second character",
                    selected = uiState.secondCharacter,
                    characters = uiState.characters,
                    onSelect = onSelectSecond
                )
                Spacer(modifier = Modifier.height(16.dp))
                if (uiState.firstCharacter != null && uiState.secondCharacter != null) {
                    Text(
                        text = uiState.compareResult ?: "",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    uiState.strongerCharacter?.let { character ->
                        CharacterCompareCard(character = character)
                    }
                } else {
                    Text("Select two characters to compare")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterSelector(
    label: String,
    selected: CharacterDomain?,
    characters: List<CharacterDomain>,
    onSelect: (CharacterDomain) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        TextField(
            value = selected?.name ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            characters.forEach { character ->
                DropdownMenuItem(
                    text = { Text(character.name) },
                    onClick = {
                        onSelect(character)
                        expanded = false
                    },
                    leadingIcon = {
                        AsyncImage(
                            model = character.image,
                            contentDescription = null,
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun CharacterCompareCard(
    character: CharacterDomain
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            AsyncImage(
                model = character.image,
                contentDescription = character.name,
                modifier = Modifier
                    .size(250.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = character.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Ki: ${character.ki}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}