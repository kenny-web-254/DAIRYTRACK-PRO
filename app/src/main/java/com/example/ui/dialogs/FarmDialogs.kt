package com.example.ui.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.DairyViewModel

// 1. Add Milk Production Dialog
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMilkDialog(
    viewModel: DairyViewModel,
    onDismiss: () -> Unit
) {
    var cattleTag by remember { mutableStateOf("COW-101") }
    var session by remember { mutableStateOf("Morning") }
    var quantityText by remember { mutableStateOf("15.0") }
    var fatText by remember { mutableStateOf("") }
    var proteinText by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val sessions = listOf("Morning", "Afternoon", "Evening")

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White,
        title = { Text("Log Milk Production", fontWeight = FontWeight.Bold, color = TextPrimary) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = cattleTag,
                    onValueChange = { cattleTag = it },
                    label = { Text("Ear Tag (e.g. COW-101 or BULK)") },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DairyGreenPrimary,
                        unfocusedBorderColor = SlateBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialog_milk_tag"),
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    sessions.forEach { s ->
                        FilterChip(
                            selected = session == s,
                            onClick = { session = s },
                            shape = RoundedCornerShape(100.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = DairyGreenPrimary,
                                selectedLabelColor = Color.White
                            ),
                            label = { Text(s, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                        )
                    }
                }

                OutlinedTextField(
                    value = quantityText,
                    onValueChange = { quantityText = it },
                    label = { Text("Quantity (Liters)") },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DairyGreenPrimary,
                        unfocusedBorderColor = SlateBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialog_milk_quantity"),
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = fatText,
                        onValueChange = { fatText = it },
                        label = { Text("Fat % (Opt)") },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DairyGreenPrimary,
                            unfocusedBorderColor = SlateBorder
                        ),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = proteinText,
                        onValueChange = { proteinText = it },
                        label = { Text("Protein % (Opt)") },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DairyGreenPrimary,
                            unfocusedBorderColor = SlateBorder
                        ),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes / Observations") },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DairyGreenPrimary,
                        unfocusedBorderColor = SlateBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val qty = quantityText.toDoubleOrNull() ?: 0.0
                    val fat = fatText.toDoubleOrNull()
                    val protein = proteinText.toDoubleOrNull()
                    if (cattleTag.isNotBlank() && qty > 0) {
                        viewModel.logMilkProduction(
                            cattleTag = cattleTag,
                            quantityLiters = qty,
                            session = session,
                            fatPct = fat,
                            proteinPct = protein,
                            notes = notes
                        )
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = DairyGreenPrimary),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("save_milk_btn")
            ) {
                Text("Save Milk Record", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(16.dp)) {
                Text("Cancel")
            }
        }
    )
}

// 2. Add Cattle Dialog
@Composable
fun AddCattleDialog(
    viewModel: DairyViewModel,
    onDismiss: () -> Unit
) {
    var earTag by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var breed by remember { mutableStateOf("Holstein Friesian") }
    var stage by remember { mutableStateOf("Lactating") }
    var birthDate by remember { mutableStateOf("2022-01-01") }
    var weightText by remember { mutableStateOf("500") }
    var lactationText by remember { mutableStateOf("1") }
    var groupPen by remember { mutableStateOf("High Yielders") }

    val breeds = listOf("Holstein Friesian", "Jersey", "Guernsey", "Ayrshire", "Sahiwal", "Crossbreed")
    val stages = listOf("Lactating", "Dry", "Heifer", "Calf", "Bull")

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White,
        title = { Text("Register New Cattle", fontWeight = FontWeight.Bold, color = TextPrimary) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = earTag,
                    onValueChange = { earTag = it },
                    label = { Text("Ear Tag Number (e.g. COW-106)") },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DairyGreenPrimary,
                        unfocusedBorderColor = SlateBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialog_cattle_tag"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Cow / Cattle Name") },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DairyGreenPrimary,
                        unfocusedBorderColor = SlateBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialog_cattle_name"),
                    singleLine = true
                )

                Text("Select Breed:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimary)
                Row(modifier = Modifier.fillMaxWidth()) {
                    breeds.take(3).forEach { b ->
                        FilterChip(
                            selected = breed == b,
                            onClick = { breed = b },
                            shape = RoundedCornerShape(100.dp),
                            label = { Text(b, fontSize = 11.sp) },
                            modifier = Modifier.padding(end = 4.dp)
                        )
                    }
                }

                Text("Select Stage:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimary)
                Row(modifier = Modifier.fillMaxWidth()) {
                    stages.take(3).forEach { s ->
                        FilterChip(
                            selected = stage == s,
                            onClick = { stage = s },
                            shape = RoundedCornerShape(100.dp),
                            label = { Text(s, fontSize = 11.sp) },
                            modifier = Modifier.padding(end = 4.dp)
                        )
                    }
                }

                OutlinedTextField(
                    value = weightText,
                    onValueChange = { weightText = it },
                    label = { Text("Weight (kg)") },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DairyGreenPrimary,
                        unfocusedBorderColor = SlateBorder
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = groupPen,
                    onValueChange = { groupPen = it },
                    label = { Text("Group / Pen Location") },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DairyGreenPrimary,
                        unfocusedBorderColor = SlateBorder
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val weight = weightText.toDoubleOrNull() ?: 450.0
                    val lactation = lactationText.toIntOrNull() ?: 1
                    if (earTag.isNotBlank() && name.isNotBlank()) {
                        viewModel.addOrUpdateCattle(
                            earTag = earTag,
                            name = name,
                            breed = breed,
                            stage = stage,
                            birthDate = birthDate,
                            weightKg = weight,
                            lactationNumber = lactation,
                            healthStatus = "Healthy",
                            groupPen = groupPen,
                            notes = "New registered cattle"
                        )
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = DairyGreenPrimary),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("save_cattle_btn")
            ) {
                Text("Save Cattle", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(16.dp)) { Text("Cancel") }
        }
    )
}

// 3. Add Health Event Dialog
@Composable
fun AddHealthDialog(
    viewModel: DairyViewModel,
    onDismiss: () -> Unit
) {
    var cattleTag by remember { mutableStateOf("COW-102") }
    var type by remember { mutableStateOf("Treatment/Medication") }
    var title by remember { mutableStateOf("Mastitis Antibiotic Treatment") }
    var medication by remember { mutableStateOf("Penicillin Injection") }
    var withdrawalDaysText by remember { mutableStateOf("4") }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White,
        title = { Text("Log Health / Medication", fontWeight = FontWeight.Bold, color = TextPrimary) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = cattleTag,
                    onValueChange = { cattleTag = it },
                    label = { Text("Ear Tag") },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DairyGreenPrimary,
                        unfocusedBorderColor = SlateBorder
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Diagnosis / Event Title") },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DairyGreenPrimary,
                        unfocusedBorderColor = SlateBorder
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = medication,
                    onValueChange = { medication = it },
                    label = { Text("Medication / Vaccine Name") },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DairyGreenPrimary,
                        unfocusedBorderColor = SlateBorder
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = withdrawalDaysText,
                    onValueChange = { withdrawalDaysText = it },
                    label = { Text("Milk Withdrawal Period (Days to Discard Milk)") },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DairyGreenPrimary,
                        unfocusedBorderColor = SlateBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialog_withdrawal_days"),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val days = withdrawalDaysText.toIntOrNull() ?: 0
                    if (cattleTag.isNotBlank() && title.isNotBlank()) {
                        viewModel.logHealthEvent(
                            cattleTag = cattleTag,
                            type = type,
                            title = title,
                            medication = medication,
                            withdrawalDays = days
                        )
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = DairyGreenPrimary),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("save_health_btn")
            ) {
                Text("Save Health Log", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(16.dp)) { Text("Cancel") }
        }
    )
}

// 4. Add Breeding Dialog
@Composable
fun AddBreedingDialog(
    viewModel: DairyViewModel,
    onDismiss: () -> Unit
) {
    var cattleTag by remember { mutableStateOf("COW-105") }
    var eventType by remember { mutableStateOf("Insemination (AI)") }
    var sireDetails by remember { mutableStateOf("Bull Sire #USA-901") }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White,
        title = { Text("Log Reproductive Event", fontWeight = FontWeight.Bold, color = TextPrimary) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = cattleTag,
                    onValueChange = { cattleTag = it },
                    label = { Text("Ear Tag") },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DairyGreenPrimary,
                        unfocusedBorderColor = SlateBorder
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = sireDetails,
                    onValueChange = { sireDetails = it },
                    label = { Text("Sire / Bull Genetics") },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DairyGreenPrimary,
                        unfocusedBorderColor = SlateBorder
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (cattleTag.isNotBlank()) {
                        viewModel.logBreedingEvent(
                            cattleTag = cattleTag,
                            eventType = eventType,
                            sireDetails = sireDetails,
                            pregnancyStatus = "Pending"
                        )
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = DairyGreenPrimary),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Save Breeding Event", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(16.dp)) { Text("Cancel") }
        }
    )
}

// 5. Add Feed Item Dialog
@Composable
fun AddFeedDialog(
    viewModel: DairyViewModel,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Concentrate") }
    var stockText by remember { mutableStateOf("250") }
    var minText by remember { mutableStateOf("100") }
    var costText by remember { mutableStateOf("0.50") }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White,
        title = { Text("Add / Restock Feed Item", fontWeight = FontWeight.Bold, color = TextPrimary) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Feed Item Name") },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DairyGreenPrimary,
                        unfocusedBorderColor = SlateBorder
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = stockText,
                    onValueChange = { stockText = it },
                    label = { Text("Stock Quantity (kg)") },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DairyGreenPrimary,
                        unfocusedBorderColor = SlateBorder
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = minText,
                    onValueChange = { minText = it },
                    label = { Text("Minimum Alert Threshold (kg)") },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DairyGreenPrimary,
                        unfocusedBorderColor = SlateBorder
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val stock = stockText.toDoubleOrNull() ?: 100.0
                    val min = minText.toDoubleOrNull() ?: 50.0
                    val cost = costText.toDoubleOrNull() ?: 0.5
                    if (name.isNotBlank()) {
                        viewModel.addOrUpdateFeed(
                            itemCode = "F-${System.currentTimeMillis() % 1000}",
                            name = name,
                            category = category,
                            stockKg = stock,
                            minAlertKg = min,
                            unitCost = cost,
                            supplier = "Local Agro"
                        )
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = DairyGreenPrimary),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Save Feed Item", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(16.dp)) { Text("Cancel") }
        }
    )
}

// 6. Add Financial Dialog
@Composable
fun AddFinancialDialog(
    viewModel: DairyViewModel,
    onDismiss: () -> Unit
) {
    var type by remember { mutableStateOf("Income") }
    var category by remember { mutableStateOf("Milk Sales") }
    var amountText by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White,
        title = { Text("Log Financial Transaction", fontWeight = FontWeight.Bold, color = TextPrimary) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    FilterChip(
                        selected = type == "Income",
                        onClick = {
                            type = "Income"
                            category = "Milk Sales"
                        },
                        shape = RoundedCornerShape(100.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = DairyGreenPrimary,
                            selectedLabelColor = Color.White
                        ),
                        label = { Text("Income (+)") },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    FilterChip(
                        selected = type == "Expense",
                        onClick = {
                            type = "Expense"
                            category = "Feed Purchase"
                        },
                        shape = RoundedCornerShape(100.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AlertRed,
                            selectedLabelColor = Color.White
                        ),
                        label = { Text("Expense (-)") }
                    )
                }

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Amount ($)") },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DairyGreenPrimary,
                        unfocusedBorderColor = SlateBorder
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DairyGreenPrimary,
                        unfocusedBorderColor = SlateBorder
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountText.toDoubleOrNull() ?: 0.0
                    if (amt > 0) {
                        viewModel.logFinancialTransaction(
                            type = type,
                            category = category,
                            amount = amt,
                            description = description
                        )
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = DairyGreenPrimary),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Save Transaction", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(16.dp)) { Text("Cancel") }
        }
    )
}
