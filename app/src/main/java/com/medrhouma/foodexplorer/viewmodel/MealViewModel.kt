package com.medrhouma.foodexplorer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.medrhouma.foodexplorer.data.api.models.Category
import com.medrhouma.foodexplorer.data.api.models.Meal
import com.medrhouma.foodexplorer.data.local.entities.FavoriteMealEntity
import com.medrhouma.foodexplorer.data.repository.MealRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * États UI génériques pour la gestion des chargements
 */
sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

/**
 * ViewModel principal de l'application
 * Gère tous les états et les interactions avec les données
 */
class MealViewModel(
    private val repository: MealRepository
) : ViewModel() {
    
    // ==================== États pour l'écran d'accueil ====================
    private val _randomMeals = MutableStateFlow<UiState<List<Meal>>>(UiState.Loading)
    val randomMeals: StateFlow<UiState<List<Meal>>> = _randomMeals.asStateFlow()
    
    // ==================== États pour les détails ====================
    private val _mealDetails = MutableStateFlow<UiState<Meal>>(UiState.Loading)
    val mealDetails: StateFlow<UiState<Meal>> = _mealDetails.asStateFlow()
    
    // ==================== États pour la recherche ====================
    private val _searchResults = MutableStateFlow<UiState<List<Meal>>>(UiState.Success(emptyList()))
    val searchResults: StateFlow<UiState<List<Meal>>> = _searchResults.asStateFlow()
    
    // ==================== États pour les catégories ====================
    private val _categories = MutableStateFlow<UiState<List<Category>>>(UiState.Loading)
    val categories: StateFlow<UiState<List<Category>>> = _categories.asStateFlow()
    
    private val _categoryMeals = MutableStateFlow<UiState<List<Meal>>>(UiState.Loading)
    val categoryMeals: StateFlow<UiState<List<Meal>>> = _categoryMeals.asStateFlow()
    
    // ==================== États pour les favoris ====================
    private val _favorites = MutableStateFlow<List<FavoriteMealEntity>>(emptyList())
    val favorites: StateFlow<List<FavoriteMealEntity>> = _favorites.asStateFlow()
    
    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()
    
    init {
        loadFavorites()
    }
    
    /**
     * Charge les recettes aléatoires pour l'écran d'accueil
     */
    fun loadRandomMeals() {
        viewModelScope.launch {
            _randomMeals.value = UiState.Loading
            repository.getRandomMeals(10).fold(
                onSuccess = { meals ->
                    _randomMeals.value = UiState.Success(meals)
                },
                onFailure = { error ->
                    _randomMeals.value = UiState.Error(error.message ?: "Erreur inconnue")
                }
            )
        }
    }
    
    /**
     * Recherche des recettes par nom ou par ingrédient
     */
    fun searchMeals(query: String, byIngredient: Boolean) {
        if (query.isBlank()) {
            _searchResults.value = UiState.Success(emptyList())
            return
        }
        
        viewModelScope.launch {
            _searchResults.value = UiState.Loading
            
            val result = if (byIngredient) {
                repository.searchMealsByIngredient(query)
            } else {
                repository.searchMealsByName(query)
            }
            
            result.fold(
                onSuccess = { meals ->
                    _searchResults.value = UiState.Success(meals)
                },
                onFailure = { error ->
                    _searchResults.value = UiState.Error(error.message ?: "Erreur inconnue")
                }
            )
        }
    }
    
    /**
     * Charge les détails d'une recette spécifique
     */
    fun getMealDetails(id: String) {
        viewModelScope.launch {
            _mealDetails.value = UiState.Loading
            
            // Vérifie aussi si c'est un favori
            checkIsFavorite(id)
            
            repository.getMealDetails(id).fold(
                onSuccess = { meal ->
                    _mealDetails.value = UiState.Success(meal)
                },
                onFailure = { error ->
                    _mealDetails.value = UiState.Error(error.message ?: "Erreur inconnue")
                }
            )
        }
    }
    
    /**
     * Charge la liste des catégories
     */
    fun loadCategories() {
        viewModelScope.launch {
            _categories.value = UiState.Loading
            repository.getCategories().fold(
                onSuccess = { categories ->
                    _categories.value = UiState.Success(categories)
                },
                onFailure = { error ->
                    _categories.value = UiState.Error(error.message ?: "Erreur inconnue")
                }
            )
        }
    }
    
    /**
     * Charge les recettes d'une catégorie spécifique
     */
    fun loadMealsByCategory(category: String) {
        viewModelScope.launch {
            _categoryMeals.value = UiState.Loading
            repository.getMealsByCategory(category).fold(
                onSuccess = { meals ->
                    _categoryMeals.value = UiState.Success(meals)
                },
                onFailure = { error ->
                    _categoryMeals.value = UiState.Error(error.message ?: "Erreur inconnue")
                }
            )
        }
    }
    
    /**
     * Bascule l'état favori d'une recette
     */
    fun toggleFavorite(meal: Meal) {
        viewModelScope.launch {
            val nowFavorite = repository.toggleFavorite(meal)
            _isFavorite.value = nowFavorite
        }
    }
    
    /**
     * Vérifie si une recette est dans les favoris
     */
    fun checkIsFavorite(id: String) {
        viewModelScope.launch {
            _isFavorite.value = repository.isFavorite(id)
        }
    }
    
    /**
     * Charge les favoris depuis la base de données locale
     */
    fun loadFavorites() {
        viewModelScope.launch {
            repository.getAllFavorites().collect { favs ->
                _favorites.value = favs
            }
        }
    }
    
    /**
     * Retire une recette des favoris par son ID
     */
    fun removeFromFavorites(id: String) {
        viewModelScope.launch {
            repository.removeFromFavorites(id)
        }
    }
}

/**
 * Factory pour créer le ViewModel avec ses dépendances
 */
class MealViewModelFactory(
    private val repository: MealRepository
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MealViewModel::class.java)) {
            return MealViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
