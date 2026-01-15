package com.prototype.geminimusicplayer.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.prototype.geminimusicplayer.R

val GoogleSansFamily = FontFamily(
  Font(R.font.outfit_medium, FontWeight.Medium),
  Font(R.font.outfit_regular, FontWeight.Normal)
)

val baseline = Typography()

// Set of Material typography styles to start with
val Typography = Typography(
  bodyLarge = TextStyle(
    fontFamily = GoogleSansFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    lineHeight = 24.sp,
    letterSpacing = 0.5.sp
  ),
  titleMedium = baseline.titleMedium.copy(
    fontFamily = GoogleSansFamily,
    fontWeight = FontWeight.Medium
  ),
  labelLarge = baseline.labelLarge.copy(
    fontFamily = GoogleSansFamily,
    fontWeight = FontWeight.Medium
  ),
  labelSmall = baseline.labelSmall.copy(
    fontFamily = GoogleSansFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 9.sp,
  ),
  bodyMedium = baseline.bodyMedium.copy(
    fontFamily = GoogleSansFamily,
    fontWeight = FontWeight.Normal
  ),
  bodySmall = baseline.bodySmall.copy(
    fontFamily = GoogleSansFamily,
    fontWeight = FontWeight.Normal
  ),
)