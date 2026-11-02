package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.draw.rotate
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
fun DeviceManagerScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val devices by viewModel.devices.collectAsState()
    val connectedDevice by viewModel.connectedDevice.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()

    var selectedSyncRate by remember { mutableStateOf("Real-Time (1 sec)") }
    val syncRates = listOf("Real-Time (1 sec)", "Eco (5 sec)", "Standard (1 min)", "Battery Saver")

    val infiniteTransition = rememberInfiniteTransition(label = "scanRotate")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("device_manager_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Active Connected Watch Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CardElevatedDark),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(listOf(CyanPulse, ElectricBlue))
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(CyanPulse.copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Watch,
                                    contentDescription = "Active Watch",
                                    tint = CyanPulse,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = connectedDevice?.name ?: "No Watch Connected",
                                    color = TextPrimary,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = connectedDevice?.model ?: "Select a device below",
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        if (connectedDevice != null && connectedDevice?.connectionState == WatchConnectionState.CONNECTED) {
                            Box(
                                modifier = Modifier
                                    .background(EmeraldVitals.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "PAIRED & ACTIVE",
                                    color = EmeraldVitals,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    if (connectedDevice != null && connectedDevice?.connectionState == WatchConnectionState.CONNECTED) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Divider(color = CardBorderDark, thickness = 1.dp)
                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            DeviceSpecItem(label = "BATTERY", value = "${connectedDevice?.batteryLevel}%")
                            DeviceSpecItem(label = "SIGNAL RSSI", value = "${connectedDevice?.rssi} dBm")
                            DeviceSpecItem(label = "FIRMWARE", value = connectedDevice?.firmwareVersion ?: "")
                            DeviceSpecItem(label = "BLE PROTOCOL", value = "GATT 0x180D")
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { viewModel.syncNow() },
                                colors = ButtonDefaults.buttonColors(containerColor = CyanPulse),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(imageVector = Icons.Default.Sync, contentDescription = "Sync", tint = MidnightBlack, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "Sync Telemetry", color = MidnightBlack, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            OutlinedButton(
                                onClick = { viewModel.disconnectDevice() },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = CoralAlert),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CoralAlert),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = "Disconnect", color = CoralAlert, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // Bluetooth Scanner Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.BluetoothSearching, contentDescription = "Scan", tint = CyanPulse)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Discovered Smartwatches",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = { viewModel.scanDevices() },
                    colors = ButtonDefaults.buttonColors(containerColor = CardElevatedDark),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("scan_devices_button")
                ) {
                    if (isScanning) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Scanning",
                            tint = CyanPulse,
                            modifier = Modifier
                                .size(16.dp)
                                .rotate(rotation)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Scanning...", color = CyanPulse, fontSize = 12.sp)
                    } else {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Scan",
                            tint = CyanPulse,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Scan BLE", color = CyanPulse, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Available Devices List
        items(devices) { dev ->
            val isCurrent = connectedDevice?.id == dev.id && dev.connectionState == WatchConnectionState.CONNECTED
            val isConnecting = dev.connectionState == WatchConnectionState.CONNECTING

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("device_item_${dev.id}"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isCurrent) CardElevatedDark else CardDark
                ),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(
                        if (isCurrent) CyanPulse else CardBorderDark
                    )
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .background(
                                    if (isCurrent) CyanPulse.copy(alpha = 0.2f) else CardBorderDark.copy(alpha = 0.5f),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Watch,
                                contentDescription = dev.name,
                                tint = if (isCurrent) CyanPulse else TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = dev.name,
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${dev.brand} • ${dev.model} • ${dev.rssi} dBm",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    if (isCurrent) {
                        Button(
                            onClick = { },
                            enabled = false,
                            colors = ButtonDefaults.buttonColors(
                                disabledContainerColor = EmeraldVitals.copy(alpha = 0.2f),
                                disabledContentColor = EmeraldVitals
                            ),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(text = "Active", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    } else if (isConnecting) {
                        CircularProgressIndicator(
                            color = CyanPulse,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Button(
                            onClick = { viewModel.connectDevice(dev.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = CyanPulse),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("connect_btn_${dev.id}")
                        ) {
                            Text(text = "Connect", color = MidnightBlack, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Telemetry Sampling Rate Preferences
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
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
                        text = "Smartwatch Telemetry Stream Rate",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Controls Bluetooth GATT transmission interval from watch biosensors",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    syncRates.forEach { rate ->
                        val isSel = selectedSyncRate == rate
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { selectedSyncRate = rate }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = rate,
                                color = if (isSel) CyanPulse else TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                            )
                            RadioButton(
                                selected = isSel,
                                onClick = { selectedSyncRate = rate },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = CyanPulse,
                                    unselectedColor = TextTertiary
                                )
                            )
                        }
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
fun DeviceSpecItem(label: String, value: String) {
    Column {
        Text(text = label, color = TextTertiary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
    }
}
