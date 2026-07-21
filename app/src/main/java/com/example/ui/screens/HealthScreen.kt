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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.HealthLogEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.DairyViewModel

@Composable
fun HealthScreen(
    viewModel: DairyViewModel,
    onOpenAddHealthDialog: () -> Unit
) {
    val healthLogs by viewModel.allHealthLogs.collectAsState()
    val activeWithdrawals by viewModel.activeWithdrawalLogs.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = AlertRedContainer),
                    border = BorderStroke(1.dp, AlertRed.copy(alpha = 0.3f)),
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
                                    text = "VETERINARY & HEALTH CARE",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AlertRed,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "${activeWithdrawals.size} Active Restraints",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextPrimary
                                )
                            }
                            Button(
                                onClick = onOpenAddHealthDialog,
                                colors = ButtonDefaults.buttonColors(containerColor = AlertRed),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.testTag("log_health_btn")
                            ) {
                                Icon(Icons.Default.MedicalServices, contentDescription = "Log Event")
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Log Health", fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Automatic milk withdrawal period tracking ensures 100% milk safety compliance.",
                            fontSize = 12.sp,
                            color = TextPrimary
                        )
                    }
                }
            }

            // Active Withdrawal Alerts List
            if (activeWithdrawals.isNotEmpty()) {
                item {
                    Text(
                        text = "ACTIVE MILK WITHDRAWAL RESTRAINTS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AlertRed,
                        letterSpacing = 1.sp
                    )
                }

                items(activeWithdrawals, key = { "w_${it.id}" }) { log ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, AlertRed.copy(alpha = 0.4f)),
                        colors = CardDefaults.cardColors(containerColor = AlertRedContainer)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = AlertRed, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "CATTLE: ${log.cattleTag}", fontWeight = FontWeight.Bold, color = AlertRed, fontSize = 13.sp)
                                Text(text = "${log.diagnosisTitle} - ${log.medicationGiven}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                Text(text = "Discard Milk Until: ${log.withdrawalEndDate} (${log.withdrawalDays} Days total)", fontSize = 11.sp, color = TextPrimary)
                            }
                        }
                    }
                }
            }

            // All Health Records
            item {
                Text(
                    text = "MEDICAL HISTORY RECORDS (${healthLogs.size})",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    letterSpacing = 1.sp
                )
            }

            if (healthLogs.isEmpty()) {
                item {
                    Text(text = "No health logs recorded yet.", color = TextSecondary)
                }
            } else {
                items(healthLogs, key = { it.id }) { log ->
                    HealthLogCard(log = log, onDelete = { viewModel.deleteHealthLog(log) })
                }
            }
        }
    }
}

@Composable
fun HealthLogCard(
    log: HealthLogEntity,
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
                    .background(if (log.status == "Active Withdrawal") AlertRedContainer else HealthyBlueContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Healing,
                    contentDescription = null,
                    tint = if (log.status == "Active Withdrawal") AlertRed else HealthyBlue,
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
                    Text(text = "${log.cattleTag} • ${log.type}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                    Surface(
                        color = if (log.status == "Active Withdrawal") AlertRedContainer else DairyGreenContainer,
                        shape = RoundedCornerShape(100.dp)
                    ) {
                        Text(
                            text = log.status.uppercase(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (log.status == "Active Withdrawal") AlertRed else DairyGreenSecondary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))
                Text(text = log.diagnosisTitle, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                if (log.medicationGiven.isNotBlank()) {
                    Text(text = "Meds: ${log.medicationGiven}", fontSize = 11.sp, color = TextSecondary)
                }
                Text(text = "${log.date} by ${log.performedBy}", fontSize = 10.sp, color = TextSecondary)
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = TextSecondary, modifier = Modifier.size(20.dp))
            }
        }
    }
}
