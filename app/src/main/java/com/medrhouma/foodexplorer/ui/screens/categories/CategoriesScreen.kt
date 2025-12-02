package com.medrhouma.foodexplorer.ui.screens.categories

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.medrhouma.foodexplorer.R
import com.medrhouma.foodexplorer.data.api.models.Category
import com.medrhouma.foodexplorer.ui.components.MealCardCompact
import com.medrhouma.foodexplorer.viewmodel.MealViewModel
import com.medrhouma.foodexplorer.viewmodel.UiState
import kotlinx.coroutines.launch

/**
 * Écran des catégories
 * Affiche toutes les catégories de recettes avec possibilité de voir les recettes de chaque catégorie
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    viewModel: MealViewModel,
    onMealClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val categoriesState by viewModel.categories.collectAsState()
    val categoryMealsState by viewModel.categoryMeals.collectAsState()
    
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    
    // Charge les catégories au premier affichage
    LaunchedEffect(Unit) {
        viewModel.loadCategories()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.categories_title)) },
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
            when (val state = categoriesState) {
                is UiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                is UiState.Success -> {
                    // Grille des catégories
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.data) { category ->
                            CategoryCard(
                                category = category,
                                onClick = {
                                    selectedCategory = category
                                    viewModel.loadMealsByCategory(category.strCategory)
                                    scope.launch {
                                        sheetState.show()
                                    }
                                }
                            )
                        }
                    }
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
                            onClick = { viewModel.loadCategories() },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text(text = stringResource(R.string.retry))
                        }
                    }
                }
            }
        }
    }
    
    // Bottom sheet pour afficher les recettes de la catégorie sélectionnée
    if (selectedCategory != null) {
        ModalBottomSheet(
            onDismissRequest = {
                scope.launch {
                    sheetState.hide()
                    selectedCategory = null
                }
            },
            sheetState = sheetState
        ) {
            CategoryMealsSheet(
                category = selectedCategory!!,
                mealsState = categoryMealsState,
                onMealClick = { mealId ->
                    scope.launch {
                        sheetState.hide()
                        selectedCategory = null
                        onMealClick(mealId)
                    }
                },
                onRetry = {
                    selectedCategory?.let {
                        viewModel.loadMealsByCategory(it.strCategory)
                    }
                }
            )
        }
    }
}

/**
 * Card pour afficher une catégorie
 */
@Composable
private fun CategoryCard(
    category: Category,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column {
            // Image de la catégorie
            AsyncImage(
                model = category.strCategoryThumb,
                contentDescription = category.strCategory,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentScale = ContentScale.Crop
            )
            
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                // Nom de la catégorie
                Text(
                    text = category.strCategory,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                // Description courte (max 2 lignes)
                category.strCategoryDescription?.let { description ->
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

/**
 * Contenu du bottom sheet affichant les recettes d'une catégorie
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryMealsSheet(
    category: Category,
    mealsState: UiState<List<com.medrhouma.foodexplorer.data.api.models.Meal>>,
    onMealClick: (String) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp)
    ) {
        // Header
        Text(
            text = "${category.strCategory} - ${stringResource(R.string.categories_recipes)}",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
        ) {
            when (mealsState) {
                is UiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                is UiState.Success -> {
                    if (mealsState.data.isEmpty()) {
                        Text(
                            text = stringResource(R.string.search_no_results),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(mealsState.data) { meal ->
                                MealCardCompact(
                                    meal = meal,
                                    onClick = { onMealClick(meal.idMeal) }
                                )
                            }
                        }
                    }
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
                            text = mealsState.message,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Button(
                            onClick = onRetry,
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
