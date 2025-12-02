package com.medrhouma.foodexplorer.data.repository

import com.medrhouma.foodexplorer.data.api.MealApiService
import com.medrhouma.foodexplorer.data.api.models.Category
import com.medrhouma.foodexplorer.data.api.models.Meal
import com.medrhouma.foodexplorer.data.local.MealDao
import com.medrhouma.foodexplorer.data.local.entities.FavoriteMealEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repository centralisant l'accès aux données
 * Combine les sources API et base de données locale
 */
class MealRepository(
    private val apiService: MealApiService,
    private val mealDao: MealDao
) {
    companion object {
        /** Buffer supplémentaire de requêtes pour obtenir des recettes uniques */
        private const val EXTRA_REQUESTS_BUFFER = 5
        /** Nombre par défaut de recettes aléatoires */
        private const val DEFAULT_RANDOM_COUNT = 10
    }
    
    // ==================== API Operations ====================
    
    /**
     * Récupère plusieurs recettes aléatoires depuis l'API
     * L'API ne retourne qu'une recette à la fois, donc on fait plusieurs appels
     */
    suspend fun getRandomMeals(count: Int = DEFAULT_RANDOM_COUNT): Result<List<Meal>> {
        return try {
            val meals = mutableListOf<Meal>()
            val seenIds = mutableSetOf<String>()
            
            // On fait plus d'appels pour avoir assez de recettes uniques
            repeat(count + EXTRA_REQUESTS_BUFFER) {
                if (meals.size >= count) return@repeat
                
                val response = apiService.getRandomMeal()
                response.meals?.firstOrNull()?.let { meal ->
                    if (meal.idMeal !in seenIds) {
                        seenIds.add(meal.idMeal)
                        meals.add(meal)
                    }
                }
            }
            
            Result.success(meals.take(count))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Recherche des recettes par nom
     */
    suspend fun searchMealsByName(name: String): Result<List<Meal>> {
        return try {
            val response = apiService.searchMealByName(name)
            Result.success(response.meals ?: emptyList())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Recherche des recettes par ingrédient
     */
    suspend fun searchMealsByIngredient(ingredient: String): Result<List<Meal>> {
        return try {
            val response = apiService.searchMealByIngredient(ingredient)
            Result.success(response.meals ?: emptyList())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Récupère les détails complets d'une recette
     */
    suspend fun getMealDetails(id: String): Result<Meal> {
        return try {
            val response = apiService.getMealDetails(id)
            val meal = response.meals?.firstOrNull()
            if (meal != null) {
                Result.success(meal)
            } else {
                Result.failure(Exception("Recette non trouvée"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Récupère toutes les catégories
     */
    suspend fun getCategories(): Result<List<Category>> {
        return try {
            val response = apiService.getCategories()
            Result.success(response.categories ?: emptyList())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Récupère les recettes d'une catégorie spécifique
     */
    suspend fun getMealsByCategory(category: String): Result<List<Meal>> {
        return try {
            val response = apiService.getMealsByCategory(category)
            Result.success(response.meals ?: emptyList())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // ==================== Database Operations ====================
    
    /**
     * Récupère tous les favoris sous forme de Flow réactif
     */
    fun getAllFavorites(): Flow<List<FavoriteMealEntity>> {
        return mealDao.getAllFavorites()
    }
    
    /**
     * Vérifie si une recette est dans les favoris
     */
    suspend fun isFavorite(id: String): Boolean {
        return mealDao.getFavoriteById(id) != null
    }
    
    /**
     * Ajoute une recette aux favoris
     */
    suspend fun addToFavorites(meal: Meal) {
        val entity = FavoriteMealEntity(
            idMeal = meal.idMeal,
            strMeal = meal.strMeal,
            strMealThumb = meal.strMealThumb,
            strCategory = meal.strCategory
        )
        mealDao.insertFavorite(entity)
    }
    
    /**
     * Retire une recette des favoris
     */
    suspend fun removeFromFavorites(id: String) {
        mealDao.deleteFavoriteById(id)
    }
    
    /**
     * Bascule l'état favori d'une recette
     */
    suspend fun toggleFavorite(meal: Meal): Boolean {
        return if (isFavorite(meal.idMeal)) {
            removeFromFavorites(meal.idMeal)
            false
        } else {
            addToFavorites(meal)
            true
        }
    }
}
