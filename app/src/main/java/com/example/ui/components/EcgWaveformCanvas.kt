package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun EcgWaveformCanvas(
    waveform: List<Float>,
    currentBpm: Int,
    hrvMs: Int,
    isStreaming: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val heartScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 60000 / currentBpm.coerceIn(40, 180), easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "heartScale"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .background(CardDark, RoundedCornerShape(16.dp))
            .border(1.dp, CardBorderDark, RoundedCornerShape(16.dp))
            .padding(12.dp)
            .testTag("ecg_waveform_card")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header: Live BPM, Status & Pulse Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Pulse",
                        tint = MagentaHeart,
                        modifier = Modifier
                            .size((18 * heartScale).dp)
                            .padding(end = 4.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$currentBpm",
                        color = TextPrimary,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = " BPM",
                        color = MagentaPulse,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(start = 2.dp, top = 6.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "HRV: $hrvMs ms",
                        color = CyanPulse,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier
                            .background(CardElevatedDark, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(if (isStreaming) EmeraldVitals else TextTertiary, RoundedCornerShape(4.dp))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isStreaming) "SINUS RHYTHM" else "SYNC PAUSED",
                        color = if (isStreaming) EmeraldVitals else TextTertiary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Oscilloscope Grid & Smooth Polyline Canvas
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                val width = size.width
                val height = size.height

                // Draw oscilloscope medical grid lines
                val gridColor = Color(0x1A00F2FE)
                val numVertical = 12
                val numHorizontal = 6

                for (i in 0..numVertical) {
                    val x = i * (width / numVertical)
                    drawLine(
                        color = gridColor,
                        start = Offset(x, 0f),
                        end = Offset(x, height),
                        strokeWidth = 1f
                    )
                }
                for (j in 0..numHorizontal) {
                    val y = j * (height / numHorizontal)
                    drawLine(
                        color = gridColor,
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = 1f
                    )
                }

                // Center baseline line
                drawLine(
                    color = Color(0x3300F2FE),
                    start = Offset(0f, height * 0.5f),
                    end = Offset(width, height * 0.5f),
                    strokeWidth = 1.5f
                )

                if (waveform.size > 1) {
                    val stepX = width / (waveform.size - 1)
                    val path = Path()

                    waveform.forEachIndexed { index, sample ->
                        val x = index * stepX
                        // Waveform normalized 0f (top) to 1f (bottom)
                        val y = (1.0f - sample) * height
                        if (index == 0) {
                            path.moveTo(x, y)
                        } else {
                            path.lineTo(x, y)
                        }
                    }

                    // Stroke with glowing cyan-to-magenta gradient
                    val brush = Brush.horizontalGradient(
                        colors = listOf(CyanPulse, CyanGlow, MagentaHeart),
                        startX = 0f,
                        endX = width
                    )

                    drawPath(
                        path = path,
                        brush = brush,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Draw glowing leading cursor at the latest sample point
                    val lastX = (waveform.size - 1) * stepX
                    val lastY = (1.0f - waveform.last()) * height
                    drawCircle(
                        color = CyanPulse,
                        radius = 4.dp.toPx(),
                        center = Offset(lastX, lastY)
                    )
                    drawCircle(
                        color = Color(0x6600F2FE),
                        radius = 8.dp.toPx(),
                        center = Offset(lastX, lastY)
                    )
                }
            }

            // Bottom sensor caption
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Lead-I Equivalent • 100Hz PPG",
                    color = TextTertiary,
                    fontSize = 10.sp
                )
                Text(
                    text = "QRS Complex: Normal (88 ms)",
                    color = TextTertiary,
                    fontSize = 10.sp
                )
            }
        }
    }
}
