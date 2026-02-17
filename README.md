# Adaptive Athlete

> Fast workout tracking with rule-based progression and adaptive planning

An Android app for busy athletes who want to log workouts in under 60 seconds and have their training plan automatically adjust based on adherence and performance.

---

## 🎯 Purpose

Build a fast daily workout tracker (inspired by Playbook's table entry UX) that generates and adjusts training plans based on real adherence and performance data.

**Target User**: Busy lifter/climber who wants a structured plan, logs quickly, and wants automatic progression without overthinking.

---

## ✨ Core Outcomes

- ✅ **"I can log today's workout in under 60 seconds"**
- ✅ **"My plan adapts next week if I miss sessions or underperform"**
- ✅ **"I see progress in 3 metrics, not 30 charts"**

---

## 📱 Features

### Today View - Fast Table Entry
- **Single table** for the day's workout
- **Inline entry** - no drilling into set details
- **Swipe gestures**: Left to delete set, Right to duplicate
- **Auto-fill**: Next set defaults to last set's values
- **One-tap**: Add set, toggle complete, adjust weight/reps/RPE

**Example:**
```
Exercise     | Set | Target      | Actual      | RPE | Done
Squat        | 1   | 5 @ 135 lb  | 5 @ 135 lb  | 8   | ✅
Bench Press  | 1   | 8 @ 185 lb  | 8 @ 185 lb  | 7   | ✅
```

### V6 Athlete Program
A pre-built **4-week training block** designed to build strength, power, and technique for V6 climbing:

**Weekly Structure:**
- **Sunday** - **Bouldering: Volume + Skill** - Movement efficiency & endurance
- **Monday** - **Gym: Upper Body** - Heavy pulling (weighted pull-ups, bench, rows)
- **Tuesday** - **Bouldering: Power** - Limit boulders, dynos, campus board
- **Wednesday** - **Gym: Legs** - Squats, RDL, box jumps for explosive power
- **Thursday** - **Bouldering: Finger Strength** - Hangboard protocol, V5-V6 attempts
- **Friday** - **Cardio: Run + Core** - Cardio intervals + core work
- **Saturday** - **Gym: Full Body** - Power cleans, OHP, farmer carries

**Progression:**
- Week 1-2: Baseline, add small weight
- Week 3: Hardest week (peak volume)
- Week 4: Deload (20% volume reduction, Saturday off)

**Daily Ritual:** Rice bucket work (3-5 min), 10k steps

Each workout displays as **"Activity Type: Focus"** format for quick calendar viewing.

### Plan View
- **Current week** (Mon-Sun) at a glance
- Each day shows workout title + primary lifts
- **Edit**: Reorder workouts, swap days, skip sessions
- **Week-by-week progression** tracking

### Program Quiz (Coming Soon)
Users will answer questions to determine which program fits their goals:
- Training days per week
- Experience level
- Primary goals (strength, V6 climbing, fat loss, etc.)
- Available equipment
- Recovery capacity

### Progress Tracking
Simple, actionable metrics:
- **Adherence %** (last 7 and 30 days)
- **Estimated 1RM trend** for 1-3 main lifts
- **Weekly volume trend**

### Adaptive Planning
The app automatically adjusts your plan weekly based on:
- **Adherence < 70%**: Reduce volume 10-20%, simplify workouts
- **Consistently high RPE (≥9)**: Reduce load 2.5-5%
- **Prescribed loads hit with RPE ≤8 for 2+ weeks**: Increase load 2.5-5% or add 1 rep

---

## 🏗️ Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose (Material 3)
- **Architecture**: MVVM
- [x] **V6 Athlete Program** - Complete 4-week training block
- [x] Program scheduling system
- [x] 7 workout templates with exercises and sets
- **Database**: Room (SQLite)
- **DI**: Hilt
- **Build**: Gradle (Kotlin DSL)
- [ ] Plan view showing scheduled workouts
- [ ] Program quiz to assign training plans

## 🗄️ Data Model
- [ ] Weekly adaptation rules engine

### Core Entities
- `UserProfile`: goal, daysPerWeek, units, equipment
- `Program`: startDate, templateType, rulesVersion
- [ ] Additional programs (Beginner, Intermediate, Hypertrophy)
- `WorkoutDay`: date, title, status (planned, done, skipped)
- `Exercise`: name, category, equipment, defaultRest
- `PlannedSet`: targetReps, targetWeight, targetRPE
- `LoggedSet`: actualReps, actualWeight, actualRPE, timestamp, completed
- `WorkoutSummary`: volume, tonnage, PR flags, notes
- `ProgressMetric`: e1RM by lift, adherence, volume

---

## 🚀 Development Roadmap

### ✅ Completed
- [x] Project structure and dependencies
- [x] Room database with DAOs
- [x] Seed data for exercises and templates
- [x] Home screen with benchmark entry points
- [x] Navigation setup

### 🔨 In Progress
- [ ] Today view with fast table entry
- [ ] Plan generator (3-day and 4-day templates)
- [ ] Weekly adaptation rules engine

### 📋 Upcoming
- [ ] Progress metrics calculation
- [ ] Progress view with charts
- [ ] Export functionality (CSV/JSON)
- [ ] Settings screen

---

## 📐 Architecture

```
app/
├── data/
│   ├── local/
│   │   ├── entities/       # Room entities
│   │   ├── dao/            # Data access objects
│   │   └── AppDatabase.kt
│   └── repository/         # Data layer abstraction
├── domain/
│   ├── model/              # Domain models
│   └── usecase/            # Business logic
├── ui/
│   ├── screens/            # Composable screens
│   ├── components/         # Reusable UI components
│   └── theme/              # Material 3 theming
└── di/                     # Hilt modules
```

---

## 🎨 Design Principles

1. **Speed First**: Log a workout in <60 seconds
2. **No Nested Screens**: Everything accessible from the main table
3. **Credible Progression**: Rule-based, not pretend-AI
4. **Offline-First**: All data stored locally
5. **Portfolio Quality**: Clean code, tested, documented

---

## 🧪 Testing

Run unit tests:
```bash
./gradlew test
```

Run on emulator:
```bash
./gradlew installDebug
```

---

## 📄 License

This project is for portfolio purposes.

---

## 👤 Author

Taylor - CS Student specializing in Android development

**Portfolio Positioning:**
- Built cross-platform app with offline-first SQLite persistence
- Designed high-speed table-based logging UX to reduce friction  
- Implemented rule-based weekly progression engine using adherence and performance signals
- Built metrics pipeline for e1RM, volume, and adherence trends

