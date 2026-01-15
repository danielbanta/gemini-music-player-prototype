package com.prototype.geminimusicplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prototype.geminimusicplayer.ui.theme.GeminiMusicPlayerTheme

class MainActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    // Ideally, use Hilt or a Factory, but direct instantiation is fine for prototype
    val playerViewModel = PlayerViewModel()

    setContent {
      GeminiMusicPlayerTheme {
        // Collect state in a lifecycle-aware manner
        val playerState by playerViewModel.state.collectAsStateWithLifecycle()

        Scaffold(
          modifier = Modifier.fillMaxSize(),
          containerColor = AppBackground,
          topBar = { GeminiTopBar() }) { innerPadding ->
          ChatScreen(
            state = playerState,
            onEvent = playerViewModel::onEvent,
            modifier = Modifier.padding(top = innerPadding.calculateTopPadding())
          )
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeminiTopBar() {
  CenterAlignedTopAppBar(
    title = {
      Text(
        text = "Gemini: Music Player Example",
        style = MaterialTheme.typography.titleMedium,
        color = TextPrimary
      )
    },
    navigationIcon = {
      IconButton (onClick = { /* Do nothing */ }) {
        Icon(
          imageVector = Icons.Default.Menu,
          contentDescription = "Menu"
        )
      }
    },
    actions = {
      IconButton(onClick = { /* Do nothing */ }) {
        Icon(
          imageVector = Icons.Default.EditNote,
          contentDescription = "New Chat"
        )
      }
    },
    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
      containerColor = AppBackground,
    )
  )
}

@Composable
fun ChatScreen(
  state: PlayerState,
  onEvent: (PlayerEvent) -> Unit,
  modifier: Modifier = Modifier,
) {
  // Column ensures input rooted at bottom
  Column(modifier = modifier.fillMaxSize()) {

    // Area above Input: Contains Chat (Background) + Player (Foreground Overlay)
    Box(
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth()
    ) {
      CurrentChat(
        onTrackSelected = { track -> onEvent(PlayerEvent.SelectTrack(track)) },
        onScroll = {
          // Minimizes the player when the user scrolls the chat
          if (state.isExpanded) onEvent(PlayerEvent.SetExpanded(false))
        },
        // Add padding so the last message isn't hidden behind the Minimized Player
        modifier = Modifier.fillMaxSize().padding(bottom = if (state.isPlayerVisible) 80.dp else 16.dp)
      )

      androidx.compose.animation.AnimatedVisibility(
        visible = state.isPlayerVisible,
        enter = slideInVertically { it } + fadeIn(),
        exit = slideOutVertically { it } + fadeOut(),
        modifier = Modifier.align(Alignment.BottomCenter)
      ) {
        MusicPlayer(
          state = state,
          onEvent = onEvent
        )
      }
    }

    // Rooted to bottom
    PromptInput()
  }
}

@Composable
fun CurrentChat(
  onTrackSelected: (Track) -> Unit,
  onScroll: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val listState = rememberLazyListState()
  val repo = remember { MockRepository() }
  val tracks = repo.getTracks()

  // Scroll to first user chat on Initial Load
  LaunchedEffect(Unit) {
    listState.scrollToItem(index = 2)
  }

  // Detect scroll to minimize player
  // LaunchedEffect offloads the check to a coroutine so the UI thread isn't blocked
  LaunchedEffect(listState.isScrollInProgress) {
    if (listState.isScrollInProgress) {
      onScroll()
    }
  }

  // Lazy Column allows us to observe scroll position/action via rememberLazyListState()
  LazyColumn(
    state = listState,
    modifier = modifier.padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(24.dp)
  ) {
    // Add dummy space to allow scrolling
    item { Text("extra space for scrolling...", modifier = Modifier.padding(16.dp), color = TextSecondary) }
    item {
      Spacer(modifier = Modifier.height(500.dp))
    }

    // Hardcoded messages
    item {
      ChatMessageBubble(
        text = "Hey Gemini, can you find me some Lo-Fi tracks for my coding video?",
        isUser = true
      )
    }
    item {
      ChatMessageBubble(
        text = "Sure! Here are some great tracks to help you focus:",
        isUser = false
      )
    }

    // The message with clickable songs
    item {
      Column(
        modifier = Modifier
          .background(PlayerBackground, RoundedCornerShape(12.dp))
          .padding(12.dp)
      ) {
        Text("Suggested Tracks", style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(8.dp))
        tracks.forEach { track ->
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { onTrackSelected(track) } // Click triggers the player
              .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(track.title, style = MaterialTheme.typography.bodyMedium)
              Text(track.artist, style = MaterialTheme.typography.bodySmall)
            }
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(500.dp))
    }
    item { Text("...extra space for scrolling.", modifier = Modifier.padding(16.dp), color = TextSecondary) }
  }
}


@Composable
fun ChatMessageBubble(text: String, isUser: Boolean) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
  ) {
    if (isUser) {
      // User's message: Display with a bubble
      Text(
        text = text,
        modifier = Modifier
          .widthIn(max = 300.dp)
          .background(PlayerAction, RoundedCornerShape(20.dp, 20.dp, 4.dp, 20.dp))
          .padding(12.dp),
        color = TextPrimary
      )
    } else {
      // Gemini's message: Display as plain text on the background
      Column(horizontalAlignment = Alignment.Start) {
        GeminiIcon(
          modifier = Modifier
            .padding(top = 14.dp, start = 4.dp)
            .size(18.dp)
        )
        Text(
          text = text,
          modifier = Modifier
            .widthIn(max = 300.dp)
            .padding(12.dp),
          color = TextPrimary
        )
      }
    }
  }
}

/* Text field for the prompt input */
@Composable
fun PromptInput(modifier: Modifier = Modifier) {
  Column(modifier = modifier.fillMaxWidth()) {
    TextField(
      value = "",
      placeholder = { Text("Ask Gemini...", color = TextSecondary) },
      onValueChange = { },
      shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
      modifier = Modifier
        .fillMaxWidth()
        .heightIn(min = 80.dp),
      colors = TextFieldDefaults.colors(
        focusedContainerColor = InputBackground,
        unfocusedContainerColor = InputBackground,
        focusedTextColor = TextPrimary,
        unfocusedTextColor = TextPrimary,
        cursorColor = TextPrimary,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent
      )
    )

    // This Spacer sits behind the system navigation bar and paints it
    // with the same color as the Input, creating a seamless look.
    Spacer(
      modifier = Modifier
        .fillMaxWidth()
        .windowInsetsBottomHeight(WindowInsets.navigationBars)
        .background(InputBackground)
    )
  }
}
