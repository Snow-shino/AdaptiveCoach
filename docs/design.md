# Adaptive Athlete - Game Design Document (GDD)

## Executive Summary

Adaptive Athlete is an intelligent workout tracking app inspired by the Playbook app's UI/UX but with advanced AI-driven progressive overload, muscle balance evaluation, and personalized training recommendations. The app tracks every set, analyzes progress patterns, and automatically adjusts workout parameters to optimize growth.

---

## 1. Core Vision

### Mission Statement
Create an offline-first Android app that acts as an intelligent training partner, eliminating guesswork from progressive overload while providing actionable insights on muscle imbalances, recovery needs, and performance optimization.

### Inspiration
- **Playbook App**: Fast inline logging, minimal taps, clean workout table UI
- **Hunter x Hunter Greed Island Arc**: Progressive skill building, daily consistency rituals, measurable improvement
- **Netero's 10,000 Punches**: Daily non-negotiable practices that compound over time

---

## 2. User Goals & Success Metrics

### Primary Goals
1. **Track workouts effortlessly** - Log sets in <3 seconds per entry
2. **Progressive overload automation** - Never guess next workout's weight/reps
3. **Muscle balance insights** - Identify weak links and imbalances
4. **Flexibility integration** - Yoga follow-alongs tailored to climbing/strength goals
5. **Daily rituals** - Netero-style daily practices (dead hangs, rice bucket, etc.)
6. **Performance benchmarks** - Regular testing and progress visualization

### Success Metrics (90-Day Arc)
- 10-15 lb fat loss
- V6 boulder sends
- One-arm hang capability
- Campus board proficiency
- Increased vertical jump for dynos
- Sub-7 minute mile run time
- Balanced push/pull strength ratios
- Daily adherence >85%

---

## 3. Core Features (MVP → Full Vision)

### Phase 1: MVP - Core Workout Tracking

#### 3.1 Inline Workout Table
**Problem**: Apps like Strong require opening new screens per set (too slow)  
**Solution**: Playbook-style inline editing

**Features**:
- Start workout from template
- All sets visible in scrollable table
- Tap cell → numeric keypad bottom sheet
- Mark set done → autosave + start rest timer
- Auto-advance to next set
- Prefill last workout's actual values
- Real-time session timer

**UI Components**:
```
[Workout: Pull Day A]  [⏱ 00:23:45]  [Rest: 2:30]  [Finish]

Exercise: Weighted Pull-ups
Cues: Dead hang start, controlled tempo

Set | Target | Actual | Weight | Done
1   | 8 reps | [8]    | [+25]  | ✓
2   | 8 reps | [7]    | [+25]  | ✓  
3   | 8 reps | [ ]    | [+25]  | ☐  ← Current focus
4   | 8 reps | [ ]    | [+25]  | ☐
```

**Input Method**: Bottom-sheet numeric keypad (fast, thumb-friendly, no keyboard obstruction)

#### 3.2 Automatic Progressive Overload Engine

**Core Logic**:
```kotlin
// Weighted exercises
if (actualReps >= targetReps + 2) {
    nextWeight += 5 lb  // User exceeded targets
} else if (actualReps >= targetReps) {
    nextWeight += 2.5 lb  // Hit targets exactly
} else if (actualReps < targetReps - 2) {
    nextWeight -= 5 lb  // Couldn't complete, deload
} else {
    nextWeight unchanged  // Close enough, repeat
}

// Bodyweight exercises
if (actualReps >= targetReps + 3) {
    nextTargetReps += 2
} else if (actualReps >= targetReps + 1) {
    nextTargetReps += 1
}

// Timed holds (hangs, planks)
if (actualSeconds >= targetSeconds) {
    nextTargetSeconds += 5
}
```

**Safety Rules**:
- Max one progression step per session
- No increases if pain reported >3/10
- Auto-deload after 2 consecutive failures
- Rest day enforced if RPE >9 on 3+ sessions in 7 days

#### 3.3 Data Model (Room SQLite)

**Entities**:

```kotlin
@Entity
data class Exercise(
    @PrimaryKey val id: Long,
    val name: String,
    val type: ExerciseType,  // WEIGHTED, BODYWEIGHT, TIMED, CARDIO
    val muscleGroups: List<MuscleGroup>,  // PRIMARY, SECONDARY
    val equipmentNeeded: String?
)

@Entity
data class WorkoutTemplate(
    @PrimaryKey val id: Long,
    val name: String,
    val type: WorkoutType,  // STRENGTH, POWER_ENDURANCE, AEROBIC, etc.
    val estimatedDuration: Int  // minutes
)

@Entity
data class TemplateExercise(
    @PrimaryKey val id: Long,
    val workoutTemplateId: Long,
    val exerciseId: Long,
    val orderIndex: Int,
    val notes: String?  // Form cues
)

@Entity
data class SetPlan(
    @PrimaryKey val id: Long,
    val templateExerciseId: Long,
    val setNumber: Int,
    val targetReps: Int?,
    val targetSeconds: Int?,
    val targetWeight: Float?,
    val restSeconds: Int  // 90, 120, 180, etc.
)

@Entity
data class WorkoutSession(
    @PrimaryKey val id: Long,
    val templateId: Long,
    val startedAt: Long,
    val endedAt: Long?,
    val rpe: Int?,  // 1-10 subjective effort
    val notes: String?
)

@Entity
data class SetEntry(
    @PrimaryKey val id: Long,
    val sessionId: Long,
    val setPlanId: Long,
    val actualReps: Int?,
    val actualSeconds: Int?,
    val actualWeight: Float?,
    val isDone: Boolean,
    val completedAt: Long?
)

@Entity
data class DailyMetrics(
    @PrimaryKey val date: Long,
    val bodyweight: Float?,
    val waistInches: Float?,
    val sleepHours: Float?,
    val painLevel: Int?,  // 0-10
    val energyLevel: Int?,  // 1-10
    val adherence: Boolean,  // Did daily ritual
    val notes: String?
)
```

---

### Phase 2: Intelligent Analysis & Recommendations

#### 3.4 Muscle Balance Analyzer

**Problem**: Unbalanced training leads to injury and plateau  
**Solution**: Track push/pull ratios, left/right imbalances, muscle group volume

**Metrics**:
- Push/Pull volume ratio (target: 1:1.2)
- Left/Right strength deltas on unilateral exercises
- Muscle group volume distribution per week
- Core/accessory ratio
- Posterior chain emphasis

**Alerts**:
```
⚠️ Push:Pull ratio is 1.4:1 (too push-dominant)
→ Add 2 extra sets of rows this week

⚠️ Left leg 12% weaker on Bulgarian split squats
→ Start sets with left leg, add 1 extra set

✓ Posterior chain volume optimal for climbing goals
```

#### 3.5 Weekly Benchmark Tests

**Purpose**: Objective progress tracking independent of daily training  
**Frequency**: Every 4 weeks (end of each mesocycle)

**Test Battery**:

1. **Strength Tests**
   - Max pull-ups (bodyweight)
   - Weighted pull-up 1RM estimate (3RM test)
   - Dead hang max time (open hand)
   - Max hang weighted (10s test)
   - Plank max time (weighted)
   - Nordic curl max reps

2. **Power Tests**
   - Vertical jump (inches)
   - Broad jump (inches)
   - Campus board max reach
   - Max boulder grade (3 attempts)

3. **Endurance Tests**
   - ARC test (minutes at V2-V3 continuous)
   - 1-mile run time
   - 4x4 benchmark (V4 targets, rest 4min)

4. **Body Composition**
   - Bodyweight
   - Waist measurement
   - Progress photos (front, side, back)

**Data Export**: CSV for external analysis, charts for trends

#### 3.6 Daily Rituals (Netero System)

**Concept**: Non-negotiable daily practices that require <10 min but compound over time

**Implementation**:
- Separate "Daily Ritual" screen
- Check-in tracker (streak counter)
- Morning notification: "Complete your ritual"

**Default Rituals**:
1. **Dead Hang Ladder**: 5×20-30s (tendon health)
2. **Hollow Body Hold**: 3×30s (core)
3. **Scap Pull-ups**: 2×10 (shoulder health)
4. **Rice Bucket Grip Work**: 5 min (finger strength, injury prevention)
5. **Mobility Sequence**: 5 min (hips, ankles, shoulders)

**User can add custom rituals**:
- Morning walk (steps target)
- Meditation
- Foam rolling
- Breathwork

#### 3.7 Rice Bucket Grip Protocol

**Exercises** (30s each, 2 rounds):
1. Finger extensions (open/close)
2. Wrist flexion/extension
3. Radial/ulnar deviation
4. Figure-8s
5. Squeeze and lift rice

**Tracking**: Simple completion checkbox + optional pain/pump notes

---

### Phase 3: AI-Driven Coaching

#### 3.8 Progress Evaluation Bot

**Triggers**:
- After every workout (immediate feedback)
- Weekly summary (Sunday night)
- Monthly deep-dive (trend analysis)

**Analysis**:

1. **Per-Workout Feedback**
```
💪 Great session! You hit targets on 4/5 exercises.

⚡ Progression applied:
- Weighted Pull-ups: +2.5 lb next session
- Seal Rows: +1 rep target

⚠️ Watch: Bench press reps dropped 2 weeks in a row
→ Deload 10% next session
```

2. **Weekly Report**
```
📊 Week 3 Summary

Adherence: 6/6 workouts ✓
Volume: 24,500 lb (↑8% vs last week)
RPE avg: 7.2 (good training stress)

🎯 Progressions: 8 exercises increased
⚠️ Stalls: 2 exercises (Shoulder press, Nordics)

💡 Recommendation: Add extra shoulder volume on Pull Day
```

3. **Monthly Trends**
```
📈 Month 1 Complete

Bodyweight: 178 → 173 lb (-5 lb) ✓
Waist: 32" → 30.5" (-1.5") ✓
Pull-ups: 12 → 16 (+4) ✓
V-grade: V4 → V5 ✓

🔍 Insights:
- Posterior chain volume optimal
- Push exercises lagging (add 1 pressing day?)
- Sleep avg 6.8hr (target 7.5hr for recovery)

Next Arc: Increase intensity, reduce volume slightly
```

#### 3.9 Intelligent Recommendations

**Machine Learning (Future)**:
- Predict optimal rest days based on RPE + volume trends
- Suggest deloads before performance drops
- Identify exercises with diminishing returns
- Recommend exercise swaps for variety

**Rule-Based (MVP)**:
```kotlin
// Stall detection
if (exerciseNoProgressionFor(weeks = 3)) {
    recommend("Try a variation (tempo, pause, deficit)")
}

// Volume check
if (weeklyVolumeIncrease > 15%) {
    recommend("High volume spike - monitor recovery closely")
}

// Imbalance correction
if (pushPullRatio > 1.3) {
    recommend("Add 2 extra pulling sets this week")
}
```

---

### Phase 4: Yoga & Mobility Integration

#### 3.10 Tailored Yoga Sequences

**User Goals** → **Sequence Selection**:
- Climbing → Hip mobility + shoulder flexibility
- Strength → Recovery flows, myofascial release
- Fat loss → Power yoga, vinyasa flow
- Injury prevention → Restorative, yin yoga

**Content Strategy**:
- Curated YouTube links (partnerships with creators)
- In-app video player (future)
- Follow-along timer with pose cues

**Daily Recommendation**:
```
🧘 Today's Recovery: 15-min Hip Flow
Based on: 3 leg workouts this week, tight hip flexors noted

[Start Yoga] → Plays video + tracks completion
```

---

## 4. User Experience Flow

### First-Time Setup (5 min)

1. **Goals Selection**
   - Primary: Strength / Climbing / Fat Loss / Endurance
   - Secondary goals (check all): Mobility, Muscle Balance, Injury Prevention

2. **Current Stats**
   - Bodyweight
   - Max pull-ups
   - Boulder grade
   - Training frequency (days/week)

3. **Choose Program**
   - Auto-recommended based on goals
   - Or select from library:
     - 16-Week Greed Island Bootcamp (climbing focus)
     - Lean Athlete 12-Week (fat loss + strength)
     - Bodybuilding Mesocycle (hypertrophy)

4. **Set Daily Rituals**
   - Pre-selected based on goals
   - Customize timing (morning/evening)

### Daily Workflow

**Morning**:
1. Notification: "Complete daily ritual" (dead hangs, rice bucket, etc.)
2. Open app → Daily Ritual screen → Check off each item
3. Optional: Log bodyweight, energy level

**Workout Time**:
1. Home screen shows "Today: Pull Day A"
2. Tap "Start Workout"
3. App creates session, prefills last workout's values
4. Log sets inline (tap cell → keypad → done)
5. Rest timer starts automatically
6. Tap "Finish" when complete
7. Rate RPE, add notes
8. See instant feedback + next session updates

**Evening**:
1. Yoga recommendation appears if recovery day
2. Weekly check-in on Sundays

---

## 5. Technical Architecture

### Tech Stack
- **Language**: Kotlin
- **UI**: Jetpack Compose
- **Database**: Room (SQLite)
- **DI**: Hilt
- **Async**: Coroutines + Flow
- **Navigation**: Compose Navigation
- **Testing**: JUnit, Mockk, Compose UI tests

### Module Structure
```
app/
  data/
    db/        # Room entities, DAOs, Database
    repository/  # Data layer
  domain/
    model/     # Business entities
    usecase/   # Business logic (progression engine, analysis)
  ui/
    home/
    workout/   # Workout table screen
    history/
    analytics/
    settings/
  util/
    timer/
    extensions/
```

### Offline-First
- All data stored locally in Room
- No network required for core features
- Optional cloud sync (Phase 5)

---

## 6. Progression System Deep Dive

### Rep-Based Progression (Hypertrophy)
```
Week 1: 3×8 @ 100 lb
Week 2: 3×9 @ 100 lb  (reps++)
Week 3: 3×10 @ 100 lb
Week 4: 3×11 @ 100 lb
Week 5: 3×8 @ 105 lb  (weight++, reps reset)
```

### Strength Progression (Lower Rep)
```
Week 1: 5×5 @ 185 lb
Week 2: 5×5 @ 190 lb  (+5 lb if all sets hit)
Week 3: 5×5 @ 195 lb
Week 4: Deload 3×5 @ 165 lb
Week 5: 5×5 @ 200 lb
```

### Bodyweight Progression
```
Pull-ups:
Week 1: 3×8
Week 2: 3×9
Week 3: 3×10
Week 4: 3×8 @ +5 lb (add weight)
```

### Climbing Progression (Grade-Based)
```
Week 1: 4×V4 (volume)
Week 2: 3×V4 + 3×V5 attempts (intensity)
Week 3: 2×V5 sends
Week 4: Deload (V3-V4 only)
```

---

## 7. Benchmark System

### Pre-Program Assessment

**Strength**:
- [ ] Max pull-ups: ____
- [ ] Pull-up +____ lb × 5 reps
- [ ] Dead hang: ____ seconds
- [ ] Max hang +____ lb × 10s
- [ ] Plank (weighted): ____ seconds

**Power**:
- [ ] Vertical jump: ____ inches
- [ ] Broad jump: ____ inches
- [ ] Campus board: ____ (max rung)
- [ ] Max boulder: V____

**Endurance**:
- [ ] ARC test: ____ min continuous
- [ ] 1-mile run: ____ min
- [ ] 4x4 V4s: Complete? Y/N

**Body Comp**:
- [ ] Weight: ____ lb
- [ ] Waist: ____ inches
- [ ] Photos: Date ______

### 4-Week Retest Protocol
- Same tests, same order, same warm-up
- Export to CSV for comparison
- App highlights improvements

---

## 8. Non-Functional Requirements

### Performance
- App launch <2s
- Database queries <100ms
- Workout table scrolling 60fps
- Autosave debounced 300ms

### Accessibility
- Large tap targets (48dp min)
- High contrast mode
- TalkBack support
- Adjustable text sizes

### Data Privacy
- All data local by default
- Optional encrypted cloud backup
- No tracking/analytics without consent

---

## 9. Future Features (Post-MVP)

### Phase 5: Social & Content
- Creator partnerships (yoga instructors, coaches)
- In-app video library
- Community challenges
- Share workouts

### Phase 6: Wearables
- Wear OS watch app (quick logging)
- Heart rate integration
- Sleep tracking (Google Fit sync)

### Phase 7: AI Coaching v2
- Computer vision form checks (camera)
- Voice logging ("Completed 8 reps at 25 pounds")
- Predictive injury risk alerts

---

## 10. Success Criteria (90-Day Pilot)

### User Retention
- 30-day retention >60%
- Daily ritual adherence >80%
- Workout completion rate >85%

### Performance Outcomes
- Avg user strength gain: +15%
- Avg bodyweight change: -5 to -10 lb
- User-reported satisfaction: >4.5/5

### Technical
- Crash rate <1%
- Database performance: queries <100ms
- App size <50MB

---

## 11. Development Roadmap

### Milestone 1: Core Workout Flow (Week 1-2)
- [ ] Room database + seed data
- [ ] Start workout creates session
- [ ] Workout table UI with inline editing
- [ ] Autosave
- [ ] Finish workflow

### Milestone 2: Progression Engine (Week 3)
- [ ] Domain logic for all exercise types
- [ ] Unit tests (15+ scenarios)
- [ ] Apply progression to next session
- [ ] Prefill last actuals

### Milestone 3: Daily Rituals (Week 4)
- [ ] Ritual tracking screen
- [ ] Streak counter
- [ ] Morning notification

### Milestone 4: Analytics (Week 5-6)
- [ ] Weekly summary
- [ ] Muscle balance calculations
- [ ] Progress charts
- [ ] Benchmark test UI

### Milestone 5: Yoga Integration (Week 7)
- [ ] Curated video links
- [ ] Recommendation engine
- [ ] Completion tracking

### Milestone 6: Polish (Week 8)
- [ ] Onboarding flow
- [ ] Settings screen
- [ ] Export data
- [ ] Beta testing

---

## 12. Appendix: Sample Workout Templates

### Template: Pull Day A (Greed Island Program)
1. Scap Pull-ups: 4×8 (warm-up)
2. Weighted Pull-ups: 5×5
3. Seal Rows: 3×10 @ 20 reps final set
4. DB Lat Rows: 3×8
5. Face Pulls: 3×15
6. Hanging Leg Raises: 3×12
7. Hollow Body: 3×30s

### Template: Push Day A
1. Shoulder Prep Circuit: 3 rounds
2. Incline Bench: 6/6/6/20
3. Arnold Press: 3×8
4. Underhand Front Raise: 3×12
5. Shrugs: 3×12
6. Weighted Plank: 4×20s

### Template: Daily Ritual
1. Dead Hangs: 5×20s
2. Hollow Body: 3×30s
3. Scap Pull-ups: 2×10
4. Rice Bucket: 5 min
5. Hip Mobility: 5 min

---

**End of GDD v1.0**  
Last updated: [Date]  
Next review: After Milestone 2 completion

