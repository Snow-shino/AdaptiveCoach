package com.example.adaptiveathlete

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.adaptiveathlete.data.db.dao.ExerciseDao
import com.example.adaptiveathlete.data.db.dao.WorkoutTemplateDao
import com.example.adaptiveathlete.data.db.dao.ProgramDao
import com.example.adaptiveathlete.data.repository.SeedDataRepository
import com.example.adaptiveathlete.data.repository.BenchmarkRepository
import com.example.adaptiveathlete.data.repository.ProgramSeedRepository
import com.example.adaptiveathlete.ui.screens.Screen
import com.example.adaptiveathlete.ui.screens.*
import com.example.adaptiveathlete.ui.theme.AdaptiveAthleteTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var seedDataRepository: SeedDataRepository

    @Inject
    lateinit var programSeedRepository: ProgramSeedRepository

    @Inject
    lateinit var exerciseDao: ExerciseDao

    @Inject
    lateinit var workoutTemplateDao: WorkoutTemplateDao

    @Inject
    lateinit var programDao: ProgramDao

    @Inject
    lateinit var benchmarkRepository: BenchmarkRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Seed database on first launch
        lifecycleScope.launch {
            try {
                Log.d("MainActivity", "🔧 Starting database initialization...")

                // First, seed exercises if needed
                val exercises = exerciseDao.getAllExercises().first()
                if (exercises.isEmpty()) {
                    Log.d("MainActivity", "📦 Seeding exercises...")
                    seedDataRepository.seedDatabase()
                    val count = exerciseDao.getAllExercises().first().size
                    Log.d("MainActivity", "✅ Seeded $count exercises")
                }

                // Create V6 Athlete Program if no active program exists
                val activeProgram = programDao.getActiveProgram()
                if (activeProgram == null) {
                    Log.d("MainActivity", "🏋️ Creating V6 Athlete Program...")
                    val programId = programSeedRepository.createV6AthleteProgram()
                    Log.d("MainActivity", "✅ V6 Athlete Program created with ID: $programId")

                    // Verify
                    val verify = programDao.getActiveProgram()
                    Log.d("MainActivity", "📋 Active program: ${verify?.name}")
                } else {
                    Log.d("MainActivity", "✅ Active program exists: ${activeProgram.name}")
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "❌ Error seeding database: ${e.message}", e)
                e.printStackTrace()
            }
        }

        enableEdgeToEdge()
        setContent {
            AdaptiveAthleteTheme {
                var currentScreen by remember { mutableStateOf<Screen>(Screen.Homepage) }
                var selectedWorkout by remember { mutableStateOf<String?>(null) }
                val benchmarks by benchmarkRepository.getAllResults().collectAsState(initial = emptyList())

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when (currentScreen) {
                        is Screen.Homepage -> HomepageScreen(
                            onBenchmarkClick = {
                                currentScreen = Screen.BenchmarkMenu
                            },
                            onWorkoutClick = { workoutType: String ->
                                Log.d("MainActivity", "Workout clicked: $workoutType")
                                selectedWorkout = workoutType
                                currentScreen = Screen.Workout
                            },
                            onDayClick = { date ->
                                Log.d("MainActivity", "Day clicked: $date")
                                // TODO: Show workouts for that day
                            }
                        )

                        is Screen.BenchmarkMenu -> BenchmarkMenuScreen(
                            onBack = { currentScreen = Screen.Homepage },
                            onStartLifting = { currentScreen = Screen.LiftingBenchmark },
                            onStartClimbing = {
                                Log.d("MainActivity", "Climbing benchmark TODO")
                            },
                            onStartRunning = {
                                Log.d("MainActivity", "Running benchmark TODO")
                            },
                            onViewHistory = { currentScreen = Screen.BenchmarkHistory }
                        )

                        is Screen.LiftingBenchmark -> LiftingBenchmarkScreen(
                            onComplete = { benchmarkData ->
                                lifecycleScope.launch {
                                    try {
                                        val id = benchmarkRepository.saveLiftingBenchmark(benchmarkData as BenchmarkData.Lifting)
                                        Log.d("MainActivity", "Benchmark saved with ID: $id")
                                    } catch (e: Exception) {
                                        Log.e("MainActivity", "Error saving benchmark", e)
                                    }
                                }
                                currentScreen = Screen.Homepage
                            },
                            onCancel = { currentScreen = Screen.BenchmarkMenu }
                        )

                        is Screen.BenchmarkHistory -> BenchmarkHistoryScreen(
                            benchmarks = benchmarks,
                            onBack = { currentScreen = Screen.BenchmarkMenu },
                            onDelete = { benchmarkId ->
                                lifecycleScope.launch {
                                    try {
                                        // TODO: Delete benchmark
                                        Log.d("MainActivity", "Delete benchmark: $benchmarkId")
                                    } catch (e: Exception) {
                                        Log.e("MainActivity", "Error deleting benchmark", e)
                                    }
                                }
                            }
                        )

                        is Screen.Workout -> {
                            // TODO: Show actual workout screen
                            Text("Workout: $selectedWorkout")
                        }

                        else -> {
                            // Handle other screens
                            Text("Screen not implemented")
                        }
                    }
                }
            }
        }
    }
}

