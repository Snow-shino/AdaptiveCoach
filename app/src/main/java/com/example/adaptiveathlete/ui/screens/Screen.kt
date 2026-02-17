package com.example.adaptiveathlete.ui.screens

sealed class Screen {
    object Homepage : Screen()
    object BenchmarkMenu : Screen()
    object LiftingBenchmark : Screen()
    object ClimbingBenchmark : Screen()
    object RunningBenchmark : Screen()
    object BenchmarkHistory : Screen()
    object Workout : Screen()
}

