package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun ConcentricActivityRings(
    movePercent: Float, // Active calories progress 0..1+
    exercisePercent: Float, // Active minutes progress 0..1+
    standPercent: Float, // Stand hours progress 0..1+
    modifier: Modifier = Modifier
) {
    val animMove by animateFloatAsState(targetValue = movePercent.coerceIn(0f, 1.5f), animationSpec = tween(900), label = "move")
    val animExercise by animateFloatAsState(targetValue = exercisePercent.coerceIn(0f, 1.5f), animationSpec = tween(1000), label = "exercise")
    val animStand by animateFloatAsState(targetValue = standPercent.coerceIn(0f, 1.5f), animationSpec = tween(1100), label = "stand")

    Canvas(modifier = modifier.size(130.dp)) {
        val strokeWidth = 10.dp.toPx()
        val spacing = 3.dp.toPx()
        val center = Offset(size.width / 2, size.height / 2)

        // Outer Ring: Move / Calories (Magenta)
        val outerRadius = (size.minDimension / 2) - strokeWidth / 2
        val outerSize = Size(outerRadius * 2, outerRadius * 2)
        val outerTopLeft = Offset(center.x - outerRadius, center.y - outerRadius)
        // Background track
        drawArc(
            color = MagentaHeart.copy(alpha = 0.2f),
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = outerTopLeft,
            size = outerSize,
            style = Stroke(width = strokeWidth)
        )
        // Progress
        drawArc(
            color = MagentaHeart,
            startAngle = -90f,
            sweepAngle = (animMove * 360f).coerceAtMost(360f),
            useCenter = false,
            topLeft = outerTopLeft,
            size = outerSize,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // Middle Ring: Exercise / Active Minutes (Cyan)
        val middleRadius = outerRadius - strokeWidth - spacing
        val middleSize = Size(middleRadius * 2, middleRadius * 2)
        val middleTopLeft = Offset(center.x - middleRadius, center.y - middleRadius)
        drawArc(
            color = CyanPulse.copy(alpha = 0.2f),
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = middleTopLeft,
            size = middleSize,
            style = Stroke(width = strokeWidth)
        )
        drawArc(
            color = CyanPulse,
            startAngle = -90f,
            sweepAngle = (animExercise * 360f).coerceAtMost(360f),
            useCenter = false,
            topLeft = middleTopLeft,
            size = middleSize,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // Inner Ring: Stand Hours (Emerald)
        val innerRadius = middleRadius - strokeWidth - spacing
        val innerSize = Size(innerRadius * 2, innerRadius * 2)
        val innerTopLeft = Offset(center.x - innerRadius, center.y - innerRadius)
        drawArc(
            color = EmeraldVitals.copy(alpha = 0.2f),
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = innerTopLeft,
            size = innerSize,
            style = Stroke(width = strokeWidth)
        )
        drawArc(
            color = EmeraldVitals,
            startAngle = -90f,
            sweepAngle = (animStand * 360f).coerceAtMost(360f),
            useCenter = false,
            topLeft = innerTopLeft,
            size = innerSize,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
}
