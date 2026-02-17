package com.example.adaptiveathlete.data.repository

import com.example.adaptiveathlete.data.db.dao.*
import com.example.adaptiveathlete.data.db.entity.*
import com.example.adaptiveathlete.data.model.*
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProgramSeedRepository @Inject constructor(
    private val exerciseDao: ExerciseDao,
    private val workoutTemplateDao: WorkoutTemplateDao,
    private val templateExerciseDao: TemplateExerciseDao,
    private val setPlanDao: SetPlanDao,
    private val programDao: ProgramDao,
    private val scheduledWorkoutDao: ScheduledWorkoutDao
) {

    /**
     * Create the V6 Athlete Program - 4 week block
     * 3 lift days, 3-4 climb days, 1 run day
     */
    suspend fun createV6AthleteProgram(startDate: LocalDate = LocalDate.now()): Long {
        // First, create all exercises needed for this program
        val exerciseIds = createV6Exercises()

        // Create workout templates
        val templateIds = mapOf(
            "sunday_climb" to createSundayClimbTemplate(exerciseIds),
            "monday_upper" to createMondayUpperTemplate(exerciseIds),
            "tuesday_power" to createTuesdayPowerTemplate(exerciseIds),
            "wednesday_lower" to createWednesdayLowerTemplate(exerciseIds),
            "thursday_finger" to createThursdayFingerTemplate(exerciseIds),
            "friday_run" to createFridayRunTemplate(exerciseIds),
            "saturday_athletic" to createSaturdayAthleticTemplate(exerciseIds)
        )

        // Create program entity
        val programId = programDao.insert(
            ProgramEntity(
                name = "V6 Athlete Program",
                description = "4-week block: 3 lift days, 3-4 climb days, 1 run day. Focus: V6 sends, strength, power, lean muscle.",
                durationWeeks = 4,
                daysPerWeek = 7,
                difficulty = 8,
                goals = listOf("strength", "power", "v6_climbing", "fat_loss", "athleticism"),
                isActive = true,
                startDate = startDate
            )
        )

        // Schedule all 4 weeks
        scheduleV6Program(programId, templateIds, startDate)

        return programId
    }

    private suspend fun scheduleV6Program(
        programId: Long,
        templateIds: Map<String, Long>,
        startDate: LocalDate
    ) {
        val scheduledWorkouts = mutableListOf<ScheduledWorkoutEntity>()

        for (week in 1..4) {
            val weekStart = startDate.plusWeeks((week - 1).toLong())

            // Find the next Sunday from weekStart
            val sunday = weekStart.with(DayOfWeek.SUNDAY)

            scheduledWorkouts.add(
                ScheduledWorkoutEntity(
                    programId = programId,
                    templateId = templateIds["sunday_climb"]!!,
                    scheduledDate = sunday,
                    weekNumber = week,
                    dayOfWeek = 7
                )
            )

            scheduledWorkouts.add(
                ScheduledWorkoutEntity(
                    programId = programId,
                    templateId = templateIds["monday_upper"]!!,
                    scheduledDate = sunday.plusDays(1),
                    weekNumber = week,
                    dayOfWeek = 1
                )
            )

            scheduledWorkouts.add(
                ScheduledWorkoutEntity(
                    programId = programId,
                    templateId = templateIds["tuesday_power"]!!,
                    scheduledDate = sunday.plusDays(2),
                    weekNumber = week,
                    dayOfWeek = 2
                )
            )

            scheduledWorkouts.add(
                ScheduledWorkoutEntity(
                    programId = programId,
                    templateId = templateIds["wednesday_lower"]!!,
                    scheduledDate = sunday.plusDays(3),
                    weekNumber = week,
                    dayOfWeek = 3
                )
            )

            scheduledWorkouts.add(
                ScheduledWorkoutEntity(
                    programId = programId,
                    templateId = templateIds["thursday_finger"]!!,
                    scheduledDate = sunday.plusDays(4),
                    weekNumber = week,
                    dayOfWeek = 4
                )
            )

            scheduledWorkouts.add(
                ScheduledWorkoutEntity(
                    programId = programId,
                    templateId = templateIds["friday_run"]!!,
                    scheduledDate = sunday.plusDays(5),
                    weekNumber = week,
                    dayOfWeek = 5
                )
            )

            // Week 4 is deload - skip Saturday
            if (week < 4) {
                scheduledWorkouts.add(
                    ScheduledWorkoutEntity(
                        programId = programId,
                        templateId = templateIds["saturday_athletic"]!!,
                        scheduledDate = sunday.plusDays(6),
                        weekNumber = week,
                        dayOfWeek = 6
                    )
                )
            }
        }

        scheduledWorkoutDao.insertAll(scheduledWorkouts)
    }

    private suspend fun createV6Exercises(): Map<String, Long> {
        val exercises = mutableMapOf<String, Long>()

        // STRENGTH EXERCISES
        exercises["weighted_pullup"] = exerciseDao.insertExercise(
            ExerciseEntity(
                name = "Weighted Pull-ups",
                type = ExerciseType.WEIGHTED,
                primaryMuscles = listOf(MuscleGroup.BACK, MuscleGroup.BICEPS),
                equipment = EquipmentType.PULL_UP_BAR,
                instructions = "Heavy pulling. Add weight belt. 5x3-5 reps. Add 2.5-5lb weekly."
            )
        )

        exercises["bench_press"] = exerciseDao.insertExercise(
            ExerciseEntity(
                name = "Bench Press",
                type = ExerciseType.WEIGHTED,
                primaryMuscles = listOf(MuscleGroup.CHEST),
                secondaryMuscles = listOf(MuscleGroup.SHOULDERS, MuscleGroup.TRICEPS),
                equipment = EquipmentType.BARBELL,
                instructions = "4x5 reps. Controlled descent, explosive press."
            )
        )

        exercises["weighted_pushup"] = exerciseDao.insertExercise(
            ExerciseEntity(
                name = "Weighted Push-ups",
                type = ExerciseType.WEIGHTED,
                primaryMuscles = listOf(MuscleGroup.CHEST, MuscleGroup.TRICEPS),
                equipment = EquipmentType.BODYWEIGHT,
                instructions = "Add plate on back. 4x5 reps."
            )
        )

        exercises["chest_supported_row"] = exerciseDao.insertExercise(
            ExerciseEntity(
                name = "Chest-Supported Rows",
                type = ExerciseType.WEIGHTED,
                primaryMuscles = listOf(MuscleGroup.BACK),
                equipment = EquipmentType.DUMBBELL,
                instructions = "3x8 reps. Squeeze shoulder blades. No momentum."
            )
        )

        exercises["dips"] = exerciseDao.insertExercise(
            ExerciseEntity(
                name = "Dips",
                type = ExerciseType.BODYWEIGHT,
                primaryMuscles = listOf(MuscleGroup.CHEST, MuscleGroup.TRICEPS),
                equipment = EquipmentType.BODYWEIGHT,
                instructions = "3x6-10 reps. Lean forward for chest emphasis."
            )
        )

        exercises["dead_hang"] = exerciseDao.insertExercise(
            ExerciseEntity(
                name = "Dead Hang",
                type = ExerciseType.TIMED,
                primaryMuscles = listOf(MuscleGroup.FOREARMS, MuscleGroup.BACK),
                equipment = EquipmentType.PULL_UP_BAR,
                instructions = "3x max time. Build finger and grip strength."
            )
        )

        exercises["face_pulls"] = exerciseDao.insertExercise(
            ExerciseEntity(
                name = "Face Pulls",
                type = ExerciseType.WEIGHTED,
                primaryMuscles = listOf(MuscleGroup.SHOULDERS),
                equipment = EquipmentType.CABLE,
                instructions = "3x12 reps. Pull to face, external rotation."
            )
        )

        // LOWER BODY
        exercises["back_squat"] = exerciseDao.insertExercise(
            ExerciseEntity(
                name = "Back Squat",
                type = ExerciseType.WEIGHTED,
                primaryMuscles = listOf(MuscleGroup.QUADS, MuscleGroup.GLUTES),
                equipment = EquipmentType.BARBELL,
                instructions = "5x3-5 reps. Heavy. Explosive + strength."
            )
        )

        exercises["romanian_deadlift"] = exerciseDao.insertExercise(
            ExerciseEntity(
                name = "Romanian Deadlift",
                type = ExerciseType.WEIGHTED,
                primaryMuscles = listOf(MuscleGroup.HAMSTRINGS, MuscleGroup.GLUTES),
                equipment = EquipmentType.BARBELL,
                instructions = "4x6 reps. Hip hinge, feel hamstring stretch."
            )
        )

        exercises["bulgarian_split_squat"] = exerciseDao.insertExercise(
            ExerciseEntity(
                name = "Bulgarian Split Squat",
                type = ExerciseType.WEIGHTED,
                primaryMuscles = listOf(MuscleGroup.QUADS, MuscleGroup.GLUTES),
                equipment = EquipmentType.DUMBBELL,
                instructions = "3x8 each leg. Single leg strength."
            )
        )

        exercises["box_jumps"] = exerciseDao.insertExercise(
            ExerciseEntity(
                name = "Box Jumps",
                type = ExerciseType.BODYWEIGHT,
                primaryMuscles = listOf(MuscleGroup.QUADS, MuscleGroup.GLUTES),
                equipment = EquipmentType.NONE,
                instructions = "4x3 reps. Explosive. For dynos."
            )
        )

        exercises["calf_raises"] = exerciseDao.insertExercise(
            ExerciseEntity(
                name = "Calf Raises",
                type = ExerciseType.WEIGHTED,
                primaryMuscles = listOf(MuscleGroup.CALVES),
                equipment = EquipmentType.DUMBBELL,
                instructions = "3x12 reps. Full ROM."
            )
        )

        // CORE
        exercises["plank"] = exerciseDao.insertExercise(
            ExerciseEntity(
                name = "Plank",
                type = ExerciseType.TIMED,
                primaryMuscles = listOf(MuscleGroup.ABS),
                equipment = EquipmentType.BODYWEIGHT,
                instructions = "3x45s. Neutral spine."
            )
        )

        exercises["hollow_hold"] = exerciseDao.insertExercise(
            ExerciseEntity(
                name = "Hollow Hold",
                type = ExerciseType.TIMED,
                primaryMuscles = listOf(MuscleGroup.ABS),
                equipment = EquipmentType.BODYWEIGHT,
                instructions = "3x max time. Lower back pressed down."
            )
        )

        exercises["side_plank"] = exerciseDao.insertExercise(
            ExerciseEntity(
                name = "Side Plank",
                type = ExerciseType.TIMED,
                primaryMuscles = listOf(MuscleGroup.OBLIQUES),
                equipment = EquipmentType.BODYWEIGHT,
                instructions = "3x max each side. Stable hips."
            )
        )

        exercises["hanging_leg_raises"] = exerciseDao.insertExercise(
            ExerciseEntity(
                name = "Hanging Leg Raises",
                type = ExerciseType.BODYWEIGHT,
                primaryMuscles = listOf(MuscleGroup.ABS),
                equipment = EquipmentType.PULL_UP_BAR,
                instructions = "3x8-12 reps. Controlled, no swinging."
            )
        )

        // ATHLETIC / POWER
        exercises["power_cleans"] = exerciseDao.insertExercise(
            ExerciseEntity(
                name = "Power Cleans",
                type = ExerciseType.WEIGHTED,
                primaryMuscles = listOf(MuscleGroup.FULL_BODY),
                equipment = EquipmentType.BARBELL,
                instructions = "4x3-5 reps. Explosive triple extension."
            )
        )

        exercises["kettlebell_swings"] = exerciseDao.insertExercise(
            ExerciseEntity(
                name = "Kettlebell Swings",
                type = ExerciseType.WEIGHTED,
                primaryMuscles = listOf(MuscleGroup.GLUTES, MuscleGroup.HAMSTRINGS),
                equipment = EquipmentType.KETTLEBELL,
                instructions = "4x3-5 reps. Hip hinge, explosive."
            )
        )

        exercises["overhead_press"] = exerciseDao.insertExercise(
            ExerciseEntity(
                name = "Overhead Press",
                type = ExerciseType.WEIGHTED,
                primaryMuscles = listOf(MuscleGroup.SHOULDERS),
                equipment = EquipmentType.BARBELL,
                instructions = "4x5 reps. Strict form, vertical bar path."
            )
        )

        exercises["pullups"] = exerciseDao.insertExercise(
            ExerciseEntity(
                name = "Pull-ups",
                type = ExerciseType.BODYWEIGHT,
                primaryMuscles = listOf(MuscleGroup.BACK, MuscleGroup.BICEPS),
                equipment = EquipmentType.PULL_UP_BAR,
                instructions = "3x max reps. Strict form."
            )
        )

        exercises["hip_thrusts"] = exerciseDao.insertExercise(
            ExerciseEntity(
                name = "Hip Thrusts",
                type = ExerciseType.WEIGHTED,
                primaryMuscles = listOf(MuscleGroup.GLUTES),
                equipment = EquipmentType.BARBELL,
                instructions = "3x8 reps. Glute activation, full extension."
            )
        )

        exercises["farmer_carry"] = exerciseDao.insertExercise(
            ExerciseEntity(
                name = "Farmer Carry",
                type = ExerciseType.WEIGHTED,
                primaryMuscles = listOf(MuscleGroup.FOREARMS, MuscleGroup.FULL_BODY),
                equipment = EquipmentType.DUMBBELL,
                instructions = "3 rounds, 40-60 yards. Heavy. Grip strength."
            )
        )

        // CLIMBING
        exercises["bouldering"] = exerciseDao.insertExercise(
            ExerciseEntity(
                name = "Bouldering",
                type = ExerciseType.CLIMBING,
                primaryMuscles = listOf(MuscleGroup.FULL_BODY),
                equipment = EquipmentType.CLIMBING_WALL,
                instructions = "See workout template for specific protocol."
            )
        )

        exercises["arc_climbing"] = exerciseDao.insertExercise(
            ExerciseEntity(
                name = "ARC Climbing",
                type = ExerciseType.CLIMBING,
                primaryMuscles = listOf(MuscleGroup.FOREARMS, MuscleGroup.BACK),
                equipment = EquipmentType.CLIMBING_WALL,
                instructions = "10 min continuous easy movement. Aerobic endurance."
            )
        )

        exercises["edge_hangs"] = exerciseDao.insertExercise(
            ExerciseEntity(
                name = "20mm Edge Hangs",
                type = ExerciseType.TIMED,
                primaryMuscles = listOf(MuscleGroup.FOREARMS),
                equipment = EquipmentType.HANGBOARD,
                instructions = "5x7 sec heavy hangs. Full rest between sets."
            )
        )

        exercises["repeaters"] = exerciseDao.insertExercise(
            ExerciseEntity(
                name = "7/3 Repeaters",
                type = ExerciseType.TIMED,
                primaryMuscles = listOf(MuscleGroup.FOREARMS),
                equipment = EquipmentType.HANGBOARD,
                instructions = "3 sets. 7 sec hang, 3 sec rest, repeat 6 times."
            )
        )

        exercises["lockoffs"] = exerciseDao.insertExercise(
            ExerciseEntity(
                name = "Lock-off Holds",
                type = ExerciseType.TIMED,
                primaryMuscles = listOf(MuscleGroup.BACK, MuscleGroup.BICEPS),
                equipment = EquipmentType.PULL_UP_BAR,
                instructions = "3x max each arm. Build isometric strength."
            )
        )

        exercises["campus_ladder"] = exerciseDao.insertExercise(
            ExerciseEntity(
                name = "Campus Ladder",
                type = ExerciseType.CLIMBING,
                primaryMuscles = listOf(MuscleGroup.FOREARMS, MuscleGroup.BACK),
                equipment = EquipmentType.CAMPUS_BOARD,
                instructions = "4 sets. Only if healthy. Explosive power."
            )
        )

        exercises["dynos"] = exerciseDao.insertExercise(
            ExerciseEntity(
                name = "Dyno Attempts",
                type = ExerciseType.CLIMBING,
                primaryMuscles = listOf(MuscleGroup.FULL_BODY),
                equipment = EquipmentType.CLIMBING_WALL,
                instructions = "3 sets. Full rest between. Explosive movement practice."
            )
        )

        // CARDIO
        exercises["run"] = exerciseDao.insertExercise(
            ExerciseEntity(
                name = "Running",
                type = ExerciseType.CARDIO,
                primaryMuscles = listOf(MuscleGroup.FULL_BODY),
                equipment = EquipmentType.NONE,
                instructions = "See workout template for specific protocol."
            )
        )

        exercises["rice_bucket"] = exerciseDao.insertExercise(
            ExerciseEntity(
                name = "Rice Bucket",
                type = ExerciseType.BODYWEIGHT,
                primaryMuscles = listOf(MuscleGroup.FOREARMS),
                equipment = EquipmentType.NONE,
                instructions = "3-5 min daily. Finger health and recovery."
            )
        )

        return exercises
    }

    // TEMPLATE CREATION FUNCTIONS

    private suspend fun createSundayClimbTemplate(exerciseIds: Map<String, Long>): Long {
        val templateId = workoutTemplateDao.insertTemplate(
            WorkoutTemplateEntity(
                name = "Bouldering: Volume + Skill",
                type = WorkoutType.SKILL,
                description = "Movement efficiency and endurance. Leave with energy.",
                estimatedDurationMinutes = 90,
                difficulty = 5
            )
        )

        // Warmup
        templateExerciseDao.insertTemplateExercise(
            TemplateExerciseEntity(
                workoutTemplateId = templateId,
                exerciseId = exerciseIds["bouldering"]!!,
                orderIndex = 0,
                notes = "3 easy problems V0-V2 warmup"
            )
        )

        // Volume climbing
        templateExerciseDao.insertTemplateExercise(
            TemplateExerciseEntity(
                workoutTemplateId = templateId,
                exerciseId = exerciseIds["bouldering"]!!,
                orderIndex = 1,
                notes = "6-8 problems V2-V4, focus clean footwork"
            )
        )

        // Flash attempts
        templateExerciseDao.insertTemplateExercise(
            TemplateExerciseEntity(
                workoutTemplateId = templateId,
                exerciseId = exerciseIds["bouldering"]!!,
                orderIndex = 2,
                notes = "3 flash attempts at V4-V5"
            )
        )

        // ARC climbing
        val arcExId = templateExerciseDao.insertTemplateExercise(
            TemplateExerciseEntity(
                workoutTemplateId = templateId,
                exerciseId = exerciseIds["arc_climbing"]!!,
                orderIndex = 3
            )
        )
        setPlanDao.insertSetPlan(
            SetPlanEntity(
                templateExerciseId = arcExId,
                setNumber = 1,
                targetSeconds = 600 // 10 minutes
            )
        )

        // Core finisher
        val legRaiseId = templateExerciseDao.insertTemplateExercise(
            TemplateExerciseEntity(
                workoutTemplateId = templateId,
                exerciseId = exerciseIds["hanging_leg_raises"]!!,
                orderIndex = 4
            )
        )
        for (set in 1..3) {
            setPlanDao.insertSetPlan(
                SetPlanEntity(
                    templateExerciseId = legRaiseId,
                    setNumber = set,
                    targetReps = 10,
                    rpe = 7
                )
            )
        }

        return templateId
    }

    private suspend fun createMondayUpperTemplate(exerciseIds: Map<String, Long>): Long {
        val templateId = workoutTemplateDao.insertTemplate(
            WorkoutTemplateEntity(
                name = "Gym: Upper Body",
                type = WorkoutType.STRENGTH,
                description = "Heavy pulling priority. Add 2.5-5 lb weekly if reps clean.",
                estimatedDurationMinutes = 60,
                difficulty = 8
            )
        )

        // Weighted pull-ups 5x3-5
        val pullupId = templateExerciseDao.insertTemplateExercise(
            TemplateExerciseEntity(
                workoutTemplateId = templateId,
                exerciseId = exerciseIds["weighted_pullup"]!!,
                orderIndex = 0
            )
        )
        for (set in 1..5) {
            setPlanDao.insertSetPlan(
                SetPlanEntity(
                    templateExerciseId = pullupId,
                    setNumber = set,
                    targetReps = 5,
                    targetWeight = 25f, // User will adjust
                    rpe = 8
                )
            )
        }

        // Bench press 4x5
        val benchId = templateExerciseDao.insertTemplateExercise(
            TemplateExerciseEntity(
                workoutTemplateId = templateId,
                exerciseId = exerciseIds["bench_press"]!!,
                orderIndex = 1
            )
        )
        for (set in 1..4) {
            setPlanDao.insertSetPlan(
                SetPlanEntity(
                    templateExerciseId = benchId,
                    setNumber = set,
                    targetReps = 5,
                    targetWeight = 135f,
                    rpe = 8
                )
            )
        }

        // Chest-supported rows 3x8
        val rowId = templateExerciseDao.insertTemplateExercise(
            TemplateExerciseEntity(
                workoutTemplateId = templateId,
                exerciseId = exerciseIds["chest_supported_row"]!!,
                orderIndex = 2
            )
        )
        for (set in 1..3) {
            setPlanDao.insertSetPlan(
                SetPlanEntity(
                    templateExerciseId = rowId,
                    setNumber = set,
                    targetReps = 8,
                    targetWeight = 50f,
                    rpe = 7
                )
            )
        }

        // Dips 3x6-10
        val dipId = templateExerciseDao.insertTemplateExercise(
            TemplateExerciseEntity(
                workoutTemplateId = templateId,
                exerciseId = exerciseIds["dips"]!!,
                orderIndex = 3
            )
        )
        for (set in 1..3) {
            setPlanDao.insertSetPlan(
                SetPlanEntity(
                    templateExerciseId = dipId,
                    setNumber = set,
                    targetReps = 8,
                    rpe = 7
                )
            )
        }

        // Dead hang 3x max
        val hangId = templateExerciseDao.insertTemplateExercise(
            TemplateExerciseEntity(
                workoutTemplateId = templateId,
                exerciseId = exerciseIds["dead_hang"]!!,
                orderIndex = 4
            )
        )
        for (set in 1..3) {
            setPlanDao.insertSetPlan(
                SetPlanEntity(
                    templateExerciseId = hangId,
                    setNumber = set,
                    targetSeconds = 45
                )
            )
        }

        // Face pulls 3x12
        val faceId = templateExerciseDao.insertTemplateExercise(
            TemplateExerciseEntity(
                workoutTemplateId = templateId,
                exerciseId = exerciseIds["face_pulls"]!!,
                orderIndex = 5
            )
        )
        for (set in 1..3) {
            setPlanDao.insertSetPlan(
                SetPlanEntity(
                    templateExerciseId = faceId,
                    setNumber = set,
                    targetReps = 12,
                    targetWeight = 40f,
                    rpe = 6
                )
            )
        }

        return templateId
    }

    private suspend fun createTuesdayPowerTemplate(exerciseIds: Map<String, Long>): Long {
        val templateId = workoutTemplateDao.insertTemplate(
            WorkoutTemplateEntity(
                name = "Bouldering: Power",
                type = WorkoutType.POWER,
                description = "Low volume, high intensity. Stop when power drops.",
                estimatedDurationMinutes = 75,
                difficulty = 9
            )
        )

        templateExerciseDao.insertTemplateExercise(
            TemplateExerciseEntity(
                workoutTemplateId = templateId,
                exerciseId = exerciseIds["bouldering"]!!,
                orderIndex = 0,
                notes = "Warmup - easy climbing"
            )
        )

        templateExerciseDao.insertTemplateExercise(
            TemplateExerciseEntity(
                workoutTemplateId = templateId,
                exerciseId = exerciseIds["bouldering"]!!,
                orderIndex = 1,
                notes = "4-6 limit boulders (V5+ attempts)"
            )
        )

        templateExerciseDao.insertTemplateExercise(
            TemplateExerciseEntity(
                workoutTemplateId = templateId,
                exerciseId = exerciseIds["dynos"]!!,
                orderIndex = 2,
                notes = "3 sets, full rest between"
            )
        )

        templateExerciseDao.insertTemplateExercise(
            TemplateExerciseEntity(
                workoutTemplateId = templateId,
                exerciseId = exerciseIds["campus_ladder"]!!,
                orderIndex = 3,
                notes = "4 sets (if healthy)"
            )
        )

        templateExerciseDao.insertTemplateExercise(
            TemplateExerciseEntity(
                workoutTemplateId = templateId,
                exerciseId = exerciseIds["bouldering"]!!,
                orderIndex = 4,
                notes = "Finish: 2 moderate problems clean"
            )
        )

        return templateId
    }

    private suspend fun createWednesdayLowerTemplate(exerciseIds: Map<String, Long>): Long {
        val templateId = workoutTemplateDao.insertTemplate(
            WorkoutTemplateEntity(
                name = "Gym: Legs",
                type = WorkoutType.STRENGTH,
                description = "Explosive + strength. Climbing legs need power, not bodybuilder fatigue.",
                estimatedDurationMinutes = 55,
                difficulty = 8
            )
        )

        // Back squat 5x3-5
        val squatId = templateExerciseDao.insertTemplateExercise(
            TemplateExerciseEntity(
                workoutTemplateId = templateId,
                exerciseId = exerciseIds["back_squat"]!!,
                orderIndex = 0
            )
        )
        for (set in 1..5) {
            setPlanDao.insertSetPlan(
                SetPlanEntity(
                    templateExerciseId = squatId,
                    setNumber = set,
                    targetReps = 5,
                    targetWeight = 185f,
                    rpe = 8
                )
            )
        }

        // Romanian deadlift 4x6
        val rdlId = templateExerciseDao.insertTemplateExercise(
            TemplateExerciseEntity(
                workoutTemplateId = templateId,
                exerciseId = exerciseIds["romanian_deadlift"]!!,
                orderIndex = 1
            )
        )
        for (set in 1..4) {
            setPlanDao.insertSetPlan(
                SetPlanEntity(
                    templateExerciseId = rdlId,
                    setNumber = set,
                    targetReps = 6,
                    targetWeight = 155f,
                    rpe = 7
                )
            )
        }

        // Bulgarian split squat 3x8 each
        val bulgarianId = templateExerciseDao.insertTemplateExercise(
            TemplateExerciseEntity(
                workoutTemplateId = templateId,
                exerciseId = exerciseIds["bulgarian_split_squat"]!!,
                orderIndex = 2
            )
        )
        for (set in 1..3) {
            setPlanDao.insertSetPlan(
                SetPlanEntity(
                    templateExerciseId = bulgarianId,
                    setNumber = set,
                    targetReps = 8,
                    targetWeight = 30f,
                    rpe = 7
                )
            )
        }

        // Box jumps 4x3
        val boxId = templateExerciseDao.insertTemplateExercise(
            TemplateExerciseEntity(
                workoutTemplateId = templateId,
                exerciseId = exerciseIds["box_jumps"]!!,
                orderIndex = 3
            )
        )
        for (set in 1..4) {
            setPlanDao.insertSetPlan(
                SetPlanEntity(
                    templateExerciseId = boxId,
                    setNumber = set,
                    targetReps = 3
                )
            )
        }

        // Calf raises 3x12
        val calfId = templateExerciseDao.insertTemplateExercise(
            TemplateExerciseEntity(
                workoutTemplateId = templateId,
                exerciseId = exerciseIds["calf_raises"]!!,
                orderIndex = 4
            )
        )
        for (set in 1..3) {
            setPlanDao.insertSetPlan(
                SetPlanEntity(
                    templateExerciseId = calfId,
                    setNumber = set,
                    targetReps = 12,
                    targetWeight = 40f
                )
            )
        }

        // Plank 3x45s
        val plankId = templateExerciseDao.insertTemplateExercise(
            TemplateExerciseEntity(
                workoutTemplateId = templateId,
                exerciseId = exerciseIds["plank"]!!,
                orderIndex = 5
            )
        )
        for (set in 1..3) {
            setPlanDao.insertSetPlan(
                SetPlanEntity(
                    templateExerciseId = plankId,
                    setNumber = set,
                    targetSeconds = 45
                )
            )
        }

        return templateId
    }

    private suspend fun createThursdayFingerTemplate(exerciseIds: Map<String, Long>): Long {
        val templateId = workoutTemplateDao.insertTemplate(
            WorkoutTemplateEntity(
                name = "Bouldering: Finger Strength",
                type = WorkoutType.POWER,
                description = "Fingerboard focus. This is your V6 builder.",
                estimatedDurationMinutes = 70,
                difficulty = 9
            )
        )

        templateExerciseDao.insertTemplateExercise(
            TemplateExerciseEntity(
                workoutTemplateId = templateId,
                exerciseId = exerciseIds["bouldering"]!!,
                orderIndex = 0,
                notes = "Warmup climbs"
            )
        )

        // 20mm edge hangs 5x7 sec
        val edgeId = templateExerciseDao.insertTemplateExercise(
            TemplateExerciseEntity(
                workoutTemplateId = templateId,
                exerciseId = exerciseIds["edge_hangs"]!!,
                orderIndex = 1
            )
        )
        for (set in 1..5) {
            setPlanDao.insertSetPlan(
                SetPlanEntity(
                    templateExerciseId = edgeId,
                    setNumber = set,
                    targetSeconds = 7
                )
            )
        }

        // 7/3 repeaters x3 sets
        val repeaterId = templateExerciseDao.insertTemplateExercise(
            TemplateExerciseEntity(
                workoutTemplateId = templateId,
                exerciseId = exerciseIds["repeaters"]!!,
                orderIndex = 2
            )
        )
        for (set in 1..3) {
            setPlanDao.insertSetPlan(
                SetPlanEntity(
                    templateExerciseId = repeaterId,
                    setNumber = set,
                    targetReps = 6 // 6 reps of 7/3 protocol
                )
            )
        }

        templateExerciseDao.insertTemplateExercise(
            TemplateExerciseEntity(
                workoutTemplateId = templateId,
                exerciseId = exerciseIds["bouldering"]!!,
                orderIndex = 3,
                notes = "3-4 hard attempts V5-V6"
            )
        )

        // Lock-offs 3x each arm
        val lockoffId = templateExerciseDao.insertTemplateExercise(
            TemplateExerciseEntity(
                workoutTemplateId = templateId,
                exerciseId = exerciseIds["lockoffs"]!!,
                orderIndex = 4
            )
        )
        for (set in 1..3) {
            setPlanDao.insertSetPlan(
                SetPlanEntity(
                    templateExerciseId = lockoffId,
                    setNumber = set,
                    targetSeconds = 10
                )
            )
        }

        return templateId
    }

    private suspend fun createFridayRunTemplate(exerciseIds: Map<String, Long>): Long {
        val templateId = workoutTemplateDao.insertTemplate(
            WorkoutTemplateEntity(
                name = "Cardio: Run + Core",
                type = WorkoutType.AEROBIC,
                description = "Keep it simple. Week 1-2: 1 mile hard + 4x200m. Week 3-4: 2 mile steady + 6x200m.",
                estimatedDurationMinutes = 40,
                difficulty = 6
            )
        )

        templateExerciseDao.insertTemplateExercise(
            TemplateExerciseEntity(
                workoutTemplateId = templateId,
                exerciseId = exerciseIds["run"]!!,
                orderIndex = 0,
                notes = "Week 1-2: 1 mile hard, walk 5 min, 4x200m fast | Week 3-4: 2 mile steady, 6x200m fast"
            )
        )

        // Hollow hold 3x max
        val hollowId = templateExerciseDao.insertTemplateExercise(
            TemplateExerciseEntity(
                workoutTemplateId = templateId,
                exerciseId = exerciseIds["hollow_hold"]!!,
                orderIndex = 1
            )
        )
        for (set in 1..3) {
            setPlanDao.insertSetPlan(
                SetPlanEntity(
                    templateExerciseId = hollowId,
                    setNumber = set,
                    targetSeconds = 30
                )
            )
        }

        // Side plank 3x each
        val sidePlankId = templateExerciseDao.insertTemplateExercise(
            TemplateExerciseEntity(
                workoutTemplateId = templateId,
                exerciseId = exerciseIds["side_plank"]!!,
                orderIndex = 2
            )
        )
        for (set in 1..3) {
            setPlanDao.insertSetPlan(
                SetPlanEntity(
                    templateExerciseId = sidePlankId,
                    setNumber = set,
                    targetSeconds = 30
                )
            )
        }

        return templateId
    }

    private suspend fun createSaturdayAthleticTemplate(exerciseIds: Map<String, Long>): Long {
        val templateId = workoutTemplateDao.insertTemplate(
            WorkoutTemplateEntity(
                name = "Gym: Full Body",
                type = WorkoutType.POWER,
                description = "Keep it clean and sharp. (Skip on week 4 deload)",
                estimatedDurationMinutes = 50,
                difficulty = 7
            )
        )

        // Power cleans or KB swings 4x3-5
        val powerCleanId = templateExerciseDao.insertTemplateExercise(
            TemplateExerciseEntity(
                workoutTemplateId = templateId,
                exerciseId = exerciseIds["power_cleans"]!!,
                orderIndex = 0
            )
        )
        for (set in 1..4) {
            setPlanDao.insertSetPlan(
                SetPlanEntity(
                    templateExerciseId = powerCleanId,
                    setNumber = set,
                    targetReps = 5,
                    targetWeight = 95f,
                    rpe = 7
                )
            )
        }

        // Overhead press 4x5
        val ohpId = templateExerciseDao.insertTemplateExercise(
            TemplateExerciseEntity(
                workoutTemplateId = templateId,
                exerciseId = exerciseIds["overhead_press"]!!,
                orderIndex = 1
            )
        )
        for (set in 1..4) {
            setPlanDao.insertSetPlan(
                SetPlanEntity(
                    templateExerciseId = ohpId,
                    setNumber = set,
                    targetReps = 5,
                    targetWeight = 95f,
                    rpe = 7
                )
            )
        }

        // Pull-ups 3x max
        val pullupId = templateExerciseDao.insertTemplateExercise(
            TemplateExerciseEntity(
                workoutTemplateId = templateId,
                exerciseId = exerciseIds["pullups"]!!,
                orderIndex = 2
            )
        )
        for (set in 1..3) {
            setPlanDao.insertSetPlan(
                SetPlanEntity(
                    templateExerciseId = pullupId,
                    setNumber = set,
                    targetReps = 10
                )
            )
        }

        // Hip thrusts 3x8
        val hipId = templateExerciseDao.insertTemplateExercise(
            TemplateExerciseEntity(
                workoutTemplateId = templateId,
                exerciseId = exerciseIds["hip_thrusts"]!!,
                orderIndex = 3
            )
        )
        for (set in 1..3) {
            setPlanDao.insertSetPlan(
                SetPlanEntity(
                    templateExerciseId = hipId,
                    setNumber = set,
                    targetReps = 8,
                    targetWeight = 135f,
                    rpe = 7
                )
            )
        }

        // Farmer carry 3 rounds
        templateExerciseDao.insertTemplateExercise(
            TemplateExerciseEntity(
                workoutTemplateId = templateId,
                exerciseId = exerciseIds["farmer_carry"]!!,
                orderIndex = 4,
                notes = "3 rounds, 40-60 yards heavy"
            )
        )

        templateExerciseDao.insertTemplateExercise(
            TemplateExerciseEntity(
                workoutTemplateId = templateId,
                exerciseId = exerciseIds["rice_bucket"]!!,
                orderIndex = 5,
                notes = "10 min mobility work"
            )
        )

        return templateId
    }
}

