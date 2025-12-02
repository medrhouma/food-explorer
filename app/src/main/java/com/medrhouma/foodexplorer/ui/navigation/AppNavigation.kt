package com.medrhouma.foodexplorer.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.medrhouma.foodexplorer.R
import com.medrhouma.foodexplorer.ui.screens.categories.CategoriesScreen
import com.medrhouma.foodexplorer.ui.screens.details.DetailsScreen
import com.medrhouma.foodexplorer.ui.screens.favorites.FavoritesScreen
import com.medrhouma.foodexplorer.ui.screens.home.HomeScreen
import com.medrhouma.foodexplorer.ui.screens.search.SearchScreen
import com.medrhouma.foodexplorer.viewmodel.MealViewModel

/**
 * Routes de navigation de l'application
 */
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Search : Screen("search")
    object Favorites : Screen("favorites")
    object Categories : Screen("categories")
    object Details : Screen("details/{mealId}") {
        fun createRoute(mealId: String) = "details/$mealId"
    }
}

/**
 * Items de la barre de navigation
 */
data class BottomNavItem(
    val screen: Screen,
    val icon: ImageVector,
    val labelResId: Int
)

/**
 * Liste des items de navigation
 */
val bottomNavItems = listOf(
    BottomNavItem(Screen.Home, Icons.Default.Home, R.string.nav_home),
    BottomNavItem(Screen.Search, Icons.Default.Search, R.string.nav_search),
    BottomNavItem(Screen.Favorites, Icons.Default.Favorite, R.string.nav_favorites),
    BottomNavItem(Screen.Categories, Icons.Default.Category, R.string.nav_categories)
)

/**
 * Composant principal de navigation de l'application
 * Gère la navigation entre les écrans et la barre de navigation
 */
@Composable
fun AppNavigation(
    viewModel: MealViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    // Détermine si on doit afficher la barre de navigation
    val showBottomBar = currentDestination?.route != Screen.Details.route
    
    Scaffold(
        modifier = modifier,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = stringResource(item.labelResId)
                                )
                            },
                            label = { Text(text = stringResource(item.labelResId)) },
                            selected = currentDestination?.hierarchy?.any { 
                                it.route == item.screen.route 
                            } == true,
                            onClick = {
                                navController.navigate(item.screen.route) {
                                    // Pop up to the start destination to avoid building up a large stack
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    // Avoid multiple copies of the same destination
                                    launchSingleTop = true
                                    // Restore state when reselecting a previously selected item
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Écran d'accueil
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onMealClick = { mealId ->
                        navController.navigate(Screen.Details.createRoute(mealId))
                    }
                )
            }
            
            // Écran de recherche
            composable(Screen.Search.route) {
                SearchScreen(
                    viewModel = viewModel,
                    onMealClick = { mealId ->
                        navController.navigate(Screen.Details.createRoute(mealId))
                    }
                )
            }
            
            // Écran des favoris
            composable(Screen.Favorites.route) {
                FavoritesScreen(
                    viewModel = viewModel,
                    onMealClick = { mealId ->
                        navController.navigate(Screen.Details.createRoute(mealId))
                    }
                )
            }
            
            // Écran des catégories
            composable(Screen.Categories.route) {
                CategoriesScreen(
                    viewModel = viewModel,
                    onMealClick = { mealId ->
                        navController.navigate(Screen.Details.createRoute(mealId))
                    }
                )
            }
            
            // Écran de détails
            composable(
                route = Screen.Details.route,
                arguments = listOf(
                    navArgument("mealId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val mealId = backStackEntry.arguments?.getString("mealId") ?: ""
                DetailsScreen(
                    mealId = mealId,
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}
