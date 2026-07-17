package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val lang by viewModel.language.collectAsStateWithLifecycle()
    val chatPartner by viewModel.activeChatRecipient.collectAsStateWithLifecycle()
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()

    var textInput by remember { mutableStateOf("") }
    var isTyping by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    if (chatPartner == null) {
        // Fallback placeholder when no donor/volunteer selected yet
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Chat, contentDescription = null, tint = CrimsonRed, modifier = Modifier.size(54.dp))
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = if (lang == "BN") "রক্তদাতার সাথে চ্যাট করুন" else "Chat with Blood Donors",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (lang == "BN") "ডোনাদের তালিকা বা ভলান্টিয়ার তালিকা থেকে ‘মেসেজ’ বাটনে চাপুন।" else "Select 'Send Message' from any donor card to begin.",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    } else {
        val partner = chatPartner!!
        Column(
            modifier = modifier.fillMaxSize()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CrimsonRed)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    viewModel.activeChatRecipient.value = null
                    viewModel.setScreen("dashboard")
                }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = partner.name.take(1),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = partner.name,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = if (isTyping) "typing..." else "Online | Group: ${partner.bloodGroup}",
                        fontSize = 9.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }

            // Message List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(chatMessages) { msg ->
                    val isMe = msg.senderName == (viewModel.currentUser.value?.name ?: "Me")
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = if (isMe) Alignment.CenterEnd else Alignment.CenterStart
                    ) {
                        Column(
                            horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
                        ) {
                            Card(
                                shape = RoundedCornerShape(
                                    topStart = 12.dp,
                                    topEnd = 12.dp,
                                    bottomStart = if (isMe) 12.dp else 0.dp,
                                    bottomEnd = if (isMe) 0.dp else 12.dp
                                ),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isMe) CrimsonRed else MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Text(
                                    text = msg.text,
                                    fontSize = 12.sp,
                                    color = if (isMe) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                            Text(
                                text = "12:05 PM",
                                fontSize = 8.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 2.dp, start = 4.dp, end = 4.dp)
                            )
                        }
                    }
                }
            }

            // Typing Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* Simulated image attachment */ }) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = CrimsonRed)
                }

                TextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    placeholder = { Text("বার্তা লিখুন...", fontSize = 12.sp) },
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                IconButton(
                    onClick = {
                        if (textInput.trim().isNotEmpty()) {
                            val userMsg = textInput
                            viewModel.sendChatMessage(userMsg)
                            textInput = ""

                            // Auto typing reply simulation visual cues
                            scope.launch {
                                delay(600)
                                isTyping = true
                                delay(1200)
                                isTyping = false
                                listState.animateScrollToItem(chatMessages.size)
                            }
                        }
                    }
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, tint = CrimsonRed)
                }
            }
        }
    }
}
