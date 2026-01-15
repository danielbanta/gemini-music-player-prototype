package com.prototype.geminimusicplayer

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prototype.geminimusicplayer.ui.theme.GeminiMusicPlayerTheme

@OptIn(ExperimentalMaterial3Api::class) // Required for SwipeToDismissBox
@Composable
fun MusicPlayer(
  state: PlayerState,
  onEvent: (PlayerEvent) -> Unit,
  modifier: Modifier = Modifier
) {
  // 1. Setup Swipe State
  val dismissState = rememberSwipeToDismissBoxState(
    confirmValueChange = {
      // Trigger Dismiss when swiped to Start or End
      if (it == SwipeToDismissBoxValue.StartToEnd || it == SwipeToDismissBoxValue.EndToStart) {
        onEvent(PlayerEvent.DismissPlayer)
        true
      } else {
        false
      }
    }
  )

  // Wrap Card in SwipeToDismissBox
  SwipeToDismissBox(
    state = dismissState,
    backgroundContent = {
      // Determine direction to align icon
      val direction = dismissState.dismissDirection

      // Animate color: Red when swiping, Transparent when idle
      val color by animateColorAsState(
        targetValue = when (dismissState.targetValue) {
          SwipeToDismissBoxValue.Settled -> Color.Transparent
          else -> Color(0xFFFF1744)
        },
        label = "DismissColor"
      )

      // The Background Container
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(color, RoundedCornerShape(16.dp))
          .padding(horizontal = 24.dp),
        contentAlignment = when (direction) {
          SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
          SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
          else -> Alignment.Center
        }
      ) {
        // Only show icon if we are actually swiping
        if (direction != SwipeToDismissBoxValue.Settled) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Dismiss",
            tint = Color.White
          )
        }
      }
    },
    // Disable swipe when expanded
    enableDismissFromStartToEnd = !state.isExpanded,
    enableDismissFromEndToStart = !state.isExpanded,
    modifier = modifier
      .fillMaxWidth()
      .padding(16.dp)
  ) {
    Card(
      modifier = modifier
        .fillMaxWidth()
        .clickable {
          // If minimized, any click on the card (that isn't a button) expands it
          if (!state.isExpanded) {
            onEvent(PlayerEvent.SetExpanded(true))
          }
        },
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = PlayerBackground),
      // Border to visually distinguish the player from "Suggested tracks" bubble
      border = BorderStroke(1.dp, Color(0xFF44474F)),
      elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
      // Note: AnimatedContent automatically handles the size change of the parent Card via the
      // SizeTransform.
      AnimatedContent(
        targetState = state.isExpanded,
        transitionSpec = {
          fadeIn(animationSpec = tween(500)) togetherWith
            fadeOut(animationSpec = tween(500)) using
            // Synchronize Size Change
            SizeTransform { initialSize, targetSize ->
              tween(durationMillis = 500)
            }
        },
        // Anchor content to the bottom. This ensures the Mini Player stays fixed while the top edge
        // slides down.
        contentAlignment = Alignment.BottomCenter,
        label = "PlayerResize"
      ) { isExpanded ->
        if (isExpanded) {
          ExpandedPlayerContent(
            state = state,
            onEvent = onEvent,
            modifier = Modifier.fillMaxWidth()
          )
        } else {
          MinimizedPlayerContent(
            state = state,
            onEvent = onEvent,
            modifier = Modifier.fillMaxWidth()
          )
        }
      }
    }
  }
}

@Composable
fun ExpandedPlayerContent(
  state: PlayerState,
  onEvent: (PlayerEvent) -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 25.dp, vertical = 25.dp)
  ) {
    // ROW 1: Album Art + Track Info + Minimize Button
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Album Art
      Image(
        painter = painterResource(id = state.currentTrack.coverResId),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
          .size(80.dp)
          .clip(RoundedCornerShape(8.dp))
      )

      Spacer(modifier = Modifier.width(16.dp))

      // Track Info
      Column(modifier = Modifier.weight(1f)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          MarqueeText(
            text = state.currentTrack.title,
            style = TitleTextStyle,
            modifier = Modifier.weight(1f).padding(end = 16.dp)
          )
          // Minimize Button
          IconButton(
            onClick = { onEvent(PlayerEvent.SetExpanded(false)) },
            modifier = Modifier.size(24.dp)
          ) {
            Icon(
              imageVector = Icons.Default.KeyboardArrowDown,
              contentDescription = "Minimize",
              tint = TextSecondary
            )
          }
        }
        Spacer(modifier = Modifier.height(6.dp))
        MarqueeText(
          text = state.currentTrack.artist,
          style = ArtistTextStyle,
          modifier = Modifier.padding(end = 40.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(11.dp))

    // ROW 2: Track Progress Bar
    Box(
      modifier = Modifier.fillMaxWidth(),
      contentAlignment = Alignment.TopCenter
    ) {
      CustomTrackBar(
        progress = state.progress,
        onValueChange = { onEvent(PlayerEvent.SeekTo(it)) },
      )

      // Time Labels
      Row(
        modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        // Calculate Current Time: Parse "MM:SS" -> Seconds -> Apply Progress -> Format
        val totalSeconds = state.duration.split(":").let { parts ->
          if (parts.size == 2) parts[0].toInt() * 60 + parts[1].toInt() else 0
        }
        val currentSeconds = (totalSeconds * state.progress).toInt()
        val currentMinutes = currentSeconds / 60
        val currentRemainderSeconds = currentSeconds % 60

        // Format as "M:SS" (e.g., "2:12")
        val currentTimeString = "%d:%02d".format(currentMinutes, currentRemainderSeconds)

        // Display current progress time
        Text(
          text = currentTimeString,
          style = MaterialTheme.typography.labelSmall,
          color = TextSecondary
        )

        // Display Total Duration from State
        Text(
          text = state.duration,
          style = MaterialTheme.typography.labelSmall,
          color = TextSecondary
        )
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // ROW 3: Controls (Repeat, Prev, Play, Next, Like)
    Row(
      modifier = Modifier
          .width(312.dp) // Matches the "312px" from the Figma spec
          .align(Alignment.CenterHorizontally),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Would need to add `isRepeatOn` to PlayerState for real state, for now I'm toggling a local
      // state to demonstrate the animation.
      var localRepeat by remember { mutableStateOf(false) }
      AnimatedRepeatButton(
        isRepeatOn = localRepeat,
        onClick = { localRepeat = !localRepeat }
      )

      // Previous
      IconButton(onClick = { onEvent(PlayerEvent.Previous) }) {
        Icon(Icons.Default.SkipPrevious, contentDescription = "Previous", tint = TextPrimary)
      }

      // Play/Pause
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .size(72.dp)
          .clip(CircleShape)
          .background(PlayerAction) // #004A77
          .clickable { onEvent(PlayerEvent.PlayPause) }
      ) {
        Icon(
          imageVector = if (state.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
          contentDescription = "Play/Pause",
          tint = TextPrimary,
          modifier = Modifier.size(36.dp)
        )
      }

      // Next
      IconButton(onClick = { onEvent(PlayerEvent.Next) }) {
        Icon(Icons.Default.SkipNext, contentDescription = "Next", tint = TextPrimary)
      }

      // Favorite (Like)
      OrganicHeartButton(
        isLiked = state.isLiked,
        onClick = { onEvent(PlayerEvent.ToggleLike) },
      )
    }
  }
}

@Composable
fun MinimizedPlayerContent(
  state: PlayerState,
  onEvent: (PlayerEvent) -> Unit,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .height(72.dp)
      .padding(horizontal = 16.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Art
    Image(
      painter = painterResource(id = state.currentTrack.coverResId),
      contentDescription = null,
      contentScale = ContentScale.Crop,
      modifier = Modifier
        .size(48.dp)
        .clip(RoundedCornerShape(4.dp))
    )

    Spacer(modifier = Modifier.width(12.dp))

    // Track Info
    Column(
      modifier = Modifier.weight(1f),
      verticalArrangement = Arrangement.Center
    ) {
      MarqueeText(
        text = state.currentTrack.title,
        style = TitleTextStyle.copy(fontSize = MaterialTheme.typography.bodyMedium.fontSize)
      )
      MarqueeText(
        text = state.currentTrack.artist,
        style = ArtistTextStyle.copy(fontSize = MaterialTheme.typography.bodySmall.fontSize)
      )
    }

    // Mini Controls
    IconButton(onClick = { onEvent(PlayerEvent.PlayPause) }) {
      Icon(
        imageVector = if (state.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
        contentDescription = "Play",
        tint = TextPrimary
      )
    }

    // Replaced Dismiss (X) with Organic Heart
    OrganicHeartButton(
      isLiked = state.isLiked,
      onClick = { onEvent(PlayerEvent.ToggleLike) }
    )
  }
}

/* Custom Track bar to match the design spec */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTrackBar(
  progress: Float,
  onValueChange: (Float) -> Unit,
  modifier: Modifier = Modifier
) {
  Slider(
    value = progress,
    onValueChange = onValueChange,
    thumb = {
      // Custom White Thumb
      Box(
        modifier = Modifier
          .size(12.dp).offset(y = (1).dp),
        contentAlignment = Alignment.Center
      ) {
        Box(
          modifier = Modifier
            .size(12.dp) // Actual size of the white circle
            .background(TextPrimary, CircleShape)
        )
      }
    },
    track = { sliderState ->
      Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterStart
      ) {
        SliderDefaults.Track(
          sliderState = sliderState,
          modifier = Modifier.height(3.dp),
          colors = SliderDefaults.colors(
            activeTrackColor = TextPrimary,
            inactiveTrackColor = Color(0xFF555B66)
          ),
          thumbTrackGapSize = 0.dp
        )
      }
    },

    modifier = modifier.fillMaxWidth()
  )
}

/* Self scrolling text composable to handle overflow automatically */
@Composable
fun MarqueeText(
  text: String,
  style: TextStyle,
  modifier: Modifier = Modifier
) {
  Text(
    text = text,
    style = style,
    maxLines = 1,
    overflow = TextOverflow.Ellipsis, // Fallback
    modifier = modifier.basicMarquee(
      iterations = Int.MAX_VALUE,
      velocity = 30.dp // Slow, smooth scroll
    )
  )
}

@Composable
fun AnimatedRepeatButton(
  isRepeatOn: Boolean,
  onClick: () -> Unit
) {
  val haptic = LocalHapticFeedback.current
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()

  // The "Feel"
  // Light haptic feed back when pressed
  LaunchedEffect(isPressed) {
    if (isPressed) {
      haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
    }
  }

  // The "Motion"
  // Animate rotation when state changes
  val rotation by animateFloatAsState(
    targetValue = if (isRepeatOn) 180f else 0f,
    animationSpec = spring(stiffness = Spring.StiffnessLow)
  )

  val color by animateColorAsState(
    targetValue = if (isRepeatOn) PlayerAction else TextSecondary
  )

  IconButton(onClick = onClick, interactionSource = interactionSource) {
    Icon(
      imageVector = Icons.Default.Repeat,
      contentDescription = "Repeat",
      tint = color,
      modifier = Modifier.graphicsLayer() { rotationZ = rotation }
    )
  }
}

@Preview(showBackground = true)
@Composable
fun MusicPlayerPreview() {
  val playerViewModel = PlayerViewModel()
  GeminiMusicPlayerTheme {
    val playerState by playerViewModel.state.collectAsStateWithLifecycle()
    MusicPlayer(playerState, playerViewModel::onEvent)
  }
}