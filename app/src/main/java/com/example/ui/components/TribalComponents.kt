package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.ui.theme.*
import kotlin.random.Random

/**
 * Draws custom African tribal geometric graphics (triangles, diamonds, lines, sun motifs)
 * in bright warm orange-gold, purple, and neon pink-red. Shows fine attention to cultural aesthetics.
 */
@Composable
fun TribalBorderPattern(
    modifier: Modifier = Modifier,
    height: Int = 24,
    color1: Color = AfroPrimaryGold,
    color2: Color = AfroAccentPink,
    color3: Color = AfroSecondaryPurple
) {
    Canvas(modifier = modifier.fillMaxWidth().height(height.dp)) {
        val width = size.width
        val h = size.height
        val segmentCount = 14
        val segmentWidth = width / segmentCount

        // Clean deep backdrop
        drawRect(Color(0xFF0F0B0F))

        for (i in 0 until segmentCount) {
            val left = i * segmentWidth
            val right = left + segmentWidth
            val cx = left + segmentWidth / 2f
            val cy = h / 2f

            // Alternate between drawing diamonds/triangles or sun motifs
            if (i % 2 == 0) {
                // Diamond
                val path = Path().apply {
                    moveTo(cx, 2f)
                    lineTo(right - 4f, cy)
                    lineTo(cx, h - 2f)
                    lineTo(left + 4f, cy)
                    close()
                }
                drawPath(path, color = color1)

                // Outer decorative lines
                drawCircle(
                    color = color2,
                    radius = h / 6f,
                    center = Offset(cx, cy)
                )
            } else {
                // Triangles pointing down and up
                val upperPath = Path().apply {
                    moveTo(left + 4f, 2f)
                    lineTo(right - 4f, 2f)
                    lineTo(cx, cy - 2f)
                    close()
                }
                drawPath(upperPath, color = color3)

                val lowerPath = Path().apply {
                    moveTo(cx, cy + 2f)
                    lineTo(right - 4f, h - 2f)
                    lineTo(left + 4f, h - 2f)
                    close()
                }
                drawPath(lowerPath, color = color2)

                // Micro dots
                drawCircle(color = color1, radius = 3f, center = Offset(cx, cy))
            }

            // Separator accent line
            drawLine(
                color = Color(0xFF2A1930),
                start = Offset(right, 0f),
                end = Offset(right, h),
                strokeWidth = 2f
            )
        }

        // Top & bottom glowing border lines
        drawLine(
            color = color1,
            start = Offset(0f, 0f),
            end = Offset(width, 0f),
            strokeWidth = 3f
        )
        drawLine(
            color = color2,
            start = Offset(0f, h),
            end = Offset(width, h),
            strokeWidth = 3f
        )
    }
}

/**
 * Procedurally draws beautiful colorful tribal avatars to replace plain user icons.
 * This guarantees gorgeous, culturally custom visual elements for all makers.
 */
@Composable
fun UserAvatarCanvas(
    avatarId: Int, // 1 to 6 determines the seed style
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = when (avatarId % 3) {
                        0 -> listOf(Color(0xFF3B154D), Color(0xFF10071C))
                        1 -> listOf(Color(0xFF4D3F15), Color(0xFF1F1005))
                        else -> listOf(Color(0xFF4D1525), Color(0xFF1E070F))
                    }
                )
            )
    ) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f
        val radius = w / 2f

        // Draw outer ring
        drawCircle(
            brush = Brush.sweepGradient(
                colors = listOf(AfroPrimaryGold, AfroAccentPink, AfroSecondaryPurple, AfroPrimaryGold)
            ),
            radius = radius - 2f,
            style = Stroke(width = 4f)
        )

        // Seed values
        val seed = avatarId * 17

        // Custom tribal features
        when (avatarId) {
            0, 1 -> { // Golden Sun dancer
                // Face base
                drawCircle(color = Color(0xFFF79F1F), radius = radius * 0.55f, center = Offset(cx, cy))
                // Visual crown/hair
                drawPath(Path().apply {
                    moveTo(cx - radius * 0.5f, cy - radius * 0.2f)
                    lineTo(cx, cy - radius * 0.9f)
                    lineTo(cx + radius * 0.5f, cy - radius * 0.2f)
                    close()
                }, color = Color(0xFFD63031))
                // Eyes
                drawCircle(color = Color.Black, radius = 4f, center = Offset(cx - 10f, cy))
                drawCircle(color = Color.Black, radius = 4f, center = Offset(cx + 10f, cy))
                // Smiling mouth
                drawArc(
                    color = Color.White,
                    startAngle = 0f,
                    sweepAngle = 180f,
                    useCenter = false,
                    size = Size(20f, 12f),
                    topLeft = Offset(cx - 10f, cy + 4f),
                    style = Stroke(width = 4f)
                )
            }
            2 -> { // Purple Princess
                drawCircle(color = Color(0xFF9B59B6), radius = radius * 0.6f, center = Offset(cx, cy))
                // Hair curves
                drawCircle(color = Color(0xFF2C3E50), radius = radius * 0.3f, center = Offset(cx - radius * 0.4f, cy - radius * 0.3f))
                drawCircle(color = Color(0xFF2C3E50), radius = radius * 0.3f, center = Offset(cx + radius * 0.4f, cy - radius * 0.3f))
                // Traditional paint marks (white dashes on cheeks)
                drawLine(Color.White, Offset(cx - 16f, cy + 5f), Offset(cx - 8f, cy + 8f), strokeWidth = 3f)
                drawLine(Color.White, Offset(cx + 16f, cy + 5f), Offset(cx + 8f, cy + 8f), strokeWidth = 3f)
                // Eyes & Lips
                drawCircle(color = Color.White, radius = 3f, center = Offset(cx - 8f, cy - 4f))
                drawCircle(color = Color.White, radius = 3f, center = Offset(cx + 8f, cy - 4f))
                drawCircle(color = Color(0xFFE74C3C), radius = 5f, center = Offset(cx, cy + 12f))
            }
            3 -> { // Cool Afro Warrior
                drawCircle(color = Color(0xFFD35400), radius = radius * 0.55f, center = Offset(cx, cy + 2f))
                // Huge dynamic Afro hair
                drawCircle(color = Color.Black, radius = radius * 0.4f, center = Offset(cx, cy - radius * 0.4f))
                drawCircle(color = Color.Black, radius = radius * 0.32f, center = Offset(cx - radius * 0.35f, cy - radius * 0.25f))
                drawCircle(color = Color.Black, radius = radius * 0.32f, center = Offset(cx + radius * 0.35f, cy - radius * 0.25f))
                // Cool shades
                drawRect(Color(0xFF2C3E50), topLeft = Offset(cx - 18f, cy - 8f), size = Size(15f, 10f))
                drawRect(Color(0xFF2C3E50), topLeft = Offset(cx + 3f, cy - 8f), size = Size(15f, 10f))
                drawLine(Color(0xFF2C3E50), Offset(cx - 5f, cy - 3f), Offset(cx + 5f, cy - 3f), strokeWidth = 3f)
                // Cheerful red smile
                drawArc(
                    color = Color(0xFFC0392B),
                    startAngle = 10f,
                    sweepAngle = 160f,
                    useCenter = false,
                    size = Size(16f, 10f),
                    topLeft = Offset(cx - 8f, cy + 8f),
                    style = Stroke(width = 4f)
                )
            }
            else -> { // Djembe Player motif
                drawCircle(color = Color(0xFFE67E22), radius = radius * 0.6f, center = Offset(cx, cy))
                // Tribal stripes
                drawLine(AfroPrimaryGold, Offset(cx, cy - radius * 0.5f), Offset(cx, cy + radius * 0.4f), strokeWidth = 4f)
                // Glowing eyes
                drawCircle(color = Color.White, radius = 4f, center = Offset(cx - 11f, cy - 2f))
                drawCircle(color = Color.White, radius = 4f, center = Offset(cx + 11f, cy - 2f))
                drawCircle(Color(0xFF2D3436), radius = 2f, center = Offset(cx - 11f, cy - 2f))
                drawCircle(Color(0xFF2D3436), radius = 2f, center = Offset(cx + 11f, cy - 2f))
                // Happy jaw
                drawCircle(Color(0xFF2C3E50), radius = 5f, center = Offset(cx, cy + 12f))
            }
        }
    }
}

/**
 * Handles floating flying hearts when the user clicks the Live stream screen.
 * Adds amazing visual feedback to keep the live simulation feel physical and fun!
 */
@Composable
fun BoxScope.LiveHeartRain(
    heartSeeds: List<Int>,
    onHeartFinished: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxHeight(0.7f)
            .width(160.dp)
            .align(Alignment.BottomEnd)
            .padding(bottom = 80.dp, end = 16.dp)
    ) {
        heartSeeds.forEach { seedId ->
            key(seedId) {
                var isFinished by remember { mutableStateOf(false) }
                
                // Animation of floating up
                val transition = rememberInfiniteTransition()
                
                val animProgress = remember { Animatable(0f) }
                
                LaunchedEffect(Unit) {
                    animProgress.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(durationMillis = 2200, easing = LinearEasing)
                    )
                    isFinished = true
                    onHeartFinished(seedId)
                }

                if (!isFinished) {
                    val progress = animProgress.value
                    
                    // Sine wave horizontal wiggle
                    val offsetWiggle = (kotlin.math.sin(progress * Math.PI * 3f) * 35.dp.value) + (seedId % 5 * 10f)
                    val bottomPadding = progress * 400f // pixels up
                    val opacityValue = if (progress < 0.2f) progress / 0.2f else 1f - ((progress - 0.2f) / 0.8f)
                    val scaleValue = if (progress < 0.15f) (progress / 0.15f) * 1.3f else 1.3f * (1f - (progress * 0.25f))

                    val heartColor = when (seedId % 5) {
                        0 -> AfroAccentPink
                        1 -> AfroPrimaryGold
                        2 -> AfroSecondaryPurple
                        3 -> Color(0xFF00FFCC)
                        else -> Color(0xFFFFCC00)
                    }

                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = null,
                        tint = heartColor,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .offset(
                                x = offsetWiggle.dp,
                                y = (-bottomPadding).dp
                            )
                            .alpha(opacityValue)
                            .size((28 * scaleValue).dp)
                    )
                }
            }
        }
    }
}
