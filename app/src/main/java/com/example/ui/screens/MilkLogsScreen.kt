package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.MilkLogEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.DairyViewModel
import java.util.Locale

@Composable
fun MilkLogsScreen(
    viewModel: DairyViewModel,
    onOpenAddMilkDialog: () -> Unit
) {
    val milkLogs by viewModel.allMilkLogs.collectAsState()
    val todayTotal by viewModel.todayMilkTotal.collectAsState()
    val lactatingCows by viewModel.lactatingCattleCount.collectAsState()

    // Calculate top producer
    val topProducers = milkLogs
        .groupBy { it.cattleTag }
        .mapValues { entry -> entry.value.sumOf { it.quantityLiters } }
        .entries
        .sortedByDescending { it.value }
        .take(5)

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Stats Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = HealthyBlueContainer),
                    border = BorderStroke(1.dp, HealthyBlue.copy(alpha = 0.3f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "TODAY'S PRODUCTION",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = HealthyBlue,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "${String.format(Locale.US, "%.1f", todayTotal)} L",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextPrimary
                                )
                            }
                            Button(
                                onClick = onOpenAddMilkDialog,
                                colors = ButtonDefaults.buttonColors(containerColor = HealthyBlue),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.testTag("open_log_milk_btn")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Log Milk")
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Log Session", fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        val avgYield = if (lactatingCows > 0) todayTotal / lactatingCows else 0.0
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Avg per Cow: ${String.format(Locale.US, "%.1f", avgYield)} L",
                                fontSize = 12.sp,
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Lactating Herd: $lactatingCows Head",
                                fontSize = 12.sp,
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // Top Milkers Leaderboard
            if (topProducers.isNotEmpty()) {
                item {
                    Text(
                        text = "TOP PRODUCING HERD",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 1.sp
                    )
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(1.dp, FarmGoldAccent.copy(alpha = 0.3f)),
                        colors = CardDefaults.cardColors(containerColor = FarmGoldContainer)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            topProducers.forEachIndexed { index, entry ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            color = FarmGoldAccent,
                                            shape = CircleShape,
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = "${index + 1}",
                                                    color = Color.White,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = entry.key,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = TextPrimary
                                        )
                                    }
                                    Text(
                                        text = "${String.format(Locale.US, "%.1f", entry.value)} L Total",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = FarmGoldAccent
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Milk Logs History
            item {
                Text(
                    text = "MILK YIELD LOGS (${milkLogs.size})",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    letterSpacing = 1.sp
                )
            }

            if (milkLogs.isEmpty()) {
                item {
                    Text(
                        text = "No milk logs recorded yet.",
                        color = TextSecondary,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
            } else {
                items(milkLogs, key = { it.id }) { log ->
                    MilkLogCard(log = log, onDelete = { viewModel.deleteMilkLog(log) })
                }
            }
        }
    }
}

@Composable
fun MilkLogCard(
    log: MilkLogEntity,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, SlateBorder),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(HealthyBlueContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.WaterDrop,
                    contentDescription = null,
                    tint = HealthyBlue,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = log.cattleTag,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = "${log.quantityLiters} L",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = HealthyBlue
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "${log.date} (${log.session})", fontSize = 11.sp, color = TextSecondary)
                    if (log.fatPercentage != null) {
                        Text(text = "• Fat: ${log.fatPercentage}%", fontSize = 11.sp, color = FarmGoldAccent, fontWeight = FontWeight.Bold)
                    }
                    if (log.proteinPercentage != null) {
                        Text(text = "• Prot: ${log.proteinPercentage}%", fontSize = 11.sp, color = DairyGreenSecondary, fontWeight = FontWeight.Bold)
                    }
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Delete log",
                    tint = TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
