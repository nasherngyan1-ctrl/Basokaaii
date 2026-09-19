package com.example.viewmodel

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.GeminiContent
import com.example.data.api.GeminiPart
import com.example.data.local.AiMemoryEntity
import com.example.data.local.ConversationEntity
import com.example.data.local.ImageStorageManager
import com.example.data.local.MessageEntity
import com.example.data.local.UserEntity
import com.example.data.repository.AiEngineStatus
import com.example.data.repository.AiRepository
import com.example.data.repository.AuthErrorType
import com.example.data.repository.AuthRepository
import com.example.data.repository.AuthResult
import com.example.data.repository.ConversationRepository
import com.example.data.repository.GenerationChunk
import com.example.data.repository.ImageGenerationResult
import com.example.data.repository.MemoryRepository
import com.example.data.repository.UserSettings
import com.example.data.repository.UserSettingsRepository
import com.example.model.AppLanguage
import com.example.model.Localization
import com.example.security.AuthSession
import com.example.security.BiometricStatus
import com.example.security.NativeBiometricHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface ChatUiState {
  object Idle : ChatUiState
  data class Generating(val partialText: String) : ChatUiState
  data class GeneratingImage(val prompt: String) : ChatUiState
  data class Error(
    val message: String,
    val canRetry: Boolean = true,
    val isApiKeyMissing: Boolean = false,
    val isImageError: Boolean = false
  ) : ChatUiState
}

enum class AuthScreenMode {
  LOGIN,
  REGISTER,
  BIOMETRIC_LOCK,
  PROFILE
}

@OptIn(ExperimentalCoroutinesApi::class)
class BasokaViewModel(
  private val conversationRepo: ConversationRepository,
  private val aiRepo: AiRepository,
  private val settingsRepo: UserSettingsRepository,
  private val authRepo: AuthRepository,
  private val memoryRepo: MemoryRepository,
  private val appContext: Context
) : ViewModel() {

  val settings: StateFlow<UserSettings> = settingsRepo.settings

  // Auth & Session State
  val currentUser: StateFlow<UserEntity?> = authRepo.personalUserFlow
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = null
    )

  private val _isAuthenticated = MutableStateFlow(false)
  val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

  private val _isAppLocked = MutableStateFlow(false)
  val isAppLocked: StateFlow<Boolean> = _isAppLocked.asStateFlow()

  private val _authScreenMode = MutableStateFlow(AuthScreenMode.LOGIN)
  val authScreenMode: StateFlow<AuthScreenMode> = _authScreenMode.asStateFlow()

  private val _authError = MutableStateFlow<String?>(null)
  val authError: StateFlow<String?> = _authError.asStateFlow()

  // Personal Memories (Isolated strictly to current user)
  val memories: StateFlow<List<AiMemoryEntity>> = currentUser
    .flatMapLatest { user ->
      if (user != null) {
        memoryRepo.getMemoriesForUser(user.id)
      } else {
        flowOf(emptyList())
      }
    }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList()
    )

  private val _isMemoryOpen = MutableStateFlow(false)
  val isMemoryOpen: StateFlow<Boolean> = _isMemoryOpen.asStateFlow()

  // Conversations & Messages (Isolated strictly to current user)
  val conversations: StateFlow<List<ConversationEntity>> = currentUser
    .flatMapLatest { user ->
      if (user != null) {
        conversationRepo.getConversationsForUser(user.id)
      } else {
        flowOf(emptyList())
      }
    }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList()
    )

  private val _activeConversationId = MutableStateFlow<Long?>(null)
  val activeConversationId: StateFlow<Long?> = _activeConversationId.asStateFlow()

  val messages: StateFlow<List<MessageEntity>> = _activeConversationId
    .flatMapLatest { id ->
      if (id != null) {
        conversationRepo.getMessages(id)
      } else {
        flowOf(emptyList())
      }
    }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList()
    )

  private val _inputText = MutableStateFlow("")
  val inputText: StateFlow<String> = _inputText.asStateFlow()

  private val _chatUiState = MutableStateFlow<ChatUiState>(ChatUiState.Idle)
  val chatUiState: StateFlow<ChatUiState> = _chatUiState.asStateFlow()

  private val _isImageMode = MutableStateFlow(false)
  val isImageMode: StateFlow<Boolean> = _isImageMode.asStateFlow()

  private val _isHistoryOpen = MutableStateFlow(false)
  val isHistoryOpen: StateFlow<Boolean> = _isHistoryOpen.asStateFlow()

  private val _isSettingsOpen = MutableStateFlow(false)
  val isSettingsOpen: StateFlow<Boolean> = _isSettingsOpen.asStateFlow()

  private val _isProfileOpen = MutableStateFlow(false)
  val isProfileOpen: StateFlow<Boolean> = _isProfileOpen.asStateFlow()

  private val _toastNotice = MutableStateFlow<String?>(null)
  val toastNotice: StateFlow<String?> = _toastNotice.asStateFlow()

  private var activeGenerationJob: Job? = null
  private var lastFailedPrompt: String? = null

  // System instruction defining Basoka AI
  private val basokaSystemInstruction: String = """
    You are Basoka AI (باسۆکا AI), a private, intelligent, and highly capable personal AI assistant running on the user's Android phone.
    
    CORE IDENTITY & BEHAVIOR:
    - Primary language: Sorani Kurdish (کوردیی سۆرانی). Always maintain deep fluency, natural syntax, and warm respectful tone in Sorani Kurdish.
    - Multilingual Capability: You fluently understand and speak Kurdish (Sorani and Kurmanji), Arabic (العربية), English, Persian (فارسی), Turkish (Türkçe), and other international languages.
    - Automatic Language Detection: Detect the language used in the user's prompt and respond primarily in that exact language with native fluency. If the user writes in Kurdish Sorani, reply in natural Kurdish Sorani with proper Kurdish script (ئ، ە، ێ، ۆ، ڵ، ڕ، ڤ، گ، چ، پ، ژ).
    - Privacy & Personal Assistant: You are a private personal companion on this device. You provide accurate, concise, helpful, and organized answers.
    - Formatting: Use clean markdown, bullet points, and short readable paragraphs optimized for mobile phone screens.
  """.trimIndent()

  init {
    viewModelScope.launch {
      // Check session and app lock state upon startup
      checkInitialAuthAndLockState()
    }

    viewModelScope.launch {
      conversations.collect { list ->
        if (_activeConversationId.value == null && list.isNotEmpty()) {
          _activeConversationId.value = list.first().id
        }
      }
    }
  }

  private suspend fun checkInitialAuthAndLockState() {
    val user = authRepo.getPersonalUser()

    if (user == null) {
      // First setup: user has not registered fingerprint for Basoka AI yet
      _isAuthenticated.value = false
      _authScreenMode.value = AuthScreenMode.REGISTER
    } else {
      // Returning user on later app launch: show clean biometric authentication screen
      _isAuthenticated.value = false
      _authScreenMode.value = AuthScreenMode.LOGIN
    }
  }

  // --- Authentication Actions ---

  /**
   * Registers a user for Basoka AI exclusively using Android BiometricPrompt.
   * Completely email-free and password-free.
   */
  fun registerWithBiometrics(
    activity: Activity,
    displayName: String,
    strings: Localization.AppStrings
  ) {
    _authError.value = null
    val biometricStatus = NativeBiometricHelper.canAuthenticateBiometric(activity)
    when (biometricStatus) {
      is BiometricStatus.NoHardware -> {
        _authError.value = strings.biometricNoHardware
        return
      }
      is BiometricStatus.NoneEnrolled -> {
        _authError.value = strings.biometricNoneEnrolled
        return
      }
      is BiometricStatus.HardwareUnavailable -> {
        _authError.value = strings.biometricHardwareUnavailable
        return
      }
      is BiometricStatus.Available -> {
        // Launch official Android BiometricPrompt
        NativeBiometricHelper.authenticate(
          activity = activity,
          title = strings.biometricSetupTitle,
          subtitle = strings.biometricPromptSubtitle,
          cancelText = strings.biometricPromptCancel,
          onSuccess = {
            viewModelScope.launch {
              val result = authRepo.registerBiometric(displayName)
              when (result) {
                is AuthResult.Success -> {
                  _isAuthenticated.value = true
                  _isAppLocked.value = false
                  _toastNotice.value = strings.biometricRegistrationSuccess
                }
                is AuthResult.Error -> {
                  _authError.value = mapAuthError(result.errorType, strings)
                }
              }
            }
          },
          onError = { errorMsg ->
            _authError.value = errorMsg
          }
        )
      }
    }
  }

  /**
   * Authenticates the user on later app launches using device's enrolled fingerprint.
   * Invokes native Android BiometricPrompt.
   */
  fun loginWithBiometrics(
    activity: Activity,
    strings: Localization.AppStrings
  ) {
    _authError.value = null
    val biometricStatus = NativeBiometricHelper.canAuthenticateBiometric(activity)
    when (biometricStatus) {
      is BiometricStatus.NoHardware -> {
        _authError.value = strings.biometricNoHardware
        return
      }
      is BiometricStatus.NoneEnrolled -> {
        _authError.value = strings.biometricNoneEnrolled
        return
      }
      is BiometricStatus.HardwareUnavailable -> {
        _authError.value = strings.biometricHardwareUnavailable
        return
      }
      is BiometricStatus.Available -> {
        // Launch official Android BiometricPrompt
        NativeBiometricHelper.authenticate(
          activity = activity,
          title = strings.biometricAuthTitle,
          subtitle = strings.biometricPromptSubtitle,
          cancelText = strings.biometricPromptCancel,
          onSuccess = {
            viewModelScope.launch {
              val result = authRepo.loginBiometric()
              when (result) {
                is AuthResult.Success -> {
                  _isAuthenticated.value = true
                  _isAppLocked.value = false
                  _toastNotice.value = strings.biometricLoginSuccess
                }
                is AuthResult.Error -> {
                  _authError.value = mapAuthError(result.errorType, strings)
                }
              }
            }
          },
          onError = { errorMsg ->
            _authError.value = errorMsg
          }
        )
      }
    }
  }

  fun resetAccount() {
    viewModelScope.launch {
      stopGeneration()
      authRepo.deletePersonalAccount()
      _isAuthenticated.value = false
      _isAppLocked.value = false
      _isSettingsOpen.value = false
      _isProfileOpen.value = false
      _isMemoryOpen.value = false
      _isHistoryOpen.value = false
      _activeConversationId.value = null
      _inputText.value = ""
      _authScreenMode.value = AuthScreenMode.REGISTER
    }
  }

  fun register(
    email: String,
    displayName: String,
    password: String,
    confirmPassword: String,
    strings: Localization.AppStrings
  ) {
    _authError.value = null
    viewModelScope.launch {
      val result = authRepo.register(email, displayName, password, confirmPassword)
      when (result) {
        is AuthResult.Success -> {
          _isAuthenticated.value = true
          _isAppLocked.value = false
          _toastNotice.value = strings.accountCreatedSuccess
        }
        is AuthResult.Error -> {
          _authError.value = mapAuthError(result.errorType, strings)
        }
      }
    }
  }

  fun login(
    email: String,
    password: String,
    strings: Localization.AppStrings
  ) {
    _authError.value = null
    viewModelScope.launch {
      val result = authRepo.login(email, password)
      when (result) {
        is AuthResult.Success -> {
          _isAuthenticated.value = true
          if (settings.value.isBiometricLockEnabled) {
            _isAppLocked.value = true
          } else {
            _isAppLocked.value = false
          }
        }
        is AuthResult.Error -> {
          _authError.value = mapAuthError(result.errorType, strings)
        }
      }
    }
  }

  fun logout() {
    viewModelScope.launch {
      stopGeneration()
      authRepo.logout()
      _isAuthenticated.value = false
      _isAppLocked.value = false
      _isSettingsOpen.value = false
      _isProfileOpen.value = false
      _isMemoryOpen.value = false
      _isHistoryOpen.value = false
      _activeConversationId.value = null
      _inputText.value = ""
      _authScreenMode.value = AuthScreenMode.LOGIN
    }
  }

  fun switchAuthMode(mode: AuthScreenMode) {
    _authError.value = null
    _authScreenMode.value = mode
  }

  fun clearAuthError() {
    _authError.value = null
  }

  // --- Biometric & App Lock Actions ---

  fun unlockWithBiometrics(
    activity: Activity,
    strings: Localization.AppStrings
  ) {
    NativeBiometricHelper.authenticate(
      activity = activity,
      title = strings.biometricUnlockTitle,
      subtitle = strings.biometricUnlockSubtitle,
      cancelText = strings.close,
      onSuccess = {
        _isAppLocked.value = false
      },
      onError = { errMsg ->
        _authError.value = errMsg
      }
    )
  }

  fun unlockWithPasswordFallback(password: String, strings: Localization.AppStrings) {
    val user = currentUser.value
    if (user != null) {
      val valid = com.example.security.SecurityUtils.verifyPassword(
        password = password,
        salt = user.passwordSalt,
        expectedHash = user.passwordHash
      )
      if (valid) {
        _isAppLocked.value = false
        _authError.value = null
      } else {
        _authError.value = strings.errorIncorrectPassword
      }
    }
  }

  fun lockAppManually() {
    _isAppLocked.value = true
  }

  // --- Chat & Conversation Actions ---

  fun onInputTextChanged(newText: String) {
    _inputText.value = newText
  }

  fun selectConversation(id: Long) {
    stopGeneration()
    _activeConversationId.value = id
    _isHistoryOpen.value = false
    _chatUiState.value = ChatUiState.Idle
  }

  fun startNewConversation() {
    stopGeneration()
    viewModelScope.launch {
      val defaultTitle = when (settings.value.language) {
        AppLanguage.KURDISH_SORANI -> "وتووێژی نوێ"
        AppLanguage.ARABIC -> "محادثة جديدة"
        AppLanguage.ENGLISH -> "New Conversation"
      }
      val userId = currentUser.value?.id ?: "local_personal_user"
      val newId = conversationRepo.createConversation(defaultTitle, userId)
      _activeConversationId.value = newId
      _isHistoryOpen.value = false
      _chatUiState.value = ChatUiState.Idle
    }
  }

  fun toggleImageMode() {
    _isImageMode.value = !_isImageMode.value
  }

  fun setImageMode(enabled: Boolean) {
    _isImageMode.value = enabled
  }

  fun triggerImageGeneration(prompt: String) {
    val cleanPrompt = prompt.trim()
    if (cleanPrompt.isEmpty()) return
    if (_chatUiState.value is ChatUiState.Generating || _chatUiState.value is ChatUiState.GeneratingImage) return

    _inputText.value = ""
    _isImageMode.value = false
    lastFailedPrompt = cleanPrompt

    viewModelScope.launch {
      var convId = _activeConversationId.value
      val user = currentUser.value
      val userId = user?.id ?: "local_personal_user"

      if (convId == null) {
        val initialTitle = "وێنە: " + cleanPrompt.take(28)
        convId = conversationRepo.createConversation(initialTitle, userId)
        _activeConversationId.value = convId
      }

      // Save user prompt message to Room DB
      conversationRepo.saveUserMessage(convId, cleanPrompt)

      // Start actual image generation process
      startImageGeneration(convId, cleanPrompt)
    }
  }

  private fun startImageGeneration(convId: Long, prompt: String) {
    activeGenerationJob?.cancel()
    _chatUiState.value = ChatUiState.GeneratingImage(prompt = prompt)

    activeGenerationJob = viewModelScope.launch {
      if (!aiRepo.isApiKeyConfigured()) {
        _chatUiState.value = ChatUiState.Error(
          message = "GEMINI_API_KEY_NOT_CONFIGURED",
          canRetry = true,
          isApiKeyMissing = true,
          isImageError = true
        )
        return@launch
      }

      val result = aiRepo.generateImage(prompt)
      when (result) {
        is ImageGenerationResult.Success -> {
          // Save generated image bytes safely into app's private files
          val localUri = ImageStorageManager.saveInternalImage(
            context = appContext,
            imageBytes = result.imageBytes,
            prefix = "basoka_gen"
          )

          // Save assistant image message in Room DB
          val caption = result.caption ?: ""
          conversationRepo.saveAssistantImageMessage(
            conversationId = convId,
            caption = caption,
            imageUri = localUri,
            prompt = prompt,
            status = "COMPLETED"
          )
          _chatUiState.value = ChatUiState.Idle
        }
        is ImageGenerationResult.Error -> {
          _chatUiState.value = ChatUiState.Error(
            message = result.message,
            canRetry = true,
            isApiKeyMissing = result.isApiKeyMissing,
            isImageError = true
          )
        }
      }
    }
  }

  fun sendMessage() {
    val text = _inputText.value.trim()
    if (text.isEmpty()) return
    if (_chatUiState.value is ChatUiState.Generating || _chatUiState.value is ChatUiState.GeneratingImage) return

    // If Image Generation Mode is active OR the user prompted to generate an image
    if (_isImageMode.value || isImageGenerationPrompt(text)) {
      triggerImageGeneration(text)
      return
    }

    _inputText.value = ""
    lastFailedPrompt = text

    viewModelScope.launch {
      var convId = _activeConversationId.value
      val user = currentUser.value
      val userId = user?.id ?: "local_personal_user"

      if (convId == null) {
        val initialTitle = text.take(35)
        convId = conversationRepo.createConversation(initialTitle, userId)
        _activeConversationId.value = convId
      }

      // 1. Check for explicit personal memory command (e.g. "ئەمە لەبیرت بێت...", "احفظ هذا...")
      if (settings.value.isMemoryEnabled) {
        val extractedMemory = memoryRepo.extractExplicitMemoryTrigger(text)
        if (extractedMemory != null) {
          val user = currentUser.value
          if (user != null) {
            val title = extractedMemory.take(30)
            memoryRepo.saveMemory(
              userId = user.id,
              content = extractedMemory,
              title = title
            )
          }
        }
      }

      // Save user message to Room database
      conversationRepo.saveUserMessage(convId, text)

      // Start streaming Gemini AI response
      startGeminiStreaming(convId)
    }
  }

  fun retryLastMessage() {
    val convId = _activeConversationId.value ?: return
    if (_chatUiState.value is ChatUiState.Generating || _chatUiState.value is ChatUiState.GeneratingImage) return

    val currentState = _chatUiState.value
    val isImageRetry = (currentState is ChatUiState.Error && currentState.isImageError) ||
      (lastFailedPrompt != null && isImageGenerationPrompt(lastFailedPrompt!!))

    if (isImageRetry && lastFailedPrompt != null) {
      startImageGeneration(convId, lastFailedPrompt!!)
    } else {
      startGeminiStreaming(convId)
    }
  }

  fun stopGeneration() {
    activeGenerationJob?.cancel()
    activeGenerationJob = null
    val currentState = _chatUiState.value
    if (currentState is ChatUiState.Generating) {
      val partial = currentState.partialText.trim()
      val convId = _activeConversationId.value
      if (convId != null && partial.isNotEmpty()) {
        viewModelScope.launch {
          conversationRepo.saveAssistantMessage(convId, partial, status = "COMPLETED")
        }
      }
    }
    _chatUiState.value = ChatUiState.Idle
  }

  fun saveImageToGallery(localUriString: String, context: Context, strings: Localization.AppStrings) {
    viewModelScope.launch {
      val result = ImageStorageManager.saveToDeviceGallery(context, localUriString)
      result.fold(
        onSuccess = {
          showToast(strings.imageSavedSuccess)
        },
        onFailure = { err ->
          showToast("${strings.imageSaveFailed}: ${err.message ?: ""}")
        }
      )
    }
  }

  fun shareImage(localUriString: String, context: Context) {
    try {
      val contentUri = ImageStorageManager.getShareableUri(context, localUriString)
      if (contentUri != null) {
        val intent = Intent(Intent.ACTION_SEND).apply {
          type = "image/png"
          putExtra(Intent.EXTRA_STREAM, contentUri)
          addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooser = Intent.createChooser(intent, "Share Image")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
      } else {
        showToast("Image file not found")
      }
    } catch (e: Exception) {
      showToast("Share failed: ${e.message}")
    }
  }

  private fun startGeminiStreaming(convId: Long) {
    activeGenerationJob?.cancel()
    _chatUiState.value = ChatUiState.Generating(partialText = "")

    activeGenerationJob = viewModelScope.launch {
      try {
        val existingMessages = conversationRepo.getMessagesSnapshot(convId)

        // Build Gemini conversation contents history
        val geminiContents = existingMessages.mapNotNull { msg ->
          val role = when (msg.sender) {
            "USER" -> "user"
            "ASSISTANT" -> "model"
            else -> null
          }
          role?.let {
            GeminiContent(
              role = it,
              parts = listOf(GeminiPart(text = msg.text))
            )
          }
        }

        // Dynamically include the authenticated user's explicitly saved personal memories
        val user = currentUser.value
        val userMemories = if (user != null && settings.value.isMemoryEnabled) {
          memoryRepo.getMemoriesSnapshot(user.id)
        } else {
          emptyList()
        }

        val dynamicInstruction = buildString {
          append(basokaSystemInstruction)
          if (userMemories.isNotEmpty()) {
            append("\n\nEXPLICIT USER PERSONAL MEMORIES (Stored with user's explicit consent):\n")
            userMemories.forEach { mem ->
              append("- ${mem.content}\n")
            }
            append("\nWhen relevant, seamlessly and accurately reference these explicit personal memories to assist the user.")
          }
          append("\n\nNOTE: If the user asked you to remember something (e.g. 'ئەمە لەبیرت بێت...'), warmly confirm in your response that you have securely saved it in their personal memory.")
        }

        val fullGeneratedText = StringBuilder()

        aiRepo.streamGenerateResponse(
          conversationHistory = geminiContents,
          systemInstructionText = dynamicInstruction
        ).collect { chunk ->
          when (chunk) {
            is GenerationChunk.TextChunk -> {
              fullGeneratedText.append(chunk.text)
              _chatUiState.value = ChatUiState.Generating(partialText = fullGeneratedText.toString())
            }
            is GenerationChunk.Completed -> {
              val finalText = chunk.fullText.ifEmpty { fullGeneratedText.toString() }
              conversationRepo.saveAssistantMessage(convId, finalText, status = "COMPLETED")
              _chatUiState.value = ChatUiState.Idle
            }
            is GenerationChunk.Error -> {
              val errorMsg = chunk.throwable.message ?: "Unknown error"
              _chatUiState.value = ChatUiState.Error(errorMsg)
            }
          }
        }
      } catch (e: Exception) {
        _chatUiState.value = ChatUiState.Error(e.message ?: "Generation error")
      }
    }
  }

  fun deleteConversation(id: Long) {
    viewModelScope.launch {
      if (_activeConversationId.value == id) {
        stopGeneration()
        _activeConversationId.value = null
      }
      conversationRepo.deleteConversation(id)
    }
  }

  fun clearAllConversations() {
    viewModelScope.launch {
      stopGeneration()
      conversationRepo.clearAll()
      _activeConversationId.value = null
    }
  }

  // --- Settings & UI Navigation ---

  fun setLanguage(language: AppLanguage) {
    settingsRepo.setLanguage(language)
  }

  fun setDarkMode(enabled: Boolean) {
    settingsRepo.setDarkMode(enabled)
  }

  fun setBiometricLock(enabled: Boolean) {
    settingsRepo.setBiometricLock(enabled)
  }

  fun setPrivateDeviceMode(enabled: Boolean) {
    settingsRepo.setPrivateDeviceMode(enabled)
  }

  fun setLockTimeoutMinutes(minutes: Int) {
    settingsRepo.setLockTimeoutMinutes(minutes)
  }

  fun setMemoryEnabled(enabled: Boolean) {
    settingsRepo.setMemoryEnabled(enabled)
  }

  fun openMemories() {
    _isMemoryOpen.value = true
  }

  fun closeMemories() {
    _isMemoryOpen.value = false
  }

  fun addMemory(title: String, content: String, strings: Localization.AppStrings) {
    val user = currentUser.value ?: return
    viewModelScope.launch {
      memoryRepo.saveMemory(
        userId = user.id,
        content = content,
        title = title
      )
      _toastNotice.value = strings.memorySavedSuccess
    }
  }

  fun updateMemory(memory: AiMemoryEntity, strings: Localization.AppStrings) {
    viewModelScope.launch {
      memoryRepo.updateMemory(memory)
      _toastNotice.value = strings.memoryUpdatedSuccess
    }
  }

  fun deleteMemory(id: Long, strings: Localization.AppStrings) {
    val user = currentUser.value ?: return
    viewModelScope.launch {
      memoryRepo.deleteMemory(id, user.id)
      _toastNotice.value = strings.memoryDeletedSuccess
    }
  }

  fun deleteAllMemories(strings: Localization.AppStrings) {
    val user = currentUser.value ?: return
    viewModelScope.launch {
      memoryRepo.deleteAllMemories(user.id)
      _toastNotice.value = strings.allMemoriesDeletedSuccess
    }
  }

  fun onSpeechInputReceived(spokenText: String) {
    val current = _inputText.value.trim()
    if (current.isEmpty()) {
      _inputText.value = spokenText
    } else {
      _inputText.value = "$current $spokenText"
    }
  }

  fun showToast(message: String) {
    _toastNotice.value = message
  }

  fun openHistory() {
    _isHistoryOpen.value = true
  }

  fun closeHistory() {
    _isHistoryOpen.value = false
  }

  fun openSettings() {
    _isSettingsOpen.value = true
  }

  fun closeSettings() {
    _isSettingsOpen.value = false
  }

  fun openProfile() {
    _isProfileOpen.value = true
  }

  fun closeProfile() {
    _isProfileOpen.value = false
  }

  fun onMicrophoneClicked(tooltipText: String) {
    _toastNotice.value = tooltipText
  }

  fun clearToastNotice() {
    _toastNotice.value = null
  }

  fun isApiKeyConfigured(): Boolean = aiRepo.isApiKeyConfigured()
  fun getAiStatus(): AiEngineStatus = aiRepo.getStatus()

  private fun mapAuthError(type: AuthErrorType, strings: Localization.AppStrings): String {
    return when (type) {
      AuthErrorType.INVALID_EMAIL_FORMAT -> strings.errorInvalidEmail
      AuthErrorType.PASSWORD_TOO_SHORT -> strings.errorPasswordShort
      AuthErrorType.PASSWORDS_DO_NOT_MATCH -> strings.errorPasswordMismatch
      AuthErrorType.USER_ALREADY_EXISTS -> strings.errorUserExists
      AuthErrorType.USER_NOT_FOUND -> strings.errorUserNotFound
      AuthErrorType.INCORRECT_PASSWORD -> strings.errorIncorrectPassword
      AuthErrorType.DEVICE_RESTRICTED -> strings.privateDeviceSubtitle
      AuthErrorType.BIOMETRIC_UNAVAILABLE -> strings.biometricNoHardware
      AuthErrorType.BIOMETRIC_NONE_ENROLLED -> strings.biometricNoneEnrolled
      AuthErrorType.BIOMETRIC_AUTHENTICATION_FAILED -> strings.biometricPromptFailed
      AuthErrorType.UNKNOWN -> strings.errorGeneral
    }
  }

  companion object {
    /**
     * Determines whether user text expresses intent to generate or draw an image.
     */
    fun isImageGenerationPrompt(text: String): Boolean {
      val lower = text.trim().lowercase()
      val hasKurdish = lower.contains("وێنە") || lower.contains("وینە") ||
        lower.contains("بکێشە") || lower.contains("نیگار") ||
        lower.contains("رەسم") || lower.contains("ڕەسم") ||
        (lower.contains("دروست") && (lower.contains("وێن") || lower.contains("شێوە") || lower.contains("رەسم") || lower.contains("ڕەسم")))
      val hasArabic = lower.contains("صورة") || lower.contains("صور") ||
        lower.contains("ارسم") || lower.contains("أنشئ صورة") || lower.contains("رسمة")
      val isVisualCreationAction = lower.contains("generate") || lower.contains("create") ||
        lower.contains("make") || lower.contains("render") || lower.contains("produce") ||
        lower.startsWith("draw") || lower.startsWith("paint")
      val hasVisualNoun = lower.contains("image") || lower.contains("photo") ||
        lower.contains("picture") || lower.contains("illustration") ||
        lower.contains("artwork") || lower.contains("sketch") || lower.contains("painting")
      val hasEnglish = (isVisualCreationAction && hasVisualNoun) ||
        lower.contains("image of") || lower.contains("photo of") ||
        lower.contains("picture of") || lower.contains("illustration of") ||
        lower.startsWith("draw ") || lower.startsWith("paint ")
      return hasKurdish || hasArabic || hasEnglish
    }
  }
}
