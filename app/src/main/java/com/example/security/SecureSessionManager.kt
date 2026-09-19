package com.example.security

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

data class AuthSession(
  val userId: String,
  val email: String,
  val sessionToken: String,
  val lastActiveTimestamp: Long
)

class SecureSessionManager(context: Context) {

  private val prefs = context.getSharedPreferences("basoka_secure_auth_prefs", Context.MODE_PRIVATE)

  private val _sessionState = MutableStateFlow(loadSession())
  val sessionFlow: StateFlow<AuthSession?> = _sessionState.asStateFlow()

  private fun loadSession(): AuthSession? {
    val userId = prefs.getString("auth_user_id", null)
    val email = prefs.getString("auth_email", null) ?: "biometric_user@basoka.ai"
    val token = prefs.getString("auth_session_token", null)
    val lastActive = prefs.getLong("auth_last_active", 0L)

    return if (!userId.isNullOrBlank() && !token.isNullOrBlank()) {
      AuthSession(
        userId = userId,
        email = email,
        sessionToken = token,
        lastActiveTimestamp = lastActive
      )
    } else {
      null
    }
  }

  fun getActiveSession(): AuthSession? = _sessionState.value

  fun saveSession(userId: String, emailOrName: String = "biometric_user@basoka.ai"): String {
    val token = UUID.randomUUID().toString()
    val now = System.currentTimeMillis()

    prefs.edit()
      .putString("auth_user_id", userId)
      .putString("auth_email", emailOrName)
      .putString("auth_session_token", token)
      .putLong("auth_last_active", now)
      .apply()

    val newSession = AuthSession(
      userId = userId,
      email = emailOrName,
      sessionToken = token,
      lastActiveTimestamp = now
    )
    _sessionState.value = newSession
    return token
  }

  fun updateLastActive() {
    val now = System.currentTimeMillis()
    prefs.edit().putLong("auth_last_active", now).apply()
    val current = _sessionState.value
    if (current != null) {
      _sessionState.value = current.copy(lastActiveTimestamp = now)
    }
  }

  fun clearSession() {
    prefs.edit()
      .remove("auth_user_id")
      .remove("auth_email")
      .remove("auth_session_token")
      .remove("auth_last_active")
      .apply()
    _sessionState.value = null
  }
}
