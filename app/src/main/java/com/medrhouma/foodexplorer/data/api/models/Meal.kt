package com.medrhouma.foodexplorer.data.api.models

import com.google.gson.annotations.SerializedName

/**
 * Response wrapper pour les recettes de l'API TheMealDB
 */
data class MealResponse(
    @SerializedName("meals")
    val meals: List<Meal>?
)

/**
 * Modèle représentant une recette complète
 * Inclut tous les champs retournés par l'API
 */
data class Meal(
    @SerializedName("idMeal")
    val idMeal: String,
    
    @SerializedName("strMeal")
    val strMeal: String,
    
    @SerializedName("strCategory")
    val strCategory: String? = null,
    
    @SerializedName("strArea")
    val strArea: String? = null,
    
    @SerializedName("strInstructions")
    val strInstructions: String? = null,
    
    @SerializedName("strMealThumb")
    val strMealThumb: String? = null,
    
    @SerializedName("strTags")
    val strTags: String? = null,
    
    @SerializedName("strYoutube")
    val strYoutube: String? = null,
    
    @SerializedName("strSource")
    val strSource: String? = null,
    
    // Ingrédients (1 à 20)
    @SerializedName("strIngredient1") val strIngredient1: String? = null,
    @SerializedName("strIngredient2") val strIngredient2: String? = null,
    @SerializedName("strIngredient3") val strIngredient3: String? = null,
    @SerializedName("strIngredient4") val strIngredient4: String? = null,
    @SerializedName("strIngredient5") val strIngredient5: String? = null,
    @SerializedName("strIngredient6") val strIngredient6: String? = null,
    @SerializedName("strIngredient7") val strIngredient7: String? = null,
    @SerializedName("strIngredient8") val strIngredient8: String? = null,
    @SerializedName("strIngredient9") val strIngredient9: String? = null,
    @SerializedName("strIngredient10") val strIngredient10: String? = null,
    @SerializedName("strIngredient11") val strIngredient11: String? = null,
    @SerializedName("strIngredient12") val strIngredient12: String? = null,
    @SerializedName("strIngredient13") val strIngredient13: String? = null,
    @SerializedName("strIngredient14") val strIngredient14: String? = null,
    @SerializedName("strIngredient15") val strIngredient15: String? = null,
    @SerializedName("strIngredient16") val strIngredient16: String? = null,
    @SerializedName("strIngredient17") val strIngredient17: String? = null,
    @SerializedName("strIngredient18") val strIngredient18: String? = null,
    @SerializedName("strIngredient19") val strIngredient19: String? = null,
    @SerializedName("strIngredient20") val strIngredient20: String? = null,
    
    // Mesures/Quantités (1 à 20)
    @SerializedName("strMeasure1") val strMeasure1: String? = null,
    @SerializedName("strMeasure2") val strMeasure2: String? = null,
    @SerializedName("strMeasure3") val strMeasure3: String? = null,
    @SerializedName("strMeasure4") val strMeasure4: String? = null,
    @SerializedName("strMeasure5") val strMeasure5: String? = null,
    @SerializedName("strMeasure6") val strMeasure6: String? = null,
    @SerializedName("strMeasure7") val strMeasure7: String? = null,
    @SerializedName("strMeasure8") val strMeasure8: String? = null,
    @SerializedName("strMeasure9") val strMeasure9: String? = null,
    @SerializedName("strMeasure10") val strMeasure10: String? = null,
    @SerializedName("strMeasure11") val strMeasure11: String? = null,
    @SerializedName("strMeasure12") val strMeasure12: String? = null,
    @SerializedName("strMeasure13") val strMeasure13: String? = null,
    @SerializedName("strMeasure14") val strMeasure14: String? = null,
    @SerializedName("strMeasure15") val strMeasure15: String? = null,
    @SerializedName("strMeasure16") val strMeasure16: String? = null,
    @SerializedName("strMeasure17") val strMeasure17: String? = null,
    @SerializedName("strMeasure18") val strMeasure18: String? = null,
    @SerializedName("strMeasure19") val strMeasure19: String? = null,
    @SerializedName("strMeasure20") val strMeasure20: String? = null
) {
    /**
     * Récupère la liste des ingrédients avec leurs quantités
     * Filtre les valeurs nulles ou vides
     */
    fun getIngredients(): List<Pair<String, String>> {
        val ingredients = listOf(
            strIngredient1 to strMeasure1,
            strIngredient2 to strMeasure2,
            strIngredient3 to strMeasure3,
            strIngredient4 to strMeasure4,
            strIngredient5 to strMeasure5,
            strIngredient6 to strMeasure6,
            strIngredient7 to strMeasure7,
            strIngredient8 to strMeasure8,
            strIngredient9 to strMeasure9,
            strIngredient10 to strMeasure10,
            strIngredient11 to strMeasure11,
            strIngredient12 to strMeasure12,
            strIngredient13 to strMeasure13,
            strIngredient14 to strMeasure14,
            strIngredient15 to strMeasure15,
            strIngredient16 to strMeasure16,
            strIngredient17 to strMeasure17,
            strIngredient18 to strMeasure18,
            strIngredient19 to strMeasure19,
            strIngredient20 to strMeasure20
        )
        
        return ingredients
            .filter { (ingredient, _) -> !ingredient.isNullOrBlank() }
            .map { (ingredient, measure) -> 
                (ingredient ?: "") to (measure?.trim() ?: "")
            }
    }
}

/**
 * Response wrapper pour les catégories de l'API TheMealDB
 */
data class CategoryResponse(
    @SerializedName("categories")
    val categories: List<Category>?
)

/**
 * Modèle représentant une catégorie de recettes
 */
data class Category(
    @SerializedName("idCategory")
    val idCategory: String,
    
    @SerializedName("strCategory")
    val strCategory: String,
    
    @SerializedName("strCategoryThumb")
    val strCategoryThumb: String?,
    
    @SerializedName("strCategoryDescription")
    val strCategoryDescription: String?
)
