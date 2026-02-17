# How to Test in Android Emulator - Quick Guide

## 🚀 Quick Start (First Time Setup)

### Step 1: Create an Emulator (One-time setup)

**In Android Studio:**

1. Click the **Device Manager** icon in the top-right toolbar (phone icon)
   - Or: Tools → Device Manager

2. Click **"Create Device"**

3. **Select Hardware:**
   - Choose: **Pixel 5** (recommended)
   - Click **Next**

4. **Select System Image:**
   - Click **"Download"** next to **API 34 (UpsideDownCake)**
   - Wait for download (~500 MB)
   - Select the downloaded image
   - Click **Next**

5. **Verify Configuration:**
   - AVD Name: `Pixel_5_API_34` (or whatever you prefer)
   - Click **Finish**

---

## ▶️ Running the App (Every Time)

### Method 1: Using the Toolbar (Easiest)

1. **Select Device:**
   - Top toolbar: Click device dropdown
   - Select your emulator (e.g., "Pixel 5 API 34")

2. **Click the Green Play Button** ▶️
   - Or press **Shift + F10**

3. **Wait:**
   - First launch takes 1-2 minutes (emulator boots)
   - Subsequent launches: 10-30 seconds
   - You'll see the Android home screen

4. **App Launches Automatically**
   - You should see "Hello Android!" text

### Method 2: Using Menu

1. Run → Run 'app'
2. Select device
3. Click OK

---

## ✅ What You Should See (Current State)

Right now, the app shows:
- White/dark screen (depending on theme)
- Text: "Hello Android!"

**This means it works!** ✅

---

## 🔧 Next: Add Database Seeding

To actually test the workout features, we need to seed the database first.

### Update MainActivity.kt:

I'll update the code to:
1. Seed database on first launch
2. Show a simple list of workout templates
3. Display a message when seeding completes

---

## 🐛 Troubleshooting Emulator Issues

### "Emulator won't start"
**Solutions:**
1. Tools → Device Manager → Right-click emulator → Cold Boot Now
2. Increase RAM: Edit emulator → Advanced Settings → RAM: 2048 MB
3. Enable Hardware Acceleration:
   - Windows: Ensure Hyper-V or HAXM is installed
   - Settings → SDK Tools → Intel x86 Emulator Accelerator

### "App won't install"
**Solutions:**
1. Build → Clean Project
2. Build → Rebuild Project
3. Uninstall app from emulator manually
4. Try again

### "Gradle build failed"
**Solutions:**
1. File → Invalidate Caches → Restart
2. File → Sync Project with Gradle Files
3. Check errors in Build Output panel

### "Emulator is too slow"
**Solutions:**
1. Use a physical device instead (faster)
2. Reduce emulator resolution
3. Enable hardware graphics:
   - Edit emulator → Graphics → Hardware

---

## 📱 Using a Physical Device (Faster!)

### Setup:

1. **Enable Developer Options on your phone:**
   - Settings → About Phone
   - Tap "Build Number" 7 times
   - Go back → Developer Options appears

2. **Enable USB Debugging:**
   - Developer Options → USB Debugging → ON

3. **Connect Phone via USB:**
   - Android Studio will detect it
   - Allow USB debugging on phone

4. **Select Phone in Device Dropdown**
   - Click Run ▶️

**Much faster than emulator!** Recommended for daily testing.

---

## 🧪 Testing Workflow

### When you make code changes:

1. **Save files** (Ctrl + S)
2. **Build** (Ctrl + F9) - optional, catches errors early
3. **Run** (Shift + F10)
4. **Wait for install** (5-10 seconds)
5. **Test the change** in emulator/device

### Hot Reload (Compose):
- After app is running
- Make small UI changes
- Click **Apply Changes** (lightning bolt icon)
- **Instant update!** No reinstall needed

---

## 📊 Logcat (Viewing Logs)

**Essential for debugging!**

1. **Open Logcat panel** (bottom of Android Studio)
2. **Filter by package:** `com.example.adaptiveathlete`
3. **See debug logs, errors, crashes**

**Add logs to your code:**
```kotlin
import android.util.Log

Log.d("MainActivity", "Database seeded successfully!")
Log.e("MainActivity", "Error: $exception")
```

---

## 🎯 What to Test First

Once I update MainActivity with database seeding:

### Test 1: Database Seeding
1. Run app
2. Check Logcat for "Database seeded" message
3. Check for 7 workout templates loaded

### Test 2: View Database
1. View → Tool Windows → App Inspection
2. Select running app
3. Database Inspector tab
4. Expand `exercises` table
5. Should see 21 exercises

### Test 3: Unit Tests (Verify Progression Engine)
1. Right-click `app/src/test/java` folder
2. Run 'All Tests'
3. Should see 20+ tests pass

---

## 🚀 Ready to Go?

**Next steps:**

1. **I'll update MainActivity** to seed database and show template count
2. **You run the app** in emulator
3. **We verify** database is populated
4. **Then we build** the Home Screen UI

Let me update the code now! 💪

