package com.example.adaptiveathlete.domain.progression

import com.example.adaptiveathlete.data.model.ExerciseType

/**
 * Core progressive overload engine.
 * Pure Kotlin - no Android dependencies.
 *
 * This determines the next workout's targets based on current performance.
 */
object ProgressionEngine {

    /**
     * Calculate next target weight for weighted exercises.
     *
     * Rules:
     * - If user exceeded targets by 2+ reps: increase weight by 5 lb
     * - If user hit exact targets: increase weight by 2.5 lb
     * - If user was close (within 1 rep): keep same weight
     * - If user failed by 2+ reps: decrease weight by 5 lb
     *
     * @param targetReps The prescribed reps
     * @param actualReps The performed reps
     * @param currentWeight Current weight in pounds
     * @param painLevel Pain reported (0-10), if >3 don't increase
     * @return Next workout's target weight
     */
    fun calculateNextWeight(
        targetReps: Int,
        actualReps: Int,
        currentWeight: Float,
        painLevel: Int = 0
    ): Float {
        // Safety: never progress if in pain
        if (painLevel > 3) {
            return currentWeight
        }

        val delta = actualReps - targetReps

        return when {
            delta >= 2 -> currentWeight + 5f      // Exceeded significantly
            delta == 1 -> currentWeight + 2.5f    // Beat target by 1
            delta == 0 -> currentWeight + 2.5f    // Hit target exactly
            delta == -1 -> currentWeight          // Close enough, repeat
            else -> maxOf(currentWeight - 5f, 0f) // Failed, deload
        }
    }

    /**
     * Calculate next target reps for bodyweight exercises.
     *
     * Rules:
     * - If exceeded by 3+: add 2 reps
     * - If exceeded by 1-2: add 1 rep
     * - If hit exactly or close: keep same
     * - If failed: keep same
     *
     * @param targetReps Current target
     * @param actualReps Actual performance
     * @param painLevel Pain level
     * @return Next target reps
     */
    fun calculateNextReps(
        targetReps: Int,
        actualReps: Int,
        painLevel: Int = 0
    ): Int {
        if (painLevel > 3) {
            return targetReps
        }

        val delta = actualReps - targetReps

        return when {
            delta >= 3 -> targetReps + 2
            delta in 1..2 -> targetReps + 1
            else -> targetReps  // Keep same if didn't exceed
        }
    }

    /**
     * Calculate next target time for timed exercises (planks, hangs, etc.)
     *
     * Rules:
     * - If exceeded by 10+ seconds: add 10s
     * - If exceeded by 5-9 seconds: add 5s
     * - If hit target ±4 seconds: add 5s
     * - If failed by more: keep same
     */
    fun calculateNextSeconds(
        targetSeconds: Int,
        actualSeconds: Int,
        painLevel: Int = 0
    ): Int {
        if (painLevel > 3) {
            return targetSeconds
        }

        val delta = actualSeconds - targetSeconds

        return when {
            delta >= 10 -> targetSeconds + 10
            delta >= 5 -> targetSeconds + 5
            delta >= -4 -> targetSeconds + 5  // Close enough counts as success
            else -> targetSeconds  // Failed, repeat
        }
    }

    /**
     * Determine if user should add weight to bodyweight exercise.
     *
     * Once bodyweight reps get too high, it's time to add weight.
     *
     * @param actualReps Current performance
     * @param threshold When to add weight (default: 12 reps)
     * @return Suggested weight to add, or 0 if not ready
     */
    fun shouldAddWeightToBodyweight(actualReps: Int, threshold: Int = 12): Float {
        return if (actualReps >= threshold) {
            5f  // Start with 5 lb
        } else {
            0f
        }
    }

    /**
     * Calculate progression across an entire set sequence.
     *
     * This looks at all sets of an exercise and determines if progression is warranted.
     * More conservative than single-set logic.
     *
     * @param setResults List of (target, actual) pairs for each set
     * @param currentWeight Current working weight
     * @return Recommended next weight
     */
    fun calculateProgressionFromSets(
        setResults: List<Pair<Int, Int>>,  // (targetReps, actualReps)
        currentWeight: Float,
        painLevel: Int = 0
    ): Float {
        if (painLevel > 3 || setResults.isEmpty()) {
            return currentWeight
        }

        val totalTarget = setResults.sumOf { it.first }
        val totalActual = setResults.sumOf { it.second }
        val setsCompleted = setResults.count { it.second >= it.first }
        val totalSets = setResults.size

        return when {
            // All sets exceeded targets
            totalActual >= totalTarget + (totalSets * 2) -> currentWeight + 5f

            // Hit all sets exactly or beat most
            setsCompleted >= totalSets -> currentWeight + 2.5f

            // Hit at least 80% of sets
            setsCompleted >= (totalSets * 0.8).toInt() -> currentWeight

            // Failed more than 20% of sets
            else -> maxOf(currentWeight - 5f, 0f)
        }
    }

    /**
     * Deload calculation.
     *
     * If user has stalled for multiple weeks, recommend a deload.
     *
     * @param consecutiveFailures Number of sessions where progression didn't occur
     * @param currentWeight Current working weight
     * @return Deload weight (typically 10-15% reduction)
     */
    fun calculateDeload(consecutiveFailures: Int, currentWeight: Float): Float {
        return when {
            consecutiveFailures >= 3 -> currentWeight * 0.85f  // 15% reduction
            consecutiveFailures >= 2 -> currentWeight * 0.90f  // 10% reduction
            else -> currentWeight
        }
    }

    /**
     * Determine if exercise performance is stalling.
     *
     * @param recentPerformances Last N session performances (true = hit targets, false = missed)
     * @return True if stalling
     */
    fun isStalling(recentPerformances: List<Boolean>): Boolean {
        if (recentPerformances.size < 2) return false

        // If failed last 2 sessions
        return recentPerformances.takeLast(2).all { !it }
    }

    /**
     * Calculate recommended rest time based on exercise type and intensity.
     */
    fun calculateRestTime(exerciseType: ExerciseType, reps: Int, rpe: Int = 7): Int {
        return when (exerciseType) {
            ExerciseType.WEIGHTED -> when {
                reps <= 5 -> 180  // Strength work needs more rest
                reps <= 8 -> 120
                else -> 90
            }
            ExerciseType.BODYWEIGHT -> 90
            ExerciseType.TIMED -> 120
            ExerciseType.CARDIO -> 60
            ExerciseType.CLIMBING -> when {
                rpe >= 8 -> 180  // Hard climbs need more rest
                else -> 120
            }
        }
    }
}

