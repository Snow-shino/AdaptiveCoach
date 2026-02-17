package com.example.adaptiveathlete.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// Data models matching ForgeFit spec
data class WorkoutExercise(
    val id: Long = 0,
    val name: String,
    val category: String = "STRENGTH",
    val sets: MutableList<SetInfo>,
    val lastBenchmarkResult: BenchmarkInfo? = null,
    val orderIndex: Int = 0
)

data class SetInfo(
    var setNumber: Int,
    var targetReps: Int?,
    var targetWeight: Float?,
    var targetTime: Int?, // seconds
    var targetRPE: Int? = null,
    var actualReps: Int? = null,
    var actualWeight: Float? = null,
    var actualTime: Int? = null,
    var actualRPE: Int? = null,
    var completed: Boolean = false
)

data class BenchmarkInfo(
    val value: String,
    val date: LocalDateTime
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UniversalWorkoutScreen(
    workoutName: String,
    exercises: List<WorkoutExercise>,
    onBack: () -> Unit,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var workoutExercises by remember { mutableStateOf(exercises.toMutableList()) }
    val dateFormatter = remember { DateTimeFormatter.ofPattern("MM-dd-yyyy") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            workoutName,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 18.sp
                        )
                        Text(
                            "Fast Table Entry",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A1A1A)
                )
            )
        },
        containerColor = Color(0xFF0A0E27)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp)
            ) {
                items(workoutExercises, key = { it.id }) { exercise ->
                    FastTableCard(
                        exercise = exercise,
                        dateFormatter = dateFormatter,
                        onAddSet = {
                            // Auto-fill from last set
                            val lastSet = exercise.sets.lastOrNull()
                            val newSetNumber = exercise.sets.size + 1
                            exercise.sets.add(
                                SetInfo(
                                    setNumber = newSetNumber,
                                    targetReps = lastSet?.targetReps,
                                    targetWeight = lastSet?.targetWeight,
                                    targetTime = lastSet?.targetTime,
                                    targetRPE = lastSet?.targetRPE,
                                    actualReps = lastSet?.actualReps,
                                    actualWeight = lastSet?.actualWeight
                                )
                            )
                        },
                        onDeleteSet = { setIndex ->
                            if (exercise.sets.size > 1) {
                                exercise.sets.removeAt(setIndex)
                                // Renumber sets
                                exercise.sets.forEachIndexed { idx, set ->
                                    set.setNumber = idx + 1
                                }
                            }
                        },
                        onDuplicateSet = { setIndex ->
                            val setToDup = exercise.sets[setIndex]
                            exercise.sets.add(
                                setIndex + 1,
                                setToDup.copy(
                                    setNumber = setIndex + 2,
                                    completed = false
                                )
                            )
                            // Renumber sets after
                            exercise.sets.forEachIndexed { idx, set ->
                                if (idx > setIndex) set.setNumber = idx + 1
                            }
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(60.dp))
                }
            }

            // Finish Workout Button (sticky bottom)
            Button(
                onClick = onComplete,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 16.dp)
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFFFF8C42), Color(0xFFFF6B35))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "FINISH WORKOUT",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FastTableCard(
    exercise: WorkoutExercise,
    dateFormatter: DateTimeFormatter,
    onAddSet: () -> Unit,
    onDeleteSet: (Int) -> Unit,
    onDuplicateSet: (Int) -> Unit
) {
    var showBenchmarkTooltip by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1F3A)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = exercise.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF8C42),
                    modifier = Modifier.weight(1f)
                )

                if (exercise.lastBenchmarkResult != null) {
                    IconButton(
                        onClick = { showBenchmarkTooltip = !showBenchmarkTooltip },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = "Benchmark",
                            tint = Color(0xFFFF8C42),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = showBenchmarkTooltip,
                        onDismissRequest = { showBenchmarkTooltip = false },
                        modifier = Modifier.background(Color(0xFF2A2F4A))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                "Last Benchmark:",
                                fontSize = 11.sp,
                                color = Color.White.copy(0.7f)
                            )
                            Text(
                                exercise.lastBenchmarkResult.value,
                                fontSize = 14.sp,
                                color = Color(0xFFFF8C42),
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "on ${exercise.lastBenchmarkResult.date.format(dateFormatter)}",
                                fontSize = 10.sp,
                                color = Color.White.copy(0.6f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Table Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TableHeaderCell("SET", 40.dp)
                TableHeaderCell("TARGET", 80.dp)
                TableHeaderCell("ACTUAL", 120.dp)
                TableHeaderCell("RPE", 50.dp)
                TableHeaderCell("✓", 40.dp)
            }

            HorizontalDivider(
                color = Color.White.copy(0.2f),
                modifier = Modifier.padding(vertical = 6.dp)
            )

            // Sets
            exercise.sets.forEachIndexed { index, set ->
                FastTableRow(
                    set = set,
                    onDelete = { onDeleteSet(index) },
                    onDuplicate = { onDuplicateSet(index) }
                )
                if (index < exercise.sets.lastIndex) {
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }

            // Add Set Button
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(
                onClick = onAddSet,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "+ Add Set",
                    color = Color(0xFFFF8C42),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun TableHeaderCell(text: String, width: Dp) {
    Text(
        text = text,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White.copy(0.6f),
        modifier = Modifier.width(width),
        textAlign = if (text == "✓") TextAlign.Center else TextAlign.Start
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FastTableRow(
    set: SetInfo,
    onDelete: () -> Unit,
    onDuplicate: () -> Unit
) {
    var offsetX by remember { mutableStateOf(0f) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        when {
                            offsetX < -100 -> onDelete()
                            offsetX > 100 -> onDuplicate()
                        }
                        offsetX = 0f
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        offsetX += dragAmount
                    }
                )
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (set.completed) Color(0xFF2A4A2F) else Color.Transparent
                )
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Set number
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFF8C42).copy(0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "${set.setNumber}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF8C42)
                )
            }

            // Target
            Text(
                text = buildTargetString(set),
                fontSize = 12.sp,
                color = Color.White.copy(0.7f),
                modifier = Modifier.width(80.dp)
            )

            // Actual (inline editable)
            Row(
                modifier = Modifier.width(120.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (set.targetReps != null || set.targetTime != null) {
                    FastInput(
                        value = (set.actualReps ?: set.actualTime)?.toString() ?: "",
                        onValueChange = {
                            val num = it.toIntOrNull()
                            if (set.targetReps != null) set.actualReps = num
                            else set.actualTime = num
                        },
                        hint = if (set.targetReps != null) "r" else "s",
                        modifier = Modifier.weight(1f)
                    )
                }
                if (set.targetWeight != null) {
                    FastInput(
                        value = set.actualWeight?.toInt()?.toString() ?: "",
                        onValueChange = { set.actualWeight = it.toFloatOrNull() },
                        hint = "lb",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // RPE
            FastInput(
                value = set.actualRPE?.toString() ?: "",
                onValueChange = { set.actualRPE = it.toIntOrNull() },
                hint = "-",
                modifier = Modifier.width(50.dp)
            )

            // Done checkbox
            Checkbox(
                checked = set.completed,
                onCheckedChange = { set.completed = it },
                modifier = Modifier.width(40.dp).size(24.dp),
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF10B981),
                    uncheckedColor = Color.White.copy(0.3f)
                )
            )
        }
    }
}

@Composable
fun FastInput(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    modifier: Modifier = Modifier
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .height(32.dp)
            .background(Color(0xFF1A1A1A), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        textStyle = TextStyle(
            color = Color.White,
            fontSize = 13.sp,
            textAlign = TextAlign.Center
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        cursorBrush = SolidColor(Color(0xFFFF8C42)),
        decorationBox = { innerTextField ->
            Box(contentAlignment = Alignment.Center) {
                if (value.isEmpty()) {
                    Text(
                        hint,
                        fontSize = 11.sp,
                        color = Color.White.copy(0.4f),
                        textAlign = TextAlign.Center
                    )
                }
                innerTextField()
            }
        }
    )
}

fun buildTargetString(set: SetInfo): String {
    return buildList {
        set.targetReps?.let { add("${it}r") }
        set.targetWeight?.let { add("@${it.toInt()}") }
        set.targetTime?.let { add("${it}s") }
        set.targetRPE?.let { add("RPE$it") }
    }.joinToString(" ")
}

