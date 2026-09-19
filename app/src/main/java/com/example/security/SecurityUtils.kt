package com.example.security

import java.security.MessageDigest
import java.security.SecureRandom

object SecurityUtils {

  /**
   * Generates a cryptographically strong 16-byte random salt in hex string format.
   */
  fun generateSalt(): String {
    val random = SecureRandom()
    val salt = ByteArray(16)
    random.nextBytes(salt)
    return salt.joinToString("") { "%02x".format(it) }
  }

  /**
   * Hashes password using SHA-256 with user-specific salt.
   * Never stores plain-text passwords.
   */
  fun hashPassword(password: String, salt: String): String {
    val md = MessageDigest.getInstance("SHA-256")
    val input = "$salt:$password".toByteArray(Charsets.UTF_8)
    val hash = md.digest(input)
    return hash.joinToString("") { "%02x".format(it) }
  }

  /**
   * Validates password against salt and stored hash.
   */
  fun verifyPassword(password: String, salt: String, expectedHash: String): Boolean {
    val actualHash = hashPassword(password, salt)
    return MessageDigest.isEqual(actualHash.toByteArray(), expectedHash.toByteArray())
  }

  /**
   * Validates email format.
   */
  fun isValidEmail(email: String): Boolean {
    return email.isNotBlank() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
  }

  /**
   * Validates password strength (at least 6 characters).
   */
  fun isValidPassword(password: String): Boolean {
    return password.length >= 6
  }
}
