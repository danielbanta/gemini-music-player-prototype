package com.prototype.geminimusicplayer

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

// Define the custom Gemini Sparkle Vector
val GeminiSparkle: ImageVector
  get() {
    if (_GeminiSparkle != null) {
      return _GeminiSparkle!!
    }
    _GeminiSparkle = ImageVector.Builder(
      name = "GeminiSparkle",
      defaultWidth = 24.dp,
      defaultHeight = 24.dp,
      viewportWidth = 24f,
      viewportHeight = 24f
    ).apply {
      // Draw a 4-pointed star (concave diamond)
      path (fill = SolidColor(Color.White)) {
        moveTo(12f, 2f)
        curveTo(14.4f, 8.4f, 15.6f, 9.6f, 22f, 12f)
        curveTo(15.6f, 14.4f, 14.4f, 15.6f, 12f, 22f)
        curveTo(9.6f, 15.6f, 8.4f, 14.4f, 2f, 12f)
        curveTo(8.4f, 9.6f, 9.6f, 8.4f, 12f, 2f)
        close()
      }
    }.build()
    return _GeminiSparkle!!
  }

private var _GeminiSparkle: ImageVector? = null

@Composable
fun GeminiIcon(
  modifier: Modifier = Modifier,
  contentDescription: String? = "Gemini AI"
) {
  val geminiGradient = Brush.linearGradient(
    colors = listOf(
      Color(0xFF4285F4), // Google Blue
      Color(0xFF9C27B0)  // Purple
    )
  )

  Icon(
    imageVector = GeminiSparkle,
    contentDescription = contentDescription,
    modifier = modifier.graphicsLayer(alpha = 0.99f) // Workaround for blending
      .drawWithCache() {
        onDrawWithContent {
          drawContent()
          drawRect(geminiGradient, blendMode = BlendMode.SrcIn)
        }
      },
    tint = Color.Unspecified // Disable default tint to let gradient show
  )
}