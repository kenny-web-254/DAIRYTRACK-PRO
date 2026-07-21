package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CattleEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.DairyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CattleScreen(
    viewModel: DairyViewModel,
    onOpenAddCattleDialog: () -> Unit
) {
    val cattleList by viewModel.filteredCattle.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val stageFilter by viewModel.stageFilter.collectAsState()
    val selectedCattle by viewModel.selectedCattle.collectAsState()

    val stages = listOf("ALL", "Lactating", "Dry", "Heifer", "Calf", "Bull")

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                label = { Text("Search Tag, Name or Breed") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_cattle"),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DairyGreenPrimary,
                    unfocusedBorderColor = SlateBorder
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(stages) { stage ->
                    FilterChip(
                        selected = stageFilter == stage,
                        onClick = { viewModel.setStageFilter(stage) },
                        label = { Text(stage, fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                        shape = RoundedCornerShape(100.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = DairyGreenPrimary,
                            selectedLabelColor = Color.White,
                            containerColor = Color.White,
                            labelColor = TextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = stageFilter == stage,
                            borderColor = SlateBorder,
                            selectedBorderColor = DairyGreenPrimary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "HERD REGISTRY (${cattleList.size})",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (cattleList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = "No cattle found",
                            tint = TextSecondary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No cattle found matching criteria", color = TextSecondary)
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(cattleList, key = { it.id }) { cattle ->
                        CattleCard(
                            cattle = cattle,
                            onClick = { viewModel.selectCattle(cattle) },
                            onDelete = { viewModel.deleteCattle(cattle) }
                        )
                    }
                }
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = onOpenAddCattleDialog,
            containerColor = DairyGreenPrimary,
            contentColor = Color.White,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .testTag("add_cattle_fab")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Cattle")
        }

        // Selected Cattle Detail Dialog / Modal
        selectedCattle?.let { cow ->
            CattleDetailDialog(
                cattle = cow,
                viewModel = viewModel,
                onDismiss = { viewModel.selectCattle(null) }
            )
        }
    }
}

@Composable
fun CattleCard(
    cattle: CattleEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
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
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        if (cattle.stage == "Lactating") HealthyBlueContainer else DairyGreenContainer
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = cattle.earTag.takeLast(2).ifEmpty { "#" },
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (cattle.stage == "Lactating") HealthyBlue else DairyGreenPrimary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = cattle.earTag,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = TextPrimary
                    )
                    Surface(
                        color = if (cattle.stage == "Lactating") HealthyBlueContainer else DairyGreenContainer,
                        shape = RoundedCornerShape(100.dp)
                    ) {
                        Text(
                            text = cattle.stage.uppercase(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (cattle.stage == "Lactating") HealthyBlue else DairyGreenSecondary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "${cattle.name} • ${cattle.breed}",
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Lact #${cattle.lactationNumber}", fontSize = 11.sp, color = TextSecondary)
                    Text(text = "•", fontSize = 11.sp, color = TextSecondary)
                    Text(text = "${cattle.weightKg} kg", fontSize = 11.sp, color = TextSecondary)
                    Text(text = "•", fontSize = 11.sp, color = TextSecondary)
                    Text(text = cattle.groupPen, fontSize = 11.sp, color = TextSecondary)
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Delete Cattle",
                    tint = TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun CattleDetailDialog(
    cattle: CattleEntity,
    viewModel: DairyViewModel,
    onDismiss: () -> Unit
) {
    val milkLogs by viewModel.repository.getMilkLogsForCattle(cattle.earTag).collectAsState(initial = emptyList())
    val healthLogs by viewModel.repository.getHealthLogsForCattle(cattle.earTag).collectAsState(initial = emptyList())
    val breedingLogs by viewModel.repository.getBreedingLogsForCattle(cattle.earTag).collectAsState(initial = emptyList())

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Pets, contentDescription = null, tint = DairyGreenPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = cattle.earTag, fontWeight = FontWeight.Bold)
                    Text(text = "${cattle.name} (${cattle.breed})", fontSize = 12.sp, color = Color.Gray)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DairyGreenContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "Stage: ${cattle.stage} | Status: ${cattle.healthStatus}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(text = "Group/Pen: ${cattle.groupPen} | Weight: ${cattle.weightKg} kg", fontSize = 12.sp)
                        Text(text = "DOB: ${cattle.birthDate} | Lactation #${cattle.lactationNumber}", fontSize = 12.sp)
                        if (cattle.notes.isNotBlank()) {
                            Text(text = "Notes: ${cattle.notes}", fontSize = 11.sp, color = Color.DarkGray)
                        }
                    }
                }

                Text(text = "Milk History (${milkLogs.size} logs)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                if (milkLogs.isEmpty()) {
                    Text(text = "No milk logs recorded for this cow.", fontSize = 12.sp, color = Color.Gray)
                } else {
                    milkLogs.take(5).forEach { log ->
                        Text(text = "• ${log.date} (${log.session}): ${log.quantityLiters} Liters", fontSize = 12.sp)
                    }
                }

                Text(text = "Health Events (${healthLogs.size} logs)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                if (healthLogs.isEmpty()) {
                    Text(text = "No health issues recorded.", fontSize = 12.sp, color = Color.Gray)
                } else {
                    healthLogs.take(3).forEach { h ->
                        Text(text = "• ${h.date}: ${h.diagnosisTitle} (${h.status})", fontSize = 12.sp)
                    }
                }

                Text(text = "Breeding Timeline (${breedingLogs.size} logs)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                if (breedingLogs.isEmpty()) {
                    Text(text = "No breeding logs recorded.", fontSize = 12.sp, color = Color.Gray)
                } else {
                    breedingLogs.take(3).forEach { b ->
                        Text(text = "• ${b.eventDate}: ${b.eventType} - ${b.pregnancyStatus}", fontSize = 12.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = DairyGreenPrimary)) {
                Text("Close")
            }
        }
    )
}
