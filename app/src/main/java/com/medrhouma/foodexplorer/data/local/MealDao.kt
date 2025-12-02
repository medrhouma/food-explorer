package com.medrhouma.foodexplorer.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.medrhouma.foodexplorer.data.local.entities.FavoriteMealEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO pour les opérations sur les recettes favorites
 */
@Dao
interface MealDao {
    
    /**
     * Récupère tous les favoris sous forme de Flow réactif
     */
    @Query("SELECT * FROM favorite_meals")
    fun getAllFavorites(): Flow<List<FavoriteMealEntity>>
    
    /**
     * Récupère un favori par son ID
     * @param id L'identifiant de la recette
     */
    @Query("SELECT * FROM favorite_meals WHERE idMeal = :id")
    suspend fun getFavoriteById(id: String): FavoriteMealEntity?
    
    /**
     * Insère une recette dans les favoris
     * Remplace si déjà existante
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(meal: FavoriteMealEntity)
    
    /**
     * Supprime une recette des favoris
     */
    @Delete
    suspend fun deleteFavorite(meal: FavoriteMealEntity)
    
    /**
     * Supprime une recette des favoris par son ID
     * @param id L'identifiant de la recette à supprimer
     */
    @Query("DELETE FROM favorite_meals WHERE idMeal = :id")
    suspend fun deleteFavoriteById(id: String)
}
