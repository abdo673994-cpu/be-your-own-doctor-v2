package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.*
import com.example.ui.MainViewModel
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    onNavigateToLab: () -> Unit,
    onNavigateToLiveWorkout: () -> Unit,
    onNavigateToDevices: () -> Unit,
    modifier: Modifier = Modifier
) {
    val device by viewModel.connectedDevice.collectAsState()
    val vitals by viewModel.liveVitals.collectAsState()
    val activity by viewModel.dailyActivity.collectAsState()
    val sleep by viewModel.sleepArchitecture.collectAsState()
    val waveform by viewModel.ecgWaveform.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Smartwatch Connection Banner
        item {
            WatchConnectionBanner(
                device = device,
                onSyncNow = { viewModel.syncNow() },
                onManageDevices = onNavigateToDevices
            )
        }

        // Hero Visual Concept Image Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, CardBorderDark, RoundedCornerShape(16.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.smartwatch_hero_1793606860733),
                    contentDescription = "Smartwatch Bioscience Visual",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                // Subtle gradient overlay for readability
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    MidnightBlack.copy(alpha = 0.85f),
                                    MidnightBlack.copy(alpha = 0.35f),
                                    Color.Transparent
                                )
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "REAL-TIME BIOSCIENCE TELEMETRY",
                        color = CyanPulse,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Continuous Autonomic & Vitals Stream",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(EmeraldVitals, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Connected via BLE • 5 Sensors Active",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Live ECG Oscilloscope Waveform
        item {
            EcgWaveformCanvas(
                waveform = waveform,
                currentBpm = vitals.heartRate,
                hrvMs = vitals.hrvRmssd,
                isStreaming = device?.connectionState == WatchConnectionState.CONNECTED
            )
        }

        // Section Title: Primary Vitals
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Core Physiological Vitals",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "View Science Lab",
                    color = CyanPulse,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .clickable { onNavigateToLab() }
                        .padding(4.dp)
                        .testTag("nav_to_lab_link")
                )
            }
        }

        // 2x2 Metric Grid: Row 1
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                VitalMetricCard(
                    title = "Heart Rate",
                    value = "${vitals.heartRate}",
                    unit = "BPM",
                    statusText = "Resting: ${vitals.restingHeartRate}",
                    accentColor = MagentaHeart,
                    icon = Icons.Default.Favorite,
                    testTag = "vital_card_heart_rate",
                    subValue = "Normal Sinus Rhythm",
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToLab
                )
                VitalMetricCard(
                    title = "HRV (rMSSD)",
                    value = "${vitals.hrvRmssd}",
                    unit = "ms",
                    statusText = "High Vagal",
                    accentColor = CyanPulse,
                    icon = Icons.Default.ShowChart,
                    testTag = "vital_card_hrv",
                    subValue = "Parasympathetic Dominant",
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToLab
                )
            }
        }

        // 2x2 Metric Grid: Row 2
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                VitalMetricCard(
                    title = "Blood Oxygen (SpO2)",
                    value = "${vitals.spo2}",
                    unit = "%",
                    statusText = "Optimal",
                    accentColor = EmeraldVitals,
                    icon = Icons.Default.Air,
                    testTag = "vital_card_spo2",
                    subValue = "Resp: ${vitals.respirationRate} brpm",
                    modifier = Modifier.weight(1f)
                )
                VitalMetricCard(
                    title = "Skin Thermistor",
                    value = String.format("%.1f", vitals.skinTempCelsius),
                    unit = "°C",
                    statusText = "+${vitals.skinTempDelta}°C",
                    accentColor = AmberWarning,
                    icon = Icons.Default.Thermostat,
                    testTag = "vital_card_temp",
                    subValue = "Normal Thermoregulation",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 2x2 Metric Grid: Row 3
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                VitalMetricCard(
                    title = "Stress Index",
                    value = "${vitals.stressScore}",
                    unit = "/100",
                    statusText = "Low Stress",
                    accentColor = VioletCircadian,
                    icon = Icons.Default.SelfImprovement,
                    testTag = "vital_card_stress",
                    subValue = "Body Battery: ${vitals.bodyBattery}%",
                    modifier = Modifier.weight(1f)
                )
                VitalMetricCard(
                    title = "VO2 Max Fitness",
                    value = String.format("%.1f", vitals.vo2Max),
                    unit = "mL/kg",
                    statusText = "Bio Age: ${vitals.biologicalAge}",
                    accentColor = ElectricBlue,
                    icon = Icons.Default.Speed,
                    testTag = "vital_card_vo2max",
                    subValue = "Top 15% (Age 29)",
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToLab
                )
            }
        }

        // Daily Activity & Metabolic Expenditure Card
        item {
            ActivityMetabolicCard(
                activity = activity,
                onStartWorkout = onNavigateToLiveWorkout
            )
        }

        // Sleep Architecture & Recovery Card
        item {
            SleepArchitectureCard(
                sleep = sleep,
                onOpenLab = onNavigateToLab
            )
        }

        // Bioscience Health Report Action Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("health_report_card")
                    .clickable { viewModel.setHealthReportVisible(true) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardElevatedDark),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(listOf(CyanPulse, MagentaHeart))
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(CyanPulse.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Biotech,
                                contentDescription = "Bioscience Report",
                                tint = CyanPulse,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Comprehensive Bioscience Report",
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Clinical breakdown of all 5 organ systems",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Open Report",
                        tint = CyanPulse
                    )
                }
            }
        }

        // Bottom Spacer for navigation bar padding
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun WatchConnectionBanner(
    device: SmartWatchDevice?,
    onSyncNow: () -> Unit,
    onManageDevices: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("watch_connection_banner"),
        shape = RoundedCornerShape(18.dp),
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onManageDevices() }
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                if (device?.connectionState == WatchConnectionState.CONNECTED) CyanPulse.copy(alpha = 0.15f)
                                else Color.Gray.copy(alpha = 0.2f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Watch,
                            contentDescription = "Watch",
                            tint = if (device?.connectionState == WatchConnectionState.CONNECTED) CyanPulse else Color.Gray,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = device?.name ?: "No Watch Connected",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Change Watch",
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Text(
                            text = if (device?.connectionState == WatchConnectionState.CONNECTED)
                                "Synced Just Now • BLE RSSI: ${device.rssi} dBm"
                            else "Disconnected • Tap to Pair",
                            color = if (device?.connectionState == WatchConnectionState.CONNECTED) EmeraldVitals else AmberWarning,
                            fontSize = 11.sp
                        )
                    }
                }

                // Battery Indicator & Sync Button
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (device != null && device.connectionState == WatchConnectionState.CONNECTED) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(CardElevatedDark, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.BatteryStd,
                                contentDescription = "Battery",
                                tint = if (device.batteryLevel > 20) EmeraldVitals else CoralAlert,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${device.batteryLevel}%",
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    IconButton(
                        onClick = onSyncNow,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("sync_now_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sync,
                            contentDescription = "Sync Watch",
                            tint = CyanPulse,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ActivityMetabolicCard(
    activity: DailyActivity,
    onStartWorkout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("activity_metabolic_card"),
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
                Text(
                    text = "Activity & Metabolism",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Button(
                    onClick = onStartWorkout,
                    colors = ButtonDefaults.buttonColors(containerColor = CyanPulse),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("start_live_session_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Start",
                        tint = MidnightBlack,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Workout",
                        color = MidnightBlack,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Concentric Activity Rings
                ConcentricActivityRings(
                    movePercent = activity.activeCalories / 600f,
                    exercisePercent = activity.activeMinutes / 60f,
                    standPercent = activity.standHours / activity.standGoalHours.toFloat(),
                    modifier = Modifier.padding(end = 16.dp)
                )

                // Breakdown stats
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Move / Calories
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).background(MagentaHeart, CircleShape))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Active Burn", color = TextSecondary, fontSize = 12.sp)
                        }
                        Text(
                            text = "${activity.activeCalories} / 600 kcal",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    // Exercise Minutes
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).background(CyanPulse, CircleShape))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Exercise", color = TextSecondary, fontSize = 12.sp)
                        }
                        Text(
                            text = "${activity.activeMinutes} / 60 min",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    // Stand Hours
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).background(EmeraldVitals, CircleShape))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Stand Time", color = TextSecondary, fontSize = 12.sp)
                        }
                        Text(
                            text = "${activity.standHours} / ${activity.standGoalHours} hr",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Divider(color = CardBorderDark, thickness = 1.dp, modifier = Modifier.padding(vertical = 2.dp))

                    // Step count summary
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Daily Steps",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "${activity.steps} steps (${String.format("%.1f", activity.distanceKm)} km)",
                            color = TextCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SleepArchitectureCard(
    sleep: SleepArchitecture,
    onOpenLab: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("sleep_architecture_card")
            .clickable { onOpenLab() },
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
                        imageVector = Icons.Default.Bedtime,
                        contentDescription = "Sleep",
                        tint = VioletCircadian,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Sleep Architecture",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .background(VioletCircadian.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "Score: ${sleep.sleepScore}/100",
                        color = VioletCircadian,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            val hours = sleep.totalSleepMinutes / 60
            val mins = sleep.totalSleepMinutes % 60
            Text(
                text = "${hours}h ${mins}m Total Duration • ${sleep.recoveryEfficiency}% Recovery Efficiency",
                color = TextSecondary,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Multi-segment horizontal stage progress bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .clip(RoundedCornerShape(7.dp))
            ) {
                Box(
                    modifier = Modifier
                        .weight(sleep.deepPercent.toFloat())
                        .fillMaxHeight()
                        .background(VioletCircadian)
                )
                Box(
                    modifier = Modifier
                        .weight(sleep.remPercent.toFloat())
                        .fillMaxHeight()
                        .background(CyanPulse)
                )
                Box(
                    modifier = Modifier
                        .weight(sleep.lightPercent.toFloat())
                        .fillMaxHeight()
                        .background(ElectricBlue)
                )
                Box(
                    modifier = Modifier
                        .weight(sleep.awakePercent.toFloat())
                        .fillMaxHeight()
                        .background(CoralAlert)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Stage legend with percentages
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StageLegendItem(label = "Deep", percent = "${sleep.deepPercent}%", color = VioletCircadian)
                StageLegendItem(label = "REM", percent = "${sleep.remPercent}%", color = CyanPulse)
                StageLegendItem(label = "Light", percent = "${sleep.lightPercent}%", color = ElectricBlue)
                StageLegendItem(label = "Awake", percent = "${sleep.awakePercent}%", color = CoralAlert)
            }
        }
    }
}

@Composable
fun StageLegendItem(label: String, percent: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "$label: ",
            color = TextTertiary,
            fontSize = 11.sp
        )
        Text(
            text = percent,
            color = TextPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}
