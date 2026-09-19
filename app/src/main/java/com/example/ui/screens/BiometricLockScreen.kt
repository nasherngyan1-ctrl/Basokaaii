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
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.ui.theme.GlowCyan
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.StatusRed
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun BiometricLockScreen(
  errorMessage: String?,
  onTriggerBiometrics: (Activity) -> Unit,
  strings: Localization.AppStrings,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val activity = context as? Activity
  val biometricStatus = remember(context) { NativeBiometricHelper.canAuthenticateBiometric(context) }

  // Pulsing animation for the futuristic fingerprint scanner ring
  val infiniteTransition = rememberInfiniteTransition(label = "lock_fingerprint_glow")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 1.0f,
    targetValue = 1.12f,
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "lock_pulse_scale"
  )
  val pulseAlpha by infiniteTransition.animateFloat(
    initialValue = 0.4f,
    targetValue = 0.9f,
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "lock_pulse_alpha"
  )

  // Auto-launch official Android BiometricPrompt on screen entry if supported
  LaunchedEffect(biometricStatus) {
    if (activity != null && biometricStatus is BiometricStatus.Available) {
      onTriggerBiometrics(activity)
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
        // Holographic Glowing Lock Icon
        Box(
          modifier = Modifier
            .size(90.dp)
            .clip(CircleShape)
            .background(DarkSurfaceDeep)
            .border(
              width = 2.dp,
              brush = Brush.linearGradient(listOf(CyberGreen, NeonCyan)),
              shape = CircleShape
            )
            .shadow(elevation = 14.dp, shape = CircleShape, spotColor = GlowCyan),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Fingerprint,
            contentDescription = strings.biometricUnlockTitle,
            tint = CyberGreen,
            modifier = Modifier.size(54.dp)
          )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
          text = strings.biometricUnlockTitle,
          color = TextPrimary,
          fontSize = 22.sp,
          fontWeight = FontWeight.Bold,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = strings.lockedStateNotice,
          color = TextSecondary,
          fontSize = 13.sp,
          textAlign = TextAlign.Center,
          lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Error message card
        AnimatedVisibility(visible = errorMessage != null) {
          errorMessage?.let { err ->
            Card(
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(containerColor = StatusRed.copy(alpha = 0.15f)),
              border = BorderStroke(1.dp, StatusRed.copy(alpha = 0.45f)),
              modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
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
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = err,
                  color = StatusRed,
                  fontSize = 12.sp,
                  lineHeight = 16.sp
                )
              }
            }
          }
        }

        // Biometric Status Warnings
        when (biometricStatus) {
          is BiometricStatus.NoHardware -> {
            Card(
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
              border = BorderStroke(1.dp, StatusRed.copy(alpha = 0.6f)),
              modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
            ) {
              Text(
                text = strings.biometricNoHardware,
                color = TextPrimary,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
              )
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
            ) {
              Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
              ) {
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
                  colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFFB300))
                ) {
                  Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = strings.openDeviceSecuritySettings,
                    fontSize = 12.sp
                  )
                }
              }
            }
          }
          else -> {}
        }

        // Biometric Action Card
        Card(
          shape = RoundedCornerShape(20.dp),
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
            // Holographic pulsating scanner
            Box(
              modifier = Modifier
                .size(100.dp)
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
                    onTriggerBiometrics(activity)
                  }
                },
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Fingerprint,
                contentDescription = strings.unlockWithBiometrics,
                tint = if (biometricStatus is BiometricStatus.Available) CyberGreen else TextTertiary,
                modifier = Modifier.size(52.dp)
              )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
              text = strings.biometricTouchSensorHint,
              color = NeonCyan,
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Primary Biometric Scan Button
            Button(
              onClick = {
                if (activity != null) {
                  onTriggerBiometrics(activity)
                }
              },
              enabled = biometricStatus is BiometricStatus.Available,
              modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("biometric_trigger_button"),
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
              Spacer(modifier = Modifier.width(10.dp))
              Text(
                text = strings.unlockWithBiometrics,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Informative device explanation
        Text(
          text = strings.biometricHardwareNotice,
          color = TextTertiary,
          fontSize = 11.sp,
          textAlign = TextAlign.Center,
          lineHeight = 15.sp,
          modifier = Modifier.padding(horizontal = 12.dp)
        )
      }
    }
  }
}
