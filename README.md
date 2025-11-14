# Adaptive UI Focusable Button Input in Compose 2025
Testing and documentation for Adaptive UI across devices and form factors

# Input Types Comparison Guide

## Overview
This guide shows the differences in how focus behaves across different input methods and device types.

---

## 1. Touch Input (Mobile/Tablet)

### Behavior
- **Activation:** Immediate on tap
- **Focus:** Briefly shows on touch, then released
- **Navigation:** Direct pointing to any button
- **Visual Feedback:** Ripple effect + brief focus highlight

### User Experience
```
User touches button → Focus + Click happen simultaneously
Button activates immediately → Focus released
No persistent focus state (unless keyboard connected)
```

### Code Pattern
```kotlin
Button(
    onClick = {
        // Activates immediately on touch
        onItemSelected(item)
    }
) {
    Text(text)
}
```

### Visual States
```
┌─────────────────┐
│   Touch Start   │ ← Ripple begins
├─────────────────┤
│  Momentary      │ ← Brief focus flash
│  Focus          │
├─────────────────┤
│   Touch End     │ ← Action executes
└─────────────────┘

Timeline: ~100-300ms total
```

---

## 2. Keyboard Input (Desktop/Laptop)

### Behavior
- **Activation:** Tab to focus, Enter/Space to activate
- **Focus:** Persistent until moved to another element
- **Navigation:** Sequential (Tab/Shift+Tab) or directional (arrows)
- **Visual Feedback:** Border + elevation + color change (persistent)

### User Experience
```
User presses Tab → Focus moves to button
Button shows focus indicator → User sees cyan border
User presses Enter → Button activates
Focus remains on button until Tab pressed again
```

### Code Pattern
```kotlin
Button(
    onClick = { onItemSelected(item) },
    modifier = Modifier
        .onFocusChanged { focusState ->
            isFocused = focusState.isFocused
            // Focus state persists
        }
        .focusable()
) {
    Text(text)
}
```

### Visual States
```
┌─────────────────┐
│    Default      │ ← No focus
└─────────────────┘
         ↓ Tab
┏━━━━━━━━━━━━━━━━━┓
┃    Focused      ┃ ← Cyan border, elevated
┃  (Persistent)   ┃    Darker background
┗━━━━━━━━━━━━━━━━━┛    Bold text
         ↓ Enter/Space
┏━━━━━━━━━━━━━━━━━┓
┃    Activated    ┃ ← Action executes
┃  (Still Focused)┃    Focus remains
┗━━━━━━━━━━━━━━━━━┛

Timeline: Focus persists indefinitely
```

### Navigation Pattern
```
Button 1 [Focused] ←─┐
Button 2             │ Tab
Button 3             │
Button 4             ↓
Button 5 ────────────┘

Shift+Tab reverses direction
```

---

## 3. Mouse Input (Desktop/Laptop)

### Behavior
- **Activation:** Click immediately activates
- **Focus:** Gains focus on click, persistent afterward
- **Navigation:** Direct pointing + optional Tab
- **Visual Feedback:** Hover effect + focus indicator after click

### User Experience
```
User hovers → Optional hover feedback
User clicks → Focus + activation simultaneously
Focus persists → Can now use keyboard navigation
```

### Code Pattern
```kotlin
Button(
    onClick = {
        // Click focuses AND activates
        onItemSelected(item)
    },
    modifier = Modifier
        .focusable() // Receives focus on click
        .hoverable(enabled = true) // Optional hover detection
) {
    Text(text)
}
```

### Visual States
```
┌─────────────────┐
│    Default      │
└─────────────────┘
         ↓ Hover
┌─────────────────┐
│   Hover State   │ ← Optional: cursor pointer
│  (Subtle hint)  │    Slight scale/brightness
└─────────────────┘
         ↓ Click
┏━━━━━━━━━━━━━━━━━┓
┃ Clicked+Focused ┃ ← Action executes
┃  (Persistent)   ┃    Focus remains
┗━━━━━━━━━━━━━━━━━┛    Can now use Tab

Timeline: 
- Hover: Immediate
- Click: ~100ms
- Focus: Persistent after click
```

---

## 4. D-Pad/Remote Input (TV/Game Console)

### Behavior
- **Activation:** Center/A button after navigation
- **Focus:** Persistent and highly visible (pulsing)
- **Navigation:** Directional (up/down/left/right)
- **Visual Feedback:** Amber border + glow + pulse animation

### User Experience
```
User presses Right → Focus moves to next button
Large amber border appears → Pulsing animation indicates focus
User presses Center → Button activates
Focus remains → Can continue navigating
```

### Code Pattern
```kotlin
Button(
    onClick = { onItemSelected(item) },
    modifier = Modifier
        .focusable()
        .border(
            width = if (isFocused) 6.dp else 0.dp, // Thicker
            color = Color(0xFFFFD600).copy(alpha = pulseAlpha)
        )
        .shadow(
            elevation = 12.dp, // Higher elevation
            spotColor = Color(0x80FFD600) // Glow effect
        )
) {
    Text(
        text = text,
        fontSize = 20.sp // Larger for 10-foot UI
    )
}
```

### Visual States
```
┌─────────────────┐
│    Default      │
└─────────────────┘
         ↓ D-Pad Direction
┏━━━━━━━━━━━━━━━━━┓
┃┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┃ ← Pulsing amber border
┃  TV Focused     ┃    High elevation shadow
┃ ✨ (Pulsing) ✨  ┃    Larger, bolder text
┃┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┃    Glow effect
┗━━━━━━━━━━━━━━━━━┛
         ↓ Center/A Button
┏━━━━━━━━━━━━━━━━━┓
┃  TV Activated   ┃ ← Action executes
┃ (Still Focused) ┃    Focus remains
┗━━━━━━━━━━━━━━━━━┛    Still pulsing

Timeline: 
- Navigation: Immediate
- Pulse: Continuous (1000ms cycle)
- Focus: Persistent until navigation
```

### Navigation Pattern (Grid)
```
Button 1   Button 2   Button 3 ──→ Right
Button 4   Button 5   Button 6
    ↑         │          ↓
   Up     [Focused]    Down
            ← Left
```

---

## Side-by-Side Comparison

| Feature | Touch | Keyboard | Mouse | D-Pad (TV) |
|---------|-------|----------|-------|------------|
| **Focus Duration** | Momentary | Persistent | Persistent | Persistent |
| **Border Width** | 0dp | 4dp | 4dp | 6dp |
| **Border Color** | N/A | Cyan | Cyan | Amber |
| **Elevation** | 2dp | 8dp | 8dp | 12dp |
| **Animation** | Ripple | Smooth | Smooth | Pulse |
| **Touch Target** | 48dp min | 48dp min | 48dp min | 64dp+ |
| **Font Weight** | Medium | Bold | Bold | Bold |
| **Font Size** | 14sp | 14sp | 14sp | 20sp |
| **Navigation** | Direct | Sequential | Direct | Directional |
| **Activation** | Tap | Enter/Space | Click | Center/A |
| **Hover Effect** | No | No | Yes | No |
| **Glow Effect** | No | No | No | Yes |

---

## Code: Detecting Input Type

### Basic Detection
```kotlin
@Composable
fun InputAwareButton(
    text: String,
    onClick: () -> Unit,
    onInputDetected: (InputType) -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    var lastFocusChange by remember { mutableStateOf(0L) }
    
    Button(
        onClick = {
            val now = System.currentTimeMillis()
            val timeSinceFocus = now - lastFocusChange
            
            val inputType = when {
                // Touch: Click without recent focus change
                timeSinceFocus > 500 -> InputType.TOUCH
                // Keyboard: Focus changed recently, then Enter/Space
                isFocused -> InputType.KEYBOARD
                // Mouse: Click causes focus
                else -> InputType.MOUSE
            }
            
            onInputDetected(inputType)
            onClick()
        },
        modifier = Modifier
            .onFocusChanged { focusState ->
                if (focusState.isFocused != isFocused) {
                    lastFocusChange = System.currentTimeMillis()
                    
                    // D-Pad/Keyboard: Focus without click
                    if (focusState.isFocused) {
                        onInputDetected(InputType.KEYBOARD_OR_DPAD)
                    }
                }
                isFocused = focusState.isFocused
            }
            .focusable()
    ) {
        Text(text)
    }
}

enum class InputType {
    TOUCH, KEYBOARD, MOUSE, KEYBOARD_OR_DPAD
}
```

### Advanced Detection with Configuration
```kotlin
@Composable
fun SmartInputDetection() {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    
    val likelyInputType = when {
        screenWidth >= 960.dp -> "D-Pad (TV)"
        screenWidth >= 600.dp -> "Keyboard/Touch (Tablet)"
        else -> "Touch (Mobile)"
    }
    
    Text("Likely input: $likelyInputType")
}
```

---

## Visual Feedback Timing

### Touch (Fast)
```
[Touch Start]────[Touch End]
    100ms
    ↓
[Ripple]────────────[Complete]
       300ms
```

### Keyboard (Deliberate)
```
[Tab]────[Button Focused]═══════════[Enter]────[Action]
  0ms         200ms (animation)        0ms
          ↑
     Focus persists indefinitely
```

### Mouse (Immediate)
```
[Hover]────[Click]═══[Focused+Action]
   0ms       0ms     ↑
                Focus persists
```

### D-Pad (Animated)
```
[Navigate]────[Button Focused]∿∿∿∿∿∿∿∿∿[Center]────[Action]
    0ms           1000ms pulse cycle      0ms
                  ↑
              Continuous pulse until navigation
```

---

## Best Practices by Input Type

### Touch-First Design
```kotlin
// Large touch targets
.heightIn(min = 48.dp)

// Immediate feedback
.clickable { /* No delay */ }

// Visual ripple
// (Built into Material buttons)

// No persistent focus required
```

### Keyboard-First Design
```kotlin
// Clear focus indicators
.border(4.dp, Color.Cyan)

// Logical tab order
// (Automatic in Compose)

// Persistent visual feedback
if (isFocused) { /* Show indicators */ }

// Support Enter and Space
// (Built into Button)
```

### Mouse-First Design
```kotlin
// Hover states
.hoverable()

// Pointer cursor
Modifier.pointerHoverIcon(PointerIcon.Hand)

// Click focuses element
.focusable()

// Support both click and keyboard after
```

### D-Pad-First Design
```kotlin
// High-contrast indicators
.border(6.dp, Color.Yellow)

// Large elements (64dp+)
.size(width = 200.dp, height = 64.dp)

// Animated focus
// Pulsing border

// Directional navigation
// (Automatic in grid layouts)

// Larger text
fontSize = 20.sp
```

---

## Testing Scenarios

### Scenario 1: Touch User Switches to Keyboard
```
1. User taps several buttons (touch)
   → No persistent focus visible
2. User connects keyboard and presses Tab
   → Focus indicator appears
3. User continues with keyboard
   → Focus persists and navigates correctly
```

### Scenario 2: Keyboard User Switches to Mouse
```
1. User tabs through buttons (keyboard)
   → Focus indicators visible
2. User clicks random button with mouse
   → That button gets focus + activates
3. User tabs again
   → Focus continues from clicked button
```

### Scenario 3: TV User with Remote
```
1. User navigates with D-Pad
   → Large, pulsing amber indicators
2. User presses Center button
   → Action executes, focus remains
3. User continues navigating
   → Pulse animation continues smoothly
```

---

## Accessibility Notes

### For Touch Users
- Minimum 48x48dp touch targets
- Clear pressed states (ripple)
- No small buttons in corners

### For Keyboard Users
- Visible focus indicators (4.5:1 contrast)
- Logical tab order
- Enter and Space both work

### For Mouse Users
- Hover feedback
- Pointer cursor changes
- Click creates focus

### For D-Pad Users
- Extra large touch targets (64dp+)
- High-contrast borders (3:1 minimum)
- Pulsing animation for visibility
- Clear directional navigation

---

## Summary Table: When Focus Appears

| Input Method | Focus Timing | Focus Duration | Visual Cues |
|--------------|-------------|----------------|-------------|
| **Touch** | On touch (brief) | ~100ms | Ripple only |
| **Keyboard** | On Tab key | Until Tab away | Border + BG + Elevation |
| **Mouse** | On click | Until click away | Border + BG + Elevation |
| **D-Pad** | On direction key | Until navigate away | Border + Glow + Pulse |

All implementations should support **all input methods simultaneously** for the best user experience.
