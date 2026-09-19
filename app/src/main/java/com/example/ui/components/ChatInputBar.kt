package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Localization
import com.example.ui.theme.DarkNavyCanvas
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DarkSurfaceDeep
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun ChatInputBar(
  value: String,
  onValueChange: (String) -> Unit,
  onSend: () -> Unit,
  onStop: () -> Unit,
  isGenerating: Boolean,
  onMicClick: () -> Unit,
  isListening: Boolean = false,
  isImageMode: Boolean = false,
  onToggleImageMode: () -> Unit = {},
  strings: Localization.AppStrings,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 12.dp, vertical = 6.dp)
  ) {
    // Active Image Mode Indicator Strip
    AnimatedVisibility(
      visible = isImageMode,
      enter = fadeIn(),
      exit = fadeOut()
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 6.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(DarkSurfaceDeep)
          .border(
            width = 1.dp,
            brush = Brush.horizontalGradient(
              listOf(ElectricViolet, NeonCyan, ElectricViolet)
            ),
            shape = RoundedCornerShape(12.dp)
          )
          .padding(horizontal = 12.dp, vertical = 6.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Palette,
              contentDescription = null,
              tint = NeonCyan,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = strings.imageGenerationModeBadge,
              color = NeonCyan,
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium
            )
          }

          Box(
            modifier = Modifier
              .size(22.dp)
              .clip(CircleShape)
              .background(Color.White.copy(alpha = 0.1f))
              .clickable { onToggleImageMode() },
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = strings.exitImageMode,
              tint = TextSecondary,
              modifier = Modifier.size(14.dp)
            )
          }
        }
      }
    }

    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Toggle Image Generation Mode button
      IconButton(
        onClick = onToggleImageMode,
        modifier = Modifier
          .padding(end = 6.dp)
          .size(44.dp)
          .clip(CircleShape)
          .background(
            if (isImageMode) ElectricViolet.copy(alpha = 0.25f) else DarkSurfaceCard
          )
          .border(
            width = 1.dp,
            brush = if (isImageMode) {
              Brush.linearGradient(listOf(ElectricViolet, NeonCyan))
            } else {
              Brush.linearGradient(listOf(DarkSurfaceBorder, DarkSurfaceBorder))
            },
            shape = CircleShape
          )
          .testTag("toggle_image_mode_button")
      ) {
        Icon(
          imageVector = Icons.Default.Palette,
          contentDescription = strings.imageGenerationMode,
          tint = if (isImageMode) NeonCyan else TextSecondary,
          modifier = Modifier.size(20.dp)
        )
      }

      // Input container with subtle futuristic electric border
      Box(
        modifier = Modifier
          .weight(1f)
          .clip(RoundedCornerShape(26.dp))
          .background(DarkSurfaceCard)
          .border(
            width = 1.dp,
            brush = Brush.horizontalGradient(
              colors = if (isImageMode) {
                listOf(ElectricViolet.copy(alpha = 0.6f), NeonCyan.copy(alpha = 0.7f), ElectricViolet.copy(alpha = 0.6f))
              } else {
                listOf(DarkSurfaceBorder, NeonCyan.copy(alpha = 0.35f), DarkSurfaceBorder)
              }
            ),
            shape = RoundedCornerShape(26.dp)
          )
          .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.CenterStart
      ) {
        if (value.isEmpty()) {
          Text(
            text = if (isImageMode) strings.imageInputPlaceholder else strings.inputPlaceholder,
            color = TextSecondary.copy(alpha = 0.65f),
            fontSize = 14.sp,
            maxLines = 1,
            modifier = Modifier.fillMaxWidth()
          )
        }
        BasicTextField(
          value = value,
          onValueChange = onValueChange,
          enabled = !isGenerating,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("message_input_field"),
          textStyle = TextStyle(
            color = TextPrimary,
            fontSize = 15.sp
          ),
          cursorBrush = SolidColor(if (isImageMode) ElectricViolet else NeonCyan),
          maxLines = 4
        )
      }

      // Microphone button for real voice input
      IconButton(
        onClick = onMicClick,
        modifier = Modifier
          .padding(start = 6.dp)
          .size(44.dp)
          .clip(CircleShape)
          .background(
            if (isListening) NeonCyan.copy(alpha = 0.2f) else DarkSurfaceCard
          )
          .border(
            1.dp,
            if (isListening) NeonCyan else DarkSurfaceBorder,
            CircleShape
          )
          .testTag("mic_button")
      ) {
        Icon(
          imageVector = Icons.Default.Mic,
          contentDescription = if (isListening) strings.speechListeningPrompt else strings.voiceInputFuture,
          tint = if (isListening) NeonCyan else TextSecondary,
          modifier = Modifier.size(20.dp)
        )
      }

      // Send or Stop Generation button with electric glow
      if (isGenerating) {
        IconButton(
          onClick = onStop,
          modifier = Modifier
            .padding(start = 6.dp)
            .size(44.dp)
            .clip(CircleShape)
            .background(StatusAmber)
            .shadow(elevation = 6.dp, shape = CircleShape, spotColor = StatusAmber)
            .testTag("stop_button_input_bar")
        ) {
          Icon(
            imageVector = Icons.Default.Stop,
            contentDescription = strings.stopGeneration,
            tint = DarkNavyCanvas,
            modifier = Modifier.size(22.dp)
          )
        }
      } else {
        val isSendEnabled = value.isNotBlank()
        IconButton(
          onClick = {
            if (isSendEnabled) {
              onSend()
            }
          },
          enabled = isSendEnabled,
          modifier = Modifier
            .padding(start = 6.dp)
            .size(44.dp)
            .clip(CircleShape)
            .background(
              brush = if (isSendEnabled) {
                if (isImageMode) {
                  Brush.linearGradient(listOf(ElectricViolet, NeonCyan))
                } else {
                  Brush.linearGradient(listOf(NeonCyan, ElectricBlue))
                }
              } else {
                Brush.linearGradient(listOf(DarkSurfaceBorder, DarkSurfaceBorder))
              }
            )
            .then(
              if (isSendEnabled) {
                Modifier.shadow(
                  elevation = 6.dp,
                  shape = CircleShape,
                  spotColor = if (isImageMode) ElectricViolet else NeonCyan
                )
              } else Modifier
            )
            .testTag("send_button")
        ) {
          Icon(
            imageVector = if (isImageMode) Icons.Default.Palette else Icons.AutoMirrored.Filled.Send,
            contentDescription = strings.send,
            tint = if (isSendEnabled) DarkNavyCanvas else TextSecondary.copy(alpha = 0.5f),
            modifier = Modifier.size(20.dp)
          )
        }
      }
    }
  }
}
