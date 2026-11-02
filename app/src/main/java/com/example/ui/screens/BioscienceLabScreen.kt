package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
fun BioscienceLabScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val selectedCategory by viewModel.selectedInsightCategory.collectAsState()
    val insights by viewModel.filteredInsights.collectAsState()
    val vitals by viewModel.liveVitals.collectAsState()

    var expandedInsightId by remember { mutableStateOf<String?>("ans_vagal") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("bioscience_lab_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardElevatedDark),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(listOf(CyanPulse, ElectricBlue))
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
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(CyanPulse.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Biotech,
                                    contentDescription = "Bioscience",
                                    tint = CyanPulse,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Bioscience Intelligence Lab",
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Box(
                            modifier = Modifier
                                .background(CyanPulse, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "SCIENCE SKILL",
                                color = MidnightBlack,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Real-time physiological mechanism breakdown, autonomic balance telemetry, neuro-endocrine circadian phase timing, and clinical biomarker diagnostics.",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Autonomic Nervous System (ANS) Balance Radar Card
        item {
            AnsBalanceCard(hrvMs = vitals.hrvRmssd, stressScore = vitals.stressScore)
        }

        // Circadian Hormone Phase Curve Card
        item {
            CircadianHormonePhaseCard()
        }

        // Category Filter Chips
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Physiological Domains",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChip(
                            selected = selectedCategory == null,
                            onClick = { viewModel.selectCategoryFilter(null) },
                            label = { Text("All Domains (${viewModel.allInsights.size})") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyanPulse,
                                selectedLabelColor = MidnightBlack,
                                containerColor = CardDark,
                                labelColor = TextSecondary
                            )
                        )
                    }
                    items(BioscienceCategory.values()) { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { viewModel.selectCategoryFilter(category) },
                            label = { Text(category.title) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyanPulse,
                                selectedLabelColor = MidnightBlack,
                                containerColor = CardDark,
                                labelColor = TextSecondary
                            )
                        )
                    }
                }
            }
        }

        // Deep-Dive Bioscience Cards List
        items(insights) { insight ->
            val isExpanded = expandedInsightId == insight.id
            val statusColor = Color(insight.statusColorHex)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("insight_card_${insight.id}")
                    .clickable {
                        expandedInsightId = if (isExpanded) null else insight.id
                    },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardDark),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(
                        if (isExpanded) statusColor.copy(alpha = 0.6f) else CardBorderDark
                    )
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Header: Domain, Title, Status
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = insight.category.title.uppercase(),
                            color = CyanPulse,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Box(
                            modifier = Modifier
                                .background(statusColor.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = insight.statusLabel,
                                color = statusColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = insight.title,
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = insight.primaryMetric,
                        color = TextCyan,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Brief mechanism excerpt
                    Text(
                        text = insight.biologicalMechanism,
                        color = TextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )

                    // Expandable Details
                    AnimatedVisibility(visible = isExpanded) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Divider(color = CardBorderDark, thickness = 1.dp)

                            // Clinical Significance
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.MedicalServices,
                                        contentDescription = "Clinical",
                                        tint = MagentaHeart,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Clinical & Physiological Significance",
                                        color = MagentaHeart,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = insight.clinicalSignificance,
                                    color = TextPrimary,
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp
                                )
                            }

                            // Evidence-Based Recommendation Protocol
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Protocol",
                                        tint = EmeraldVitals,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Target Action Protocol",
                                        color = EmeraldVitals,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = insight.scientificRecommendation,
                                    color = TextPrimary,
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp
                                )
                            }

                            // Peer Reviewed Literature Reference
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(CardElevatedDark, RoundedCornerShape(8.dp))
                                    .padding(10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.Top) {
                                    Icon(
                                        imageVector = Icons.Default.MenuBook,
                                        contentDescription = "Citation",
                                        tint = TextTertiary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Reference: ${insight.peerReviewedReference}",
                                        color = TextTertiary,
                                        fontSize = 11.sp,
                                        lineHeight = 15.sp,
                                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                    )
                                }
                            }
                        }
                    }

                    // Expand / Collapse affordance
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isExpanded) "Show Less" else "Clinical Breakdown",
                            color = CyanPulse,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Toggle",
                            tint = CyanPulse,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Clinical Reference Standards Table
        item {
            ClinicalReferenceTable()
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun AnsBalanceCard(
    hrvMs: Int,
    stressScore: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("ans_balance_card"),
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
                        imageVector = Icons.Default.Psychology,
                        contentDescription = "Autonomic",
                        tint = CyanPulse,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Autonomic Balance (Sympathetic vs Vagal)",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "ANS Ratio: 1.4:1",
                    color = EmeraldVitals,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Two-sided Balance Bar: Sympathetic (Red/Coral) vs Parasympathetic (Emerald)
            val sympatheticPercent = 38
            val parasympatheticPercent = 62

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp))
            ) {
                Box(
                    modifier = Modifier
                        .weight(sympatheticPercent.toFloat())
                        .fillMaxHeight()
                        .background(CoralAlert)
                )
                Box(
                    modifier = Modifier
                        .weight(parasympatheticPercent.toFloat())
                        .fillMaxHeight()
                        .background(EmeraldVitals)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Sympathetic (Fight-or-Flight)", color = CoralAlert, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    Text(text = "$sympatheticPercent% (Stress Score: $stressScore)", color = TextSecondary, fontSize = 11.sp)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Parasympathetic (Rest-and-Digest)", color = EmeraldVitals, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    Text(text = "$parasympatheticPercent% (Vagal Tone: $hrvMs ms)", color = TextSecondary, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun CircadianHormonePhaseCard(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("circadian_phase_card"),
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
                        imageVector = Icons.Default.WbSunny,
                        contentDescription = "Circadian",
                        tint = AmberWarning,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Circadian Endocrine Telemetry",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "Phase: Evening Melatonin",
                    color = VioletCircadian,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Milestone timeline
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CircadianMilestone(time = "07:15", label = "Cortisol Peak", color = AmberWarning)
                CircadianMilestone(time = "14:30", label = "Coordination Peak", color = CyanPulse)
                CircadianMilestone(time = "21:45", label = "Melatonin Onset", color = VioletCircadian)
                CircadianMilestone(time = "23:00", label = "Sleep Gate", color = MagentaHeart)
            }
        }
    }
}

@Composable
fun CircadianMilestone(time: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = time,
            color = TextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = label,
            color = TextTertiary,
            fontSize = 10.sp
        )
    }
}

@Composable
fun ClinicalReferenceTable(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("clinical_reference_table"),
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
                text = "Clinical Biomarker Standards",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Peer-reviewed normative reference ranges for healthy adults",
                color = TextSecondary,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            ReferenceTableRow(metric = "Heart Rate Variability (rMSSD)", current = "64 ms", normal = "40–80 ms", status = "Optimal", statusColor = EmeraldVitals)
            Divider(color = CardBorderDark, thickness = 1.dp, modifier = Modifier.padding(vertical = 6.dp))
            ReferenceTableRow(metric = "Resting Heart Rate", current = "58 BPM", normal = "50–70 BPM", status = "Athletic", statusColor = EmeraldVitals)
            Divider(color = CardBorderDark, thickness = 1.dp, modifier = Modifier.padding(vertical = 6.dp))
            ReferenceTableRow(metric = "Blood Oxygen (SpO2)", current = "98%", normal = "95–100%", status = "Normal", statusColor = EmeraldVitals)
            Divider(color = CardBorderDark, thickness = 1.dp, modifier = Modifier.padding(vertical = 6.dp))
            ReferenceTableRow(metric = "VO2 Max Cardiorespiratory", current = "48.5 mL/kg", normal = "38–46 mL/kg", status = "Top 15%", statusColor = CyanPulse)
            Divider(color = CardBorderDark, thickness = 1.dp, modifier = Modifier.padding(vertical = 6.dp))
            ReferenceTableRow(metric = "Deep Sleep (Slow-Wave)", current = "22%", normal = "15–25%", status = "Restorative", statusColor = VioletCircadian)
        }
    }
}

@Composable
fun ReferenceTableRow(
    metric: String,
    current: String,
    normal: String,
    status: String,
    statusColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = metric, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Text(text = "Standard: $normal", color = TextTertiary, fontSize = 11.sp)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(text = current, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            Text(text = status, color = statusColor, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}
