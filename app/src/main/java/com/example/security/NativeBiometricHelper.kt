package com.example.security

import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings

sealed interface BiometricStatus {
  object Available : BiometricStatus
  object NoHardware : BiometricStatus
  object NoneEnrolled : BiometricStatus
  object HardwareUnavailable : BiometricStatus
}

object NativeBiometricHelper {

  /**
   * Checks if biometric sensors or device credentials (PIN / Pattern / Password) are available.
   * Utilizes standard Android framework KeyguardManager.
   */
  fun checkDeviceSecurity(context: Context): Boolean {
    val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager
    return keyguardManager?.isDeviceSecure ?: false
  }

  /**
   * Evaluates native biometric hardware presence on device.
   */
  fun canAuthenticateBiometric(context: Context): BiometricStatus {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      val biometricManager = context.getSystemService(android.hardware.biometrics.BiometricManager::class.java)
      if (biometricManager == null) {
        BiometricStatus.NoHardware
      } else {
        when (biometricManager.canAuthenticate(android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
          android.hardware.biometrics.BiometricManager.BIOMETRIC_SUCCESS -> BiometricStatus.Available
          android.hardware.biometrics.BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricStatus.NoneEnrolled
          android.hardware.biometrics.BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> BiometricStatus.NoHardware
          else -> BiometricStatus.HardwareUnavailable
        }
      }
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      val biometricManager = context.getSystemService(android.hardware.biometrics.BiometricManager::class.java)
      if (biometricManager != null) {
        @Suppress("DEPRECATION")
        when (biometricManager.canAuthenticate()) {
          android.hardware.biometrics.BiometricManager.BIOMETRIC_SUCCESS -> BiometricStatus.Available
          android.hardware.biometrics.BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricStatus.NoneEnrolled
          android.hardware.biometrics.BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> BiometricStatus.NoHardware
          else -> BiometricStatus.HardwareUnavailable
        }
      } else {
        BiometricStatus.NoHardware
      }
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      @Suppress("DEPRECATION")
      val fingerprintManager = context.getSystemService(android.hardware.fingerprint.FingerprintManager::class.java)
      if (fingerprintManager == null || !fingerprintManager.isHardwareDetected) {
        BiometricStatus.NoHardware
      } else if (!fingerprintManager.hasEnrolledFingerprints()) {
        BiometricStatus.NoneEnrolled
      } else {
        BiometricStatus.Available
      }
    } else {
      BiometricStatus.NoHardware
    }
  }

  /**
   * Launches official Android BiometricPrompt for devices running Android P (API 28) and above.
   * Never stores or accesses raw biometric data; only receives authentication callback result.
   */
  fun authenticate(
    activity: Activity,
    title: String,
    subtitle: String,
    cancelText: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
  ) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
      try {
        val executor = activity.mainExecutor
        val promptBuilder = android.hardware.biometrics.BiometricPrompt.Builder(activity)
          .setTitle(title)
          .setSubtitle(subtitle)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
          promptBuilder.setAllowedAuthenticators(
            android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_STRONG
          )
        }

        promptBuilder.setNegativeButton(cancelText, executor) { _, _ ->
          onError(cancelText)
        }

        val prompt = promptBuilder.build()
        val cancellationSignal = android.os.CancellationSignal()

        prompt.authenticate(
          cancellationSignal,
          executor,
          object : android.hardware.biometrics.BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: android.hardware.biometrics.BiometricPrompt.AuthenticationResult?) {
              super.onAuthenticationSucceeded(result)
              onSuccess()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
              super.onAuthenticationError(errorCode, errString)
              onError(errString?.toString() ?: "Authentication Error")
            }

            override fun onAuthenticationFailed() {
              super.onAuthenticationFailed()
              onError("Biometric authentication failed")
            }
          }
        )
      } catch (e: Exception) {
        onError(e.message ?: "Biometric prompt error")
      }
    } else {
      // Older API fallback
      val keyguard = activity.getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager
      if (keyguard != null && keyguard.isKeyguardSecure) {
        onSuccess()
      } else {
        onError("Biometrics not supported on this Android version")
      }
    }
  }

  /**
   * Navigates user to Android device security settings to register/enroll their fingerprint.
   */
  fun openBiometricSettings(context: Context) {
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
          putExtra(
            Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
            android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_STRONG
          )
          addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(enrollIntent)
      } else {
        val intent = Intent(Settings.ACTION_SECURITY_SETTINGS).apply {
          addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
      }
    } catch (e: Exception) {
      try {
        val fallbackIntent = Intent(Settings.ACTION_SETTINGS).apply {
          addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(fallbackIntent)
      } catch (_: Exception) {}
    }
  }
}
