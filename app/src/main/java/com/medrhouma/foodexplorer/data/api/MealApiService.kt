package com.medrhouma.foodexplorer.data.api

import com.medrhouma.foodexplorer.data.api.models.CategoryResponse
import com.medrhouma.foodexplorer.data.api.models.MealResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interface définissant les endpoints de l'API TheMealDB
 * Base URL: https://www.themealdb.com/api/json/v1/1/
 */
interface MealApiService {
    
    /**
     * Récupère une recette aléatoire
     */
    @GET("random.php")
    suspend fun getRandomMeal(): MealResponse
    
    /**
     * Recherche des recettes par nom
     * @param name Le nom de la recette à rechercher
     */
    @GET("search.php")
    suspend fun searchMealByName(@Query("s") name: String): MealResponse
    
    /**
     * Recherche des recettes par ingrédient
     * @param ingredient L'ingrédient à rechercher
     */
    @GET("filter.php")
    suspend fun searchMealByIngredient(@Query("i") ingredient: String): MealResponse
    
    /**
     * Récupère les détails complets d'une recette par son ID
     * @param id L'identifiant de la recette
     */
    @GET("lookup.php")
    suspend fun getMealDetails(@Query("i") id: String): MealResponse
    
    /**
     * Récupère la liste de toutes les catégories
     */
    @GET("categories.php")
    suspend fun getCategories(): CategoryResponse
    
    /**
     * Récupère les recettes d'une catégorie spécifique
     * @param category Le nom de la catégorie
     */
    @GET("filter.php")
    suspend fun getMealsByCategory(@Query("c") category: String): MealResponse
}
