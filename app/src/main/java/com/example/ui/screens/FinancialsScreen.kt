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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.FinancialLogEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.DairyViewModel
import java.util.Locale

@Composable
fun FinancialsScreen(
    viewModel: DairyViewModel,
    onOpenAddFinancialDialog: () -> Unit
) {
    val logs by viewModel.allFinancialLogs.collectAsState()
    val income by viewModel.totalIncome.collectAsState()
    val expense by viewModel.totalExpense.collectAsState()
    val netMargin by viewModel.netProfit.collectAsState()
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Net Margin Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = FarmGoldContainer),
                    border = BorderStroke(1.dp, FarmGoldAccent.copy(alpha = 0.3f)),
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
                                    text = "NET FARM PROFIT",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FarmGoldAccent,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "$${String.format(Locale.US, "%.2f", netMargin)}",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextPrimary
                                )
                            }

                            Button(
                                onClick = onOpenAddFinancialDialog,
                                colors = ButtonDefaults.buttonColors(containerColor = FarmGoldAccent),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.testTag("add_transaction_btn")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Log Tx", fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.ArrowUpward, contentDescription = "Income", tint = DairyGreenPrimary, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Column {
                                    Text(text = "Total Revenue", fontSize = 11.sp, color = TextSecondary)
                                    Text(text = "$${String.format(Locale.US, "%.2f", income)}", fontWeight = FontWeight.Bold, color = DairyGreenPrimary, fontSize = 14.sp)
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.ArrowDownward, contentDescription = "Expense", tint = AlertRed, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Column {
                                    Text(text = "Total Expenses", fontSize = 11.sp, color = TextSecondary)
                                    Text(text = "$${String.format(Locale.US, "%.2f", expense)}", fontWeight = FontWeight.Bold, color = AlertRed, fontSize = 14.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Report Export Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DairyGreenContainer),
                    border = BorderStroke(1.dp, DairyGreenBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Export Dairy Summary Report", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DairyGreenPrimary)
                            Text(text = "Share full herd, milk, health and financial summary text via Messaging, Email or Notes.", fontSize = 11.sp, color = TextPrimary)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Button(
                            onClick = { viewModel.shareFarmReport(context) },
                            colors = ButtonDefaults.buttonColors(containerColor = DairyGreenPrimary),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.testTag("share_report_btn")
                        ) {
                            Icon(Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Share", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Transactions History
            item {
                Text(
                    text = "FINANCIAL LOGS (${logs.size})",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    letterSpacing = 1.sp
                )
            }

            if (logs.isEmpty()) {
                item {
                    Text(text = "No financial records logged yet.", color = TextSecondary)
                }
            } else {
                items(logs, key = { it.id }) { log ->
                    FinancialLogCard(log = log, onDelete = { viewModel.deleteFinancialLog(log) })
                }
            }
        }
    }
}

@Composable
fun FinancialLogCard(
    log: FinancialLogEntity,
    onDelete: () -> Unit
) {
    val isIncome = log.type == "Income"

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
                    .background(if (isIncome) DairyGreenContainer else AlertRedContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isIncome) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                    contentDescription = null,
                    tint = if (isIncome) DairyGreenPrimary else AlertRed,
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
                    Text(text = log.category, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                    Text(
                        text = "${if (isIncome) "+" else "-"}$${String.format(Locale.US, "%.2f", log.amount)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = if (isIncome) DairyGreenPrimary else AlertRed
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))
                Text(text = log.description, fontSize = 11.sp, color = TextSecondary)
                Text(text = log.date, fontSize = 10.sp, color = TextSecondary)
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = TextSecondary, modifier = Modifier.size(20.dp))
            }
        }
    }
}
