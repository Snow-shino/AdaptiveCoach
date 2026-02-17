package com.example.adaptiveathlete.data.model

enum class ExerciseType {
    WEIGHTED,      // Bench press, weighted pull-ups, etc.
    BODYWEIGHT,    // Push-ups, pull-ups, etc.
    TIMED,         // Planks, dead hangs, etc.
    CARDIO,        // Running, rowing, etc.
    CLIMBING       // Bouldering, route climbing
}

enum class MuscleGroup {
    // Upper Body
    CHEST,
    BACK,
    SHOULDERS,
    BICEPS,
    TRICEPS,
    FOREARMS,

    // Core
    ABS,
    OBLIQUES,
    LOWER_BACK,

    // Lower Body
    QUADS,
    HAMSTRINGS,
    GLUTES,
    CALVES,

    // Full Body
    FULL_BODY
}

enum class WorkoutType {
    STRENGTH,           // Heavy lifting, low reps
    POWER,              // Explosive movements
    POWER_ENDURANCE,    // 4x4s, circuits
    AEROBIC,            // Long easy climbing, cardio
    HYPERTROPHY,        // Muscle building, higher volume
    SKILL,              // Technique work, mobility
    RECOVERY            // Light movement, yoga
}

enum class EquipmentType {
    BARBELL,
    DUMBBELL,
    KETTLEBELL,
    CABLE,
    MACHINE,
    BODYWEIGHT,
    HANGBOARD,
    CAMPUS_BOARD,
    CLIMBING_WALL,
    PULL_UP_BAR,
    RESISTANCE_BAND,
    NONE
}

