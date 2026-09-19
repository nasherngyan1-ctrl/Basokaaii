package com.example.voice

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class BasokaVoiceManager(
  private val context: Context,
  private val onNoticeCallback: ((String) -> Unit)? = null
) : TextToSpeech.OnInitListener {

  private var tts: TextToSpeech? = null
  private var isInitialized = false

  private val _isSpeaking = MutableStateFlow(false)
  val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

  private val _activeMessageId = MutableStateFlow<Long?>(null)
  val activeMessageId: StateFlow<Long?> = _activeMessageId.asStateFlow()

  private val _isKurdishSupported = MutableStateFlow(false)
  val isKurdishSupported: StateFlow<Boolean> = _isKurdishSupported.asStateFlow()

  private var lastSpokenText: String = ""
  private var currentUtteranceId: String = ""

  init {
    tts = TextToSpeech(context.applicationContext, this)
  }

  override fun onInit(status: Int) {
    if (status == TextToSpeech.SUCCESS) {
      isInitialized = true
      setupLanguageSupport()
      setupUtteranceListener()
    } else {
      isInitialized = false
    }
  }

  private fun setupLanguageSupport() {
    val localTts = tts ?: return

    // Test Kurdish Sorani (ckb) and Kurdish (ku)
    val ckbLocale = Locale("ckb")
    val kuLocale = Locale("ku")

    val ckbStatus = localTts.isLanguageAvailable(ckbLocale)
    val kuStatus = localTts.isLanguageAvailable(kuLocale)

    if (ckbStatus >= TextToSpeech.LANG_AVAILABLE) {
      localTts.language = ckbLocale
      _isKurdishSupported.value = true
    } else if (kuStatus >= TextToSpeech.LANG_AVAILABLE) {
      localTts.language = kuLocale
      _isKurdishSupported.value = true
    } else {
      _isKurdishSupported.value = false
      // Fallback: use Arabic or Default if available
      val arLocale = Locale("ar")
      if (localTts.isLanguageAvailable(arLocale) >= TextToSpeech.LANG_AVAILABLE) {
        localTts.language = arLocale
      } else {
        localTts.language = Locale.getDefault()
      }
    }
  }

  private fun setupUtteranceListener() {
    tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
      override fun onStart(utteranceId: String?) {
        _isSpeaking.value = true
      }

      override fun onDone(utteranceId: String?) {
        _isSpeaking.value = false
        _activeMessageId.value = null
      }

      override fun onError(utteranceId: String?) {
        _isSpeaking.value = false
        _activeMessageId.value = null
      }

      override fun onError(utteranceId: String?, errorCode: Int) {
        _isSpeaking.value = false
        _activeMessageId.value = null
      }
    })
  }

  /**
   * Speaks the provided assistant response text.
   * If the current device's TTS engine does not have Kurdish voice data installed,
   * it provides a clear user notice and continues with best available system voice.
   */
  fun speak(
    messageId: Long,
    text: String,
    noticeKurdishUnavailable: String? = null
  ) {
    val localTts = tts
    if (!isInitialized || localTts == null) {
      onNoticeCallback?.invoke("Text-to-Speech is initializing...")
      return
    }

    if (_isSpeaking.value && _activeMessageId.value == messageId) {
      // Toggle pause/stop if already speaking this message
      stop()
      return
    }

    stop()

    if (!_isKurdishSupported.value && noticeKurdishUnavailable != null) {
      onNoticeCallback?.invoke(noticeKurdishUnavailable)
    }

    lastSpokenText = text
    _activeMessageId.value = messageId
    currentUtteranceId = "basoka_utterance_${messageId}_${System.currentTimeMillis()}"

    val params = Bundle()
    localTts.speak(text, TextToSpeech.QUEUE_FLUSH, params, currentUtteranceId)
    _isSpeaking.value = true
  }

  fun toggleSpeak(
    messageId: Long,
    text: String,
    noticeKurdishUnavailable: String? = null
  ) {
    speak(messageId, text, noticeKurdishUnavailable)
  }

  fun pause() {
    stop()
  }

  fun stop() {
    try {
      tts?.stop()
    } catch (_: Exception) {}
    _isSpeaking.value = false
    _activeMessageId.value = null
  }

  fun release() {
    try {
      tts?.stop()
      tts?.shutdown()
    } catch (_: Exception) {}
    tts = null
    isInitialized = false
    _isSpeaking.value = false
    _activeMessageId.value = null
  }
}
