package com.prototype.geminimusicplayer

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// --- THEME COLORS ---
// Matching the Music Player Spec
val AppBackground = Color(0xFF131314)
val PlayerBackground = Color(0xFF2E3240)
val PlayerAction = Color(0xFF004A77)
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFFB8B8B1)
val TrackBackground = Color(0xFF555B66)
val InputBackground = Color(0xFF1E1F20)

// Define the Family.
// "Outfit" is the open-source twin to Google Sans
val GoogleSansFamily = FontFamily(
  Font(R.font.outfit_medium, FontWeight.Medium),
  Font(R.font.outfit_regular, FontWeight.Normal)
)

// Define the specific styles from the PRD
val TitleTextStyle = TextStyle(
  fontFamily = GoogleSansFamily,
  fontWeight = FontWeight.Medium, // Google Sans Medium
  fontSize = 21.sp,
  color = TextPrimary
)

val ArtistTextStyle = TextStyle(
  fontFamily = GoogleSansFamily,
  fontWeight = FontWeight.Normal, // Google Sans Regular
  fontSize = 12.sp,
  color = TextPrimary.copy(alpha = 0.5f) // 50% Opacity
)