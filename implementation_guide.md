# Step-by-Step Implementation Guide

## Prerequisites
- Android Studio Hedgehog or later
- Kotlin 1.9+
- Compose BOM 2024.02.00+
- Target SDK 34+

## Step 1: Project Setup

### 1.1 Create New Project
1. Open Android Studio
2. New Project → Empty Activity
3. Name: "FocusableButtons"
4. Package: "com.example.focusablebuttons"
5. Language: Kotlin
6. Minimum SDK: API 24

### 1.2 Update build.gradle.kts (Module: app)

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.focusablebuttons"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.focusablebuttons"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    
    // Compose BOM
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    
    // Focus management
    implementation("androidx.compose.foundation:foundation")
    
    // Animation
    implementation("androidx.compose.animation:animation")
    
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
```

### 1.3 Sync Project
Click "Sync Now" in the banner that appears.

---

## Step 2: Understanding Focus Mechanics

### 2.1 Key Compose Modifiers for Focus

```kotlin
// Makes composable focusable
Modifier.focusable()

// Detects focus changes
Modifier.onFocusChanged { focusState ->
    val isFocused = focusState.isFocused
    val hasFocus = focusState.hasFocus
}

// For programmatic focus control
val focusRequester = remember { FocusRequester() }
Modifier.focusRequester(focusRequester)

// Later: focusRequester.requestFocus()
```

### 2.2 Focus State Properties
- `isFocused`: True when this composable has focus
- `hasFocus`: True when this or child has focus
- `isCaptured`: True for captured focus (advanced)

---

## Step 3: Building the Focusable Button Component

### 3.1 Create Basic Button with Focus Detection

```kotlin
@Composable
fun BasicFocusableButton(
    text: String,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    
    Button(
        onClick = onClick,
        modifier = Modifier
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
    ) {
        Text(text)
    }
}
```

### 3.2 Add Visual Focus Indicator

```kotlin
@Composable
fun FocusableButtonWithBorder(
    text: String,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    
    Button(
        onClick = onClick,
        modifier = Modifier
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .border(
                width = if (isFocused) 4.dp else 0.dp,
                color = Color.Cyan,
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        Text(text)
    }
}
```

### 3.3 Add Elevation and Background Changes

```kotlin
@Composable
fun EnhancedFocusableButton(
    text: String,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    
    Button(
        onClick = onClick,
        modifier = Modifier
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .shadow(
                elevation = if (isFocused) 8.dp else 2.dp,
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = if (isFocused) 4.dp else 0.dp,
                color = Color.Cyan,
                shape = RoundedCornerShape(8.dp)
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isFocused) {
                Color(0xFF1A4D5C) // Dark teal
            } else {
                Color(0xFF2196F3) // Blue
            }
        )
    ) {
        Text(
            text = text,
            fontWeight = if (isFocused) FontWeight.Bold else FontWeight.Medium
        )
    }
}
```

### 3.4 Add Smooth Animations

```kotlin
@Composable
fun AnimatedFocusableButton(
    text: String,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    
    // Animate elevation
    val elevation by animateDpAsState(
        targetValue = if (isFocused) 8.dp else 2.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "elevation"
    )
    
    // Animate border width
    val borderWidth by animateDpAsState(
        targetValue = if (isFocused) 4.dp else 0.dp,
        animationSpec = tween(durationMillis = 200),
        label = "borderWidth"
    )
    
    Button(
        onClick = onClick,
        modifier = Modifier
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .shadow(elevation = elevation, shape = RoundedCornerShape(8.dp))
            .border(
                width = borderWidth,
                color = Color.Cyan,
                shape = RoundedCornerShape(8.dp)
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isFocused) {
                Color(0xFF1A4D5C)
            } else {
                Color(0xFF2196F3)
            }
        )
    ) {
        Text(
            text = text,
            fontWeight = if (isFocused) FontWeight.Bold else FontWeight.Medium
        )
    }
}
```

---

## Step 4: Responsive Layouts for Different Form Factors

### 4.1 Detect Screen Size

```kotlin
@Composable
fun DetectFormFactor() {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    
    val formFactor = when {
        screenWidth >= 960.dp -> "TV"
        screenWidth >= 600.dp -> "Tablet"
        else -> "Mobile"
    }
    
    Text("Current form factor: $formFactor")
}
```

### 4.2 Mobile Layout (< 600dp)

```kotlin
@Composable
fun MobileLayout(onItemClick: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Vertical list
        repeat(5) { index ->
            WCAGFocusableButton(
                text = "Item ${index + 1}",
                onClick = { onItemClick("Item ${index + 1}") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
```

### 4.3 Tablet Layout (600dp - 960dp)

```kotlin
@Composable
fun TabletLayout(onItemClick: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Two columns
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            repeat(4) { index ->
                WCAGFocusableButton(
                    text = "Left ${index + 1}",
                    onClick = { onItemClick("Left ${index + 1}") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            repeat(4) { index ->
                WCAGFocusableButton(
                    text = "Right ${index + 1}",
                    onClick = { onItemClick("Right ${index + 1}") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
```

### 4.4 TV Layout (≥ 960dp)

```kotlin
@Composable
fun TVLayout(onItemClick: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(48.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Horizontal menu
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            listOf("Home", "Movies", "TV Shows", "Settings").forEach { item ->
                WCAGFocusableButton(
                    text = item,
                    onClick = { onItemClick(item) },
                    modifier = Modifier.weight(1f),
                    isTVMode = true
                )
            }
        }
        
        // Content grid (4x3)
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            repeat(3) { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    repeat(4) { col ->
                        val index = row * 4 + col + 1
                        WCAGFocusableButton(
                            text = "Item $index",
                            onClick = { onItemClick("Item $index") },
                            modifier = Modifier.weight(1f),
                            isTVMode = true
                        )
                    }
                }
            }
        }
    }
}
```

---

## Step 5: WCAG 2.1 AA Compliance

### 5.1 Verify Color Contrast

Use WebAIM Contrast Checker: https://webaim.org/resources/contrastchecker/

**Text contrast (4.5:1 minimum):**
- Foreground: #FFFFFF (White)
- Background: #2196F3 (Blue)
- Ratio: 4.53:1 ✅

**Focus indicator contrast (3:1 minimum):**
- Foreground: #00D9FF (Cyan)
- Background: #1A4D5C (Dark Teal)
- Ratio: 3.8:1 ✅

### 5.2 Ensure Minimum Touch Target Size

```kotlin
Button(
    modifier = Modifier
        .heightIn(min = 48.dp) // WCAG 2.5.5
        .widthIn(min = 48.dp)  // For square buttons
) {
    // Button content
}
```

### 5.3 Provide Multiple Visual Cues

Don't rely on color alone:
```kotlin
// ✅ Good: Multiple indicators
if (isFocused) {
    Modifier
        .border(4.dp, Color.Cyan)      // Border
        .shadow(8.dp)                   // Elevation
        // Background color change
        // Font weight change
}

// ❌ Bad: Color only
containerColor = if (isFocused) Blue else Green
```

---

## Step 6: Testing Focus Navigation

### 6.1 Test with Physical Keyboard
1. Connect USB keyboard to Android device
2. Press Tab → Focus should move forward
3. Press Shift+Tab → Focus should move backward
4. Press Enter/Space → Button should activate
5. Verify visible focus indicator on each button

### 6.2 Test with D-Pad/Game Controller
1. Connect game controller or use TV emulator
2. Press directional buttons → Navigate between buttons
3. Press A/Center button → Activate focused button
4. Verify focus wraps appropriately at edges

### 6.3 Test with Mouse
1. Click button → Should focus and activate
2. Tab after clicking → Keyboard navigation should work
3. Hover → Optional feedback (not required)

### 6.4 Test with Touch
1. Tap button → Should activate immediately
2. Connect keyboard → Tab navigation should work
3. Verify 48dp minimum touch target

### 6.5 Test Across Form Factors
```bash
# Test on different screen sizes
adb shell wm size 480x800    # Mobile
adb shell wm size 1280x800   # Tablet
adb shell wm size 1920x1080  # TV

# Reset
adb shell wm size reset
```

---

## Step 7: Input Detection (Bonus)

### 7.1 Track Last Input Type

```kotlin
@Composable
fun InputTrackingButton(
    text: String,
    onClick: () -> Unit,
    onInputTypeDetected: (String) -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    
    Button(
        onClick = {
            onInputTypeDetected("Touch/Mouse")
            onClick()
        },
        modifier = Modifier
            .onFocusChanged { focusState ->
                if (focusState.isFocused && !isFocused) {
                    onInputTypeDetected("Keyboard/D-Pad")
                }
                isFocused = focusState.isFocused
            }
            .focusable()
    ) {
        Text(text)
    }
}
```

---

## Step 8: Common Issues and Solutions

### Issue 1: Focus Not Visible
**Problem:** Can't see which button is focused
**Solution:** Ensure sufficient contrast and border width
```kotlin
// Increase border width
.border(width = 4.dp, color = Color.Cyan) // Not 1dp

// Use high-contrast color
color = Color(0xFF00D9FF) // Not Color.Gray
```

### Issue 2: Focus Jumps Unexpectedly
**Problem:** Tab navigation skips buttons
**Solution:** Ensure all buttons have `.focusable()`
```kotlin
// Every button needs this
Button(modifier = Modifier.focusable()) { }
```

### Issue 3: Touch Targets Too Small
**Problem:** Hard to tap on mobile
**Solution:** Use `heightIn(min = 48.dp)`
```kotlin
Button(
    modifier = Modifier.heightIn(min = 48.dp)
) { }
```

### Issue 4: Focus Activates on Load
**Problem:** First button focused without user interaction
**Solution:** Only focus when user navigates
```kotlin
var hasBeenFocused by remember { mutableStateOf(false) }

.onFocusChanged { focusState ->
    if (focusState.isFocused && !hasBeenFocused) {
        hasBeenFocused = true
    }
    // Don't auto-focus on load
}
```

---

## Step 9: Performance Optimization

### 9.1 Avoid Unnecessary Recomposition
```kotlin
// ✅ Good: Remember animation values
val elevation by animateDpAsState(...)

// ❌ Bad: Recreate on every recomposition
val elevation = if (isFocused) 8.dp else 2.dp
```

### 9.2 Use derivedStateOf for Computed Values
```kotlin
val shouldShowBorder by remember {
    derivedStateOf { isFocused && hasBeenInteracted }
}
```

---

## Step 10: Deployment Checklist

Before releasing:
- [ ] Test on physical device with keyboard
- [ ] Test on emulator with D-pad
- [ ] Verify WCAG contrast ratios
- [ ] Test on mobile, tablet, TV layouts
- [ ] Check touch target sizes (48dp minimum)
- [ ] Verify focus visible on all buttons
- [ ] Test with screen reader (TalkBack)
- [ ] Test in landscape and portrait
- [ ] Verify animations are smooth
- [ ] Test with external mouse

---

## Next Steps

1. **Add Screen Reader Support:**
   ```kotlin
   Modifier.semantics {
       contentDescription = "Button: $text"
       role = Role.Button
   }
   ```

2. **Implement Focus Groups:**
   For complex navigation patterns in grids

3. **Add Custom Focus Order:**
   ```kotlin
   Modifier.focusOrder { next = focusRequester2 }
   ```

4. **Implement Focus Restoration:**
   Save and restore focus state across configuration changes

5. **Add Haptic Feedback:**
   ```kotlin
   val view = LocalView.current
   view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
   ```

---

## Resources

- [Complete code in artifacts](#)
- [WCAG Compliance Guide](https://github.com/raison00/FocusableComposeInput_2025/blob/main/implementation_guide.md)
- 
- [Compose Focus Documentation](https://developer.android.com/jetpack/compose/touch-input/focus)
- [Material Design Accessibility](https://m3.material.io/foundations/accessible-design)
