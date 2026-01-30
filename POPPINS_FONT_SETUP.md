# Adding Poppins Font to LankaSmartMart

## Steps to Add Poppins Font Files

### 1. Download Poppins Font
Visit: https://fonts.google.com/specimen/Poppins

Click "Download family" button

### 2. Extract Font Files
From the downloaded ZIP, you need these 4 files:
- `Poppins-Regular.ttf` (Weight: 400)
- `Poppins-Medium.ttf` (Weight: 500)
- `Poppins-SemiBold.ttf` (Weight: 600)
- `Poppins-Bold.ttf` (Weight: 700)

### 3. Rename Files (Important!)
Rename the files to lowercase with underscores:
- `Poppins-Regular.ttf` → `poppins_regular.ttf`
- `Poppins-Medium.ttf` → `poppins_medium.ttf`
- `Poppins-SemiBold.ttf` → `poppins_semibold.ttf`
- `Poppins-Bold.ttf` → `poppins_bold.ttf`

### 4. Copy to Font Directory
Copy all 4 renamed files to:
`app/src/main/res/font/`

### 5. Update Type.kt
After copying the font files, edit:
`app/src/main/java/com/example/lankasmartmart/ui/theme/Type.kt`

**Uncomment these lines** (around line 18-28):
```kotlin
import androidx.compose.ui.text.font.Font
import com.example.lankasmartmart.R

val PoppinsFontFamily = FontFamily(
    Font(R.font.poppins_regular, FontWeight.Normal),
    Font(R.font.poppins_medium, FontWeight.Medium),
    Font(R.font.poppins_semibold, FontWeight.SemiBold),
    Font(R.font.poppins_bold, FontWeight.Bold)
)
```

**Then change line 33** from:
```kotlin
val AppFontFamily = FontFamily.Default
```

To:
```kotlin
val AppFontFamily = PoppinsFontFamily
```

### 6. Rebuild the App
Run: `./gradlew clean assembleDebug`

That's it! Your entire app will now use Poppins font family across all screens and components.

## What's Already Done
✅ Typography system updated with all Material 3 text styles
✅ All text styles configured to use AppFontFamily variable
✅ Font family structure ready for Poppins integration

## What You Need to Do
1. Download Poppins fonts (4 files)
2. Rename and copy to `app/src/main/res/font/`
3. Uncomment Poppins setup in Type.kt
4. Rebuild app
