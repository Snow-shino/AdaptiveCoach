package com.example.adaptiveathlete.data.repository

import com.example.adaptiveathlete.data.db.dao.*
import com.example.adaptiveathlete.data.db.entity.*
import com.example.adaptiveathlete.data.model.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SeedDataRepository @Inject constructor(
    private val exerciseDao: ExerciseDao,
    private val workoutTemplateDao: WorkoutTemplateDao,
    private val templateExerciseDao: TemplateExerciseDao,
    private val setPlanDao: SetPlanDao
) {

    /**
     * Initialize database with exercises and workout templates.
     * Call this on first app launch.
     */
    suspend fun seedDatabase() {
        // Create exercises
        val exerciseIds = createExercises()

        // Create workout templates
        createPullDayA(exerciseIds)
        createPushDayA(exerciseIds)
        createPosteriorChainDay(exerciseIds)
        createFullBodyMetabolic(exerciseIds)
        createDailyRitual(exerciseIds)
        createAerobicClimbingDay(exerciseIds)
        createPowerClimbingDay(exerciseIds)
    }

    private suspend fun createExercises(): Map<String, Long> {
        val exercises = listOf(
            // Pull exercises
            ExerciseEntity(
                name = "Weighted Pull-ups",
                type = ExerciseType.WEIGHTED,
                primaryMuscles = listOf(MuscleGroup.BACK, MuscleGroup.BICEPS),
                secondaryMuscles = listOf(MuscleGroup.FOREARMS),
                equipment = EquipmentType.PULL_UP_BAR,
                instructions = "Dead hang start, controlled tempo, chin over bar"
            ),
            ExerciseEntity(
                name = "Scap Pull-ups",
                type = ExerciseType.BODYWEIGHT,
                primaryMuscles = listOf(MuscleGroup.BACK),
                equipment = EquipmentType.PULL_UP_BAR,
                instructions = "Just retract scapula, no arm bend"
            ),
            ExerciseEntity(
                name = "Seal Rows",
                type = ExerciseType.WEIGHTED,
                primaryMuscles = listOf(MuscleGroup.BACK),
                equipment = EquipmentType.BARBELL,
                instructions = "Chest supported, pull to sternum"
            ),
            ExerciseEntity(
                name = "DB Lat Rows",
                type = ExerciseType.WEIGHTED,
                primaryMuscles = listOf(MuscleGroup.BACK),
                equipment = EquipmentType.DUMBBELL,
                instructions = "One arm at a time, full stretch"
            ),
            ExerciseEntity(
                name = "Face Pulls",
                type = ExerciseType.WEIGHTED,
                primaryMuscles = listOf(MuscleGroup.SHOULDERS),
                equipment = EquipmentType.CABLE,
                instructions = "Pull to face, external rotation"
            ),
            ExerciseEntity(
                name = "Hanging Leg Raises",
                type = ExerciseType.BODYWEIGHT,
                primaryMuscles = listOf(MuscleGroup.ABS),
                equipment = EquipmentType.PULL_UP_BAR,
                instructions = "Control descent, no swinging"
            ),
            ExerciseEntity(
                name = "Hollow Body Hold",
                type = ExerciseType.TIMED,
                primaryMuscles = listOf(MuscleGroup.ABS),
                equipment = EquipmentType.BODYWEIGHT,
                instructions = "Lower back flat, arms overhead"
            ),

            // Push exercises
            ExerciseEntity(
                name = "Incline Bench Press",
                type = ExerciseType.WEIGHTED,
                primaryMuscles = listOf(MuscleGroup.CHEST, MuscleGroup.SHOULDERS),
                secondaryMuscles = listOf(MuscleGroup.TRICEPS),
                equipment = EquipmentType.BARBELL,
                instructions = "Control descent, pause on chest"
            ),
            ExerciseEntity(
                name = "Arnold Press",
                type = ExerciseType.WEIGHTED,
                primaryMuscles = listOf(MuscleGroup.SHOULDERS),
                equipment = EquipmentType.DUMBBELL,
                instructions = "Rotate palms during press"
            ),
            ExerciseEntity(
                name = "Underhand Front Raise",
                type = ExerciseType.WEIGHTED,
                primaryMuscles = listOf(MuscleGroup.SHOULDERS),
                equipment = EquipmentType.DUMBBELL,
                instructions = "Palms up, raise to eye level"
            ),
            ExerciseEntity(
                name = "Shrugs",
                type = ExerciseType.WEIGHTED,
                primaryMuscles = listOf(MuscleGroup.BACK),
                equipment = EquipmentType.DUMBBELL,
                instructions = "Straight up, pause at top"
            ),
            ExerciseEntity(
                name = "Weighted Plank",
                type = ExerciseType.TIMED,
                primaryMuscles = listOf(MuscleGroup.ABS),
                equipment = EquipmentType.BODYWEIGHT,
                instructions = "Add plate on back, neutral spine"
            ),

            // Posterior chain
            ExerciseEntity(
                name = "Trap Bar Deadlift",
                type = ExerciseType.WEIGHTED,
                primaryMuscles = listOf(MuscleGroup.GLUTES, MuscleGroup.HAMSTRINGS, MuscleGroup.BACK),
                equipment = EquipmentType.BARBELL,
                instructions = "Chest up, drive through heels"
            ),
            ExerciseEntity(
                name = "Bulgarian Split Squat",
                type = ExerciseType.WEIGHTED,
                primaryMuscles = listOf(MuscleGroup.QUADS, MuscleGroup.GLUTES),
                equipment = EquipmentType.DUMBBELL,
                instructions = "Front knee at 90°, control descent"
            ),
            ExerciseEntity(
                name = "Nordic Curls",
                type = ExerciseType.BODYWEIGHT,
                primaryMuscles = listOf(MuscleGroup.HAMSTRINGS),
                equipment = EquipmentType.BODYWEIGHT,
                instructions = "Slow eccentric, use hands to assist"
            ),
            ExerciseEntity(
                name = "Back Extensions",
                type = ExerciseType.BODYWEIGHT,
                primaryMuscles = listOf(MuscleGroup.LOWER_BACK),
                equipment = EquipmentType.BODYWEIGHT,
                instructions = "Hyperextension bench, neutral spine"
            ),

            // Daily ritual
            ExerciseEntity(
                name = "Dead Hang",
                type = ExerciseType.TIMED,
                primaryMuscles = listOf(MuscleGroup.FOREARMS, MuscleGroup.BACK),
                equipment = EquipmentType.PULL_UP_BAR,
                instructions = "Open hand, passive hang"
            ),
            ExerciseEntity(
                name = "Rice Bucket Grip Work",
                type = ExerciseType.TIMED,
                primaryMuscles = listOf(MuscleGroup.FOREARMS),
                equipment = EquipmentType.NONE,
                instructions = "30s each: open/close, flex/extend, circles"
            ),

            // Climbing
            ExerciseEntity(
                name = "Continuous Climbing (ARC)",
                type = ExerciseType.CLIMBING,
                primaryMuscles = listOf(MuscleGroup.FULL_BODY),
                equipment = EquipmentType.CLIMBING_WALL,
                instructions = "V2-V4, nose breathing, shake every 4-6 moves"
            ),
            ExerciseEntity(
                name = "Boulder Projecting",
                type = ExerciseType.CLIMBING,
                primaryMuscles = listOf(MuscleGroup.FULL_BODY),
                equipment = EquipmentType.CLIMBING_WALL,
                instructions = "Work V4-V6 problems, stop when power drops"
            ),
            ExerciseEntity(
                name = "Max Hang (Hangboard)",
                type = ExerciseType.TIMED,
                primaryMuscles = listOf(MuscleGroup.FOREARMS),
                equipment = EquipmentType.HANGBOARD,
                instructions = "Open hand, 10s max weight"
            ),
            ExerciseEntity(
                name = "Campus Board Ladders",
                type = ExerciseType.CLIMBING,
                primaryMuscles = listOf(MuscleGroup.BACK, MuscleGroup.FOREARMS),
                equipment = EquipmentType.CAMPUS_BOARD,
                instructions = "1-3-5-7, explosive, feet on for now"
            )
        )

        exerciseDao.insertExercises(exercises)

        // Return a map of exercise names to IDs for template creation
        return exercises.mapIndexed { index, exercise ->
            exercise.name to (index + 1).toLong()
        }.toMap()
    }

    private suspend fun createPullDayA(exerciseIds: Map<String, Long>) {
        val templateId = workoutTemplateDao.insertTemplate(
            WorkoutTemplateEntity(
                name = "Pull Day A - Strength",
                type = WorkoutType.STRENGTH,
                description = "Upper back and biceps focused pulling day",
                estimatedDurationMinutes = 60,
                difficulty = 7
            )
        )

        // Scap pull-ups warmup
        val scapExId = templateExerciseDao.insertTemplateExercise(
            TemplateExerciseEntity(
                workoutTemplateId = templateId,
                exerciseId = exerciseIds["Scap Pull-ups"]!!,
                orderIndex = 1,
                notes = "Warm-up: scapular activation"
            )
        )
        setPlanDao.insertSetPlans(listOf(
            SetPlanEntity(templateExerciseId = scapExId, setNumber = 1, targetReps = 8, restSeconds = 60),
            SetPlanEntity(templateExerciseId = scapExId, setNumber = 2, targetReps = 8, restSeconds = 60),
            SetPlanEntity(templateExerciseId = scapExId, setNumber = 3, targetReps = 8, restSeconds = 60),
            SetPlanEntity(templateExerciseId = scapExId, setNumber = 4, targetReps = 8, restSeconds = 90)
        ))

        // Weighted pull-ups
        val pullupExId = templateExerciseDao.insertTemplateExercise(
            TemplateExerciseEntity(
                workoutTemplateId = templateId,
                exerciseId = exerciseIds["Weighted Pull-ups"]!!,
                orderIndex = 2,
                notes = "Main strength movement - add weight when hit all 5s"
            )
        )
        setPlanDao.insertSetPlans(listOf(
            SetPlanEntity(templateExerciseId = pullupExId, setNumber = 1, targetReps = 5, targetWeight = 0f, restSeconds = 180),
            SetPlanEntity(templateExerciseId = pullupExId, setNumber = 2, targetReps = 5, targetWeight = 0f, restSeconds = 180),
            SetPlanEntity(templateExerciseId = pullupExId, setNumber = 3, targetReps = 5, targetWeight = 0f, restSeconds = 180),
            SetPlanEntity(templateExerciseId = pullupExId, setNumber = 4, targetReps = 5, targetWeight = 0f, restSeconds = 180),
            SetPlanEntity(templateExerciseId = pullupExId, setNumber = 5, targetReps = 5, targetWeight = 0f, restSeconds = 180)
        ))

        // Seal rows
        val sealRowExId = templateExerciseDao.insertTemplateExercise(
            TemplateExerciseEntity(
                workoutTemplateId = templateId,
                exerciseId = exerciseIds["Seal Rows"]!!,
                orderIndex = 3,
                notes = "Last set to 20 reps (drop weight if needed)"
            )
        )
        setPlanDao.insertSetPlans(listOf(
            SetPlanEntity(templateExerciseId = sealRowExId, setNumber = 1, targetReps = 10, targetWeight = 0f, restSeconds = 120),
            SetPlanEntity(templateExerciseId = sealRowExId, setNumber = 2, targetReps = 10, targetWeight = 0f, restSeconds = 120),
            SetPlanEntity(templateExerciseId = sealRowExId, setNumber = 3, targetReps = 20, targetWeight = 0f, restSeconds = 120)
        ))

        // DB lat rows
        val latRowExId = templateExerciseDao.insertTemplateExercise(
            TemplateExerciseEntity(
                workoutTemplateId = templateId,
                exerciseId = exerciseIds["DB Lat Rows"]!!,
                orderIndex = 4
            )
        )
        setPlanDao.insertSetPlans(listOf(
            SetPlanEntity(templateExerciseId = latRowExId, setNumber = 1, targetReps = 8, targetWeight = 0f, restSeconds = 90),
            SetPlanEntity(templateExerciseId = latRowExId, setNumber = 2, targetReps = 8, targetWeight = 0f, restSeconds = 90),
            SetPlanEntity(templateExerciseId = latRowExId, setNumber = 3, targetReps = 8, targetWeight = 0f, restSeconds = 90)
        ))

        // Face pulls
        val facePullExId = templateExerciseDao.insertTemplateExercise(
            TemplateExerciseEntity(
                workoutTemplateId = templateId,
                exerciseId = exerciseIds["Face Pulls"]!!,
                orderIndex = 5,
                notes = "Rear delt and shoulder health"
            )
        )
        setPlanDao.insertSetPlans(listOf(
            SetPlanEntity(templateExerciseId = facePullExId, setNumber = 1, targetReps = 15, targetWeight = 0f, restSeconds = 75),
            SetPlanEntity(templateExerciseId = facePullExId, setNumber = 2, targetReps = 15, targetWeight = 0f, restSeconds = 75),
            SetPlanEntity(templateExerciseId = facePullExId, setNumber = 3, targetReps = 15, targetWeight = 0f, restSeconds = 75)
        ))

        // Hanging leg raises
        val legRaiseExId = templateExerciseDao.insertTemplateExercise(
            TemplateExerciseEntity(
                workoutTemplateId = templateId,
                exerciseId = exerciseIds["Hanging Leg Raises"]!!,
                orderIndex = 6
            )
        )
        setPlanDao.insertSetPlans(listOf(
            SetPlanEntity(templateExerciseId = legRaiseExId, setNumber = 1, targetReps = 12, restSeconds = 60),
            SetPlanEntity(templateExerciseId = legRaiseExId, setNumber = 2, targetReps = 12, restSeconds = 60),
            SetPlanEntity(templateExerciseId = legRaiseExId, setNumber = 3, targetReps = 12, restSeconds = 60)
        ))

        // Hollow body hold
        val hollowExId = templateExerciseDao.insertTemplateExercise(
            TemplateExerciseEntity(
                workoutTemplateId = templateId,
                exerciseId = exerciseIds["Hollow Body Hold"]!!,
                orderIndex = 7
            )
        )
        setPlanDao.insertSetPlans(listOf(
            SetPlanEntity(templateExerciseId = hollowExId, setNumber = 1, targetSeconds = 30, restSeconds = 60),
            SetPlanEntity(templateExerciseId = hollowExId, setNumber = 2, targetSeconds = 30, restSeconds = 60),
            SetPlanEntity(templateExerciseId = hollowExId, setNumber = 3, targetSeconds = 30, restSeconds = 60)
        ))
    }

    private suspend fun createPushDayA(exerciseIds: Map<String, Long>) {
        val templateId = workoutTemplateDao.insertTemplate(
            WorkoutTemplateEntity(
                name = "Push Day A - Shoulders",
                type = WorkoutType.STRENGTH,
                description = "Chest, shoulders, and triceps",
                estimatedDurationMinutes = 50,
                difficulty = 6
            )
        )

        // Incline bench
        val benchExId = templateExerciseDao.insertTemplateExercise(
            TemplateExerciseEntity(
                workoutTemplateId = templateId,
                exerciseId = exerciseIds["Incline Bench Press"]!!,
                orderIndex = 1,
                notes = "6/6/6/20 pattern - last set drop weight"
            )
        )
        setPlanDao.insertSetPlans(listOf(
            SetPlanEntity(templateExerciseId = benchExId, setNumber = 1, targetReps = 6, targetWeight = 0f, restSeconds = 150),
            SetPlanEntity(templateExerciseId = benchExId, setNumber = 2, targetReps = 6, targetWeight = 0f, restSeconds = 150),
            SetPlanEntity(templateExerciseId = benchExId, setNumber = 3, targetReps = 6, targetWeight = 0f, restSeconds = 150),
            SetPlanEntity(templateExerciseId = benchExId, setNumber = 4, targetReps = 20, targetWeight = 0f, restSeconds = 120)
        ))

        // Arnold press
        val arnoldExId = templateExerciseDao.insertTemplateExercise(
            TemplateExerciseEntity(
                workoutTemplateId = templateId,
                exerciseId = exerciseIds["Arnold Press"]!!,
                orderIndex = 2
            )
        )
        setPlanDao.insertSetPlans(listOf(
            SetPlanEntity(templateExerciseId = arnoldExId, setNumber = 1, targetReps = 8, targetWeight = 0f, restSeconds = 120),
            SetPlanEntity(templateExerciseId = arnoldExId, setNumber = 2, targetReps = 8, targetWeight = 0f, restSeconds = 120),
            SetPlanEntity(templateExerciseId = arnoldExId, setNumber = 3, targetReps = 8, targetWeight = 0f, restSeconds = 120)
        ))

        // Weighted plank
        val plankExId = templateExerciseDao.insertTemplateExercise(
            TemplateExerciseEntity(
                workoutTemplateId = templateId,
                exerciseId = exerciseIds["Weighted Plank"]!!,
                orderIndex = 3,
                notes = "Add 5 lb every week until 45s possible"
            )
        )
        setPlanDao.insertSetPlans(listOf(
            SetPlanEntity(templateExerciseId = plankExId, setNumber = 1, targetSeconds = 20, targetWeight = 0f, restSeconds = 90),
            SetPlanEntity(templateExerciseId = plankExId, setNumber = 2, targetSeconds = 20, targetWeight = 0f, restSeconds = 90),
            SetPlanEntity(templateExerciseId = plankExId, setNumber = 3, targetSeconds = 20, targetWeight = 0f, restSeconds = 90),
            SetPlanEntity(templateExerciseId = plankExId, setNumber = 4, targetSeconds = 20, targetWeight = 0f, restSeconds = 90)
        ))
    }

    private suspend fun createPosteriorChainDay(exerciseIds: Map<String, Long>) {
        val templateId = workoutTemplateDao.insertTemplate(
            WorkoutTemplateEntity(
                name = "Posterior Chain Day",
                type = WorkoutType.STRENGTH,
                description = "Deadlifts, hamstrings, glutes - metabolism driver",
                estimatedDurationMinutes = 55,
                difficulty = 8
            )
        )

        // Trap bar deadlift
        val deadliftExId = templateExerciseDao.insertTemplateExercise(
            TemplateExerciseEntity(
                workoutTemplateId = templateId,
                exerciseId = exerciseIds["Trap Bar Deadlift"]!!,
                orderIndex = 1,
                notes = "Alternate conventional and trap bar weekly"
            )
        )
        setPlanDao.insertSetPlans(listOf(
            SetPlanEntity(templateExerciseId = deadliftExId, setNumber = 1, targetReps = 8, targetWeight = 0f, restSeconds = 180),
            SetPlanEntity(templateExerciseId = deadliftExId, setNumber = 2, targetReps = 8, targetWeight = 0f, restSeconds = 180),
            SetPlanEntity(templateExerciseId = deadliftExId, setNumber = 3, targetReps = 8, targetWeight = 0f, restSeconds = 180)
        ))

        // Bulgarian split squats
        val splitSquatExId = templateExerciseDao.insertTemplateExercise(
            TemplateExerciseEntity(
                workoutTemplateId = templateId,
                exerciseId = exerciseIds["Bulgarian Split Squat"]!!,
                orderIndex = 2,
                notes = "Start with weaker leg"
            )
        )
        setPlanDao.insertSetPlans(listOf(
            SetPlanEntity(templateExerciseId = splitSquatExId, setNumber = 1, targetReps = 8, targetWeight = 0f, restSeconds = 120),
            SetPlanEntity(templateExerciseId = splitSquatExId, setNumber = 2, targetReps = 6, targetWeight = 0f, restSeconds = 120),
            SetPlanEntity(templateExerciseId = splitSquatExId, setNumber = 3, targetReps = 5, targetWeight = 0f, restSeconds = 120)
        ))

        // Nordic curls
        val nordicExId = templateExerciseDao.insertTemplateExercise(
            TemplateExerciseEntity(
                workoutTemplateId = templateId,
                exerciseId = exerciseIds["Nordic Curls"]!!,
                orderIndex = 3,
                notes = "Slow eccentric, assisted concentric OK"
            )
        )
        setPlanDao.insertSetPlans(listOf(
            SetPlanEntity(templateExerciseId = nordicExId, setNumber = 1, targetReps = 5, restSeconds = 120),
            SetPlanEntity(templateExerciseId = nordicExId, setNumber = 2, targetReps = 5, restSeconds = 120),
            SetPlanEntity(templateExerciseId = nordicExId, setNumber = 3, targetReps = 5, restSeconds = 120),
            SetPlanEntity(templateExerciseId = nordicExId, setNumber = 4, targetReps = 5, restSeconds = 120)
        ))
    }

    private suspend fun createFullBodyMetabolic(exerciseIds: Map<String, Long>) {
        workoutTemplateDao.insertTemplate(
            WorkoutTemplateEntity(
                name = "Full Body Metabolic",
                type = WorkoutType.POWER_ENDURANCE,
                description = "Circuit style, fat burning, short rest",
                estimatedDurationMinutes = 45,
                difficulty = 7
            )
        )
        // TODO: Add exercises and sets when circuit format is defined
    }

    private suspend fun createDailyRitual(exerciseIds: Map<String, Long>) {
        val templateId = workoutTemplateDao.insertTemplate(
            WorkoutTemplateEntity(
                name = "Daily Ritual (Netero Protocol)",
                type = WorkoutType.SKILL,
                description = "Non-negotiable daily practices - tendon health and consistency",
                estimatedDurationMinutes = 10,
                difficulty = 3
            )
        )

        // Dead hangs
        val hangExId = templateExerciseDao.insertTemplateExercise(
            TemplateExerciseEntity(
                workoutTemplateId = templateId,
                exerciseId = exerciseIds["Dead Hang"]!!,
                orderIndex = 1,
                notes = "Open hand, passive hang for tendon health"
            )
        )
        setPlanDao.insertSetPlans(listOf(
            SetPlanEntity(templateExerciseId = hangExId, setNumber = 1, targetSeconds = 20, restSeconds = 60),
            SetPlanEntity(templateExerciseId = hangExId, setNumber = 2, targetSeconds = 20, restSeconds = 60),
            SetPlanEntity(templateExerciseId = hangExId, setNumber = 3, targetSeconds = 20, restSeconds = 60),
            SetPlanEntity(templateExerciseId = hangExId, setNumber = 4, targetSeconds = 20, restSeconds = 60),
            SetPlanEntity(templateExerciseId = hangExId, setNumber = 5, targetSeconds = 20, restSeconds = 60)
        ))

        // Hollow body
        val hollowExId = templateExerciseDao.insertTemplateExercise(
            TemplateExerciseEntity(
                workoutTemplateId = templateId,
                exerciseId = exerciseIds["Hollow Body Hold"]!!,
                orderIndex = 2
            )
        )
        setPlanDao.insertSetPlans(listOf(
            SetPlanEntity(templateExerciseId = hollowExId, setNumber = 1, targetSeconds = 30, restSeconds = 60),
            SetPlanEntity(templateExerciseId = hollowExId, setNumber = 2, targetSeconds = 30, restSeconds = 60),
            SetPlanEntity(templateExerciseId = hollowExId, setNumber = 3, targetSeconds = 30, restSeconds = 60)
        ))

        // Scap pull-ups
        val scapExId = templateExerciseDao.insertTemplateExercise(
            TemplateExerciseEntity(
                workoutTemplateId = templateId,
                exerciseId = exerciseIds["Scap Pull-ups"]!!,
                orderIndex = 3
            )
        )
        setPlanDao.insertSetPlans(listOf(
            SetPlanEntity(templateExerciseId = scapExId, setNumber = 1, targetReps = 10, restSeconds = 60),
            SetPlanEntity(templateExerciseId = scapExId, setNumber = 2, targetReps = 10, restSeconds = 60)
        ))

        // Rice bucket
        val riceExId = templateExerciseDao.insertTemplateExercise(
            TemplateExerciseEntity(
                workoutTemplateId = templateId,
                exerciseId = exerciseIds["Rice Bucket Grip Work"]!!,
                orderIndex = 4,
                notes = "2 rounds of: open/close, flex/extend, radial/ulnar, circles"
            )
        )
        setPlanDao.insertSetPlans(listOf(
            SetPlanEntity(templateExerciseId = riceExId, setNumber = 1, targetSeconds = 300, restSeconds = 0)  // 5 min total
        ))
    }

    private suspend fun createAerobicClimbingDay(exerciseIds: Map<String, Long>) {
        val templateId = workoutTemplateDao.insertTemplate(
            WorkoutTemplateEntity(
                name = "Aerobic Climbing Day",
                type = WorkoutType.AEROBIC,
                description = "Engine building, continuous movement, nose breathing",
                estimatedDurationMinutes = 90,
                difficulty = 5
            )
        )

        val arcExId = templateExerciseDao.insertTemplateExercise(
            TemplateExerciseEntity(
                workoutTemplateId = templateId,
                exerciseId = exerciseIds["Continuous Climbing (ARC)"]!!,
                orderIndex = 1,
                notes = "Continuous V2-V4, shake every 4-6 moves"
            )
        )
        setPlanDao.insertSetPlans(listOf(
            SetPlanEntity(templateExerciseId = arcExId, setNumber = 1, targetSeconds = 360, restSeconds = 120),  // 6 min
            SetPlanEntity(templateExerciseId = arcExId, setNumber = 2, targetSeconds = 360, restSeconds = 120)
        ))
    }

    private suspend fun createPowerClimbingDay(exerciseIds: Map<String, Long>) {
        val templateId = workoutTemplateDao.insertTemplate(
            WorkoutTemplateEntity(
                name = "Power Climbing Day",
                type = WorkoutType.POWER,
                description = "Boulder projecting and limit moves",
                estimatedDurationMinutes = 60,
                difficulty = 8
            )
        )

        val projectExId = templateExerciseDao.insertTemplateExercise(
            TemplateExerciseEntity(
                workoutTemplateId = templateId,
                exerciseId = exerciseIds["Boulder Projecting"]!!,
                orderIndex = 1,
                notes = "V4-V6, stop when power drops"
            )
        )
        setPlanDao.insertSetPlans(listOf(
            SetPlanEntity(templateExerciseId = projectExId, setNumber = 1, targetReps = 1, restSeconds = 180),
            SetPlanEntity(templateExerciseId = projectExId, setNumber = 2, targetReps = 1, restSeconds = 180),
            SetPlanEntity(templateExerciseId = projectExId, setNumber = 3, targetReps = 1, restSeconds = 180)
        ))
    }
}

