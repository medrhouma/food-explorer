package com.medrhouma.foodexplorer.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.medrhouma.foodexplorer.data.local.entities.FavoriteMealEntity

/**
 * Base de données Room pour le stockage local des favoris
 * Utilise le pattern Singleton pour garantir une seule instance
 */
@Database(
    entities = [FavoriteMealEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MealDatabase : RoomDatabase() {
    
    abstract fun mealDao(): MealDao
    
    companion object {
        @Volatile
        private var INSTANCE: MealDatabase? = null
        
        /**
         * Retourne l'instance unique de la base de données
         * Crée une nouvelle instance si elle n'existe pas encore
         */
        fun getDatabase(context: Context): MealDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MealDatabase::class.java,
                    "meal_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
