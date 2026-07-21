package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*
import com.example.ui.viewmodel.DairyViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DairyViewModel,
    onNavigateToTab: (Int) -> Unit,
    onOpenAddMilkDialog: () -> Unit,
    onOpenAddCattleDialog: () -> Unit,
    onOpenAddHealthDialog: () -> Unit
) {
    val activeCount by viewModel.activeCattleCount.collectAsState()
    val lactatingCount by viewModel.lactatingCattleCount.collectAsState()
    val todayMilk by viewModel.todayMilkTotal.collectAsState()
    val activeWithdrawals by viewModel.activeWithdrawalLogs.collectAsState()
    val lowFeeds by viewModel.lowStockFeed.collectAsState()
    val netProfit by viewModel.netProfit.collectAsState()
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Hero Farm Header Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.img_dairy_hero),
                        contentDescription = "Farm Hero Banner",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.85f)
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Surface(
                            color = DairyGreenPrimary,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.padding(bottom = 6.dp)
                        ) {
                            Text(
                                text = "OAKWOOD DAIRY ESTATE",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Text(
                            text = "DairyTrack Pro System",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Live Operations • ${viewModel.todayDate}",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // 2. High Density AI Insight Banner
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = DairyGreenContainer),
                border = BorderStroke(1.dp, DairyGreenBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToTab(6) }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 6.dp)
                    ) {
                        Surface(
                            color = DairyGreenSecondary,
                            shape = RoundedCornerShape(100.dp)
                        ) {
                            Text(
                                text = "GEMINI AI",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = "Production Forecast",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = DairyGreenPrimary
                        )
                    }
                    Text(
                        text = "Optimal milk yield predicted for active herd. Hydration and ration cycles configured for maximum protein output.",
                        fontSize = 12.sp,
                        color = DairyGreenPrimary,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // 3. Urgent Warning Alerts (Withdrawal & Low Stock)
        if (activeWithdrawals.isNotEmpty() || lowFeeds.isNotEmpty()) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "ATTENTION NEEDED",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 1.sp
                    )

                    activeWithdrawals.forEach { w ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = AlertRedContainer),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, AlertRed.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "Milk Withdrawal Warning",
                                    tint = AlertRed,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "MILK WITHDRAWAL ALERT: ${w.cattleTag}",
                                        fontWeight = FontWeight.Bold,
                                        color = AlertRed,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = "${w.diagnosisTitle} (${w.medicationGiven}). Discard milk until ${w.withdrawalEndDate ?: "N/A"}.",
                                        fontSize = 11.sp,
                                        color = TextPrimary
                                    )
                                }
                            }
                        }
                    }

                    lowFeeds.forEach { f ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = WarningOrangeContainer),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, WarningOrange.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Inventory2,
                                    contentDescription = "Low Feed Warning",
                                    tint = WarningOrange,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "LOW FEED STOCK: ${f.name}",
                                        fontWeight = FontWeight.Bold,
                                        color = WarningOrange,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = "Current: ${f.stockQuantityKg} kg (Min Threshold: ${f.minAlertThresholdKg} kg)",
                                        fontSize = 11.sp,
                                        color = TextPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. Key Metrics KPI Grid
        item {
            Text(
                text = "PERFORMANCE SNAPSHOT",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 1.sp
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                KpiCard(
                    title = "Milk Yield",
                    value = "${String.format(Locale.US, "%.1f", todayMilk)}L",
                    subtitle = "$lactatingCount Milking",
                    icon = Icons.Default.WaterDrop,
                    color = HealthyBlue,
                    containerColor = HealthyBlueContainer,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToTab(2) }
                )
                KpiCard(
                    title = "Active Herd",
                    value = "$activeCount",
                    subtitle = "$lactatingCount Milking",
                    icon = Icons.Default.Pets,
                    color = DairyGreenSecondary,
                    containerColor = DairyGreenContainer,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToTab(1) }
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                KpiCard(
                    title = "Net Income",
                    value = "$${String.format(Locale.US, "%.0f", netProfit)}",
                    subtitle = "Financial Net",
                    icon = Icons.Default.AttachMoney,
                    color = FarmGoldAccent,
                    containerColor = FarmGoldContainer,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToTab(5) }
                )
                KpiCard(
                    title = "Health Watch",
                    value = "${activeWithdrawals.size}",
                    subtitle = "Under Watch",
                    icon = Icons.Default.Healing,
                    color = AlertRed,
                    containerColor = AlertRedContainer,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToTab(3) }
                )
            }
        }

        // 5. Quick Actions
        item {
            Text(
                text = "QUICK ACTIONS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 1.sp
            )
        }

        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                item {
                    QuickActionButton(
                        label = "Record Milk",
                        icon = Icons.Default.AddCircle,
                        containerColor = DairyGreenPrimary,
                        contentColor = Color.White,
                        onClick = onOpenAddMilkDialog,
                        tag = "action_log_milk"
                    )
                }
                item {
                    QuickActionButton(
                        label = "Add Cattle",
                        icon = Icons.Default.PersonAdd,
                        containerColor = FarmGoldAccent,
                        contentColor = Color.White,
                        onClick = onOpenAddCattleDialog,
                        tag = "action_add_cattle"
                    )
                }
                item {
                    QuickActionButton(
                        label = "Health Check",
                        icon = Icons.Default.MedicalServices,
                        containerColor = AlertRed,
                        contentColor = Color.White,
                        onClick = onOpenAddHealthDialog,
                        tag = "action_log_health"
                    )
                }
                item {
                    QuickActionButton(
                        label = "DairyAI Assistant",
                        icon = Icons.Default.AutoAwesome,
                        containerColor = DairyGreenSecondary,
                        contentColor = Color.White,
                        onClick = { onNavigateToTab(6) },
                        tag = "action_ask_ai"
                    )
                }
                item {
                    QuickActionButton(
                        label = "Export Report",
                        icon = Icons.Default.Share,
                        containerColor = TextSecondary,
                        contentColor = Color.White,
                        onClick = { viewModel.shareFarmReport(context) },
                        tag = "action_export_report"
                    )
                }
            }
        }
    }
}

@Composable
fun KpiCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    containerColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, SlateBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title.uppercase(),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = DairyGreenSecondary,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = value,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(containerColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = color,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
                color = containerColor,
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = color,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
fun QuickActionButton(
    label: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
    tag: String
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = containerColor, contentColor = contentColor),
        shape = RoundedCornerShape(20.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        modifier = Modifier.testTag(tag)
    ) {
        Icon(imageVector = icon, contentDescription = label, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
}
