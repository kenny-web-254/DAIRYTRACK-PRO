package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.dialogs.*
import com.example.ui.screens.*
import com.example.ui.theme.DairyTrackTheme
import com.example.ui.theme.DairyGreenPrimary
import com.example.ui.theme.DairyGreenBorder
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.DairyViewModel

data class NavTabItem(
    val title: String,
    val icon: ImageVector,
    val tag: String
)

class MainActivity : ComponentActivity() {

    private val viewModel: DairyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DairyTrackTheme {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(viewModel: DairyViewModel) {
    var selectedTab by remember { mutableIntStateOf(0) }

    // Dialog state controllers
    var showAddMilkDialog by remember { mutableStateOf(false) }
    var showAddCattleDialog by remember { mutableStateOf(false) }
    var showAddHealthDialog by remember { mutableStateOf(false) }
    var showAddBreedingDialog by remember { mutableStateOf(false) }
    var showAddFeedDialog by remember { mutableStateOf(false) }
    var showAddFinancialDialog by remember { mutableStateOf(false) }

    val tabs = listOf(
        NavTabItem("Overview", Icons.Default.Dashboard, "tab_overview"),
        NavTabItem("Cattle", Icons.Default.Pets, "tab_cattle"),
        NavTabItem("Milk", Icons.Default.WaterDrop, "tab_milk"),
        NavTabItem("Health", Icons.Default.Healing, "tab_health"),
        NavTabItem("Breed&Feed", Icons.Default.Grass, "tab_breed_feed"),
        NavTabItem("Finance", Icons.Default.AttachMoney, "tab_finance"),
        NavTabItem("DairyAI", Icons.Default.AutoAwesome, "tab_ai")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = DairyGreenBorder,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "D",
                                    color = DairyGreenPrimary,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "DairyTrack Pro",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 17.sp
                            )
                            Text(
                                text = "Version 3.0 • Production",
                                color = DairyGreenBorder,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { /* Notification action */ },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(36.dp)
                            .background(Color.White.copy(alpha = 0.15f), shape = RoundedCornerShape(10.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DairyGreenPrimary,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 6.dp,
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                tabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = { Icon(tab.icon, contentDescription = tab.title, modifier = Modifier.size(20.dp)) },
                        label = {
                            Text(
                                text = tab.title.uppercase(),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DairyGreenPrimary,
                            selectedTextColor = DairyGreenPrimary,
                            indicatorColor = DairyGreenBorder,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag(tab.tag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> DashboardScreen(
                    viewModel = viewModel,
                    onNavigateToTab = { selectedTab = it },
                    onOpenAddMilkDialog = { showAddMilkDialog = true },
                    onOpenAddCattleDialog = { showAddCattleDialog = true },
                    onOpenAddHealthDialog = { showAddHealthDialog = true }
                )
                1 -> CattleScreen(
                    viewModel = viewModel,
                    onOpenAddCattleDialog = { showAddCattleDialog = true }
                )
                2 -> MilkLogsScreen(
                    viewModel = viewModel,
                    onOpenAddMilkDialog = { showAddMilkDialog = true }
                )
                3 -> HealthScreen(
                    viewModel = viewModel,
                    onOpenAddHealthDialog = { showAddHealthDialog = true }
                )
                4 -> BreedingAndFeedScreen(
                    viewModel = viewModel,
                    onOpenAddBreedingDialog = { showAddBreedingDialog = true },
                    onOpenAddFeedDialog = { showAddFeedDialog = true }
                )
                5 -> FinancialsScreen(
                    viewModel = viewModel,
                    onOpenAddFinancialDialog = { showAddFinancialDialog = true }
                )
                6 -> AiSpecialistScreen(
                    viewModel = viewModel
                )
            }
        }

        // Active Modal Dialogs
        if (showAddMilkDialog) {
            AddMilkDialog(viewModel = viewModel, onDismiss = { showAddMilkDialog = false })
        }
        if (showAddCattleDialog) {
            AddCattleDialog(viewModel = viewModel, onDismiss = { showAddCattleDialog = false })
        }
        if (showAddHealthDialog) {
            AddHealthDialog(viewModel = viewModel, onDismiss = { showAddHealthDialog = false })
        }
        if (showAddBreedingDialog) {
            AddBreedingDialog(viewModel = viewModel, onDismiss = { showAddBreedingDialog = false })
        }
        if (showAddFeedDialog) {
            AddFeedDialog(viewModel = viewModel, onDismiss = { showAddFeedDialog = false })
        }
        if (showAddFinancialDialog) {
            AddFinancialDialog(viewModel = viewModel, onDismiss = { showAddFinancialDialog = false })
        }
    }
}
