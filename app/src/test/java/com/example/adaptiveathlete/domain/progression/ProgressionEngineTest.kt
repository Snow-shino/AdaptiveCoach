package com.example.adaptiveathlete.domain.progression

import com.example.adaptiveathlete.data.model.ExerciseType
import org.junit.Assert.*
import org.junit.Test

class ProgressionEngineTest {

    @Test
    fun `calculateNextWeight increases by 5 when exceeded by 2+ reps`() {
        val result = ProgressionEngine.calculateNextWeight(
            targetReps = 8,
            actualReps = 10,
            currentWeight = 100f
        )
        assertEquals(105f, result, 0.01f)
    }

    @Test
    fun `calculateNextWeight increases by 2_5 when hit exactly`() {
        val result = ProgressionEngine.calculateNextWeight(
            targetReps = 8,
            actualReps = 8,
            currentWeight = 100f
        )
        assertEquals(102.5f, result, 0.01f)
    }

    @Test
    fun `calculateNextWeight stays same when close`() {
        val result = ProgressionEngine.calculateNextWeight(
            targetReps = 8,
            actualReps = 7,
            currentWeight = 100f
        )
        assertEquals(100f, result, 0.01f)
    }

    @Test
    fun `calculateNextWeight decreases by 5 when failed by 2+ reps`() {
        val result = ProgressionEngine.calculateNextWeight(
            targetReps = 8,
            actualReps = 5,
            currentWeight = 100f
        )
        assertEquals(95f, result, 0.01f)
    }

    @Test
    fun `calculateNextWeight never goes below zero`() {
        val result = ProgressionEngine.calculateNextWeight(
            targetReps = 8,
            actualReps = 3,
            currentWeight = 2.5f
        )
        assertEquals(0f, result, 0.01f)
    }

    @Test
    fun `calculateNextWeight stays same when pain is high`() {
        val result = ProgressionEngine.calculateNextWeight(
            targetReps = 8,
            actualReps = 10,
            currentWeight = 100f,
            painLevel = 5
        )
        assertEquals(100f, result, 0.01f)
    }

    @Test
    fun `calculateNextReps adds 2 when exceeded by 3+`() {
        val result = ProgressionEngine.calculateNextReps(
            targetReps = 10,
            actualReps = 14
        )
        assertEquals(12, result)
    }

    @Test
    fun `calculateNextReps adds 1 when exceeded by 1-2`() {
        val result = ProgressionEngine.calculateNextReps(
            targetReps = 10,
            actualReps = 11
        )
        assertEquals(11, result)
    }

    @Test
    fun `calculateNextReps stays same when not exceeded`() {
        val result = ProgressionEngine.calculateNextReps(
            targetReps = 10,
            actualReps = 10
        )
        assertEquals(10, result)
    }

    @Test
    fun `calculateNextReps stays same when failed`() {
        val result = ProgressionEngine.calculateNextReps(
            targetReps = 10,
            actualReps = 7
        )
        assertEquals(10, result)
    }

    @Test
    fun `calculateNextSeconds adds 10 when exceeded by 10+`() {
        val result = ProgressionEngine.calculateNextSeconds(
            targetSeconds = 30,
            actualSeconds = 45
        )
        assertEquals(40, result)
    }

    @Test
    fun `calculateNextSeconds adds 5 when exceeded by 5-9`() {
        val result = ProgressionEngine.calculateNextSeconds(
            targetSeconds = 30,
            actualSeconds = 37
        )
        assertEquals(35, result)
    }

    @Test
    fun `calculateNextSeconds adds 5 when hit target`() {
        val result = ProgressionEngine.calculateNextSeconds(
            targetSeconds = 30,
            actualSeconds = 30
        )
        assertEquals(35, result)
    }

    @Test
    fun `calculateNextSeconds stays same when failed significantly`() {
        val result = ProgressionEngine.calculateNextSeconds(
            targetSeconds = 30,
            actualSeconds = 20
        )
        assertEquals(30, result)
    }

    @Test
    fun `shouldAddWeightToBodyweight suggests 5lb when reps hit threshold`() {
        val result = ProgressionEngine.shouldAddWeightToBodyweight(
            actualReps = 12,
            threshold = 12
        )
        assertEquals(5f, result, 0.01f)
    }

    @Test
    fun `shouldAddWeightToBodyweight returns 0 when below threshold`() {
        val result = ProgressionEngine.shouldAddWeightToBodyweight(
            actualReps = 10,
            threshold = 12
        )
        assertEquals(0f, result, 0.01f)
    }

    @Test
    fun `calculateProgressionFromSets increases when all sets exceeded`() {
        val sets = listOf(
            8 to 10,
            8 to 9,
            8 to 9
        )
        val result = ProgressionEngine.calculateProgressionFromSets(
            setResults = sets,
            currentWeight = 100f
        )
        assertEquals(105f, result, 0.01f)
    }

    @Test
    fun `calculateProgressionFromSets increases moderately when all sets hit`() {
        val sets = listOf(
            8 to 8,
            8 to 8,
            8 to 8
        )
        val result = ProgressionEngine.calculateProgressionFromSets(
            setResults = sets,
            currentWeight = 100f
        )
        assertEquals(102.5f, result, 0.01f)
    }

    @Test
    fun `calculateProgressionFromSets stays same when 80 percent hit`() {
        val sets = listOf(
            8 to 8,
            8 to 8,
            8 to 8,
            8 to 7,
            8 to 7
        )
        val result = ProgressionEngine.calculateProgressionFromSets(
            setResults = sets,
            currentWeight = 100f
        )
        assertEquals(100f, result, 0.01f)
    }

    @Test
    fun `calculateProgressionFromSets decreases when many sets failed`() {
        val sets = listOf(
            8 to 5,
            8 to 5,
            8 to 4
        )
        val result = ProgressionEngine.calculateProgressionFromSets(
            setResults = sets,
            currentWeight = 100f
        )
        assertEquals(95f, result, 0.01f)
    }

    @Test
    fun `calculateDeload reduces by 15 percent after 3 failures`() {
        val result = ProgressionEngine.calculateDeload(
            consecutiveFailures = 3,
            currentWeight = 100f
        )
        assertEquals(85f, result, 0.01f)
    }

    @Test
    fun `calculateDeload reduces by 10 percent after 2 failures`() {
        val result = ProgressionEngine.calculateDeload(
            consecutiveFailures = 2,
            currentWeight = 100f
        )
        assertEquals(90f, result, 0.01f)
    }

    @Test
    fun `calculateDeload keeps same after 1 failure`() {
        val result = ProgressionEngine.calculateDeload(
            consecutiveFailures = 1,
            currentWeight = 100f
        )
        assertEquals(100f, result, 0.01f)
    }

    @Test
    fun `isStalling returns true when last 2 sessions failed`() {
        val performances = listOf(true, true, false, false)
        val result = ProgressionEngine.isStalling(performances)
        assertTrue(result)
    }

    @Test
    fun `isStalling returns false when last session succeeded`() {
        val performances = listOf(true, false, false, true)
        val result = ProgressionEngine.isStalling(performances)
        assertFalse(result)
    }

    @Test
    fun `isStalling returns false with insufficient data`() {
        val performances = listOf(false)
        val result = ProgressionEngine.isStalling(performances)
        assertFalse(result)
    }

    @Test
    fun `calculateRestTime returns 180s for low rep weighted`() {
        val result = ProgressionEngine.calculateRestTime(
            exerciseType = ExerciseType.WEIGHTED,
            reps = 5
        )
        assertEquals(180, result)
    }

    @Test
    fun `calculateRestTime returns 90s for high rep weighted`() {
        val result = ProgressionEngine.calculateRestTime(
            exerciseType = ExerciseType.WEIGHTED,
            reps = 12
        )
        assertEquals(90, result)
    }

    @Test
    fun `calculateRestTime returns 180s for hard climbs`() {
        val result = ProgressionEngine.calculateRestTime(
            exerciseType = ExerciseType.CLIMBING,
            reps = 1,
            rpe = 9
        )
        assertEquals(180, result)
    }
}

