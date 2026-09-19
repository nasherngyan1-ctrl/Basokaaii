package com.example.ui.screens

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.R
import com.example.data.local.MessageEntity
import com.example.model.AppLanguage
import com.example.model.Localization
import com.example.ui.components.BasokaElectricBackground
import com.example.ui.components.ChatInputBar
import com.example.ui.components.FullscreenImageDialog
import com.example.ui.components.GenerationErrorCard
import com.example.ui.components.ImageGeneratingBubble
import com.example.ui.components.MessageBubble
import com.example.ui.components.StreamingAssistantBubble
import com.example.ui.theme.DarkNavyCanvas
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DarkSurfaceDeep
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.GlowCyan
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.viewmodel.ChatUiState
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(
  messages: List<MessageEntity>,
  inputText: String,
  chatUiState: ChatUiState,
  onInputTextChanged: (String) -> Unit,
  onSendMessage: () -> Unit,
  onStopGeneration: () -> Unit,
  onRetry: () -> Unit,
  onMicrophoneClick: () -> Unit = {},
  onNewConversation: () -> Unit,
  onOpenHistory: () -> Unit,
  onOpenSettings: () -> Unit,
  onOpenProfile: () -> Unit,
  onLockApp: () -> Unit,
  onLanguageCycle: () -> Unit,
  currentLanguage: AppLanguage,
  strings: Localization.AppStrings,
  toastNotice: String?,
  onClearToastNotice: () -> Unit,
  isSpeaking: Boolean = false,
  activeSpeakingMessageId: Long? = null,
  onToggleSpeakMessage: (Long, String) -> Unit = { _, _ -> },
  onStopSpeaking: () -> Unit = {},
  onSpeechInputResult: (String) -> Unit = {},
  isImageMode: Boolean = false,
  onToggleImageMode: () -> Unit = {},
  onSaveImage: (String) -> Unit = {},
  onShareImage: (String) -> Unit = {},
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val listState = rememberLazyListState()
  val coroutineScope = rememberCoroutineScope()
  val snackbarHostState = remember { SnackbarHostState() }
  val clipboardManager = LocalClipboardManager.current

  var isSpeechListening by remember { mutableStateOf(false) }
  var fullscreenImageState by remember { mutableStateOf<Pair<String, String?>?>(null) }

  val speechLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.StartActivityForResult()
  ) { result ->
    isSpeechListening = false
    if (result.resultCode == Activity.RESULT_OK) {
      val data = result.data
      val spoken = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
      if (!spoken.isNullOrBlank()) {
        onSpeechInputResult(spoken)
      }
    }
  }

  fun launchSpeechRecognizer() {
    try {
      val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        val langTag = when (currentLanguage) {
          AppLanguage.KURDISH_SORANI -> "ckb"
          AppLanguage.ARABIC -> "ar"
          AppLanguage.ENGLISH -> "en"
        }
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, langTag)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, langTag)
        putExtra(RecognizerIntent.EXTRA_PROMPT, strings.speechListeningPrompt)
      }
      isSpeechListening = true
      speechLauncher.launch(intent)
    } catch (e: Exception) {
      isSpeechListening = false
      coroutineScope.launch {
        snackbarHostState.showSnackbar(strings.speechServiceNotAvailable)
      }
    }
  }

  val micPermissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    if (isGranted) {
      launchSpeechRecognizer()
    } else {
      coroutineScope.launch {
        snackbarHostState.showSnackbar(strings.micPermissionNeeded)
      }
    }
  }

  fun handleMicClick() {
    val permissionCheck = ContextCompat.checkSelfPermission(
      context,
      Manifest.permission.RECORD_AUDIO
    )
    if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
      launchSpeechRecognizer()
    } else {
      micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }
  }

  val isGenerating = chatUiState is ChatUiState.Generating || chatUiState is ChatUiState.GeneratingImage

  // Auto-scroll when messages update or generating changes
  LaunchedEffect(messages.size, isGenerating) {
    val totalCount = messages.size + if (isGenerating || chatUiState is ChatUiState.Error) 1 else 0
    if (totalCount > 0) {
      listState.animateScrollToItem(totalCount - 1)
    }
  }

  LaunchedEffect(toastNotice) {
    if (toastNotice != null) {
      snackbarHostState.showSnackbar(toastNotice)
      onClearToastNotice()
    }
  }

  BasokaElectricBackground(modifier = modifier) {
    Box(modifier = Modifier.fillMaxSize()) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .statusBarsPadding()
          .navigationBarsPadding()
          .imePadding()
      ) {
        // Top App Bar with Branding & Navigation
        ChatTopBar(
          strings = strings,
          currentLanguage = currentLanguage,
          onLanguageCycle = onLanguageCycle,
          onNewConversation = onNewConversation,
          onOpenHistory = onOpenHistory,
          onOpenSettings = onOpenSettings,
          onOpenProfile = onOpenProfile,
          onLockApp = onLockApp
        )

        // Chat Conversation Stream
        Box(
          modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
        ) {
          if (messages.isEmpty() && chatUiState is ChatUiState.Idle) {
            EmptyChatGreeting(
              strings = strings,
              onOpenSettings = onOpenSettings,
              onSelectSuggestion = { prompt, isImage ->
                onInputTextChanged(prompt)
                if (isImage && !isImageMode) {
                  onToggleImageMode()
                }
              },
              modifier = Modifier.align(Alignment.Center)
            )
          } else {
            LazyColumn(
              state = listState,
              modifier = Modifier.fillMaxSize(),
              verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              items(messages, key = { it.id }) { msg ->
                MessageBubble(
                  message = msg,
                  strings = strings,
                  onOpenSettings = onOpenSettings,
                  onCopyText = { copied ->
                    clipboardManager.setText(AnnotatedString(copied))
                    coroutineScope.launch {
                      snackbarHostState.showSnackbar(strings.copiedToClipboard)
                    }
                  },
                  isSpeaking = isSpeaking && activeSpeakingMessageId == msg.id,
                  onToggleSpeak = {
                    onToggleSpeakMessage(msg.id, msg.text)
                  },
                  onSaveImage = onSaveImage,
                  onShareImage = onShareImage,
                  onViewFullscreen = { uri, prompt ->
                    fullscreenImageState = Pair(uri, prompt)
                  }
                )
              }

              // Real-time live streaming text response bubble
              if (chatUiState is ChatUiState.Generating) {
                item(key = "generating_bubble") {
                  StreamingAssistantBubble(
                    partialText = chatUiState.partialText,
                    strings = strings,
                    onStop = onStopGeneration
                  )
                }
              }

              // Real image generation progress bubble
              if (chatUiState is ChatUiState.GeneratingImage) {
                item(key = "generating_image_bubble") {
                  ImageGeneratingBubble(
                    prompt = chatUiState.prompt,
                    strings = strings,
                    onStop = onStopGeneration
                  )
                }
              }

              // Error or configuration needed state card
              if (chatUiState is ChatUiState.Error) {
                item(key = "error_card") {
                  GenerationErrorCard(
                    errorMessage = chatUiState.message,
                    strings = strings,
                    onRetry = onRetry,
                    onOpenSettings = onOpenSettings
                  )
                }
              }
            }
          }
        }

        // Active Audio Playback Bar
        AnimatedVisibility(
          visible = isSpeaking,
          enter = fadeIn(),
          exit = fadeOut()
        ) {
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 14.dp, vertical = 4.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceDeep),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f))
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                  contentDescription = null,
                  tint = NeonCyan,
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = strings.audioPlaybackActive,
                  color = NeonCyan,
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Medium
                )
              }
              IconButton(
                onClick = onStopSpeaking,
                modifier = Modifier
                  .size(28.dp)
                  .testTag("stop_audio_bar_button")
              ) {
                Icon(
                  imageVector = Icons.Default.Stop,
                  contentDescription = strings.stopSpeaking,
                  tint = Color.White,
                  modifier = Modifier.size(18.dp)
                )
              }
            }
          }
        }

        // Message Input Bar with Image Generation mode support
        ChatInputBar(
          value = inputText,
          onValueChange = onInputTextChanged,
          onSend = onSendMessage,
          onStop = onStopGeneration,
          isGenerating = isGenerating,
          onMicClick = { handleMicClick() },
          isListening = isSpeechListening,
          isImageMode = isImageMode,
          onToggleImageMode = onToggleImageMode,
          strings = strings
        )
      }

      // SnackBar for mic placeholder notice or copy confirmation
      SnackbarHost(
        hostState = snackbarHostState,
        modifier = Modifier
          .align(Alignment.BottomCenter)
          .padding(bottom = 70.dp)
      )

      // Fullscreen Image Viewer Dialog
      fullscreenImageState?.let { (uri, prompt) ->
        FullscreenImageDialog(
          imageUri = uri,
          prompt = prompt,
          strings = strings,
          onDismiss = { fullscreenImageState = null },
          onSaveImage = onSaveImage,
          onShareImage = onShareImage
        )
      }
    }
  }
}

@Composable
fun ChatTopBar(
  strings: Localization.AppStrings,
  currentLanguage: AppLanguage,
  onLanguageCycle: () -> Unit,
  onNewConversation: () -> Unit,
  onOpenHistory: () -> Unit,
  onOpenSettings: () -> Unit,
  onOpenProfile: () -> Unit,
  onLockApp: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 14.dp, vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Brand Avatar / Icon
    Box(
      modifier = Modifier
        .size(38.dp)
        .clip(CircleShape)
        .background(DarkSurfaceCard)
        .border(
          width = 1.dp,
          brush = Brush.linearGradient(listOf(NeonCyan, ElectricViolet)),
          shape = CircleShape
        )
        .clickable { onOpenProfile() },
      contentAlignment = Alignment.Center
    ) {
      Image(
        painter = painterResource(id = R.drawable.ic_basoka_icon),
        contentDescription = strings.appName,
        modifier = Modifier.size(24.dp)
      )
    }

    Spacer(modifier = Modifier.width(10.dp))

    // Title & Status
    Column(
      modifier = Modifier
        .weight(1f)
        .clickable { onOpenProfile() }
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = strings.appName,
          color = TextPrimary,
          fontSize = 17.sp,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(6.dp))
        // Pulse indicator
        Box(
          modifier = Modifier
            .size(7.dp)
            .clip(CircleShape)
            .background(NeonCyan)
        )
      }
      Text(
        text = strings.appSubtitle,
        color = TextTertiary,
        fontSize = 11.sp
      )
    }

    // Language Quick Selector
    Box(
      modifier = Modifier
        .clip(RoundedCornerShape(12.dp))
        .background(DarkSurfaceDeep)
        .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(12.dp))
        .clickable { onLanguageCycle() }
        .padding(horizontal = 8.dp, vertical = 6.dp)
        .testTag("quick_language_switch_button"),
      contentAlignment = Alignment.Center
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.Language,
          contentDescription = null,
          tint = NeonCyan,
          modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = currentLanguage.code.uppercase(),
          color = NeonCyan,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }

    Spacer(modifier = Modifier.width(4.dp))

    // New Conversation Button
    IconButton(
      onClick = onNewConversation,
      modifier = Modifier
        .size(36.dp)
        .testTag("new_chat_top_button")
    ) {
      Icon(
        imageVector = Icons.Default.Add,
        contentDescription = strings.newChat,
        tint = TextPrimary,
        modifier = Modifier.size(20.dp)
      )
    }

    // History Button
    IconButton(
      onClick = onOpenHistory,
      modifier = Modifier
        .size(36.dp)
        .testTag("history_top_button")
    ) {
      Icon(
        imageVector = Icons.Default.History,
        contentDescription = strings.history,
        tint = TextPrimary,
        modifier = Modifier.size(20.dp)
      )
    }

    // Quick Lock App Button
    IconButton(
      onClick = onLockApp,
      modifier = Modifier
        .size(36.dp)
        .testTag("lock_app_top_button")
    ) {
      Icon(
        imageVector = Icons.Default.Lock,
        contentDescription = strings.appLockTitle,
        tint = TextPrimary,
        modifier = Modifier.size(18.dp)
      )
    }

    // Settings Button
    IconButton(
      onClick = onOpenSettings,
      modifier = Modifier
        .size(36.dp)
        .testTag("settings_top_button")
    ) {
      Icon(
        imageVector = Icons.Default.Settings,
        contentDescription = strings.settings,
        tint = TextPrimary,
        modifier = Modifier.size(20.dp)
      )
    }
  }
}

@Composable
fun EmptyChatGreeting(
  strings: Localization.AppStrings,
  onOpenSettings: () -> Unit,
  onSelectSuggestion: (String, Boolean) -> Unit = { _, _ -> },
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .padding(horizontal = 24.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Glowing central logo badge
    Box(
      modifier = Modifier
        .size(76.dp)
        .clip(CircleShape)
        .background(DarkSurfaceDeep)
        .border(
          width = 1.5.dp,
          brush = Brush.linearGradient(listOf(NeonCyan, ElectricViolet)),
          shape = CircleShape
        ),
      contentAlignment = Alignment.Center
    ) {
      Image(
        painter = painterResource(id = R.drawable.ic_basoka_icon),
        contentDescription = null,
        modifier = Modifier.size(46.dp)
      )
    }

    Spacer(modifier = Modifier.height(18.dp))

    Text(
      text = strings.welcomeTitle,
      color = TextPrimary,
      fontSize = 20.sp,
      fontWeight = FontWeight.Bold,
      textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
      text = strings.welcomeDesc,
      color = TextSecondary,
      fontSize = 14.sp,
      textAlign = TextAlign.Center,
      lineHeight = 20.sp
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Quick suggestion prompt chips
    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      // Image generation prompt chip
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(DarkSurfaceCard)
          .border(
            1.dp,
            Brush.horizontalGradient(listOf(ElectricViolet.copy(alpha = 0.5f), NeonCyan.copy(alpha = 0.4f))),
            RoundedCornerShape(12.dp)
          )
          .clickable {
            onSelectSuggestion("وێنەی شارێکی داهاتوو لە شاخەکانی کوردستان لە ساڵی ٢٠٧٠ دروست بکە", true)
          }
          .padding(horizontal = 14.dp, vertical = 10.dp)
          .testTag("suggestion_image_chip")
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(text = "🎨", fontSize = 16.sp)
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "وێنەی شارێکی داهاتوو لە شاخەکانی کوردستان لە ساڵی ٢٠٧٠ دروست بکە",
            color = NeonCyan,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
          )
        }
      }

      // Conversation prompt chip
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(DarkSurfaceCard)
          .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(12.dp))
          .clickable {
            onSelectSuggestion("باسی مێژووی پڕشنگداری نەتەوەی کورد و داهاتووی تەکنەلۆژیا بکە", false)
          }
          .padding(horizontal = 14.dp, vertical = 10.dp)
          .testTag("suggestion_chat_chip")
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(text = "💡", fontSize = 16.sp)
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "باسی مێژووی پڕشنگداری نەتەوەی کورد و داهاتووی تەکنەلۆژیا بکە",
            color = TextSecondary,
            fontSize = 12.sp
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Architecture Readiness Notice Card
    Card(
      shape = RoundedCornerShape(14.dp),
      colors = CardDefaults.cardColors(
        containerColor = DarkSurfaceCard.copy(alpha = 0.85f)
      ),
      border = androidx.compose.foundation.BorderStroke(
        1.dp,
        NeonCyan.copy(alpha = 0.25f)
      ),
      modifier = Modifier
        .fillMaxWidth()
        .clickable { onOpenSettings() }
        .testTag("welcome_architecture_card")
    ) {
      Row(
        modifier = Modifier.padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.Default.AutoAwesome,
          contentDescription = null,
          tint = NeonCyan,
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = strings.initialNoticeTitle,
            color = NeonCyan,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = strings.initialNoticeBody,
            color = TextTertiary,
            fontSize = 12.sp,
            lineHeight = 16.sp
          )
        }
      }
    }
  }
}
