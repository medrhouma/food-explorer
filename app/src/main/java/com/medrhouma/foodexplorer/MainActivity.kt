package com.medrhouma.foodexplorer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.medrhouma.foodexplorer.data.api.MealApiService
import com.medrhouma.foodexplorer.data.local.MealDatabase
import com.medrhouma.foodexplorer.data.repository.MealRepository
import com.medrhouma.foodexplorer.ui.navigation.AppNavigation
import com.medrhouma.foodexplorer.ui.theme.FoodExplorerTheme
import com.medrhouma.foodexplorer.viewmodel.MealViewModel
import com.medrhouma.foodexplorer.viewmodel.MealViewModelFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Activité principale de l'application FoodExplorer
 * Configure les dépendances et lance l'interface Compose
 */
class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Configuration du client HTTP avec logging
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
        
        // Configuration de Retrofit pour l'API TheMealDB
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.themealdb.com/api/json/v1/1/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        
        val apiService = retrofit.create(MealApiService::class.java)
        
        // Initialisation de la base de données Room
        val database = MealDatabase.getDatabase(applicationContext)
        val mealDao = database.mealDao()
        
        // Création du repository
        val repository = MealRepository(apiService, mealDao)
        
        // Factory pour le ViewModel
        val viewModelFactory = MealViewModelFactory(repository)
        
        setContent {
            FoodExplorerTheme {
                // Création du ViewModel avec la factory
                val mealViewModel: MealViewModel = viewModel(factory = viewModelFactory)
                
                // Navigation principale de l'application
                AppNavigation(viewModel = mealViewModel)
            }
        }
    }
}
