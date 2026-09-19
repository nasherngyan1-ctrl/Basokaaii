package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.local.MessageEntity
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MessageBubble(
  message: MessageEntity,
  strings: Localization.AppStrings,
  onOpenSettings: () -> Unit,
  onCopyText: (String) -> Unit,
  isSpeaking: Boolean = false,
  onToggleSpeak: () -> Unit = {},
  onSaveImage: (String) -> Unit = {},
  onShareImage: (String) -> Unit = {},
  onViewFullscreen: (String, String?) -> Unit = { _, _ -> },
  modifier: Modifier = Modifier
) {
  val isUser = message.sender == "USER"
  val isImageMessage = message.messageType == "IMAGE"
  val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
  val timeString = timeFormatter.format(Date(message.timestamp))

  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 14.dp, vertical = 5.dp),
    horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
  ) {
    if (isUser) {
      // User Message Bubble
      Box(
        modifier = Modifier
          .clip(
            RoundedCornerShape(
              topStart = 18.dp,
              topEnd = 18.dp,
              bottomStart = 18.dp,
              bottomEnd = 4.dp
            )
          )
          .background(
            Brush.linearGradient(
              colors = listOf(
                Color(0xFF16243D),
                Color(0xFF0F1A2E)
              )
            )
          )
          .border(
            width = 1.dp,
            brush = Brush.linearGradient(
              colors = listOf(
                NeonCyan.copy(alpha = 0.5f),
                ElectricViolet.copy(alpha = 0.25f)
              )
            ),
            shape = RoundedCornerShape(
              topStart = 18.dp,
              topEnd = 18.dp,
              bottomStart = 18.dp,
              bottomEnd = 4.dp
            )
          )
          .padding(horizontal = 16.dp, vertical = 11.dp)
          .testTag("user_message_bubble")
      ) {
        Column {
          Text(
            text = message.text,
            color = TextPrimary,
            fontSize = 15.sp,
            lineHeight = 22.sp
          )
          Spacer(modifier = Modifier.height(4.dp))
          Row(
            modifier = Modifier.align(Alignment.End),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = timeString,
              color = TextTertiary,
              fontSize = 11.sp
            )
            Spacer(modifier = Modifier.width(4.dp))
            Box(
              modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(NeonCyan)
            )
          }
        }
      }
    } else if (isImageMessage) {
      // Assistant Generated Image Bubble
      AssistantImageBubble(
        message = message,
        timeString = timeString,
        strings = strings,
        onSaveImage = onSaveImage,
        onShareImage = onShareImage,
        onViewFullscreen = onViewFullscreen,
        onCopyText = onCopyText
      )
    } else {
      // Standard Assistant Text Message Bubble
      Column(
        modifier = Modifier
          .fillMaxWidth(0.96f)
          .testTag("assistant_message_bubble")
      ) {
        // Sender header badge
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(bottom = 5.dp, start = 4.dp)
        ) {
          Box(
            modifier = Modifier
              .size(18.dp)
              .clip(CircleShape)
              .background(NeonCyan.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = null,
              tint = NeonCyan,
              modifier = Modifier.size(12.dp)
            )
          }
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = strings.appName,
            color = NeonCyan,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
          )
        }

        Box(
          modifier = Modifier
            .clip(
              RoundedCornerShape(
                topStart = 4.dp,
                topEnd = 18.dp,
                bottomStart = 18.dp,
                bottomEnd = 18.dp
              )
            )
            .background(DarkSurfaceCard)
            .border(
              width = 1.dp,
              color = DarkSurfaceBorder,
              shape = RoundedCornerShape(
                topStart = 4.dp,
                topEnd = 18.dp,
                bottomStart = 18.dp,
                bottomEnd = 18.dp
              )
            )
            .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
          Column {
            Text(
              text = message.text,
              color = TextPrimary,
              fontSize = 15.sp,
              lineHeight = 23.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = timeString,
                color = TextTertiary,
                fontSize = 11.sp
              )

              Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                  onClick = onToggleSpeak,
                  modifier = Modifier
                    .size(24.dp)
                    .testTag("speak_message_${message.id}")
                ) {
                  Icon(
                    imageVector = if (isSpeaking) Icons.Default.Stop else Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = if (isSpeaking) strings.stopSpeaking else strings.playSpeaking,
                    tint = if (isSpeaking) NeonCyan else TextTertiary,
                    modifier = Modifier.size(15.dp)
                  )
                }

                Spacer(modifier = Modifier.width(6.dp))

                IconButton(
                  onClick = { onCopyText(message.text) },
                  modifier = Modifier.size(24.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = strings.copiedToClipboard,
                    tint = TextTertiary,
                    modifier = Modifier.size(14.dp)
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}

/**
 * Assistant Generated Image Bubble
 */
@Composable
fun AssistantImageBubble(
  message: MessageEntity,
  timeString: String,
  strings: Localization.AppStrings,
  onSaveImage: (String) -> Unit,
  onShareImage: (String) -> Unit,
  onViewFullscreen: (String, String?) -> Unit,
  onCopyText: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val imageUri = message.imageUri ?: ""
  val prompt = message.imagePrompt ?: ""

  Column(
    modifier = modifier
      .fillMaxWidth(0.98f)
      .testTag("assistant_image_bubble")
  ) {
    // Header with futuristic AI Palette badge
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 6.dp, start = 4.dp, end = 4.dp)
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(20.dp)
            .clip(CircleShape)
            .background(NeonCyan.copy(alpha = 0.2f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Palette,
            contentDescription = null,
            tint = NeonCyan,
            modifier = Modifier.size(13.dp)
          )
        }
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "${strings.appName} · ${strings.imageGenerationTitle}",
          color = NeonCyan,
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold
        )
      }

      // Model badge
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(6.dp))
          .background(ElectricViolet.copy(alpha = 0.25f))
          .border(0.5.dp, ElectricViolet.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
          .padding(horizontal = 6.dp, vertical = 2.dp)
      ) {
        Text(
          text = strings.imageModelBadge,
          color = ElectricViolet,
          fontSize = 10.sp,
          fontWeight = FontWeight.Medium
        )
      }
    }

    // Main Image Card Container
    Box(
      modifier = Modifier
        .clip(
          RoundedCornerShape(
            topStart = 4.dp,
            topEnd = 18.dp,
            bottomStart = 18.dp,
            bottomEnd = 18.dp
          )
        )
        .background(DarkSurfaceCard)
        .border(
          width = 1.dp,
          brush = Brush.linearGradient(
            colors = listOf(
              NeonCyan.copy(alpha = 0.6f),
              ElectricViolet.copy(alpha = 0.4f),
              DarkSurfaceBorder
            )
          ),
          shape = RoundedCornerShape(
            topStart = 4.dp,
            topEnd = 18.dp,
            bottomStart = 18.dp,
            bottomEnd = 18.dp
          )
        )
        .padding(10.dp)
    ) {
      Column {
        // Prompt chip if available
        if (prompt.isNotEmpty()) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(DarkSurfaceDeep)
              .border(0.5.dp, DarkSurfaceBorder, RoundedCornerShape(10.dp))
              .padding(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "${strings.imagePromptLabel} \"$prompt\"",
                color = TextSecondary,
                fontSize = 12.sp,
                maxLines = 2,
                modifier = Modifier.weight(1f)
              )
              IconButton(
                onClick = { onCopyText(prompt) },
                modifier = Modifier.size(22.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.ContentCopy,
                  contentDescription = strings.copyPrompt,
                  tint = TextTertiary,
                  modifier = Modifier.size(12.dp)
                )
              }
            }
          }
          Spacer(modifier = Modifier.height(8.dp))
        }

        // Image Display Box with clickable fullscreen
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 200.dp, max = 340.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(DarkNavyCanvas)
            .border(1.dp, NeonCyan.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
            .clickable { onViewFullscreen(imageUri, prompt) },
          contentAlignment = Alignment.Center
        ) {
          AsyncImage(
            model = ImageRequest.Builder(context)
              .data(imageUri)
              .crossfade(true)
              .build(),
            contentDescription = prompt.ifEmpty { strings.imageGenerationTitle },
            contentScale = ContentScale.Crop,
            modifier = Modifier
              .fillMaxWidth()
              .heightIn(min = 200.dp, max = 340.dp)
              .clip(RoundedCornerShape(14.dp))
              .testTag("generated_async_image")
          )

          // Fullscreen icon overlay
          Box(
            modifier = Modifier
              .align(Alignment.TopEnd)
              .padding(8.dp)
              .size(32.dp)
              .clip(CircleShape)
              .background(Color.Black.copy(alpha = 0.6f))
              .clickable { onViewFullscreen(imageUri, prompt) },
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Fullscreen,
              contentDescription = strings.viewFullscreen,
              tint = Color.White,
              modifier = Modifier.size(18.dp)
            )
          }
        }

        // Accompanying description/caption if Gemini returned text
        if (message.text.isNotBlank() && message.text != prompt) {
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = message.text,
            color = TextPrimary,
            fontSize = 14.sp,
            lineHeight = 21.sp,
            modifier = Modifier.padding(horizontal = 4.dp)
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Action controls: Save to Gallery & Share
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = timeString,
            color = TextTertiary,
            fontSize = 11.sp
          )

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            // Share button
            IconButton(
              onClick = { onShareImage(imageUri) },
              modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(DarkSurfaceDeep)
                .border(0.5.dp, DarkSurfaceBorder, CircleShape)
                .testTag("share_image_button")
            ) {
              Icon(
                imageVector = Icons.Default.Share,
                contentDescription = strings.shareImage,
                tint = NeonCyan,
                modifier = Modifier.size(15.dp)
              )
            }

            // Save to Device / Download Button
            OutlinedButton(
              onClick = { onSaveImage(imageUri) },
              modifier = Modifier
                .height(32.dp)
                .testTag("save_image_button"),
              colors = ButtonDefaults.outlinedButtonColors(
                contentColor = NeonCyan
              ),
              border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.6f)),
              shape = RoundedCornerShape(10.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Download,
                contentDescription = null,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = strings.saveToGallery,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
              )
            }
          }
        }
      }
    }
  }
}

/**
 * Visual Progress Card when image is generating
 */
@Composable
fun ImageGeneratingBubble(
  prompt: String,
  strings: Localization.AppStrings,
  onStop: () -> Unit,
  modifier: Modifier = Modifier
) {
  val infiniteTransition = rememberInfiniteTransition(label = "image_gen_glow")
  val glowAlpha by infiniteTransition.animateFloat(
    initialValue = 0.35f,
    targetValue = 0.95f,
    animationSpec = infiniteRepeatable(
      animation = tween(900),
      repeatMode = RepeatMode.Reverse
    ),
    label = "image_glow_alpha"
  )

  Column(
    modifier = modifier
      .fillMaxWidth(0.98f)
      .padding(horizontal = 14.dp, vertical = 6.dp)
      .testTag("image_generating_bubble")
  ) {
    // Header badge
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(bottom = 6.dp, start = 4.dp)
    ) {
      Box(
        modifier = Modifier
          .size(20.dp)
          .clip(CircleShape)
          .background(NeonCyan.copy(alpha = glowAlpha * 0.4f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Brush,
          contentDescription = null,
          tint = NeonCyan,
          modifier = Modifier.size(13.dp)
        )
      }
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = "${strings.appName} · ${strings.generatingImage}",
        color = NeonCyan,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold
      )
    }

    Box(
      modifier = Modifier
        .clip(
          RoundedCornerShape(
            topStart = 4.dp,
            topEnd = 18.dp,
            bottomStart = 18.dp,
            bottomEnd = 18.dp
          )
        )
        .background(DarkSurfaceCard)
        .border(
          width = 1.2.dp,
          brush = Brush.horizontalGradient(
            listOf(
              NeonCyan.copy(alpha = glowAlpha),
              ElectricViolet.copy(alpha = 0.5f),
              ElectricBlue.copy(alpha = 0.3f)
            )
          ),
          shape = RoundedCornerShape(
            topStart = 4.dp,
            topEnd = 18.dp,
            bottomStart = 18.dp,
            bottomEnd = 18.dp
          )
        )
        .padding(14.dp)
    ) {
      Column {
        // Shimmer Preview Canvas Box
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(DarkSurfaceDeep)
            .border(
              1.dp,
              Brush.linearGradient(
                listOf(
                  NeonCyan.copy(alpha = glowAlpha * 0.6f),
                  ElectricViolet.copy(alpha = 0.3f)
                )
              ),
              RoundedCornerShape(14.dp)
            ),
          contentAlignment = Alignment.Center
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
          ) {
            CircularProgressIndicator(
              color = NeonCyan,
              strokeWidth = 2.5.dp,
              modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = strings.generatingImageProgress,
              color = TextPrimary,
              fontSize = 13.sp,
              fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = strings.imageModelBadge,
              color = ElectricViolet,
              fontSize = 11.sp,
              fontWeight = FontWeight.Normal
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Display the user prompt
        Text(
          text = "\"$prompt\"",
          color = TextSecondary,
          fontSize = 13.sp,
          lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End
        ) {
          OutlinedButton(
            onClick = onStop,
            modifier = Modifier
              .height(30.dp)
              .testTag("cancel_image_generation_button"),
            colors = ButtonDefaults.outlinedButtonColors(
              contentColor = StatusAmber
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, StatusAmber.copy(alpha = 0.6f)),
            shape = RoundedCornerShape(8.dp)
          ) {
            Text(
              text = strings.cancelImageGeneration,
              fontSize = 11.sp,
              fontWeight = FontWeight.Medium
            )
          }
        }
      }
    }
  }
}

/**
 * Fullscreen Image Viewer Modal Dialog
 */
@Composable
fun FullscreenImageDialog(
  imageUri: String,
  prompt: String?,
  strings: Localization.AppStrings,
  onDismiss: () -> Unit,
  onSaveImage: (String) -> Unit,
  onShareImage: (String) -> Unit
) {
  val context = LocalContext.current

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(Color.Black.copy(alpha = 0.94f))
        .padding(16.dp)
    ) {
      Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
      ) {
        // Top action bar with title & close button
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = strings.imageGenerationTitle,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
          )

          IconButton(
            onClick = onDismiss,
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(Color.White.copy(alpha = 0.15f))
              .testTag("close_fullscreen_dialog_button")
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = strings.close,
              tint = Color.White,
              modifier = Modifier.size(20.dp)
            )
          }
        }

        // Center high-res image
        Box(
          modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
            .padding(vertical = 16.dp),
          contentAlignment = Alignment.Center
        ) {
          AsyncImage(
            model = ImageRequest.Builder(context)
              .data(imageUri)
              .crossfade(true)
              .build(),
            contentDescription = prompt ?: strings.imageGenerationTitle,
            contentScale = ContentScale.Fit,
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
          )
        }

        // Bottom prompt and action buttons
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
        ) {
          if (!prompt.isNullOrBlank()) {
            Text(
              text = "\"$prompt\"",
              color = Color.White.copy(alpha = 0.85f),
              fontSize = 13.sp,
              modifier = Modifier.padding(bottom = 12.dp)
            )
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            OutlinedButton(
              onClick = { onShareImage(imageUri) },
              modifier = Modifier
                .weight(1f)
                .height(42.dp),
              colors = ButtonDefaults.outlinedButtonColors(
                contentColor = NeonCyan
              ),
              border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.6f)),
              shape = RoundedCornerShape(10.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Share,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = strings.shareImage,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
              )
            }

            OutlinedButton(
              onClick = { onSaveImage(imageUri) },
              modifier = Modifier
                .weight(1f)
                .height(42.dp)
                .testTag("fullscreen_save_button"),
              colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color.White,
                containerColor = NeonCyan.copy(alpha = 0.25f)
              ),
              border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan),
              shape = RoundedCornerShape(10.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Download,
                contentDescription = null,
                tint = NeonCyan,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = strings.saveToGallery,
                color = NeonCyan,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
              )
            }
          }
        }
      }
    }
  }
}

/**
 * Live streaming bubble when Basoka AI is typing in real-time
 */
@Composable
fun StreamingAssistantBubble(
  partialText: String,
  strings: Localization.AppStrings,
  onStop: () -> Unit,
  modifier: Modifier = Modifier
) {
  val infiniteTransition = rememberInfiniteTransition(label = "pulse_glow")
  val glowAlpha by infiniteTransition.animateFloat(
    initialValue = 0.3f,
    targetValue = 0.9f,
    animationSpec = infiniteRepeatable(
      animation = tween(800),
      repeatMode = RepeatMode.Reverse
    ),
    label = "glow_alpha"
  )

  Column(
    modifier = modifier
      .fillMaxWidth(0.96f)
      .padding(horizontal = 14.dp, vertical = 6.dp)
      .testTag("streaming_assistant_bubble")
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(bottom = 5.dp, start = 4.dp)
    ) {
      Box(
        modifier = Modifier
          .size(18.dp)
          .clip(CircleShape)
          .background(NeonCyan.copy(alpha = glowAlpha * 0.4f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.AutoAwesome,
          contentDescription = null,
          tint = NeonCyan,
          modifier = Modifier.size(12.dp)
        )
      }
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = "${strings.appName} · ${strings.generatingResponse}",
        color = NeonCyan,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold
      )
    }

    Box(
      modifier = Modifier
        .clip(
          RoundedCornerShape(
            topStart = 4.dp,
            topEnd = 18.dp,
            bottomStart = 18.dp,
            bottomEnd = 18.dp
          )
        )
        .background(DarkSurfaceCard)
        .border(
          width = 1.dp,
          brush = Brush.horizontalGradient(
            listOf(
              NeonCyan.copy(alpha = glowAlpha * 0.7f),
              ElectricViolet.copy(alpha = 0.3f)
            )
          ),
          shape = RoundedCornerShape(
            topStart = 4.dp,
            topEnd = 18.dp,
            bottomStart = 18.dp,
            bottomEnd = 18.dp
          )
        )
        .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
      Column {
        if (partialText.isEmpty()) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(vertical = 4.dp)
          ) {
            Box(
              modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(NeonCyan.copy(alpha = glowAlpha))
            )
            Box(
              modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(ElectricBlue.copy(alpha = (1f - glowAlpha).coerceIn(0.2f, 0.9f)))
            )
            Box(
              modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(ElectricViolet.copy(alpha = glowAlpha))
            )
          }
        } else {
          Text(
            text = partialText,
            color = TextPrimary,
            fontSize = 15.sp,
            lineHeight = 23.sp
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End
        ) {
          OutlinedButton(
            onClick = onStop,
            modifier = Modifier
              .height(30.dp)
              .testTag("stop_generation_button"),
            colors = ButtonDefaults.outlinedButtonColors(
              contentColor = StatusAmber
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, StatusAmber.copy(alpha = 0.6f)),
            shape = RoundedCornerShape(8.dp)
          ) {
            Text(
              text = strings.stopGeneration,
              fontSize = 11.sp,
              fontWeight = FontWeight.Medium
            )
          }
        }
      }
    }
  }
}

/**
 * Error / Needs Configuration Banner Card
 */
@Composable
fun GenerationErrorCard(
  errorMessage: String,
  strings: Localization.AppStrings,
  onRetry: () -> Unit,
  onOpenSettings: () -> Unit,
  modifier: Modifier = Modifier
) {
  val isKeyMissing = errorMessage.contains("GEMINI_API_KEY") ||
    errorMessage.contains("API_KEY") ||
    errorMessage.contains("AWAITING_API_KEY")
  val isImageError = errorMessage.contains("image") || errorMessage.contains("Image") || errorMessage.contains("مۆدێل")

  val displayErrorText = when {
    isKeyMissing && isImageError -> strings.errorImageApiKeyMissing
    isKeyMissing -> strings.errorApiKeyMissing
    isImageError -> "${strings.errorImageGenerationFailed} ($errorMessage)"
    errorMessage.contains("Unable to resolve host") || errorMessage.contains("timeout") -> strings.errorNetwork
    else -> "${strings.errorGeneral} ($errorMessage)"
  }

  Box(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 14.dp, vertical = 6.dp)
      .clip(RoundedCornerShape(16.dp))
      .background(DarkSurfaceDeep)
      .border(
        width = 1.dp,
        brush = Brush.horizontalGradient(
          colors = listOf(
            StatusAmber.copy(alpha = 0.7f),
            ElectricViolet.copy(alpha = 0.3f),
            DarkSurfaceBorder
          )
        ),
        shape = RoundedCornerShape(16.dp)
      )
      .padding(14.dp)
      .testTag("generation_error_card")
  ) {
    Column {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Box(
          modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(StatusAmber.copy(alpha = 0.15f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.WarningAmber,
            contentDescription = null,
            tint = StatusAmber,
            modifier = Modifier.size(16.dp)
          )
        }

        Text(
          text = if (isKeyMissing) strings.initialNoticeTitle else strings.errorGeneral,
          color = StatusAmber,
          fontWeight = FontWeight.SemiBold,
          fontSize = 13.sp
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = displayErrorText,
        color = TextSecondary,
        fontSize = 13.sp,
        lineHeight = 19.sp
      )

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        if (isKeyMissing) {
          OutlinedButton(
            onClick = onOpenSettings,
            colors = ButtonDefaults.outlinedButtonColors(
              contentColor = NeonCyan
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
              .weight(1f)
              .height(36.dp)
              .testTag("error_open_settings_button")
          ) {
            Icon(
              imageVector = Icons.Default.Settings,
              contentDescription = null,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = strings.configureApiAction,
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium
            )
          }
        }

        OutlinedButton(
          onClick = onRetry,
          colors = ButtonDefaults.outlinedButtonColors(
            contentColor = if (isKeyMissing) TextSecondary else StatusAmber
          ),
          border = androidx.compose.foundation.BorderStroke(
            1.dp,
            (if (isKeyMissing) DarkSurfaceBorder else StatusAmber).copy(alpha = 0.6f)
          ),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier
            .weight(1f)
            .height(36.dp)
            .testTag("error_retry_button")
        ) {
          Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = null,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = strings.retry,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
          )
        }
      }
    }
  }
}
