package com.prototype.geminimusicplayer

data class PlayerState(
  val currentTrack: Track = PlayerViewModel.EmptyTrack,
  val isPlaying: Boolean = false,
  val progress: Float = 0f,
  val duration: String = "0:00",
  val isLiked: Boolean = false,
  val isPlayerVisible: Boolean = false,
  val isExpanded: Boolean = true // Track expanded/minimized state
)

data class Track(
  val id: String,
  val title: String,
  val artist: String,
  val coverResId: Int,
  val audioResId: Int,
  val duration: String = "0:00",
)
