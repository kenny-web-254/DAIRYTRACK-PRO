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
import com.example.data.local.BreedingLogEntity
import com.example.data.local.FeedInventoryEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.DairyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreedingAndFeedScreen(
    viewModel: DairyViewModel,
    onOpenAddBreedingDialog: () -> Unit,
    onOpenAddFeedDialog: () -> Unit
) {
    var selectedSection by remember { mutableIntStateOf(0) } // 0: Breeding, 1: Feed
    val breedingLogs by viewModel.allBreedingLogs.collectAsState()
    val pregnantLogs by viewModel.pregnantCattleLogs.collectAsState()
    val feedList by viewModel.allFeedInventory.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Section Tab Switcher
        PrimaryTabRow(
            selectedTabIndex = selectedSection,
            containerColor = Color.Transparent,
            contentColor = DairyGreenPrimary
        ) {
            Tab(
                selected = selectedSection == 0,
                onClick = { selectedSection = 0 },
                text = { Text("Breeding (${pregnantLogs.size} Pregnant)", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                icon = { Icon(Icons.Default.Egg, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
            Tab(
                selected = selectedSection == 1,
                onClick = { selectedSection = 1 },
                text = { Text("Feed Inventory (${feedList.size} Items)", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                icon = { Icon(Icons.Default.Grass, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedSection == 0) {
            // Breeding Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "REPRODUCTIVE PIPELINE",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    letterSpacing = 1.sp
                )
                Button(
                    onClick = onOpenAddBreedingDialog,
                    colors = ButtonDefaults.buttonColors(containerColor = FarmGoldAccent),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.testTag("log_breeding_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Log Event", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = FarmGoldContainer),
                        shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(1.dp, FarmGoldAccent.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(text = "GESTATION TRACKER", fontWeight = FontWeight.Bold, color = FarmGoldAccent, fontSize = 11.sp, letterSpacing = 0.5.sp)
                            Text(text = "${pregnantLogs.size} Confirmed Pregnant Cows", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                            Text(text = "Standard Gestation Period: 283 Days from Artificial Insemination", fontSize = 12.sp, color = TextPrimary)
                        }
                    }
                }

                items(breedingLogs, key = { it.id }) { log ->
                    BreedingLogCard(log = log, onDelete = { viewModel.deleteBreedingLog(log) })
                }
            }

        } else {
            // Feed Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "FEED INVENTORY & RATIONS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    letterSpacing = 1.sp
                )
                Button(
                    onClick = onOpenAddFeedDialog,
                    colors = ButtonDefaults.buttonColors(containerColor = DairyGreenPrimary),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.testTag("add_feed_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Feed", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(feedList, key = { it.id }) { item ->
                    FeedItemCard(item = item, onDelete = { viewModel.deleteFeedItem(item) })
                }
            }
        }
    }
}

@Composable
fun BreedingLogCard(
    log: BreedingLogEntity,
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
                    .background(FarmGoldContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Favorite, contentDescription = null, tint = FarmGoldAccent, modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = "${log.cattleTag} • ${log.eventType}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                Text(text = "Status: ${log.pregnancyStatus}", fontSize = 12.sp, color = FarmGoldAccent, fontWeight = FontWeight.Bold)
                if (!log.expectedCalvingDate.isNull_or_blank_safe()) {
                    Text(text = "Expected Calving: ${log.expectedCalvingDate}", fontSize = 11.sp, color = DairyGreenSecondary, fontWeight = FontWeight.Bold)
                }
                Text(text = "Date: ${log.eventDate} | Sire: ${log.sireDetails}", fontSize = 10.sp, color = TextSecondary)
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = TextSecondary, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun FeedItemCard(
    item: FeedInventoryEntity,
    onDelete: () -> Unit
) {
    val isLow = item.stockQuantityKg <= item.minAlertThresholdKg

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, if (isLow) WarningOrange else SlateBorder),
        colors = CardDefaults.cardColors(
            containerColor = if (isLow) WarningOrangeContainer else Color.White
        ),
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
                    .background(if (isLow) WarningOrangeContainer else DairyGreenContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Inventory2,
                    contentDescription = null,
                    tint = if (isLow) WarningOrange else DairyGreenPrimary,
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
                    Text(text = item.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                    Text(
                        text = "${item.stockQuantityKg} kg",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = if (isLow) WarningOrange else DairyGreenPrimary
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))
                Text(text = "Category: ${item.category} | Min: ${item.minAlertThresholdKg} kg", fontSize = 11.sp, color = TextSecondary)
                Text(text = "Cost: $${item.unitCostPerKg}/kg | Supplier: ${item.supplier}", fontSize = 10.sp, color = TextSecondary)
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = TextSecondary, modifier = Modifier.size(20.dp))
            }
        }
    }
}

fun String?.isNull_or_blank_safe(): Boolean = this == null || this.isBlank()
