package com.example.ui.screens

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.UserEntity
import com.example.model.Localization
import com.example.security.BiometricStatus
import com.example.security.NativeBiometricHelper
import com.example.ui.components.BasokaElectricBackground
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.DarkNavyCanvas
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DarkSurfaceDeep
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.GlowCyan
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.StatusRed
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.viewmodel.AuthScreenMode

@Composable
fun AuthScreen(
  mode: AuthScreenMode,
  errorMessage: String?,
  currentUser: UserEntity?,
  onRegisterBiometrics: (activity: Activity, displayName: String) -> Unit,
  onLoginBiometrics: (activity: Activity) -> Unit,
  onSwitchMode: (AuthScreenMode) -> Unit,
  onClearError: () -> Unit,
  onResetAccount: () -> Unit,
  strings: Localization.AppStrings,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val activity = context as? Activity
  val biometricStatus = remember(context) { NativeBiometricHelper.canAuthenticateBiometric(context) }

  var displayName by remember { mutableStateOf("") }

  // Pulsing animation for the futuristic fingerprint scanner target
  val infiniteTransition = rememberInfiniteTransition(label = "fingerprint_glow")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 1.0f,
    targetValue = 1.12f,
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse_scale"
  )
  val pulseAlpha by infiniteTransition.animateFloat(
    initialValue = 0.35f,
    targetValue = 0.85f,
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse_alpha"
  )

  // Auto-launch official Android BiometricPrompt on screen entry for returning user
  LaunchedEffect(mode, biometricStatus) {
    if (mode == AuthScreenMode.LOGIN && biometricStatus is BiometricStatus.Available && activity != null) {
      onLoginBiometrics(activity)
    }
  }

  CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
    BasokaElectricBackground(modifier = modifier) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .statusBarsPadding()
          .navigationBarsPadding()
          .imePadding()
          .verticalScroll(rememberScrollState())
          .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Glowing Basoka AI Logo Badge
        Box(
          modifier = Modifier
            .size(76.dp)
            .clip(CircleShape)
            .background(DarkSurfaceDeep)
            .border(
              width = 1.5.dp,
              brush = Brush.linearGradient(listOf(NeonCyan, ElectricViolet)),
              shape = CircleShape
            )
            .shadow(elevation = 12.dp, shape = CircleShape, spotColor = GlowCyan),
          contentAlignment = Alignment.Center
        ) {
          Image(
            painter = painterResource(id = R.drawable.ic_basoka_icon),
            contentDescription = strings.appName,
            modifier = Modifier.size(46.dp)
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = strings.appName,
          color = TextPrimary,
          fontSize = 24.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.sp
        )

        Text(
          text = strings.appSubtitle,
          color = NeonCyan,
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Private Device Biometric Badge
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = DarkSurfaceDeep.copy(alpha = 0.85f)),
          border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.35f))
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Security,
              contentDescription = null,
              tint = CyberGreen,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = strings.personalAccountBadge,
              color = CyberGreen,
              fontSize = 11.sp,
              fontWeight = FontWeight.Medium
            )
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Error banner
        AnimatedVisibility(visible = errorMessage != null) {
          errorMessage?.let { error ->
            Card(
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(containerColor = StatusRed.copy(alpha = 0.15f)),
              border = BorderStroke(1.dp, StatusRed.copy(alpha = 0.5f)),
              modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .testTag("auth_error_card")
            ) {
              Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.ErrorOutline,
                  contentDescription = null,
                  tint = StatusRed,
                  modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                  text = error,
                  color = StatusRed,
                  fontSize = 13.sp,
                  lineHeight = 18.sp
                )
              }
            }
          }
        }

        // Biometric Hardware / Enrollment Status Banner (if device has issues)
        when (biometricStatus) {
          is BiometricStatus.NoHardware -> {
            Card(
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
              border = BorderStroke(1.dp, StatusRed.copy(alpha = 0.6f)),
              modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .testTag("auth_no_hardware_card")
            ) {
              Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.ErrorOutline,
                  contentDescription = null,
                  tint = StatusRed,
                  modifier = Modifier.size(32.dp)
                )
                Text(
                  text = strings.biometricNoHardware,
                  color = TextPrimary,
                  fontSize = 13.sp,
                  lineHeight = 19.sp,
                  textAlign = TextAlign.Center
                )
              }
            }
          }
          is BiometricStatus.NoneEnrolled -> {
            Card(
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
              border = BorderStroke(1.dp, Color(0xFFFFB300).copy(alpha = 0.6f)),
              modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .testTag("auth_none_enrolled_card")
            ) {
              Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Fingerprint,
                  contentDescription = null,
                  tint = Color(0xFFFFB300),
                  modifier = Modifier.size(34.dp)
                )
                Text(
                  text = strings.biometricNoneEnrolled,
                  color = TextPrimary,
                  fontSize = 13.sp,
                  lineHeight = 19.sp,
                  textAlign = TextAlign.Center
                )
                OutlinedButton(
                  onClick = { NativeBiometricHelper.openBiometricSettings(context) },
                  shape = RoundedCornerShape(12.dp),
                  border = BorderStroke(1.dp, Color(0xFFFFB300)),
                  colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFFB300)),
                  modifier = Modifier.testTag("auth_open_settings_button")
                ) {
                  Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = strings.openDeviceSecuritySettings,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                  )
                }
              }
            }
          }
          is BiometricStatus.HardwareUnavailable -> {
            Card(
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
              border = BorderStroke(1.dp, Color(0xFFFF9800)),
              modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
            ) {
              Text(
                text = strings.biometricHardwareUnavailable,
                color = TextSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(14.dp)
              )
            }
          }
          is BiometricStatus.Available -> {
            // Biometrics fully ready and supported
          }
        }

        // Main Electric Biometric Card
        Card(
          shape = RoundedCornerShape(22.dp),
          colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard.copy(alpha = 0.95f)),
          border = BorderStroke(1.dp, DarkSurfaceBorder),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            if (mode == AuthScreenMode.REGISTER) {
              // --- FIRST TIME SETUP MODE ---
              Box(
                modifier = Modifier
                  .size(68.dp)
                  .clip(CircleShape)
                  .background(DarkSurfaceDeep)
                  .border(BorderStroke(1.5.dp, CyberGreen), CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Fingerprint,
                  contentDescription = null,
                  tint = CyberGreen,
                  modifier = Modifier.size(40.dp)
                )
              }

              Spacer(modifier = Modifier.height(14.dp))

              Text(
                text = strings.biometricSetupTitle,
                color = TextPrimary,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
              )

              Spacer(modifier = Modifier.height(6.dp))

              Text(
                text = strings.biometricSetupSubtitle,
                color = TextSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                textAlign = TextAlign.Center
              )

              Spacer(modifier = Modifier.height(18.dp))

              // Optional Display Name Field (NO EMAIL, NO PASSWORD)
              OutlinedTextField(
                value = displayName,
                onValueChange = {
                  displayName = it
                  if (errorMessage != null) onClearError()
                },
                label = { Text(strings.userNameOptionalLabel) },
                leadingIcon = {
                  Icon(Icons.Default.Person, contentDescription = null, tint = NeonCyan)
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                  focusedTextColor = TextPrimary,
                  unfocusedTextColor = TextPrimary,
                  focusedBorderColor = NeonCyan,
                  unfocusedBorderColor = DarkSurfaceBorder,
                  focusedLabelColor = NeonCyan,
                  unfocusedLabelColor = TextSecondary,
                  cursorColor = NeonCyan
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("auth_display_name_input")
              )

              Spacer(modifier = Modifier.height(16.dp))

              // Feature perks in Kurdish Sorani
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .background(DarkSurfaceDeep.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                  .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                FeatureBullet(text = "پاراستنی تەواوی گفتوگۆ و یادەوەرییە کەسییەکان بە پەنجەمۆر")
                FeatureBullet(text = "بەکارهێنانی هەستەوەری ڕاستەقینەی مۆبایل (Android BiometricPrompt)")
                FeatureBullet(text = "بەبێ پێویستی بە ئیمەیڵ یان وشەی نهێنی")
              }

              Spacer(modifier = Modifier.height(20.dp))

              // Enable Fingerprint Button
              Button(
                onClick = {
                  if (activity != null) {
                    onRegisterBiometrics(activity, displayName)
                  }
                },
                enabled = biometricStatus is BiometricStatus.Available,
                modifier = Modifier
                  .fillMaxWidth()
                  .height(52.dp)
                  .testTag("auth_enable_fingerprint_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                  containerColor = CyberGreen,
                  contentColor = DarkNavyCanvas,
                  disabledContainerColor = DarkSurfaceDeep,
                  disabledContentColor = TextTertiary
                )
              ) {
                Icon(
                  imageVector = Icons.Default.Fingerprint,
                  contentDescription = null,
                  modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = strings.biometricEnableButton,
                  fontSize = 15.sp,
                  fontWeight = FontWeight.Bold
                )
              }

              if (currentUser != null) {
                Spacer(modifier = Modifier.height(12.dp))
                TextButton(
                  onClick = {
                    onClearError()
                    onSwitchMode(AuthScreenMode.LOGIN)
                  },
                  modifier = Modifier.testTag("auth_switch_to_login_button")
                ) {
                  Text(
                    text = strings.biometricAuthTitle,
                    color = NeonCyan,
                    fontSize = 13.sp
                  )
                }
              }

            } else {
              // --- RETURNING USER FINGERPRINT AUTHENTICATION MODE ---
              Text(
                text = strings.biometricAuthTitle,
                color = TextPrimary,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
              )

              Spacer(modifier = Modifier.height(6.dp))

              Text(
                text = strings.biometricAuthSubtitle,
                color = TextSecondary,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
              )

              if (currentUser != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                  shape = RoundedCornerShape(10.dp),
                  colors = CardDefaults.cardColors(containerColor = DarkSurfaceDeep),
                  border = BorderStroke(1.dp, DarkSurfaceBorder)
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Icon(
                      imageVector = Icons.Default.Person,
                      contentDescription = null,
                      tint = NeonCyan,
                      modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                      text = currentUser.displayName,
                      color = TextPrimary,
                      fontSize = 13.sp,
                      fontWeight = FontWeight.SemiBold
                    )
                  }
                }
              }

              Spacer(modifier = Modifier.height(26.dp))

              // Holographic Futuristic Fingerprint Scanner Ring
              Box(
                modifier = Modifier
                  .size(110.dp)
                  .scale(pulseScale)
                  .clip(CircleShape)
                  .background(NeonCyan.copy(alpha = 0.08f * pulseAlpha))
                  .border(
                    width = 2.dp,
                    brush = Brush.radialGradient(
                      listOf(
                        NeonCyan.copy(alpha = pulseAlpha),
                        ElectricBlue.copy(alpha = 0.2f)
                      )
                    ),
                    shape = CircleShape
                  )
                  .clickable {
                    if (activity != null && biometricStatus is BiometricStatus.Available) {
                      onLoginBiometrics(activity)
                    }
                  }
                  .testTag("auth_fingerprint_sensor_badge"),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Fingerprint,
                  contentDescription = strings.biometricScanButton,
                  tint = if (biometricStatus is BiometricStatus.Available) NeonCyan else TextTertiary,
                  modifier = Modifier.size(56.dp)
                )
              }

              Spacer(modifier = Modifier.height(20.dp))

              Text(
                text = strings.biometricTouchSensorHint,
                color = NeonCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
              )

              Spacer(modifier = Modifier.height(22.dp))

              // Scan Fingerprint Action Button
              Button(
                onClick = {
                  if (activity != null) {
                    onLoginBiometrics(activity)
                  }
                },
                enabled = biometricStatus is BiometricStatus.Available,
                modifier = Modifier
                  .fillMaxWidth()
                  .height(52.dp)
                  .testTag("auth_scan_fingerprint_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                  containerColor = NeonCyan,
                  contentColor = DarkNavyCanvas,
                  disabledContainerColor = DarkSurfaceDeep,
                  disabledContentColor = TextTertiary
                )
              ) {
                Icon(
                  imageVector = Icons.Default.Fingerprint,
                  contentDescription = null,
                  modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = strings.biometricScanButton,
                  fontSize = 15.sp,
                  fontWeight = FontWeight.Bold
                )
              }

              Spacer(modifier = Modifier.height(16.dp))

              // Reset account option (clean re-enrollment)
              TextButton(
                onClick = onResetAccount,
                modifier = Modifier.testTag("auth_reset_account_button")
              ) {
                Icon(
                  imageVector = Icons.Default.Refresh,
                  contentDescription = null,
                  tint = TextTertiary,
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = strings.switchAccountReset,
                  color = TextTertiary,
                  fontSize = 12.sp
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(24.dp))
      }
    }
  }
}

@Composable
private fun FeatureBullet(text: String) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.fillMaxWidth()
  ) {
    Icon(
      imageVector = Icons.Default.CheckCircle,
      contentDescription = null,
      tint = CyberGreen,
      modifier = Modifier.size(14.dp)
    )
    Spacer(modifier = Modifier.width(8.dp))
    Text(
      text = text,
      color = TextSecondary,
      fontSize = 11.sp,
      lineHeight = 16.sp
    )
  }
}
