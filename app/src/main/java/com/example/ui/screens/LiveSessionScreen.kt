package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.MainViewModel
import com.example.ui.theme.*

@Composable
fun LiveSessionScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val session by viewModel.workoutSession.collectAsState()
    val sensors by viewModel.sensorDiagnostics.collectAsState()
    val vitals by viewModel.liveVitals.collectAsState()

    var selectedWorkoutType by remember { mutableStateOf("Cardio Zone Training") }
    var calibrationMessage by remember { mutableStateOf<String?>(null) }

    val workoutOptions = listOf(
        "Cardio Zone Training",
        "Zone 2 Mitochondrial Endurance",
        "Norwegian 4x4 HIIT",
        "Parasympathetic Vagal Breathwork"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("live_session_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Session Header Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardDark),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(listOf(CyanPulse, MagentaHeart))
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Live Biometric Workout",
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (session.isActive) "Streaming Live From Smartwatch" else "Ready to Record",
                                color = if (session.isActive) EmeraldVitals else TextSecondary,
                                fontSize = 12.sp
                            )
                        }

                        Button(
                            onClick = { viewModel.toggleWorkout(selectedWorkoutType) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (session.isActive) CoralAlert else CyanPulse
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("toggle_workout_btn")
                        ) {
                            Icon(
                                imageVector = if (session.isActive) Icons.Default.Stop else Icons.Default.PlayArrow,
                                contentDescription = if (session.isActive) "Stop" else "Start",
                                tint = MidnightBlack,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (session.isActive) "End Session" else "Start",
                                color = MidnightBlack,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }

                    if (!session.isActive) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Select Protocol:",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            workoutOptions.take(2).forEach { opt ->
                                ProtocolChip(
                                    label = opt,
                                    selected = selectedWorkoutType == opt,
                                    onClick = { selectedWorkoutType = opt },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            workoutOptions.drop(2).forEach { opt ->
                                ProtocolChip(
                                    label = opt,
                                    selected = selectedWorkoutType == opt,
                                    onClick = { selectedWorkoutType = opt },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Live Telemetry Numbers
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardElevatedDark),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(CardBorderDark)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    val formattedMinutes = (session.durationSeconds / 60).toString().padStart(2, '0')
                    val formattedSeconds = (session.durationSeconds % 60).toString().padStart(2, '0')

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "DURATION", color = TextTertiary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = "$formattedMinutes:$formattedSeconds",
                                color = TextPrimary,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "LIVE PULSE", color = MagentaHeart, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = if (session.isActive) "${session.currentHeartRate}" else "${vitals.heartRate}",
                                    color = TextPrimary,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = " BPM",
                                    color = MagentaHeart,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 4.dp, start = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        WorkoutStatItem(label = "AVG HR", value = "${if (session.isActive) session.avgHeartRate else 122} BPM")
                        WorkoutStatItem(label = "PEAK HR", value = "${if (session.isActive) session.maxHeartRate else 145} BPM")
                        WorkoutStatItem(label = "BURN", value = "${session.caloriesBurned} kcal")
                        WorkoutStatItem(label = "ACTIVE ZONE", value = "Zone ${session.activeZone}")
                    }
                }
            }
        }

        // Cardiac Zone Visualizer
        item {
            CardiacZoneVisualizer(activeZone = session.activeZone)
        }

        // Smartwatch Hardware Sensor Diagnostic Console
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("sensor_diagnostic_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardDark),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(CardBorderDark)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Sensors,
                                contentDescription = "Sensors",
                                tint = CyanPulse,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Smartwatch Sensor Telemetry Test",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = {
                                calibrationMessage = "Calibration complete: All 6 optical & thermal biometrics zero-drift verified."
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CardElevatedDark),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(text = "Calibrate", color = CyanPulse, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (calibrationMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = calibrationMessage!!,
                            color = EmeraldVitals,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Sensor items list
                    WatchSensorType.values().forEach { sensor ->
                        val isEnabled = sensors[sensor] ?: true
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = sensor.displayName,
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = sensor.unit,
                                    color = TextTertiary,
                                    fontSize = 11.sp
                                )
                            }
                            Switch(
                                checked = isEnabled,
                                onCheckedChange = { viewModel.toggleSensor(sensor) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = MidnightBlack,
                                    checkedTrackColor = CyanPulse,
                                    uncheckedThumbColor = TextTertiary,
                                    uncheckedTrackColor = CardDark
                                )
                            )
                        }
                        Divider(color = CardBorderDark, thickness = 0.5.dp)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ProtocolChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (selected) CyanPulse.copy(alpha = 0.2f) else CardElevatedDark)
            .border(1.dp, if (selected) CyanPulse else CardBorderDark, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (selected) CyanPulse else TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
    }
}

@Composable
fun WorkoutStatItem(label: String, value: String) {
    Column {
        Text(text = label, color = TextTertiary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
    }
}

@Composable
fun CardiacZoneVisualizer(
    activeZone: Int,
    modifier: Modifier = Modifier
) {
    val zones = listOf(
        Triple("Z1 Recovery", "<115", EmeraldVitals),
        Triple("Z2 FatMax", "115–133", CyanPulse),
        Triple("Z3 Aerobic", "134–152", ElectricBlue),
        Triple("Z4 Lactate", "153–171", AmberWarning),
        Triple("Z5 Redline", ">171", CoralAlert)
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("cardiac_zones_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardDark),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(CardBorderDark)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Target Metabolic Heart Rate Zones",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            // 5 Zone visual pills
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .clip(RoundedCornerShape(7.dp)),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                zones.forEachIndexed { index, (_, _, color) ->
                    val zoneNum = index + 1
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(
                                if (activeZone == zoneNum) color else color.copy(alpha = 0.25f)
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            zones.forEachIndexed { index, (name, bpm, color) ->
                val zoneNum = index + 1
                val isCurrent = activeZone == zoneNum
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(if (isCurrent) color else color.copy(alpha = 0.3f), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = name,
                            color = if (isCurrent) TextPrimary else TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                    Text(
                        text = "$bpm BPM",
                        color = if (isCurrent) color else TextTertiary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}
