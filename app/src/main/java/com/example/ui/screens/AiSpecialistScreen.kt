package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import com.example.ui.theme.*
import com.example.ui.viewmodel.AiChatMessage
import com.example.ui.viewmodel.DairyViewModel

@Composable
fun AiSpecialistScreen(
    viewModel: DairyViewModel
) {
    val messages by viewModel.aiMessages.collectAsState()
    val isThinking by viewModel.isAiThinking.collectAsState()
    var inputText by remember { mutableStateOf("") }

    val promptSuggestions = listOf(
        "Diagnose milk yield drop in COW-102",
        "Suggest high-yielding ration formula",
        "Check antibiotic withdrawal guidelines",
        "Calculate feed cost per liter of milk"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // AI Header Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DairyGreenContainer),
            border = BorderStroke(1.dp, DairyGreenBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(DairyGreenPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Gemini AI Specialist",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "DairyAI Specialist (Gemini AI)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = DairyGreenPrimary
                    )
                    Text(
                        text = "Real-time veterinary, nutrition & yield management advice",
                        fontSize = 12.sp,
                        color = TextPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Suggested Prompts Row
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(promptSuggestions) { suggestion ->
                SuggestionChip(
                    onClick = {
                        inputText = suggestion
                        viewModel.askDairyAiSpecialist(suggestion)
                        inputText = ""
                    },
                    shape = RoundedCornerShape(100.dp),
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = Color.White,
                        labelColor = TextPrimary
                    ),
                    border = SuggestionChipDefaults.suggestionChipBorder(
                        enabled = true,
                        borderColor = SlateBorder
                    ),
                    label = { Text(suggestion, fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                    icon = { Icon(Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(16.dp), tint = DairyGreenPrimary) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Chat Conversation Stream
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages) { msg ->
                ChatBubble(message = msg)
            }

            if (isThinking) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = DairyGreenPrimary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "DairyAI Specialist analyzing farm data...",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Prompt Input Field & Send Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text("Ask DairyAI Specialist...") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("ai_input_field"),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DairyGreenPrimary,
                    unfocusedBorderColor = SlateBorder
                ),
                maxLines = 3
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (inputText.isNotBlank()) {
                        viewModel.askDairyAiSpecialist(inputText)
                        inputText = ""
                    }
                },
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(DairyGreenPrimary)
                    .testTag("ai_send_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun ChatBubble(message: AiChatMessage) {
    val isUser = message.sender == "USER"

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Surface(
            color = if (isUser) DairyGreenPrimary else Color.White,
            contentColor = if (isUser) Color.White else TextPrimary,
            shape = RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart = if (isUser) 18.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 18.dp
            ),
            border = if (isUser) null else BorderStroke(1.dp, SlateBorder),
            tonalElevation = 0.dp,
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = if (isUser) "You" else "DairyAI Specialist",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isUser) Color.White.copy(alpha = 0.9f) else DairyGreenPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = message.message,
                    fontSize = 13.sp,
                    color = if (isUser) Color.White else TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = message.timestamp,
                    fontSize = 10.sp,
                    color = if (isUser) Color.White.copy(alpha = 0.7f) else TextSecondary,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}
