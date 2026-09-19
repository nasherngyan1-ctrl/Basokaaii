package com.example.ui.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UserEntity
import com.example.data.repository.UserSettings
import com.example.model.AppLanguage
import com.example.model.Localization
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.DarkNavyCanvas
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DarkSurfaceDeep
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusRed
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun SettingsScreen(
  settings: UserSettings,
  currentUser: UserEntity?,
  onLanguageChange: (AppLanguage) -> Unit,
  onDarkModeToggle: (Boolean) -> Unit,
  onBiometricToggle: (Boolean) -> Unit,
  onPrivateDeviceToggle: (Boolean) -> Unit,
  onLockTimeoutChange: (Int) -> Unit,
  onMemoryToggle: (Boolean) -> Unit,
  onOpenMemories: () -> Unit = {},
  onOpenProfile: () -> Unit,
  onLockAppManually: () -> Unit,
  onLogout: () -> Unit,
  onBack: () -> Unit,
  strings: Localization.AppStrings,
  modifier: Modifier = Modifier
) {
  Scaffold(
    containerColor = DarkNavyCanvas,
    topBar = {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .statusBarsPadding()
          .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(
          onClick = onBack,
          modifier = Modifier.testTag("settings_back_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = strings.back,
            tint = TextPrimary
          )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = strings.settings,
          color = TextPrimary,
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }
  ) { padding ->
    Column(
      modifier = modifier
        .fillMaxSize()
        .padding(padding)
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 16.dp, vertical = 8.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // 1. Personal Account Card Header
      SettingsSectionCard(
        title = strings.accountSection,
        icon = Icons.Default.AccountCircle
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenProfile() }
            .padding(vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(44.dp)
              .clip(CircleShape)
              .background(DarkSurfaceDeep)
              .border(1.dp, NeonCyan, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.AccountCircle,
              contentDescription = null,
              tint = NeonCyan,
              modifier = Modifier.size(28.dp)
            )
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = currentUser?.displayName ?: "Personal User",
              color = TextPrimary,
              fontSize = 15.sp,
              fontWeight = FontWeight.SemiBold
            )
            Text(
              text = strings.biometricFingerprintActive,
              color = CyberGreen,
              fontSize = 12.sp
            )
          }
          Text(
            text = strings.profileTitle,
            color = NeonCyan,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
          )
        }
      }

      // 2. Security & Biometrics Section
      SettingsSectionCard(
        title = strings.securitySection,
        icon = Icons.Default.Security
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
          // App Lock (Biometrics)
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = strings.appLockTitle,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
              )
              Text(
                text = strings.appLockSubtitle,
                color = TextTertiary,
                fontSize = 12.sp
              )
            }
            Switch(
              checked = settings.isBiometricLockEnabled,
              onCheckedChange = onBiometricToggle,
              colors = SwitchDefaults.colors(
                checkedThumbColor = CyberGreen,
                checkedTrackColor = Color(0xFF0C3822),
                uncheckedThumbColor = TextTertiary,
                uncheckedTrackColor = DarkSurfaceDeep
              )
            )
          }

          // Private Device Mode
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = strings.privateDeviceTitle,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
              )
              Text(
                text = strings.privateDeviceSubtitle,
                color = TextTertiary,
                fontSize = 12.sp
              )
            }
            Switch(
              checked = settings.isPrivateDeviceMode,
              onCheckedChange = onPrivateDeviceToggle,
              colors = SwitchDefaults.colors(
                checkedThumbColor = NeonCyan,
                checkedTrackColor = Color(0xFF0C2438),
                uncheckedThumbColor = TextTertiary,
                uncheckedTrackColor = DarkSurfaceDeep
              )
            )
          }

          // Lock Timeout selector (when app lock is active)
          if (settings.isBiometricLockEnabled) {
            Column(modifier = Modifier.fillMaxWidth()) {
              Text(
                text = strings.lockTimeoutTitle,
                color = TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
              )
              Spacer(modifier = Modifier.height(8.dp))
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                listOf(
                  0 to strings.lockImmediately,
                  1 to strings.lockAfter1Min,
                  5 to strings.lockAfter5Min
                ).forEach { (mins, label) ->
                  val isSelected = settings.lockTimeoutMinutes == mins
                  Box(
                    modifier = Modifier
                      .weight(1f)
                      .clip(RoundedCornerShape(10.dp))
                      .background(if (isSelected) NeonCyan.copy(alpha = 0.15f) else DarkSurfaceDeep)
                      .border(
                        width = 1.dp,
                        color = if (isSelected) NeonCyan else DarkSurfaceBorder,
                        shape = RoundedCornerShape(10.dp)
                      )
                      .clickable { onLockTimeoutChange(mins) }
                      .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                  ) {
                    Text(
                      text = label,
                      color = if (isSelected) NeonCyan else TextSecondary,
                      fontSize = 11.sp,
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                  }
                }
              }
            }

            // Manual Lock App now button
            Button(
              onClick = onLockAppManually,
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = DarkSurfaceDeep,
                contentColor = NeonCyan
              ),
              border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.3f)),
              modifier = Modifier
                .fillMaxWidth()
                .height(42.dp)
            ) {
              Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text("قوفڵکردنی باسۆکا ئێستا (Lock App Now)", fontSize = 12.sp)
            }
          }
        }
      }

      // 3. Language Section
      SettingsSectionCard(
        title = strings.languageSection,
        icon = Icons.Default.Language
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          AppLanguage.values().forEach { lang ->
            val isSelected = settings.language == lang
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(if (isSelected) NeonCyan.copy(alpha = 0.12f) else Color.Transparent)
                .clickable { onLanguageChange(lang) }
                .padding(horizontal = 12.dp, vertical = 10.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = lang.nativeName,
                  color = if (isSelected) NeonCyan else TextPrimary,
                  fontSize = 14.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
                Text(
                  text = lang.englishName,
                  color = TextTertiary,
                  fontSize = 11.sp
                )
              }
              if (isSelected) {
                Icon(
                  imageVector = Icons.Default.Check,
                  contentDescription = null,
                  tint = NeonCyan,
                  modifier = Modifier.size(18.dp)
                )
              }
            }
          }
        }
      }

      // 4. Theme & Appearance
      SettingsSectionCard(
        title = strings.themeSection,
        icon = Icons.Default.DarkMode
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = strings.darkMode,
              color = TextPrimary,
              fontSize = 14.sp,
              fontWeight = FontWeight.Medium
            )
            Text(
              text = "OLED Futuristic Dark & Neon",
              color = TextTertiary,
              fontSize = 12.sp
            )
          }
          Switch(
            checked = settings.isDarkMode,
            onCheckedChange = onDarkModeToggle,
            colors = SwitchDefaults.colors(
              checkedThumbColor = NeonCyan,
              checkedTrackColor = Color(0xFF0C2438),
              uncheckedThumbColor = TextTertiary,
              uncheckedTrackColor = DarkSurfaceDeep
            )
          )
        }
      }

      // 5. Personal AI Memory
      SettingsSectionCard(
        title = strings.memorySection,
        icon = Icons.Default.Psychology
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = strings.personalMemoryTitle,
              color = TextPrimary,
              fontSize = 14.sp,
              fontWeight = FontWeight.Medium
            )
            Text(
              text = strings.personalMemorySubtitle,
              color = TextTertiary,
              fontSize = 12.sp
            )
          }
          Switch(
            checked = settings.isMemoryEnabled,
            onCheckedChange = onMemoryToggle,
            colors = SwitchDefaults.colors(
              checkedThumbColor = NeonCyan,
              checkedTrackColor = Color(0xFF0C2438),
              uncheckedThumbColor = TextTertiary,
              uncheckedTrackColor = DarkSurfaceDeep
            )
          )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Button(
          onClick = onOpenMemories,
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = DarkSurfaceDeep,
            contentColor = NeonCyan
          ),
          border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.35f)),
          modifier = Modifier
            .fillMaxWidth()
            .height(42.dp)
            .testTag("open_memories_button")
        ) {
          Icon(Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(17.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(strings.manageMemoriesAction, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }
      }

      // 6. Architecture Status Card
      SettingsSectionCard(
        title = strings.architectureSection,
        icon = Icons.Default.AutoAwesome
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          ArchitectureStatusRow(
            icon = Icons.Default.AutoAwesome,
            title = strings.geminiApiStatus,
            desc = strings.geminiApiDesc,
            isReady = true,
            strings = strings
          )
          ArchitectureStatusRow(
            icon = Icons.Default.Storage,
            title = strings.localDbStatus,
            desc = strings.localDbDesc,
            isReady = true,
            strings = strings
          )
          ArchitectureStatusRow(
            icon = Icons.Default.Fingerprint,
            title = strings.biometricLockTitle,
            desc = strings.biometricHardwareNotice,
            isReady = true,
            strings = strings
          )
          ArchitectureStatusRow(
            icon = Icons.Default.CloudQueue,
            title = strings.secureBackendStatus,
            desc = strings.secureBackendDesc,
            isReady = false,
            strings = strings
          )
        }
      }

      // 7. Logout Button
      Button(
        onClick = onLogout,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = StatusRed.copy(alpha = 0.15f),
          contentColor = StatusRed
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, StatusRed.copy(alpha = 0.35f)),
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .testTag("settings_logout_button")
      ) {
        Icon(
          imageVector = Icons.Default.ExitToApp,
          contentDescription = null,
          tint = StatusRed,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = strings.logoutButton,
          fontSize = 14.sp,
          fontWeight = FontWeight.SemiBold
        )
      }

      // App Version
      Text(
        text = strings.version,
        color = TextTertiary,
        fontSize = 11.sp,
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 8.dp),
        textAlign = androidx.compose.ui.text.style.TextAlign.Center
      )
    }
  }
}

@Composable
private fun SettingsSectionCard(
  title: String,
  icon: ImageVector,
  content: @Composable () -> Unit
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
    border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 12.dp)
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = NeonCyan,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = title,
          color = NeonCyan,
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 0.5.sp
        )
      }
      content()
    }
  }
}

@Composable
private fun ArchitectureStatusRow(
  icon: ImageVector,
  title: String,
  desc: String,
  isReady: Boolean,
  strings: Localization.AppStrings
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = if (isReady) CyberGreen else StatusAmber,
      modifier = Modifier.size(18.dp)
    )
    Spacer(modifier = Modifier.width(10.dp))
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        color = TextPrimary,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium
      )
      Text(
        text = desc,
        color = TextTertiary,
        fontSize = 11.sp,
        lineHeight = 15.sp
      )
    }
    Spacer(modifier = Modifier.width(6.dp))
    Box(
      modifier = Modifier
        .clip(RoundedCornerShape(6.dp))
        .background(if (isReady) CyberGreen.copy(alpha = 0.15f) else StatusAmber.copy(alpha = 0.15f))
        .padding(horizontal = 6.dp, vertical = 3.dp)
    ) {
      Text(
        text = if (isReady) strings.statusReady else strings.statusConfigNeeded,
        color = if (isReady) CyberGreen else StatusAmber,
        fontSize = 9.sp,
        fontWeight = FontWeight.Bold
      )
    }
  }
}
