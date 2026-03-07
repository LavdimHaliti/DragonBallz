package com.example.dragonballz.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dragonballz.ui.screens.CharacterCompareScreen
import com.example.dragonballz.ui.screens.CharacterDetailScreen
import com.example.dragonballz.ui.screens.CharacterListScreen
import com.example.dragonballz.ui.screens.FavoriteCharactersScreen

@Composable
fun DragonBallzApp(navController: NavHostController = rememberNavController()) {
    DragonBallzNavGraph(navController)
}

@Composable
fun DragonBallzNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.CharacterList.route
    ) {
        composable(route = Screen.CharacterList.route) {
            CharacterListScreen(
                navigateToDetail = { id ->
                    navController.navigate(
                        Screen.CharacterDetail.createRoute(
                            id
                        )
                    )
                },
                navigateToFavorites = { navController.navigate(Screen.FavoriteCharacters.route) },
                navigateToCompare = { characters ->
                    navController.navigate(
                        Screen.CompareCharacters.createRoute(
                            characters
                        )
                    )
                }
            )
        }

        composable(route = Screen.CharacterDetail.route) { backStackEntry ->
            val args = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: 0
            CharacterDetailScreen(
                id = args,
                navigateBack = { navController.navigateUp() }
            )
        }

        composable(route = Screen.FavoriteCharacters.route) {
            FavoriteCharactersScreen(
                navigateBack = { navController.navigateUp() },
                navigateToDetails = { id ->
                    navController.navigate(
                        Screen.CharacterDetail.createRoute(
                            id
                        )
                    )
                }
            )
        }

        composable(route = Screen.CompareCharacters.route) {
            CharacterCompareScreen(
                navigateBack = { navController.navigateUp() }
            )
        }
    }
}