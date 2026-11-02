package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.WatchConnectionState
import com.example.ui.MainViewModel
import com.example.ui.screens.*
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val currentTab by viewModel.currentNavIndex.collectAsState()
                val connectedDevice by viewModel.connectedDevice.collectAsState()
                val showReport by viewModel.showHealthReport.collectAsState()
                val vitals by viewModel.liveVitals.collectAsState()
                val sleep by viewModel.sleepArchitecture.collectAsState()

                // Support Android Predictive Back / Back button navigation
                BackHandler(enabled = currentTab != 0) {
                    viewModel.setNavIndex(0)
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MidnightBlack,
                    contentWindowInsets = WindowInsets.safeDrawing,
                    topBar = {
                        TopAppBar(
                            title = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_app_logo),
                                        contentDescription = "Your Own Doctor Logo",
                                        modifier = Modifier.size(30.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "Your Own Doctor",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 18.sp,
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "SCIENCE",
                                        color = CyanPulse,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp,
                                        modifier = Modifier
                                            .background(CyanPulse.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            },
                            actions = {
                                if (connectedDevice != null && connectedDevice?.connectionState == WatchConnectionState.CONNECTED) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .background(CardDark, RoundedCornerShape(20.dp))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .background(EmeraldVitals, CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "${connectedDevice?.batteryLevel}%",
                                            color = TextPrimary,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = { viewModel.setHealthReportVisible(true) },
                                    modifier = Modifier.testTag("top_bar_report_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Assessment,
                                        contentDescription = "Report",
                                        tint = CyanPulse
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MidnightBlack,
                                titleContentColor = TextPrimary
                            )
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            containerColor = SurfaceDark,
                            tonalElevation = 8.dp,
                            modifier = Modifier
                                .windowInsetsPadding(WindowInsets.navigationBars)
                                .testTag("main_bottom_nav")
                        ) {
                            NavigationBarItem(
                                selected = currentTab == 0,
                                onClick = { viewModel.setNavIndex(0) },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.MonitorHeart,
                                        contentDescription = "Dashboard"
                                    )
                                },
                                label = { Text("Dashboard", fontSize = 11.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MidnightBlack,
                                    selectedTextColor = CyanPulse,
                                    indicatorColor = CyanPulse,
                                    unselectedIconColor = TextSecondary,
                                    unselectedTextColor = TextSecondary
                                ),
                                modifier = Modifier.testTag("nav_item_dashboard")
                            )

                            NavigationBarItem(
                                selected = currentTab == 1,
                                onClick = { viewModel.setNavIndex(1) },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Biotech,
                                        contentDescription = "Science Lab"
                                    )
                                },
                                label = { Text("Science Lab", fontSize = 11.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MidnightBlack,
                                    selectedTextColor = CyanPulse,
                                    indicatorColor = CyanPulse,
                                    unselectedIconColor = TextSecondary,
                                    unselectedTextColor = TextSecondary
                                ),
                                modifier = Modifier.testTag("nav_item_science_lab")
                            )

                            NavigationBarItem(
                                selected = currentTab == 2,
                                onClick = { viewModel.setNavIndex(2) },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.FitnessCenter,
                                        contentDescription = "Workout"
                                    )
                                },
                                label = { Text("Session", fontSize = 11.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MidnightBlack,
                                    selectedTextColor = CyanPulse,
                                    indicatorColor = CyanPulse,
                                    unselectedIconColor = TextSecondary,
                                    unselectedTextColor = TextSecondary
                                ),
                                modifier = Modifier.testTag("nav_item_session")
                            )

                            NavigationBarItem(
                                selected = currentTab == 3,
                                onClick = { viewModel.setNavIndex(3) },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Watch,
                                        contentDescription = "Devices"
                                    )
                                },
                                label = { Text("Watch", fontSize = 11.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MidnightBlack,
                                    selectedTextColor = CyanPulse,
                                    indicatorColor = CyanPulse,
                                    unselectedIconColor = TextSecondary,
                                    unselectedTextColor = TextSecondary
                                ),
                                modifier = Modifier.testTag("nav_item_devices")
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (currentTab) {
                            0 -> DashboardScreen(
                                viewModel = viewModel,
                                onNavigateToLab = { viewModel.setNavIndex(1) },
                                onNavigateToLiveWorkout = { viewModel.setNavIndex(2) },
                                onNavigateToDevices = { viewModel.setNavIndex(3) }
                            )
                            1 -> BioscienceLabScreen(
                                viewModel = viewModel
                            )
                            2 -> LiveSessionScreen(
                                viewModel = viewModel
                            )
                            3 -> DeviceManagerScreen(
                                viewModel = viewModel
                            )
                        }

                        if (showReport) {
                            HealthReportDialog(
                                vitals = vitals,
                                sleep = sleep,
                                onDismiss = { viewModel.setHealthReportVisible(false) }
                            )
                        }
                    }
                }
            }
        }
    }
}
