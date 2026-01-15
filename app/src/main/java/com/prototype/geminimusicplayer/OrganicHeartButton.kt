package com.prototype.geminimusicplayer

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun OrganicHeartButton(
  isLiked: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  iconSize: Dp = 26.dp
) {
  // Setup - Haptics & Interaction
  val haptic = LocalHapticFeedback.current
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()

  // Animation States
  val scale = remember { Animatable(1f) }
  val fillProgress = remember { Animatable(if (isLiked) 1f else 0f) }
  val particles = remember { List(12) { Particle() } } // 12 random particles
  val particleRadius = remember { Animatable(0f) }
  val particleAlpha = remember { Animatable(0f) }

  // The "Squish" (Anticipation)
  // When pressed, shrink to 0.9. When released (if not liking), return to 1.0.
  LaunchedEffect(isPressed) {
    if (isPressed) {
      haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove) // Light Tap
      scale.animateTo(0.9f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
    } else {
      // Only return to 1.0 if we aren't about to "Pop" (handled in the isLiked block)
      if (!isLiked) {
        scale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioLowBouncy))
      }
    }
  }

  // The "Pop" & "Fill" (Impact)
  LaunchedEffect(isLiked) {
    if (isLiked) {
      // The Fill (Wick) - 150ms fast fill
      launch {
        fillProgress.animateTo(1f, tween(150))
      }

      // The Pop (Impact) - Spring physics to 1.2x then settle
      launch {
        // "Heartbeat" Thump at max expansion
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        scale.animateTo(1.2f, spring(dampingRatio = Spring.DampingRatioHighBouncy))
        scale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioLowBouncy))
      }

      // C. Particle Explosion
      launch {
        particleAlpha.snapTo(1f)
        particleRadius.snapTo(0f)
        // Expand outward
        launch { particleRadius.animateTo(1.5f, tween(400, easing = LinearOutSlowInEasing)) }
        // Fade out
        launch { particleAlpha.animateTo(0f, tween(400)) }
      }
    } else {
      // Reset state when un-liked
      fillProgress.snapTo(0f)
      scale.animateTo(1f)
    }
  }

  Box(
    modifier = modifier
      .size(48.dp)
      .clickable(
        interactionSource = interactionSource,
        indication = null // Disable default ripple
      ) { onClick() },
    contentAlignment = Alignment.Center
  ) {
    // LAYER 1: Particles (Behind the heart)
    if (particleAlpha.value > 0f) {
      Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        val maxRadius = size.width * 0.8f  // Explosion radius

        particles.forEach { particle ->
          // Calculate current position based on expanding radius
          val currentDist = particleRadius.value * maxRadius * particle.speed
          val x = center.x + cos(particle.angle) * currentDist
          val y = center.y + sin(particle.angle) * currentDist

          drawCircle(
            color = particle.color.copy(alpha = particleAlpha.value),
            radius = particle.size * size.width * 0.05f, // Scale size relative to canvas
            center = Offset(x.toFloat(), y.toFloat())
          )
        }
      }
    }

    // LAYER 2: The Heart Icon
    Box(
      modifier = Modifier
          .size(iconSize)
          .graphicsLayer {
            scaleX = scale.value
            scaleY = scale.value
          }
    ) {
      // A. The Outline (Always visible, grey)
      Icon(
        imageVector = Icons.Default.FavoriteBorder,
        contentDescription = null,
        tint = TextSecondary,
        modifier = Modifier.fillMaxSize()
      )

      // B. The Fill
      // Draw the FULL red heart, but we clip the Box containing it.
      // As fillProgress goes 0 -> 1, the clip height grows from bottom -> top.
      if (fillProgress.value > 0f) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .clip(GenericShape { size, _ ->
              // Clip definition: A rectangle that grows from bottom
              moveTo(0f, size.height) // Start Bottom-Left
              lineTo(size.width, size.height) // Bottom-Right
              lineTo(size.width, size.height * (1 - fillProgress.value)) // Top-Right (Variable)
              lineTo(0f, size.height * (1 - fillProgress.value)) // Top-Left (Variable)
              close()
            })
        ) {
          Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = "Liked",
            tint = Color(0xFFE91E63),
            modifier = Modifier.fillMaxSize()
          )
        }
      }
    }
  }
}

// Simple data class for random particle effects
private data class Particle(
  val angle: Double = Random.nextDouble(0.0, 2 * Math.PI),
  val speed: Float = Random.nextFloat() * 0.5f + 0.5f, // Random speed 0.5-1.0
  val size: Float = Random.nextFloat() * 0.5f + 0.5f,
  val color: Color = listOf(
    Color(0xFFE91E63), // Pink
    Color(0xFFFFC107), // Amber
    Color(0xFF9C27B0), // Purple
    Color.White
  ).random()
)
