package com.medrhouma.foodexplorer.ui.screens.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.medrhouma.foodexplorer.R
import com.medrhouma.foodexplorer.data.api.models.Meal
import com.medrhouma.foodexplorer.viewmodel.MealViewModel
import com.medrhouma.foodexplorer.viewmodel.UiState

/**
 * Écran de détails d'une recette
 * Affiche toutes les informations de la recette avec possibilité d'ajouter aux favoris
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    mealId: String,
    viewModel: MealViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val mealState by viewModel.mealDetails.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()
    
    // Charge les détails au premier affichage
    LaunchedEffect(mealId) {
        viewModel.getMealDetails(mealId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    when (val state = mealState) {
                        is UiState.Success -> Text(state.data.strMeal)
                        else -> Text(stringResource(R.string.loading))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    // Badge si favori
                    if (isFavorite) {
                        Badge(
                            modifier = Modifier.padding(end = 8.dp),
                            containerColor = MaterialTheme.colorScheme.secondary
                        ) {
                            Text(
                                text = stringResource(R.string.details_in_favorites),
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            // FAB pour ajouter/retirer des favoris
            if (mealState is UiState.Success) {
                FloatingActionButton(
                    onClick = {
                        (mealState as? UiState.Success)?.data?.let { meal ->
                            viewModel.toggleFavorite(meal)
                        }
                    },
                    containerColor = if (isFavorite) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.secondary
                    }
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (isFavorite) {
                            stringResource(R.string.details_remove_favorite)
                        } else {
                            stringResource(R.string.details_add_favorite)
                        },
                        tint = MaterialTheme.colorScheme.onSecondary
                    )
                }
            }
        },
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = mealState) {
                is UiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                is UiState.Success -> {
                    MealDetailsContent(meal = state.data)
                }
                
                is UiState.Error -> {
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
                            onClick = { viewModel.getMealDetails(mealId) },
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

/**
 * Contenu détaillé de la recette
 */
@Composable
private fun MealDetailsContent(
    meal: Meal,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Image grande en haut
        AsyncImage(
            model = meal.strMealThumb,
            contentDescription = meal.strMeal,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            contentScale = ContentScale.Crop
        )
        
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Nom de la recette
            Text(
                text = meal.strMeal,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Catégorie et origine
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Catégorie
                meal.strCategory?.let { category ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = category,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
                
                // Origine
                meal.strArea?.let { area ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = area,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Section ingrédients
            Text(
                text = stringResource(R.string.details_ingredients),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Liste des ingrédients
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    meal.getIngredients().forEach { (ingredient, measure) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "• $ingredient",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            if (measure.isNotBlank()) {
                                Text(
                                    text = measure,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Section instructions
            Text(
                text = stringResource(R.string.details_instructions),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Instructions de préparation
            meal.strInstructions?.let { instructions ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Text(
                        text = instructions,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(12.dp),
                        lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.3
                    )
                }
            }
            
            // Espace pour le FAB
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
