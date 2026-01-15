package com.prototype.geminimusicplayer

sealed class PlayerEvent {
  data object PlayPause : PlayerEvent()
  data object Next : PlayerEvent()
  data object Previous : PlayerEvent()
  data object ToggleLike : PlayerEvent()
  data object DismissPlayer : PlayerEvent()
  data class SelectTrack(val track: Track) : PlayerEvent()
  data class SeekTo(val position: Float) : PlayerEvent()
  data class SetExpanded(val expanded: Boolean) : PlayerEvent() // Handle minimization
}