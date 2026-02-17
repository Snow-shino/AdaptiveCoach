# Quick Start Guide - Adaptive Athlete

## ⚡ First-Time Setup

### 1. Sync Dependencies
**In Android Studio:**
1. File → Sync Project with Gradle Files
2. Wait for download (~3 minutes)
3. Build → Make Project

**Expected outcome:** Build succeeds, no errors

---

### 2. Run Unit Tests
Verify progression engine works:

```bash
# Windows
gradlew test

# Or in Android Studio
Right-click test/java folder → Run 'All Tests'
```

**Expected outcome:** All 20+ tests pass ✅

---

### 3. Seed the Database
Add this code to `MainActivity.kt`:

```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var seedDataRepository: SeedDataRepository
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // One-time seed on first launch
        lifecycleScope.launch {
            // Check if already seeded (query exercises table)
            // If empty, call seedDataRepository.seedDatabase()
        }
        
        enableEdgeToEdge()
        setContent {
            AdaptiveAthleteTheme {
                // Your UI here
            }
        }
    }
}
```

---

## 🎨 Building the UI - Step by Step

### Step 1: Create Home Screen

**File: `ui/home/HomeScreen.kt`**

```kotlin
package com.example.adaptiveathlete.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onStartWorkout: (Long) -> Unit
) {
    val templates by viewModel.templates.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Adaptive Athlete") })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(templates) { template ->
                WorkoutTemplateCard(
                    template = template,
                    onClick = { onStartWorkout(template.id) }
                )
            }
        }
    }
}

@Composable
fun WorkoutTemplateCard(
    template: WorkoutTemplateEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = template.name,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = template.description ?: "",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${template.estimatedDurationMinutes} min • Difficulty ${template.difficulty}/10",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}
```

**File: `ui/home/HomeViewModel.kt`**

```kotlin
package com.example.adaptiveathlete.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adaptiveathlete.data.db.dao.WorkoutTemplateDao
import com.example.adaptiveathlete.data.db.entity.WorkoutTemplateEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    workoutTemplateDao: WorkoutTemplateDao
) : ViewModel() {
    
    val templates: StateFlow<List<WorkoutTemplateEntity>> = 
        workoutTemplateDao.getAllTemplates()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )
}
```

---

### Step 2: Create Workout Repository

**File: `data/repository/WorkoutRepository.kt`**

```kotlin
package com.example.adaptiveathlete.data.repository

import com.example.adaptiveathlete.data.db.dao.*
import com.example.adaptiveathlete.data.db.entity.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkoutRepository @Inject constructor(
    private val workoutSessionDao: WorkoutSessionDao,
    private val setEntryDao: SetEntryDao,
    private val templateExerciseDao: TemplateExerciseDao,
    private val setPlanDao: SetPlanDao
) {
    
    /**
     * Start a new workout session from a template.
     * Creates session and prefilled set entries.
     */
    suspend fun startWorkout(templateId: Long): Long {
        // Create session
        val sessionId = workoutSessionDao.insertSession(
            WorkoutSessionEntity(
                templateId = templateId,
                startedAt = System.currentTimeMillis()
            )
        )
        
        // Get all exercises in template
        val templateExercises = templateExerciseDao.getExercisesForTemplateSync(templateId)
        
        // For each exercise, create set entries from set plans
        templateExercises.forEach { templateExercise ->
            val setPlans = setPlanDao.getSetPlansForTemplateExerciseSync(templateExercise.id)
            
            val setEntries = setPlans.map { setPlan ->
                SetEntryEntity(
                    sessionId = sessionId,
                    setPlanId = setPlan.id,
                    // Prefill with last completed values (TODO: fetch from history)
                    actualReps = null,
                    actualWeight = setPlan.targetWeight,
                    isDone = false
                )
            }
            
            setEntryDao.insertEntries(setEntries)
        }
        
        return sessionId
    }
    
    /**
     * Get active workout session.
     */
    fun getActiveSession(): Flow<WorkoutSessionEntity?> {
        return workoutSessionDao.getActiveSessionFlow()
    }
    
    /**
     * Get all set entries for a session.
     */
    fun getSetEntriesForSession(sessionId: Long): Flow<List<SetEntryEntity>> {
        return setEntryDao.getEntriesForSession(sessionId)
    }
    
    /**
     * Update a set entry (when user logs reps/weight).
     */
    suspend fun updateSetEntry(setEntry: SetEntryEntity) {
        setEntryDao.updateEntry(setEntry)
    }
    
    /**
     * Finish workout session.
     */
    suspend fun finishWorkout(sessionId: Long, rpe: Int?, notes: String?) {
        val session = workoutSessionDao.getSessionById(sessionId) ?: return
        
        workoutSessionDao.updateSession(
            session.copy(
                endedAt = System.currentTimeMillis(),
                rpe = rpe,
                notes = notes
            )
        )
        
        // TODO: Apply progression to next workout's set plans
    }
}
```

---

### Step 3: Navigation Setup

**File: `ui/navigation/NavGraph.kt`**

```kotlin
package com.example.adaptiveathlete.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.adaptiveathlete.ui.home.HomeScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Workout : Screen("workout/{sessionId}") {
        fun createRoute(sessionId: Long) = "workout/$sessionId"
    }
}

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onStartWorkout = { templateId ->
                    // TODO: Start workout, get session ID, navigate
                    // navController.navigate(Screen.Workout.createRoute(sessionId))
                }
            )
        }
        
        composable(Screen.Workout.route) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getString("sessionId")?.toLongOrNull() ?: 0
            // TODO: WorkoutTableScreen(sessionId)
        }
    }
}
```

**Update `MainActivity.kt`:**

```kotlin
setContent {
    AdaptiveAthleteTheme {
        AppNavGraph()
    }
}
```

---

### Step 4: Build Workout Table Screen

**File: `ui/workout/WorkoutTableScreen.kt`**

This is the main screen - I can provide full implementation when you're ready.

**Key components:**
- Sticky header (timer, workout name, finish button)
- Scrollable list of ExerciseCards
- Each ExerciseCard has a sets table
- Tap cell → NumericKeypad bottom sheet
- Mark done → autosave + rest timer

---

## 🧩 Component Breakdown

### What You'll Build:

1. **HomeScreen** ✅ (code above)
   - Lists workout templates
   - Tap to start

2. **WorkoutTableScreen** (next)
   - Main workout UI
   - Inline set logging
   - Rest timer
   - Finish button

3. **ExerciseCard** (component)
   - Exercise name + cues
   - Sets table

4. **SetRow** (component)
   - Set number
   - Target (8 reps @ 100 lb)
   - Actual (editable)
   - Done checkbox

5. **NumericKeypad** (component)
   - Bottom sheet
   - Number buttons
   - Done/Cancel

6. **Timer Components**
   - Session elapsed timer
   - Rest countdown timer

---

## 📦 What's Already Done

✅ Database entities  
✅ DAOs with all queries  
✅ Progression engine + tests  
✅ Seed data (7 templates, 21 exercises)  
✅ Hilt setup  
✅ Type converters  

**You just need to build the UI layer.**

---

## 🎯 Immediate Next Steps

1. **Sync Gradle** (most important!)
2. **Run tests** to verify engine works
3. **Create `HomeScreen.kt` and `HomeViewModel.kt`** (copy code above)
4. **Create `WorkoutRepository.kt`** (copy code above)
5. **Update `MainActivity.kt`** to use `AppNavGraph`
6. **Run app** → Should see list of 7 workout templates

Once you see the template list, you're 30% done with MVP!

---

## 🆘 Troubleshooting

### Gradle Sync Fails
- Check internet connection
- Update Android Studio to latest
- File → Invalidate Caches → Restart

### Hilt Errors
- Make sure `@HiltAndroidApp` is on `Application` class
- Make sure `android:name=".AdaptiveAthleteApp"` is in manifest
- Rebuild project

### Room Errors
- Run `./gradlew kaptDebug` to regenerate code
- Or Build → Rebuild Project

---

## 💬 When You're Ready

Reply with:
- "Synced successfully" → I'll provide WorkoutTableScreen code
- "Having sync issues" → I'll help troubleshoot
- "Want to modify design" → I'll adjust

Or just say **"Build the workout screen"** and I'll provide complete code for the main UI. 🚀

