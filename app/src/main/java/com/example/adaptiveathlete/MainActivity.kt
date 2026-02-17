package com.example.adaptiveathlete

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.adaptiveathlete.data.db.dao.ExerciseDao
import com.example.adaptiveathlete.data.db.dao.WorkoutTemplateDao
import com.example.adaptiveathlete.data.repository.SeedDataRepository
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
    lateinit var exerciseDao: ExerciseDao

    @Inject
    lateinit var workoutTemplateDao: WorkoutTemplateDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Seed database on first launch
        lifecycleScope.launch {
            try {
                val exercises = exerciseDao.getAllExercises().first()
                if (exercises.isEmpty()) {
                    Log.d("MainActivity", "Seeding database...")
                    seedDataRepository.seedDatabase()
                    Log.d("MainActivity", "Database seeded successfully!")
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Error seeding database", e)
            }
        }

        enableEdgeToEdge()
        setContent {
            AdaptiveAthleteTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        modifier = Modifier.padding(innerPadding),
                        exerciseDao = exerciseDao,
                        workoutTemplateDao = workoutTemplateDao
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    exerciseDao: ExerciseDao,
    workoutTemplateDao: WorkoutTemplateDao
) {
    val exercises by exerciseDao.getAllExercises().collectAsState(initial = emptyList())
    val templates by workoutTemplateDao.getAllTemplates().collectAsState(initial = emptyList())

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🏋️ Adaptive Athlete",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Database Status",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(16.dp))

                if (exercises.isEmpty()) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Loading database...")
                } else {
                    Text(
                        text = "✅ ${exercises.size} exercises loaded",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "✅ ${templates.size} workout templates ready",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (templates.isNotEmpty()) {
            Text(
                text = "Workout Templates:",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            templates.forEach { template ->
                Text(
                    text = "• ${template.name}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "✨ Ready to build the UI!",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}