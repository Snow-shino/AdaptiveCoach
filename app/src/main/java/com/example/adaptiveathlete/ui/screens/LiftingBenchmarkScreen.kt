package com.example.adaptiveathlete.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LiftingBenchmarkScreen(
    onComplete: (BenchmarkData) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    var bodyweight by remember { mutableStateOf("") }
    var pullUpMaxReps by remember { mutableStateOf("") }
    var pullUp5RMWeight by remember { mutableStateOf("") }
    var pushUpMaxReps by remember { mutableStateOf("") }
    var leg5RMWeight by remember { mutableStateOf("") }
    var deadHangTime by remember { mutableStateOf("") }
    var hollowHoldTime by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A1A1A),
                        Color(0xFF2D1810),
                        Color(0xFF1A1A1A)
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
                text = "🟢",
                fontSize = 48.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "LIFTING BENCHMARK",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Warm up properly. Test strength, not ego.\nLeave 5% in the tank.",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Bodyweight
            BenchmarkSection(
                title = "Bodyweight",
                emoji = "⚖️"
            ) {
                BenchmarkInputField(
                    label = "Bodyweight (lbs)",
                    value = bodyweight,
                    onValueChange = { bodyweight = it },
                    suffix = "lbs"
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Pull Strength
            BenchmarkSection(
                title = "1. Pull Strength (Upper Body)",
                emoji = "💪"
            ) {
                BenchmarkInputField(
                    label = "Pull-ups - Max strict reps",
                    value = pullUpMaxReps,
                    onValueChange = { pullUpMaxReps = it },
                    suffix = "reps"
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Rest 3 min, then:",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                BenchmarkInputField(
                    label = "5RM Weighted Pull-up",
                    value = pullUp5RMWeight,
                    onValueChange = { pullUp5RMWeight = it },
                    suffix = "lbs added"
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Push Strength
            BenchmarkSection(
                title = "2. Push Strength",
                emoji = "🔥"
            ) {
                BenchmarkInputField(
                    label = "Push-ups - Max strict reps",
                    value = pushUpMaxReps,
                    onValueChange = { pushUpMaxReps = it },
                    suffix = "reps"
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Lower Body
            BenchmarkSection(
                title = "3. Lower Body",
                emoji = "🦵"
            ) {
                BenchmarkInputField(
                    label = "5RM (Squat or Split Squat)",
                    value = leg5RMWeight,
                    onValueChange = { leg5RMWeight = it },
                    suffix = "lbs per leg"
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Core
            BenchmarkSection(
                title = "4. Core",
                emoji = "🎯"
            ) {
                BenchmarkInputField(
                    label = "Hollow body hold - Max time",
                    value = hollowHoldTime,
                    onValueChange = { hollowHoldTime = it },
                    suffix = "seconds"
                )
                Spacer(modifier = Modifier.height(12.dp))
                BenchmarkInputField(
                    label = "Dead hang - Max time",
                    value = deadHangTime,
                    onValueChange = { deadHangTime = it },
                    suffix = "seconds"
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f).height(56.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    ),
                    border = BorderStroke(1.dp, Color(0xFF5A5A5A))
                ) {
                    Text("Cancel")
                }

                Button(
                    onClick = {
                        onComplete(
                            BenchmarkData.Lifting(
                                bodyweight = bodyweight.toFloatOrNull() ?: 0f,
                                pullUpMaxReps = pullUpMaxReps.toIntOrNull() ?: 0,
                                pullUp5RMWeight = pullUp5RMWeight.toFloatOrNull() ?: 0f,
                                pushUpMaxReps = pushUpMaxReps.toIntOrNull() ?: 0,
                                leg5RMWeight = leg5RMWeight.toFloatOrNull() ?: 0f,
                                deadHangTime = deadHangTime.toIntOrNull() ?: 0,
                                hollowHoldTime = hollowHoldTime.toIntOrNull() ?: 0
                            )
                        )
                    },
                    modifier = Modifier.weight(1f).height(56.dp),
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
                                        Color(0xFFFF8C42),
                                        Color(0xFFFF6B35)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Complete Test", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun BenchmarkSection(
    title: String,
    emoji: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A2A2A).copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(
                    text = emoji,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            content()
        }
    }
}

@Composable
fun BenchmarkInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    suffix: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFFFF8C42),
                    unfocusedBorderColor = Color(0xFF5A5A5A),
                    focusedContainerColor = Color(0xFF1A1A1A),
                    unfocusedContainerColor = Color(0xFF1A1A1A),
                    cursorColor = Color(0xFFFF8C42)
                ),
                singleLine = true
            )
            Text(
                text = suffix,
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

sealed class BenchmarkData {
    data class Lifting(
        val bodyweight: Float,
        val pullUpMaxReps: Int,
        val pullUp5RMWeight: Float,
        val pushUpMaxReps: Int,
        val leg5RMWeight: Float,
        val deadHangTime: Int,
        val hollowHoldTime: Int
    ) : BenchmarkData()

    data class Climbing(
        val maxVGrade: String,
        val attemptedVGrade: String,
        val maxHangWeight: Float?,
        val maxHangTime: Int?,
        val repeatersCompleted: Boolean
    ) : BenchmarkData()

    data class Running(
        val mileTime: Int?,
        val cooperDistance: Float?
    ) : BenchmarkData()
}

