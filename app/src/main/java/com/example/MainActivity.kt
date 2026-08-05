package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Newspaper
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.SignalCellularAlt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.border
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.NewsSentimentScreen
import com.example.ui.screens.OtcDashboardScreen
import com.example.ui.screens.ResourcesScreen
import com.example.ui.screens.SignalsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TerminalBlack
import com.example.ui.theme.TerminalCyan
import com.example.ui.theme.TerminalGold
import com.example.ui.theme.TerminalLightGrey
import com.example.ui.theme.TerminalMediumGrey
import com.example.ui.viewmodel.OtcViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainLayout()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainLayout() {
    val viewModel: OtcViewModel = viewModel()
    val isAdvanced by viewModel.isAdvancedMode.collectAsState()
    var currentTab by remember { mutableIntStateOf(0) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.otc_launcher_logo_1783552574101),
                            contentDescription = "Ndine Analytics Logo",
                            modifier = Modifier
                                .size(30.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .border(1.dp, TerminalCyan.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "NDINEANALYTICS ALPHA",
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            letterSpacing = 1.sp,
                            color = Color.White
                        )
                    }
                },
                actions = {
                    Row(
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = if (isAdvanced) "PRO" else "SIMPLE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isAdvanced) TerminalGold else TerminalCyan,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Switch(
                            checked = isAdvanced,
                            onCheckedChange = { 
                                viewModel.isAdvancedMode.value = it
                                currentTab = 0
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = TerminalGold,
                                checkedTrackColor = TerminalGold.copy(alpha = 0.4f),
                                uncheckedThumbColor = TerminalCyan,
                                uncheckedTrackColor = TerminalCyan.copy(alpha = 0.2f)
                            ),
                            modifier = Modifier.testTag("global_mode_toggle_switch")
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TerminalBlack,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = TerminalBlack,
                tonalElevation = 8.dp,
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                if (isAdvanced) {
                    // --- ADVANCED PRO TABS ---
                    // Tab 0: Dashboard
                    NavigationBarItem(
                        selected = currentTab == 0,
                        onClick = { currentTab = 0 },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == 0) Icons.Default.Analytics else Icons.Outlined.Analytics,
                                contentDescription = "Dashboard"
                            )
                        },
                        label = { Text("Dashboard", fontSize = 9.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = TerminalCyan,
                            indicatorColor = TerminalCyan,
                            unselectedIconColor = TerminalLightGrey,
                            unselectedTextColor = TerminalLightGrey
                        ),
                        modifier = Modifier.testTag("nav_item_tracker")
                    )

                    // Tab 1: Signals
                    NavigationBarItem(
                        selected = currentTab == 1,
                        onClick = { currentTab = 1 },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == 1) Icons.Default.SignalCellularAlt else Icons.Outlined.SignalCellularAlt,
                                contentDescription = "Signals"
                            )
                        },
                        label = { Text("Signals", fontSize = 9.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = TerminalCyan,
                            indicatorColor = TerminalCyan,
                            unselectedIconColor = TerminalLightGrey,
                            unselectedTextColor = TerminalLightGrey
                        ),
                        modifier = Modifier.testTag("nav_item_signals")
                    )

                    // Tab 2: AI Analyst
                    NavigationBarItem(
                        selected = currentTab == 2,
                        onClick = { currentTab = 2 },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Forum,
                                contentDescription = "AI Analyst"
                            )
                        },
                        label = { Text("AI Analyst", fontSize = 9.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = TerminalCyan,
                            indicatorColor = TerminalCyan,
                            unselectedIconColor = TerminalLightGrey,
                            unselectedTextColor = TerminalLightGrey
                        ),
                        modifier = Modifier.testTag("nav_item_sentiment")
                    )

                    // Tab 3: Resources
                    NavigationBarItem(
                        selected = currentTab == 3,
                        onClick = { currentTab = 3 },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == 3) Icons.Default.Book else Icons.Outlined.Book,
                                contentDescription = "Resources"
                            )
                        },
                        label = { Text("Resources", fontSize = 9.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = TerminalCyan,
                            indicatorColor = TerminalCyan,
                            unselectedIconColor = TerminalLightGrey,
                            unselectedTextColor = TerminalLightGrey
                        ),
                        modifier = Modifier.testTag("nav_item_resources")
                    )

                    // Tab 4: Settings
                    NavigationBarItem(
                        selected = currentTab == 4,
                        onClick = { currentTab = 4 },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == 4) Icons.Default.Settings else Icons.Outlined.Settings,
                                contentDescription = "Settings"
                            )
                        },
                        label = { Text("Settings", fontSize = 9.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = TerminalCyan,
                            indicatorColor = TerminalCyan,
                            unselectedIconColor = TerminalLightGrey,
                            unselectedTextColor = TerminalLightGrey
                        ),
                        modifier = Modifier.testTag("nav_item_settings")
                    )
                } else {
                    // --- SIMPLE NEWCOMER TABS ---
                    // Tab 0: Dashboard
                    NavigationBarItem(
                        selected = currentTab == 0,
                        onClick = { currentTab = 0 },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == 0) Icons.Default.Analytics else Icons.Outlined.Analytics,
                                contentDescription = "Dashboard"
                            )
                        },
                        label = { Text("Dashboard", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = TerminalCyan,
                            indicatorColor = TerminalCyan,
                            unselectedIconColor = TerminalLightGrey,
                            unselectedTextColor = TerminalLightGrey
                        ),
                        modifier = Modifier.testTag("nav_item_tracker")
                    )

                    // Tab 1: Resources
                    NavigationBarItem(
                        selected = currentTab == 1,
                        onClick = { currentTab = 1 },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == 1) Icons.Default.Book else Icons.Outlined.Book,
                                contentDescription = "Resources"
                            )
                        },
                        label = { Text("Resources", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = TerminalCyan,
                            indicatorColor = TerminalCyan,
                            unselectedIconColor = TerminalLightGrey,
                            unselectedTextColor = TerminalLightGrey
                        ),
                        modifier = Modifier.testTag("nav_item_resources")
                    )

                    // Tab 2: Settings
                    NavigationBarItem(
                        selected = currentTab == 2,
                        onClick = { currentTab = 2 },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == 2) Icons.Default.Settings else Icons.Outlined.Settings,
                                contentDescription = "Settings"
                            )
                        },
                        label = { Text("Settings", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = TerminalCyan,
                            indicatorColor = TerminalCyan,
                            unselectedIconColor = TerminalLightGrey,
                            unselectedTextColor = TerminalLightGrey
                        ),
                        modifier = Modifier.testTag("nav_item_settings")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(TerminalBlack)
                .padding(innerPadding)
        ) {
            if (isAdvanced) {
                Crossfade(targetState = currentTab, label = "advancedTabTransition") { tab ->
                    when (tab) {
                        0 -> OtcDashboardScreen(viewModel = viewModel)
                        1 -> SignalsScreen(viewModel = viewModel)
                        2 -> NewsSentimentScreen(viewModel = viewModel)
                        3 -> ResourcesScreen()
                        4 -> SettingsScreen(viewModel = viewModel)
                    }
                }
            } else {
                Crossfade(targetState = currentTab, label = "simpleTabTransition") { tab ->
                    when (tab) {
                        0 -> OtcDashboardScreen(viewModel = viewModel)
                        1 -> ResourcesScreen()
                        2 -> SettingsScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }
}
