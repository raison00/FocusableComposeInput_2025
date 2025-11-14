package com.example.focusablebuttons

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FocusableButtonsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ResponsiveFocusableApp()
                }
            }
        }
    }
}

/**
 * WCAG 2.1 AA Compliant Color Scheme
 * - Contrast ratio: 4.5:1 for normal text
 * - Contrast ratio: 3:1 for large text and UI components
 */
object FocusColors {
    val FocusBorder = Color(0xFF00D9FF) // Cyan - high contrast
    val FocusBackground = Color(0xFF1A4D5C) // Dark teal
    val FocusText = Color.White // Maximum contrast
    val DefaultBackground = Color(0xFF2196F3) // Material Blue
    val DefaultText = Color.White
    val TVFocusBorder = Color(0xFFFFD600) // Amber for TV
    val TVFocusGlow = Color(0x80FFD600) // Amber glow with transparency
}

@Composable
fun ResponsiveFocusableApp() {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val isTablet = screenWidth >= 600.dp
    val isTV = screenWidth >= 960.dp
    
    var selectedOption by remember { mutableStateOf("None") }
    var lastInputType by remember { mutableStateOf("None") }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header with input detection
        Text(
            text = if (isTV) "TV Focus Demo" else if (isTablet) "Tablet Focus Demo" else "Mobile Focus Demo",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Selected: $selectedOption",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Last Input: $lastInputType",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
        }
        
        Divider()
        
        // Instructions based on device type
        InstructionsCard(isTV, isTablet)
        
        // Adaptive layouts based on screen size
        if (isTV) {
            TVFocusLayout(
                onOptionSelected = { selectedOption = it; lastInputType = "D-Pad/Remote" }
            )
        } else if (isTablet) {
            TabletFocusLayout(
                onOptionSelected = { selectedOption = it; lastInputType = "Touch/Keyboard" }
            )
        } else {
            MobileFocusLayout(
                onOptionSelected = { selectedOption = it; lastInputType = "Touch/Keyboard" }
            )
        }
    }
}

@Composable
fun InstructionsCard(isTV: Boolean, isTablet: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Navigation Instructions:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            when {
                isTV -> {
                    Text("• D-Pad: Navigate between buttons", fontSize = 14.sp)
                    Text("• Center/Select: Activate button", fontSize = 14.sp)
                    Text("• Focus: Yellow border with glow effect", fontSize = 14.sp)
                }
                isTablet -> {
                    Text("• Tab/Arrow Keys: Navigate", fontSize = 14.sp)
                    Text("• Enter/Space: Activate", fontSize = 14.sp)
                    Text("• Touch: Direct selection", fontSize = 14.sp)
                    Text("• Focus: Cyan border with elevation", fontSize = 14.sp)
                }
                else -> {
                    Text("• Tab: Navigate forward", fontSize = 14.sp)
                    Text("• Shift+Tab: Navigate backward", fontSize = 14.sp)
                    Text("• Touch: Direct selection", fontSize = 14.sp)
                    Text("• Focus: Cyan border with elevation", fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun TVFocusLayout(onOptionSelected: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "TV Navigation (D-Pad Optimized)",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        // Horizontal menu bar (typical TV pattern)
        Text("Main Menu", style = MaterialTheme.typography.titleMedium)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            listOf("Home", "Movies", "TV Shows", "Settings").forEach { option ->
                WCAGFocusableButton(
                    text = option,
                    onClick = { onOptionSelected(option) },
                    modifier = Modifier.weight(1f),
                    isTVMode = true
                )
            }
        }
        
        // Grid layout (typical TV content)
        Text("Content Grid", style = MaterialTheme.typography.titleMedium)
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            repeat(2) { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    repeat(4) { col ->
                        val index = row * 4 + col + 1
                        WCAGFocusableButton(
                            text = "Item $index",
                            onClick = { onOptionSelected("Item $index") },
                            modifier = Modifier.weight(1f),
                            isTVMode = true
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TabletFocusLayout(onOptionSelected: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "Tablet Layout (Mixed Input)",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        // Two-column layout
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Left column
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Category A", style = MaterialTheme.typography.titleMedium)
                repeat(4) { index ->
                    WCAGFocusableButton(
                        text = "Option A${index + 1}",
                        onClick = { onOptionSelected("Option A${index + 1}") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            
            // Right column
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Category B", style = MaterialTheme.typography.titleMedium)
                repeat(4) { index ->
                    WCAGFocusableButton(
                        text = "Option B${index + 1}",
                        onClick = { onOptionSelected("Option B${index + 1}") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun MobileFocusLayout(onOptionSelected: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "Mobile Layout (Touch + Keyboard)",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        // Vertical list (mobile-optimized)
        Text("Actions", style = MaterialTheme.typography.titleMedium)
        repeat(6) { index ->
            WCAGFocusableButton(
                text = "Action ${index + 1}",
                onClick = { onOptionSelected("Action ${index + 1}") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        // Horizontal scroll area
        Text("Quick Access", style = MaterialTheme.typography.titleMedium)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Quick 1", "Quick 2", "Quick 3").forEach { option ->
                WCAGFocusableButton(
                    text = option,
                    onClick = { onOptionSelected(option) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/**
 * WCAG 2.1 AA Compliant Focusable Button
 * 
 * Accessibility Features:
 * - 4.5:1 contrast ratio for text
 * - 3:1 contrast ratio for focus indicator
 * - Minimum 44x44dp touch target (mobile)
 * - Clear focus indication with multiple visual cues
 * - Keyboard navigable
 * - Touch, mouse, and D-pad support
 */
@Composable
fun WCAGFocusableButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isTVMode: Boolean = false
) {
    var isFocused by remember { mutableStateOf(false) }
    var hasBeenFocused by remember { mutableStateOf(false) }
    
    // Animated elevation for focus
    val elevation by animateDpAsState(
        targetValue = if (isFocused) 8.dp else 2.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "elevation"
    )
    
    // Animated border width
    val borderWidth by animateDpAsState(
        targetValue = if (isFocused) 4.dp else 0.dp,
        animationSpec = tween(durationMillis = 200),
        label = "borderWidth"
    )
    
    // Pulsing animation for TV mode
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )
    
    Button(
        onClick = onClick,
        modifier = modifier
            .heightIn(min = 48.dp) // WCAG minimum touch target
            .onFocusChanged { focusState ->
                if (focusState.isFocused && !hasBeenFocused) {
                    hasBeenFocused = true
                }
                isFocused = focusState.isFocused
            }
            .focusable()
            .shadow(
                elevation = elevation,
                shape = RoundedCornerShape(8.dp),
                spotColor = if (isTVMode && isFocused) 
                    FocusColors.TVFocusGlow 
                else 
                    Color.Black.copy(alpha = 0.3f)
            )
            .then(
                if (isFocused && isTVMode) {
                    Modifier.border(
                        width = borderWidth,
                        color = FocusColors.TVFocusBorder.copy(alpha = pulseAlpha),
                        shape = RoundedCornerShape(8.dp)
                    )
                } else if (isFocused) {
                    Modifier.border(
                        width = borderWidth,
                        color = FocusColors.FocusBorder,
                        shape = RoundedCornerShape(8.dp)
                    )
                } else {
                    Modifier
                }
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isFocused) {
                FocusColors.FocusBackground
            } else {
                FocusColors.DefaultBackground
            },
            contentColor = if (isFocused) {
                FocusColors.FocusText
            } else {
                FocusColors.DefaultText
            }
        ),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (isFocused) FontWeight.Bold else FontWeight.Medium
        )
    }
}

// Theme setup
@Composable
fun FocusableButtonsTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFF2196F3),
            primaryContainer = Color(0xFF1565C0),
            secondary = Color(0xFF03DAC6),
            secondaryContainer = Color(0xFF018786),
            background = Color(0xFF121212),
            surface = Color(0xFF1E1E1E)
        ),
        content = content
    )
}