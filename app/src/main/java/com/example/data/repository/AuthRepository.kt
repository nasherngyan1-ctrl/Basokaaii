package com.example.data.repository

import com.example.data.local.BasokaDao
import com.example.data.local.UserEntity
import com.example.security.SecureSessionManager
import com.example.security.SecurityUtils
import kotlinx.coroutines.flow.Flow
import java.util.UUID

sealed interface AuthResult {
  data class Success(val user: UserEntity) : AuthResult
  data class Error(val errorType: AuthErrorType, val debugMessage: String? = null) : AuthResult
}

enum class AuthErrorType {
  INVALID_EMAIL_FORMAT,
  PASSWORD_TOO_SHORT,
  PASSWORDS_DO_NOT_MATCH,
  USER_ALREADY_EXISTS,
  USER_NOT_FOUND,
  INCORRECT_PASSWORD,
  DEVICE_RESTRICTED,
  BIOMETRIC_UNAVAILABLE,
  BIOMETRIC_NONE_ENROLLED,
  BIOMETRIC_AUTHENTICATION_FAILED,
  UNKNOWN
}

class AuthRepository(
  private val dao: BasokaDao,
  private val sessionManager: SecureSessionManager
) {

  val personalUserFlow: Flow<UserEntity?> = dao.getPersonalUserFlow()

  suspend fun getPersonalUser(): UserEntity? = dao.getPersonalUser()

  suspend fun isUserRegistered(): Boolean {
    return dao.getPersonalUser() != null
  }

  suspend fun getActiveSession() = sessionManager.getActiveSession()

  /**
   * Registers a user account exclusively via biometric fingerprint authentication.
   * Completely email-free and password-free.
   */
  suspend fun registerBiometric(displayName: String): AuthResult {
    val cleanName = displayName.trim().ifEmpty { "بەکارھێنەری باسۆکا" }
    val userId = UUID.randomUUID().toString()
    val now = System.currentTimeMillis()

    val newUser = UserEntity(
      id = userId,
      email = "biometric_user@basoka.ai",
      displayName = cleanName,
      passwordHash = "",
      passwordSalt = "",
      createdAt = now,
      lastLoginAt = now
    )

    dao.insertUser(newUser)
    sessionManager.saveSession(newUser.id, newUser.displayName)
    return AuthResult.Success(newUser)
  }

  /**
   * Authenticates the local registered user using Android biometric fingerprint.
   * Does not require email or password.
   */
  suspend fun loginBiometric(): AuthResult {
    val user = dao.getPersonalUser()
      ?: return AuthResult.Error(AuthErrorType.USER_NOT_FOUND)

    val updatedUser = user.copy(lastLoginAt = System.currentTimeMillis())
    dao.insertUser(updatedUser)
    sessionManager.saveSession(updatedUser.id, updatedUser.displayName)
    return AuthResult.Success(updatedUser)
  }

  suspend fun register(
    email: String,
    displayName: String,
    password: String,
    confirmPassword: String
  ): AuthResult {
    val cleanEmail = email.trim().lowercase()
    val cleanName = displayName.trim().ifEmpty { "Personal User" }

    if (!SecurityUtils.isValidEmail(cleanEmail)) {
      return AuthResult.Error(AuthErrorType.INVALID_EMAIL_FORMAT)
    }
    if (!SecurityUtils.isValidPassword(password)) {
      return AuthResult.Error(AuthErrorType.PASSWORD_TOO_SHORT)
    }
    if (password != confirmPassword) {
      return AuthResult.Error(AuthErrorType.PASSWORDS_DO_NOT_MATCH)
    }

    val existingUser = dao.getUserByEmail(cleanEmail)
    if (existingUser != null) {
      return AuthResult.Error(AuthErrorType.USER_ALREADY_EXISTS)
    }

    // Hash password with cryptographic salt
    val salt = SecurityUtils.generateSalt()
    val hash = SecurityUtils.hashPassword(password, salt)

    val newUser = UserEntity(
      id = UUID.randomUUID().toString(),
      email = cleanEmail,
      displayName = cleanName,
      passwordHash = hash,
      passwordSalt = salt,
      createdAt = System.currentTimeMillis(),
      lastLoginAt = System.currentTimeMillis()
    )

    dao.insertUser(newUser)
    sessionManager.saveSession(newUser.id, newUser.email)
    return AuthResult.Success(newUser)
  }

  suspend fun login(email: String, password: String): AuthResult {
    val cleanEmail = email.trim().lowercase()

    if (!SecurityUtils.isValidEmail(cleanEmail)) {
      return AuthResult.Error(AuthErrorType.INVALID_EMAIL_FORMAT)
    }

    val user = dao.getUserByEmail(cleanEmail)
      ?: return AuthResult.Error(AuthErrorType.USER_NOT_FOUND)

    val isPasswordCorrect = SecurityUtils.verifyPassword(
      password = password,
      salt = user.passwordSalt,
      expectedHash = user.passwordHash
    )

    if (!isPasswordCorrect) {
      return AuthResult.Error(AuthErrorType.INCORRECT_PASSWORD)
    }

    // Update last login
    val updatedUser = user.copy(lastLoginAt = System.currentTimeMillis())
    dao.insertUser(updatedUser)

    sessionManager.saveSession(user.id, user.email)
    return AuthResult.Success(updatedUser)
  }

  suspend fun logout() {
    sessionManager.clearSession()
  }

  suspend fun deletePersonalAccount() {
    sessionManager.clearSession()
    dao.deleteAllUsers()
  }
}
