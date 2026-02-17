# Testing the App Right Now - Simple Steps

## 🎯 What We're Testing

The app will:
1. Seed the database with 21 exercises and 7 workout templates
2. Display a count of loaded data
3. Show the list of workout templates

This proves the database and seed data are working! ✅

---

## 📱 Step-by-Step Testing

### **Step 1: Create an Emulator** (if you don't have one)

1. In Android Studio, click **Device Manager** (phone icon in top-right)
2. Click **"Create Device"**
3. Select **Pixel 5** → Next
4. Download **API 34** system image (if not already downloaded)
5. Click **Finish**

### **Step 2: Sync Gradle** (Important!)

1. Click **File → Sync Project with Gradle Files**
2. Wait 2-5 minutes for dependencies to download
3. Look for **"BUILD SUCCESSFUL"** in the Build panel

### **Step 3: Run the App**

1. In the top toolbar, select your emulator from the device dropdown
2. Click the **green play button ▶️** (or press **Shift + F10**)
3. Wait for emulator to boot (1-2 min first time)
4. App will install and launch automatically

### **Step 4: What You Should See**

The app will display:

```
🏋️ Adaptive Athlete

┌─────────────────────────┐
│   Database Status       │
│                         │
│ ✅ 21 exercises loaded  │
│ ✅ 7 workout templates  │
│     ready               │
└─────────────────────────┘

Workout Templates:
• Pull Day A - Strength
• Push Day A - Shoulders
• Posterior Chain Day
• Full Body Metabolic
• Daily Ritual (Netero Protocol)
• Aerobic Climbing Day
• Power Climbing Day

✨ Ready to build the UI!
```

**If you see this, it works!** 🎉

---

## 🔍 Verify Database in Android Studio

### Option 1: App Inspection (Easiest)

1. With app running, go to **View → Tool Windows → App Inspection**
2. Select your running app in dropdown
3. Click **Database Inspector** tab
4. Expand **adaptive_athlete_db**
5. Click **exercises** table → See all 21 exercises
6. Click **workout_templates** table → See all 7 templates

### Option 2: Logcat (See Logs)

1. Open **Logcat** panel (bottom of Android Studio)
2. Filter: Type `MainActivity` in search box
3. Look for:
   ```
   D/MainActivity: Seeding database...
   D/MainActivity: Database seeded successfully!
   ```

---

## 🧪 Run Unit Tests (Verify Progression Engine)

1. In Project view, navigate to: `app/src/test/java`
2. Right-click the `java` folder
3. Select **"Run 'All Tests'"**
4. Wait ~10 seconds
5. **Expected result:** All 20+ tests pass ✅

These tests verify the progression engine calculates weight/rep increases correctly.

---

## ❌ If Something Goes Wrong

### "Build Failed"
**Check:**
1. Did Gradle sync complete?
2. Any errors in Build Output panel?
3. Try: File → Invalidate Caches → Restart

### "App Crashes on Launch"
**Check Logcat for errors:**
1. Look for red text (errors)
2. Copy error message
3. Most common: Hilt dependency injection issue

**Common fix:**
- Build → Clean Project
- Build → Rebuild Project
- Run again

### "Database Not Seeding"
**Check Logcat:**
- Look for "Seeding database..." message
- Look for any exceptions/errors
- Database may already be seeded from previous run

**To reset:**
- Long-press app icon in emulator → App Info → Storage → Clear Data
- Run app again

### "Emulator Won't Start"
**Try:**
1. Device Manager → Cold Boot Now
2. Or create a new emulator (Pixel 5, API 33 or 34)
3. Ensure you have 4+ GB RAM allocated

---

## 🚀 Next Steps After Testing

Once you see the template list:

### **Option 1: I Build the Home Screen**
Say: **"Build the home screen"**

I'll create:
- HomeScreen.kt with clickable template cards
- HomeViewModel.kt to load templates
- Navigation to workout screen

### **Option 2: You Want to Code**
Say: **"Show me how to build the home screen"**

I'll provide step-by-step code for you to add.

### **Option 3: Dive Deeper**
Say: **"Show me the database"** or **"Explain the seed data"**

I'll show you how the database works and how to query it.

---

## 📊 Quick Reference

| Action | Shortcut |
|--------|----------|
| Run app | Shift + F10 |
| Build project | Ctrl + F9 |
| Stop app | Shift + F5 |
| Open Logcat | Alt + 6 |
| Sync Gradle | Ctrl + Shift + O |
| Device Manager | Ctrl + Shift + A → "Device Manager" |

---

## ✅ Success Criteria

You've succeeded when you see:
- ✅ Emulator running
- ✅ App installed and launched
- ✅ "21 exercises loaded" message
- ✅ "7 workout templates ready" message
- ✅ List of all workout template names
- ✅ No crashes or errors in Logcat

**When this works, the foundation is solid and we can build the real UI!** 💪

---

## 💬 What to Reply

After testing, tell me:
- **"It works!"** → Great! Let's build the home screen next
- **"Build failed: [error]"** → I'll help troubleshoot
- **"App crashes: [error]"** → I'll fix the issue

