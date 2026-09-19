package com.example.ui.components

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import com.example.ui.theme.DarkNavyCanvas
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.NeonCyan

@Composable
fun BasokaElectricBackground(
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit
) {
  val infiniteTransition = rememberInfiniteTransition(label = "electric_pulse")

  val pulseAlpha by infiniteTransition.animateFloat(
    initialValue = 0.12f,
    targetValue = 0.28f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 3200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse_alpha"
  )

  val ringScale by infiniteTransition.animateFloat(
    initialValue = 0.85f,
    targetValue = 1.15f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 4000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "ring_scale"
  )

  val rotationAngle by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 30000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "slow_drift"
  )

  val paintGlow = remember {
    Paint().apply {
      isAntiAlias = true
      textAlign = Paint.Align.CENTER
      typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
      letterSpacing = 0.25f
    }
  }

  Box(modifier = modifier.fillMaxSize()) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val width = size.width
      val height = size.height
      val centerX = width / 2f
      val centerY = height * 0.42f

      // 1. Deep Space Base Gradient
      drawRect(
        brush = Brush.verticalGradient(
          colors = listOf(
            DarkNavyCanvas,
            Color(0xFF0A1020),
            DarkNavyCanvas
          )
        )
      )

      // 2. Central Electric Energy Nebula Core
      val coreRadius = width * 0.75f * ringScale
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(
            NeonCyan.copy(alpha = pulseAlpha * 0.75f),
            ElectricViolet.copy(alpha = pulseAlpha * 0.45f),
            ElectricBlue.copy(alpha = pulseAlpha * 0.2f),
            Color.Transparent
          ),
          center = Offset(centerX, centerY),
          radius = coreRadius
        ),
        center = Offset(centerX, centerY),
        radius = coreRadius
      )

      // 3. Subtle Electric Ring Pulses
      val ringRadius1 = width * 0.38f * ringScale
      val ringRadius2 = width * 0.52f * ringScale
      drawCircle(
        color = NeonCyan.copy(alpha = pulseAlpha * 0.25f),
        center = Offset(centerX, centerY),
        radius = ringRadius1,
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
      )
      drawCircle(
        color = ElectricViolet.copy(alpha = pulseAlpha * 0.18f),
        center = Offset(centerX, centerY),
        radius = ringRadius2,
        style = androidx.compose.ui.graphics.drawscope.Stroke(
          width = 1.5f,
          pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(15f, 25f))
        )
      )

      // 4. Subtle Background "BASOKA AI" Electric Glowing Typography
      drawIntoCanvas { canvas ->
        val native = canvas.nativeCanvas
        val textSizeSp = (width * 0.11f).coerceIn(36f, 54f)

        paintGlow.textSize = textSizeSp

        // Outer glow layer
        paintGlow.color = NeonCyan.copy(alpha = pulseAlpha * 0.28f).toArgb()
        paintGlow.setShadowLayer(
          28f,
          0f,
          0f,
          NeonCyan.copy(alpha = pulseAlpha * 0.85f).toArgb()
        )
        native.drawText("BASOKA AI", centerX, centerY, paintGlow)

        // Mid glow layer
        paintGlow.color = ElectricViolet.copy(alpha = pulseAlpha * 0.35f).toArgb()
        paintGlow.setShadowLayer(
          14f,
          0f,
          0f,
          ElectricViolet.copy(alpha = pulseAlpha * 0.6f).toArgb()
        )
        native.drawText("BASOKA AI", centerX, centerY, paintGlow)

        // Crisp electric core text
        paintGlow.color = Color.White.copy(alpha = pulseAlpha * 0.42f).toArgb()
        paintGlow.clearShadowLayer()
        native.drawText("BASOKA AI", centerX, centerY, paintGlow)
      }
    }

    // Foreground content (Chat messages, inputs, top bars)
    content()
  }
}
