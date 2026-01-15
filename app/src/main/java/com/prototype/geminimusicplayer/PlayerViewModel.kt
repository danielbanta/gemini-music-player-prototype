package com.prototype.geminimusicplayer

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PlayerViewModel : ViewModel() {
  private val repository = MockRepository()
  private val playlist = repository.getTracks() // Load our hardcoded tracks

  // Backing property for state
  private val _state = MutableStateFlow(PlayerState())
  val state = _state.asStateFlow()

  fun onEvent(event: PlayerEvent) {
    when (event) {
      is PlayerEvent.SelectTrack -> {
        _state.update { it.copy(
          currentTrack = event.track,
          isPlayerVisible = true,
          isExpanded = true,
          isPlaying = true,
          duration = event.track.duration,
          progress = 0f
        )}
      }
      is PlayerEvent.Next -> playNextTrack()
      is PlayerEvent.Previous -> playPreviousTrack()
      is PlayerEvent.PlayPause -> _state.update { it.copy(isPlaying = !it.isPlaying) }
      is PlayerEvent.ToggleLike -> _state.update { it.copy(isLiked = !it.isLiked) }
      is PlayerEvent.DismissPlayer -> _state.update { it.copy(isPlayerVisible = false, isPlaying = false) }
      is PlayerEvent.SetExpanded -> _state.update { it.copy(isExpanded = event.expanded) }
      is PlayerEvent.SeekTo -> _state.update { it.copy(progress = event.position)}
    }
  }

  // Cycles through the playlist
  private fun playNextTrack() {
    val currentIndex = playlist.indexOfFirst { it.id == _state.value.currentTrack.id }
    if (currentIndex != -1) {
      val nextIndex = (currentIndex + 1) % playlist.size // Cycle loop
      val nextTrack = playlist[nextIndex]
      _state.update { it.copy(currentTrack = nextTrack, duration = nextTrack.duration, progress = 0f) }
    }
  }

  private fun playPreviousTrack() {
    val currentIndex = playlist.indexOfFirst { it.id == _state.value.currentTrack.id }
    if (currentIndex != -1) {
      val prevIndex = if (currentIndex - 1 < 0) playlist.size - 1 else currentIndex - 1
      val prevTrack = playlist[prevIndex]
      _state.update { it.copy(currentTrack = prevTrack, duration = prevTrack.duration, progress = 0f) }
    }
  }

  companion object {
    val EmptyTrack = Track("0", "Nothing Playing", "", 0, 0)
  }
}