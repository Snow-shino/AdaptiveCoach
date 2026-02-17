package com.example.adaptiveathlete.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adaptiveathlete.data.db.dao.ScheduledWorkoutDao
import com.example.adaptiveathlete.data.db.dao.WorkoutTemplateDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*
import javax.inject.Inject

data class WorkoutItem(
    val id: Long,
    val name: String,
    val scheduledWorkoutId: Long
)

@HiltViewModel
class HomepageViewModel @Inject constructor(
    private val scheduledWorkoutDao: ScheduledWorkoutDao,
    private val workoutTemplateDao: WorkoutTemplateDao
) : ViewModel() {

    private val _todaysWorkouts = MutableStateFlow<List<WorkoutItem>>(emptyList())
    val todaysWorkouts: StateFlow<List<WorkoutItem>> = _todaysWorkouts.asStateFlow()

    fun loadWorkoutsForDate(date: LocalDate) {
        viewModelScope.launch {
            Log.d("HomepageViewModel", "📅 Loading workouts for date: $date")
            scheduledWorkoutDao.getByDate(date).collect { scheduledWorkouts ->
                Log.d("HomepageViewModel", "📋 Found ${scheduledWorkouts.size} scheduled workouts")
                val workoutItems = scheduledWorkouts.mapNotNull { scheduled ->
                    Log.d("HomepageViewModel", "  - Scheduled workout ID: ${scheduled.id}, templateId: ${scheduled.templateId}")
                    val template = workoutTemplateDao.getTemplateById(scheduled.templateId)
                    if (template == null) {
                        Log.w("HomepageViewModel", "  ⚠️ Template not found for ID: ${scheduled.templateId}")
                    } else {
                        Log.d("HomepageViewModel", "  ✅ Template found: ${template.name}")
                    }
                    template?.let {
                        WorkoutItem(
                            id = template.id,
                            name = template.name,
                            scheduledWorkoutId = scheduled.id
                        )
                    }
                }
                Log.d("HomepageViewModel", "✅ Loaded ${workoutItems.size} workout items")
                workoutItems.forEach { Log.d("HomepageViewModel", "  - ${it.name}") }
                _todaysWorkouts.value = workoutItems
            }
        }
    }
}

@Composable
fun HomepageScreen(
    onBenchmarkClick: () -> Unit,
    onWorkoutClick: (String) -> Unit,
    onDayClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomepageViewModel = hiltViewModel()
) {
    val today = remember { LocalDate.now() }
    val currentWeek = remember { getCurrentWeek(today) }
    var selectedDate by remember { mutableStateOf(today) }
    val todaysWorkouts by viewModel.todaysWorkouts.collectAsState()

    LaunchedEffect(selectedDate) {
        Log.d("HomepageScreen", "🔄 Selected date changed to: $selectedDate")
        viewModel.loadWorkoutsForDate(selectedDate)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0A0E27),
                        Color(0xFF1A1F3A),
                        Color(0xFF0A0E27)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            // Header
            Text(
                text = "🏋️",
                fontSize = 48.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "HOMEPAGE",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Text(
                text = "Your training at a glance",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Weekly Calendar
            Text(
                text = "THIS WEEK",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.7f),
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            WeeklyCalendar(
                week = currentWeek,
                selectedDate = selectedDate,
                onDateClick = { date ->
                    selectedDate = date
                    onDayClick(date)
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Workouts Today Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1A1F3A)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "WORKOUTS TODAY",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "${todaysWorkouts.size}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6366F1)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (todaysWorkouts.isEmpty()) {
                        Text(
                            text = "No workouts scheduled for this day",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        todaysWorkouts.forEach { workout ->
                            WorkoutButton(
                                workoutName = workout.name,
                                onClick = { onWorkoutClick(workout.name) }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Benchmarks Button
            Button(
                onClick = onBenchmarkClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF6366F1),
                                    Color(0xFF8B5CF6)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📊",
                            fontSize = 24.sp,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            "VIEW BENCHMARKS",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Quick Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    icon = "🔥",
                    value = "7",
                    label = "Day Streak",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    icon = "💪",
                    value = "${todaysWorkouts.size}",
                    label = "Today",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun WeeklyCalendar(
    week: List<LocalDate>,
    selectedDate: LocalDate,
    onDateClick: (LocalDate) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(week) { date ->
            DayCard(
                date = date,
                isSelected = date == selectedDate,
                isToday = date == LocalDate.now(),
                onClick = { onDateClick(date) }
            )
        }
    }
}

@Composable
fun DayCard(
    date: LocalDate,
    isSelected: Boolean,
    isToday: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isSelected -> Color(0xFF6366F1)
        else -> Color(0xFF1A1F3A)
    }

    Card(
        modifier = Modifier
            .width(70.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 16.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.7f),
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = date.dayOfMonth.toString(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            if (isToday) {
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF10B981))
                )
            }
        }
    }
}

@Composable
fun WorkoutButton(
    workoutName: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF2A2F4A)
        ),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = workoutName,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Text(
                text = "→",
                fontSize = 20.sp,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun StatCard(
    icon: String,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.05f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = icon,
                fontSize = 28.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

fun getCurrentWeek(date: LocalDate): List<LocalDate> {
    val startOfWeek = date.minusDays(date.dayOfWeek.value.toLong() - 1)
    return (0..6).map { startOfWeek.plusDays(it.toLong()) }
}

