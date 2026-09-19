package com.example.data.repository

import android.content.Context
import com.example.model.AppLanguage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UserSettings(
  val language: AppLanguage = AppLanguage.KURDISH_SORANI,
  val isDarkMode: Boolean = true,
  val isBiometricLockEnabled: Boolean = false,
  val isPrivateDeviceMode: Boolean = true,
  val lockTimeoutMinutes: Int = 0, // 0 = Immediately when app is closed / reopened
  val isMemoryEnabled: Boolean = true
)

class UserSettingsRepository(context: Context) {

  private val prefs = context.getSharedPreferences("basoka_settings", Context.MODE_PRIVATE)

  private val _settings = MutableStateFlow(loadSettings())
  val settings: StateFlow<UserSettings> = _settings.asStateFlow()

  private fun loadSettings(): UserSettings {
    val langCode = prefs.getString("key_language", AppLanguage.KURDISH_SORANI.code)
    val lang = AppLanguage.values().find { it.code == langCode } ?: AppLanguage.KURDISH_SORANI
    val dark = prefs.getBoolean("key_dark_mode", true)
    val biometric = prefs.getBoolean("key_biometric", false)
    val privateDevice = prefs.getBoolean("key_private_device", true)
    val lockTimeout = prefs.getInt("key_lock_timeout", 0)
    val memory = prefs.getBoolean("key_memory", true)

    return UserSettings(
      language = lang,
      isDarkMode = dark,
      isBiometricLockEnabled = biometric,
      isPrivateDeviceMode = privateDevice,
      lockTimeoutMinutes = lockTimeout,
      isMemoryEnabled = memory
    )
  }

  fun setLanguage(language: AppLanguage) {
    prefs.edit().putString("key_language", language.code).apply()
    _settings.value = _settings.value.copy(language = language)
  }

  fun setDarkMode(enabled: Boolean) {
    prefs.edit().putBoolean("key_dark_mode", enabled).apply()
    _settings.value = _settings.value.copy(isDarkMode = enabled)
  }

  fun setBiometricLock(enabled: Boolean) {
    prefs.edit().putBoolean("key_biometric", enabled).apply()
    _settings.value = _settings.value.copy(isBiometricLockEnabled = enabled)
  }

  fun setPrivateDeviceMode(enabled: Boolean) {
    prefs.edit().putBoolean("key_private_device", enabled).apply()
    _settings.value = _settings.value.copy(isPrivateDeviceMode = enabled)
  }

  fun setLockTimeoutMinutes(minutes: Int) {
    prefs.edit().putInt("key_lock_timeout", minutes).apply()
    _settings.value = _settings.value.copy(lockTimeoutMinutes = minutes)
  }

  fun setMemoryEnabled(enabled: Boolean) {
    prefs.edit().putBoolean("key_memory", enabled).apply()
    _settings.value = _settings.value.copy(isMemoryEnabled = enabled)
  }
}
