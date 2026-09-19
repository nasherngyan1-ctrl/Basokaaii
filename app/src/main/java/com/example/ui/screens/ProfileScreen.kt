package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.UserEntity
import com.example.model.Localization
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.DarkNavyCanvas
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DarkSurfaceDeep
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.StatusRed
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProfileScreen(
  user: UserEntity?,
  onBack: () -> Unit,
  onLogout: () -> Unit,
  onOpenSecuritySettings: () -> Unit,
  strings: Localization.AppStrings,
  modifier: Modifier = Modifier
) {
  val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

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
          modifier = Modifier.testTag("profile_back_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = strings.back,
            tint = TextPrimary
          )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = strings.profileTitle,
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
        .padding(horizontal = 16.dp, vertical = 12.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // User Avatar Badge
      Box(
        modifier = Modifier
          .size(80.dp)
          .clip(CircleShape)
          .background(DarkSurfaceDeep)
          .border(
            width = 1.5.dp,
            brush = Brush.linearGradient(listOf(NeonCyan, ElectricViolet)),
            shape = CircleShape
          ),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Person,
          contentDescription = null,
          tint = NeonCyan,
          modifier = Modifier.size(44.dp)
        )
      }

      Text(
        text = user?.displayName ?: "Personal User",
        color = TextPrimary,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold
      )

      // Device badge
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceDeep.copy(alpha = 0.9f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberGreen.copy(alpha = 0.3f))
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Security,
            contentDescription = null,
            tint = CyberGreen,
            modifier = Modifier.size(15.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = strings.personalAccountBadge,
            color = CyberGreen,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // User Information Details Card
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          // Authentication method
          ProfileInfoRow(
            icon = Icons.Default.Fingerprint,
            label = strings.authMethodLabel,
            value = strings.biometricFingerprintActive
          )

          // Created At
          val createdStr = user?.createdAt?.let { dateFormatter.format(Date(it)) } ?: "—"
          ProfileInfoRow(
            icon = Icons.Default.Security,
            label = "تاریخی تۆمارکردن",
            value = createdStr
          )

          // Last Active
          val lastLoginStr = user?.lastLoginAt?.let { dateFormatter.format(Date(it)) } ?: "—"
          ProfileInfoRow(
            icon = Icons.Default.Lock,
            label = "دواین چوونەژوورەوە",
            value = lastLoginStr
          )
        }
      }

      // Quick Security Settings Button
      OutlinedButton(
        onClick = onOpenSecuritySettings,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonCyan),
        border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f)),
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .testTag("profile_security_settings_button")
      ) {
        Icon(
          imageVector = Icons.Default.Fingerprint,
          contentDescription = null,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = strings.securitySection,
          fontSize = 14.sp
        )
      }

      // Logout Button
      Button(
        onClick = onLogout,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = StatusRed.copy(alpha = 0.2f),
          contentColor = StatusRed
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, StatusRed.copy(alpha = 0.5f)),
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .testTag("profile_logout_button")
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
    }
  }
}

@Composable
private fun ProfileInfoRow(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  label: String,
  value: String
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .size(36.dp)
        .clip(CircleShape)
        .background(DarkSurfaceDeep),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = NeonCyan,
        modifier = Modifier.size(18.dp)
      )
    }
    Spacer(modifier = Modifier.width(12.dp))
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = label,
        color = TextTertiary,
        fontSize = 11.sp
      )
      Text(
        text = value,
        color = TextPrimary,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium
      )
    }
  }
}
