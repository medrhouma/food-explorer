package com.medrhouma.foodexplorer.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.medrhouma.foodexplorer.R
import com.medrhouma.foodexplorer.ui.components.MealCard
import com.medrhouma.foodexplorer.viewmodel.MealViewModel
import com.medrhouma.foodexplorer.viewmodel.UiState

/**
 * Écran d'accueil affichant les recettes aléatoires
 * Charge 10+ recettes depuis l'API au démarrage
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MealViewModel,
    onMealClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val mealsState by viewModel.randomMeals.collectAsState()
    
    // Charge les recettes au premier affichage
    LaunchedEffect(Unit) {
        viewModel.loadRandomMeals()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = stringResource(R.string.home_title),
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = stringResource(R.string.home_subtitle),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = mealsState) {
                is UiState.Loading -> {
                    // État de chargement
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                is UiState.Success -> {
                    // Liste des recettes
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(state.data) { meal ->
                            MealCard(
                                meal = meal,
                                onClick = { onMealClick(meal.idMeal) }
                            )
                        }
                    }
                }
                
                is UiState.Error -> {
                    // État d'erreur avec bouton retry
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp)
                            .align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Button(
                            onClick = { viewModel.loadRandomMeals() },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text(text = stringResource(R.string.retry))
                        }
                    }
                }
            }
        }
    }
}
