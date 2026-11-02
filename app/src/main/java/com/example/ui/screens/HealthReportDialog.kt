package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.LiveVitals
import com.example.model.SleepArchitecture
import com.example.ui.theme.*

@Composable
fun HealthReportDialog(
    vitals: LiveVitals,
    sleep: SleepArchitecture,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f)
                .testTag("health_report_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardDark),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.verticalGradient(listOf(CyanPulse, MagentaHeart))
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Bioscience Physiological Report",
                            color = TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Smartwatch Clinical Summary",
                            color = CyanPulse,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Divider(color = CardBorderDark, thickness = 1.dp)

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Spacer(modifier = Modifier.height(6.dp))

                    // Biological Age Highlight Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CardElevatedDark, RoundedCornerShape(12.dp))
                            .border(1.dp, CyanPulse.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "BIOLOGICAL VITALITY AGE", color = CyanPulse, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = "${vitals.biologicalAge}",
                                        color = TextPrimary,
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = " YRS",
                                        color = TextSecondary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(bottom = 4.dp, start = 2.dp)
                                    )
                                }
                                Text(
                                    text = "Chronological: ${vitals.chronologicalAge} yrs (-5.0 yr advantage)",
                                    color = EmeraldVitals,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .background(EmeraldVitals.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(text = "Tier 1 Vitality", color = EmeraldVitals, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Key Biomarker Table
                    Text(text = "Multi-System Biomarker Summary", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)

                    BiomarkerReportRow("Autonomic Tone (HRV)", "${vitals.hrvRmssd} ms", "Normal: 40-80 ms", "Optimal Vagal")
                    BiomarkerReportRow("Resting Heart Rate", "${vitals.restingHeartRate} BPM", "Normal: 60-100 BPM", "Athletic Bradycardia")
                    BiomarkerReportRow("Oxygen Saturation", "${vitals.spo2}%", "Normal: 95-100%", "Full Saturation")
                    BiomarkerReportRow("Cardiorespiratory (VO2)", "${vitals.vo2Max} mL/kg", "Norm: 38-44 mL/kg", "Superior Tier")
                    BiomarkerReportRow("Blood Pressure (Est)", "${vitals.bloodPressureSys}/${vitals.bloodPressureDia} mmHg", "Norm: <120/80 mmHg", "Normotensive")
                    BiomarkerReportRow("Deep Sleep Recovery", "${sleep.deepPercent}% (${sleep.totalSleepMinutes / 60}h)", "Norm: 15-25%", "High SWS Flush")

                    Spacer(modifier = Modifier.height(6.dp))

                    // Clinical Recommendations
                    Text(text = "Evidence-Based Prescription", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)

                    RecommendationBullet(
                        title = "Zone 2 Polarized Aerobic",
                        description = "Sustain 150 min/wk at 115-133 BPM to elevate mitochondrial cristae density."
                    )
                    RecommendationBullet(
                        title = "Vagal Acetylcholine Modulation",
                        description = "Perform 5 minutes of 4-7-8 resonant diaphragmatic breathing before nocturnal sleep."
                    )
                    RecommendationBullet(
                        title = "Suprachiasmatic Photoreception",
                        description = "Absorb natural outdoor lux within 30 min of sunrise to align cortisol rhythm."
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = CyanPulse),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Dismiss Report", color = MidnightBlack, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun BiomarkerReportRow(
    title: String,
    value: String,
    normal: String,
    status: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardElevatedDark.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Text(text = normal, color = TextTertiary, fontSize = 10.sp)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(text = value, color = CyanPulse, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            Text(text = status, color = EmeraldVitals, fontSize = 10.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun RecommendationBullet(title: String, description: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = CyanPulse,
            modifier = Modifier
                .size(16.dp)
                .padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = title, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(text = description, color = TextSecondary, fontSize = 11.sp, lineHeight = 15.sp)
        }
    }
}
