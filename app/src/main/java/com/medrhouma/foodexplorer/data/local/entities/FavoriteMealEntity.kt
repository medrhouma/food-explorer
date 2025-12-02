package com.medrhouma.foodexplorer.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity Room représentant une recette favorite stockée localement
 */
@Entity(tableName = "favorite_meals")
data class FavoriteMealEntity(
    @PrimaryKey
    val idMeal: String,
    val strMeal: String,
    val strMealThumb: String?,
    val strCategory: String?
)
