package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import com.example.data.local.BasokaDatabase
import com.example.data.repository.AuthRepository
import com.example.data.repository.BasokaAiRepositoryImpl
import com.example.data.repository.ConversationRepository
import com.example.data.repository.MemoryRepository
import com.example.data.repository.UserSettingsRepository
import com.example.model.AppLanguage
import com.example.model.Localization
import com.example.security.SecureSessionManager
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.BiometricLockScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.MemoryScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.BasokaTheme
import com.example.ui.theme.DarkNavyCanvas
import com.example.viewmodel.AuthScreenMode
import com.example.viewmodel.BasokaViewModel
import com.example.voice.BasokaVoiceManager

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val context = LocalContext.current
      val viewModel = remember {
        val database = BasokaDatabase.getDatabase(context)
        val convRepo = ConversationRepository(database.basokaDao())
        val aiRepo = BasokaAiRepositoryImpl()
        val settingsRepo = UserSettingsRepository(context)
        val sessionManager = SecureSessionManager(context)
        val authRepo = AuthRepository(database.basokaDao(), sessionManager)
        val memoryRepo = MemoryRepository(database.basokaDao())
        BasokaViewModel(convRepo, aiRepo, settingsRepo, authRepo, memoryRepo, context.applicationContext)
      }

      BasokaApp(viewModel = viewModel)
    }
  }
}

@Composable
fun BasokaApp(viewModel: BasokaViewModel) {
  val context = LocalContext.current
  val settings by viewModel.settings.collectAsState()
  val currentUser by viewModel.currentUser.collectAsState()
  val isAuthenticated by viewModel.isAuthenticated.collectAsState()
  val isAppLocked by viewModel.isAppLocked.collectAsState()
  val authScreenMode by viewModel.authScreenMode.collectAsState()
  val authError by viewModel.authError.collectAsState()

  val conversations by viewModel.conversations.collectAsState()
  val activeConversationId by viewModel.activeConversationId.collectAsState()
  val messages by viewModel.messages.collectAsState()
  val inputText by viewModel.inputText.collectAsState()
  val chatUiState by viewModel.chatUiState.collectAsState()
  val isHistoryOpen by viewModel.isHistoryOpen.collectAsState()
  val isSettingsOpen by viewModel.isSettingsOpen.collectAsState()
  val isProfileOpen by viewModel.isProfileOpen.collectAsState()
  val isMemoryOpen by viewModel.isMemoryOpen.collectAsState()
  val memories by viewModel.memories.collectAsState()
  val toastNotice by viewModel.toastNotice.collectAsState()
  val isImageMode by viewModel.isImageMode.collectAsState()

  val voiceManager = remember {
    BasokaVoiceManager(context) { notice ->
      viewModel.showToast(notice)
    }
  }
  DisposableEffect(Unit) {
    onDispose {
      voiceManager.release()
    }
  }
  val isSpeaking by voiceManager.isSpeaking.collectAsState()
  val activeSpeakingMessageId by voiceManager.activeMessageId.collectAsState()

  val strings = Localization.getStrings(settings.language)

  BasokaTheme(darkTheme = settings.isDarkMode) {
    // Dynamic RTL support based on selected language (Kurdish Sorani & Arabic -> RTL, English -> LTR)
    CompositionLocalProvider(LocalLayoutDirection provides settings.language.layoutDirection) {
      Surface(
        modifier = Modifier.fillMaxSize(),
        color = DarkNavyCanvas
      ) {
        val targetScreen = when {
          !isAuthenticated -> AppScreen.AUTH
          isAppLocked -> AppScreen.BIOMETRIC_LOCK
          isProfileOpen -> AppScreen.PROFILE
          isMemoryOpen -> AppScreen.MEMORIES
          isSettingsOpen -> AppScreen.SETTINGS
          isHistoryOpen -> AppScreen.HISTORY
          else -> AppScreen.CHAT
        }

        AnimatedContent(
          targetState = targetScreen,
          transitionSpec = {
            fadeIn() togetherWith fadeOut()
          },
          label = "screen_transition"
        ) { screen ->
          when (screen) {
            AppScreen.AUTH -> {
              AuthScreen(
                mode = authScreenMode,
                errorMessage = authError,
                currentUser = currentUser,
                onRegisterBiometrics = { activity, displayName ->
                  viewModel.registerWithBiometrics(activity, displayName, strings)
                },
                onLoginBiometrics = { activity ->
                  viewModel.loginWithBiometrics(activity, strings)
                },
                onSwitchMode = { viewModel.switchAuthMode(it) },
                onClearError = { viewModel.clearAuthError() },
                onResetAccount = { viewModel.resetAccount() },
                strings = strings
              )
            }
            AppScreen.BIOMETRIC_LOCK -> {
              BiometricLockScreen(
                errorMessage = authError,
                onTriggerBiometrics = { activity ->
                  viewModel.unlockWithBiometrics(activity, strings)
                },
                strings = strings
              )
            }
            AppScreen.PROFILE -> {
              ProfileScreen(
                user = currentUser,
                onBack = { viewModel.closeProfile() },
                onLogout = { viewModel.logout() },
                onOpenSecuritySettings = {
                  viewModel.closeProfile()
                  viewModel.openSettings()
                },
                strings = strings
              )
            }
            AppScreen.MEMORIES -> {
              MemoryScreen(
                memories = memories,
                currentUser = currentUser,
                strings = strings,
                onBack = { viewModel.closeMemories() },
                onAddMemory = { title, content ->
                  viewModel.addMemory(title, content, strings)
                },
                onUpdateMemory = { updated ->
                  viewModel.updateMemory(updated, strings)
                },
                onDeleteMemory = { id ->
                  viewModel.deleteMemory(id, strings)
                },
                onDeleteAllMemories = {
                  viewModel.deleteAllMemories(strings)
                }
              )
            }
            AppScreen.SETTINGS -> {
              SettingsScreen(
                settings = settings,
                currentUser = currentUser,
                onLanguageChange = { viewModel.setLanguage(it) },
                onDarkModeToggle = { viewModel.setDarkMode(it) },
                onBiometricToggle = { viewModel.setBiometricLock(it) },
                onPrivateDeviceToggle = { viewModel.setPrivateDeviceMode(it) },
                onLockTimeoutChange = { viewModel.setLockTimeoutMinutes(it) },
                onMemoryToggle = { viewModel.setMemoryEnabled(it) },
                onOpenMemories = { viewModel.openMemories() },
                onOpenProfile = { viewModel.openProfile() },
                onLockAppManually = { viewModel.lockAppManually() },
                onLogout = { viewModel.logout() },
                onBack = { viewModel.closeSettings() },
                strings = strings
              )
            }
            AppScreen.HISTORY -> {
              HistoryScreen(
                conversations = conversations,
                activeConversationId = activeConversationId,
                onSelectConversation = { viewModel.selectConversation(it) },
                onNewConversation = { viewModel.startNewConversation() },
                onDeleteConversation = { viewModel.deleteConversation(it) },
                onBack = { viewModel.closeHistory() },
                strings = strings
              )
            }
            AppScreen.CHAT -> {
              ChatScreen(
                messages = messages,
                inputText = inputText,
                chatUiState = chatUiState,
                onInputTextChanged = { viewModel.onInputTextChanged(it) },
                onSendMessage = { viewModel.sendMessage() },
                onStopGeneration = { viewModel.stopGeneration() },
                onRetry = { viewModel.retryLastMessage() },
                onMicrophoneClick = { viewModel.onMicrophoneClicked(strings.voiceTooltip) },
                onNewConversation = { viewModel.startNewConversation() },
                onOpenHistory = { viewModel.openHistory() },
                onOpenSettings = { viewModel.openSettings() },
                onOpenProfile = { viewModel.openProfile() },
                onLockApp = { viewModel.lockAppManually() },
                onLanguageCycle = {
                  val nextLang = when (settings.language) {
                    AppLanguage.KURDISH_SORANI -> AppLanguage.ARABIC
                    AppLanguage.ARABIC -> AppLanguage.ENGLISH
                    AppLanguage.ENGLISH -> AppLanguage.KURDISH_SORANI
                  }
                  viewModel.setLanguage(nextLang)
                },
                currentLanguage = settings.language,
                strings = strings,
                toastNotice = toastNotice,
                onClearToastNotice = { viewModel.clearToastNotice() },
                isSpeaking = isSpeaking,
                activeSpeakingMessageId = activeSpeakingMessageId,
                onToggleSpeakMessage = { id, text ->
                  voiceManager.toggleSpeak(id, text, strings.kurdishTtsUnavailableNotice)
                },
                onStopSpeaking = { voiceManager.stop() },
                onSpeechInputResult = { spoken ->
                  viewModel.onSpeechInputReceived(spoken)
                },
                isImageMode = isImageMode,
                onToggleImageMode = { viewModel.toggleImageMode() },
                onSaveImage = { uri -> viewModel.saveImageToGallery(uri, context, strings) },
                onShareImage = { uri -> viewModel.shareImage(uri, context) }
              )
            }
          }
        }
      }
    }
  }
}

enum class AppScreen {
  AUTH,
  BIOMETRIC_LOCK,
  CHAT,
  HISTORY,
  SETTINGS,
  PROFILE,
  MEMORIES
}

// Supporting composable for screenshot testing
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Text(text = "Hello $name!", modifier = modifier)
}
