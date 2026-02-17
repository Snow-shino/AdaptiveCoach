package com.example.adaptiveathlete.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.adaptiveathlete.data.db.entity.BenchmarkResultEntity
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BenchmarkHistoryScreen(
    benchmarks: List<BenchmarkResultEntity>,
    onBack: () -> Unit,
    onDelete: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormatter = remember { DateTimeFormatter.ofPattern("MMM dd, yyyy • hh:mm a") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Benchmark History",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A1A1A),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFF0A0E27)
    ) { paddingValues ->
        if (benchmarks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "📊",
                        fontSize = 64.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "No benchmarks yet",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        "Complete your first test to see results here",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(benchmarks, key = { it.id }) { benchmark ->
                    BenchmarkCard(
                        benchmark = benchmark,
                        dateFormatter = dateFormatter,
                        onDelete = { onDelete(benchmark.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun BenchmarkCard(
    benchmark: BenchmarkResultEntity,
    dateFormatter: DateTimeFormatter,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    val (emoji, gradientColors) = when (benchmark.benchmarkType) {
        "LIFTING" -> "🟢" to listOf(Color(0xFF10B981), Color(0xFF059669))
        "CLIMBING" -> "🔵" to listOf(Color(0xFF3B82F6), Color(0xFF2563EB))
        "RUNNING" -> "🔴" to listOf(Color(0xFFEF4444), Color(0xFFDC2626))
        else -> "📊" to listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1F3A)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(emoji, fontSize = 32.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "${benchmark.benchmarkType} BENCHMARK",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = benchmark.completedAt.format(dateFormatter),
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                }

                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Show relevant metrics
            when (benchmark.benchmarkType) {
                "LIFTING" -> LiftingMetrics(benchmark)
                "CLIMBING" -> ClimbingMetrics(benchmark)
                "RUNNING" -> RunningMetrics(benchmark)
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Benchmark?") },
            text = { Text("This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun LiftingMetrics(benchmark: BenchmarkResultEntity) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        MetricRow("Bodyweight", "${benchmark.bodyweight} lbs")
        MetricRow("Pull-ups (max)", "${benchmark.pullUpMaxReps} reps")
        MetricRow("Pull-up 5RM", "+${benchmark.pullUp5RMWeight} lbs")
        MetricRow("Push-ups (max)", "${benchmark.pushUpMaxReps} reps")
        MetricRow("Leg 5RM", "${benchmark.leg5RMWeight} lbs")
        MetricRow("Dead hang", "${benchmark.deadHangTime}s")
        MetricRow("Hollow hold", "${benchmark.hollowHoldTime}s")
    }
}

@Composable
fun ClimbingMetrics(benchmark: BenchmarkResultEntity) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        MetricRow("Max V-Grade Sent", benchmark.maxVGrade ?: "N/A")
        MetricRow("Attempted Grade", benchmark.attemptedVGrade ?: "N/A")
        benchmark.maxHangWeight?.let { MetricRow("Max Hang Weight", "+${it} lbs") }
        benchmark.maxHangTime?.let { MetricRow("Max Hang Time", "${it}s") }
        MetricRow("Repeaters", if (benchmark.repeatersCompleted == true) "✅ Completed" else "❌ Failed")
    }
}

@Composable
fun RunningMetrics(benchmark: BenchmarkResultEntity) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        benchmark.mileTime?.let {
            val minutes = it / 60
            val seconds = it % 60
            MetricRow("Mile Time", "${minutes}:${seconds.toString().padStart(2, '0')}")
        }
        benchmark.cooperDistance?.let {
            MetricRow("Cooper Test", "${it}m")
        }
    }
}

@Composable
fun MetricRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
    }}

