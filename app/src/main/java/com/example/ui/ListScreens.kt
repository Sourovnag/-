package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.MockData
import com.example.data.UserEntity
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FindBloodScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val lang by viewModel.language.collectAsStateWithLifecycle()
    val donors by viewModel.allDonors.collectAsStateWithLifecycle()

    val searchBg by viewModel.searchBloodGroup.collectAsStateWithLifecycle()
    val searchArea by viewModel.searchArea.collectAsStateWithLifecycle()
    val onlyVerified by viewModel.searchOnlyVerified.collectAsStateWithLifecycle()
    val onlyAvailable by viewModel.searchOnlyAvailable.collectAsStateWithLifecycle()

    var expandedBgDropdown by remember { mutableStateOf(false) }
    var expandedAreaDropdown by remember { mutableStateOf(false) }

    val bloodGroups = listOf("All", "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
    val areas = listOf("All") + MockData.CUMILLA_AREAS

    // Filter Donors
    val filteredDonors = remember(donors, searchBg, searchArea, onlyVerified, onlyAvailable) {
        donors.filter { donor ->
            val bgMatch = searchBg == "All" || donor.bloodGroup == searchBg
            val areaMatch = searchArea == "All" || donor.upazila.contains(searchArea) || donor.area.contains(searchArea)
            val verifiedMatch = !onlyVerified || donor.verificationStatus == "Verified"
            val availableMatch = !onlyAvailable || donor.isAvailable
            bgMatch && areaMatch && verifiedMatch && availableMatch
        }
    }

    var selectedRadarDonorName by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // --- 1. Glowing Radar Map Simulator ---
        RadarMapAnimation(
            matchingDonorsCount = filteredDonors.size.coerceAtMost(6),
            onPointClicked = { name ->
                selectedRadarDonorName = name
            }
        )

        if (selectedRadarDonorName.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Gold.copy(alpha = 0.15f)),
                border = BorderStroke(1.dp, Gold),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (lang == "BN") "রাডার সিগন্যাল: $selectedRadarDonorName আপনার হাসপাতালের নিকটেই আছেন!" else "Radar: $selectedRadarDonorName is close to hospital!",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(0.9f)
                    )
                    Icon(
                        Icons.Default.Close,
                        contentDescription = null,
                        modifier = Modifier
                            .clickable { selectedRadarDonorName = "" }
                            .weight(0.1f)
                    )
                }
            }
        }

        // --- 2. Filter Form Card ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Blood group selector
                    ExposedDropdownMenuBox(
                        expanded = expandedBgDropdown,
                        onExpandedChange = { expandedBgDropdown = !expandedBgDropdown },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = searchBg,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(Translations.get("blood_group", lang)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedBgDropdown) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = expandedBgDropdown,
                            onDismissRequest = { expandedBgDropdown = false }
                        ) {
                            bloodGroups.forEach { bg ->
                                DropdownMenuItem(
                                    text = { Text(bg) },
                                    onClick = {
                                        viewModel.searchBloodGroup.value = bg
                                        expandedBgDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    // Area selector
                    ExposedDropdownMenuBox(
                        expanded = expandedAreaDropdown,
                        onExpandedChange = { expandedAreaDropdown = !expandedAreaDropdown },
                        modifier = Modifier.weight(1.5f)
                    ) {
                        OutlinedTextField(
                            value = searchArea,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(Translations.get("area", lang)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedAreaDropdown) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = expandedAreaDropdown,
                            onDismissRequest = { expandedAreaDropdown = false }
                        ) {
                            areas.forEach { areaItem ->
                                DropdownMenuItem(
                                    text = { Text(areaItem) },
                                    onClick = {
                                        viewModel.searchArea.value = areaItem
                                        expandedAreaDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Checkboxes for quick criteria
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = onlyVerified,
                            onCheckedChange = { viewModel.searchOnlyVerified.value = it },
                            colors = CheckboxDefaults.colors(checkedColor = EmeraldGreenLight)
                        )
                        Text(if (lang == "BN") "ভেরিফাইড ডোনার" else "Verified Only", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = onlyAvailable,
                            onCheckedChange = { viewModel.searchOnlyAvailable.value = it },
                            colors = CheckboxDefaults.colors(checkedColor = EmeraldGreenLight)
                        )
                        Text(if (lang == "BN") "প্রস্তুত ডোনার" else "Available Only", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // --- 3. Results List ---
        Text(
            text = "${filteredDonors.size} ${if (lang == "BN") "জন রক্তদাতা পাওয়া গেছে" else "Matching Donors Found"}",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredDonors) { donor ->
                DonorResultCard(donor = donor, lang = lang, onChatClicked = {
                    viewModel.startChatWith(donor)
                })
            }
        }
    }
}

@Composable
fun DonorResultCard(
    donor: UserEntity,
    lang: String,
    onChatClicked: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Custom drawn Avatar circle
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                if (donor.isAvailable) EmeraldGreen.copy(alpha = 0.15f) else Color.LightGray.copy(alpha = 0.2f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = donor.name.take(1),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (donor.isAvailable) EmeraldGreenLight else Color.Gray
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = donor.name,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (donor.verificationStatus == "Verified") {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.Verified, contentDescription = "Verified", tint = EmeraldGreenLight, modifier = Modifier.size(14.dp))
                            }
                        }
                        Text(
                            text = "${Translations.get("area", lang)}: ${donor.area}",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Blood Group Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(CrimsonRed)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = donor.bloodGroup,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Stats row (Gamification badges, availability status, and total donations)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Availability status indicator pill
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (donor.isAvailable) EmeraldGreenLight else PendingColor)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (donor.isAvailable) {
                            if (lang == "BN") "রক্তদানে প্রস্তুত" else "Ready to Donate"
                        } else {
                            if (lang == "BN") "প্রস্তুত নয়" else "Busy (Recently Donated)"
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (donor.isAvailable) EmeraldGreenLight else PendingColor
                    )
                }

                // Donations summary count
                Text(
                    text = "${Translations.get("total_donations", lang)}: ${donor.totalDonations} ${if (lang == "BN") "বার" else "times"}",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )

                // Gamification badge showing
                if (donor.badge != "None") {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Gold.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = donor.badge,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = GoldAccent
                        )
                    }
                }
            }

            Divider(modifier = Modifier.padding(vertical = 12.dp))

            // Action Buttons (Call, Chat, WhatsApp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { /* Simulated dial action */ },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(Translations.get("call", lang), fontSize = 11.sp, color = Color.White)
                }

                Button(
                    onClick = onChatClicked,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(Translations.get("chat", lang), fontSize = 11.sp, color = Color.White)
                }
            }
        }
    }
}

// --- Active Blood Requests Screen ---
@Composable
fun RequestsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val lang by viewModel.language.collectAsStateWithLifecycle()
    val requests by viewModel.allRequests.collectAsStateWithLifecycle()

    var filterBg by remember { mutableStateOf("All") }
    val bloodGroups = listOf("All", "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")

    val filteredRequests = remember(requests, filterBg) {
        requests.filter { req ->
            filterBg == "All" || req.bloodGroup == filterBg
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Filter bar
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            bloodGroups.forEach { bg ->
                val isSelected = filterBg == bg
                FilterChip(
                    selected = isSelected,
                    onClick = { filterBg = bg },
                    label = { Text(bg) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CrimsonRed,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Text(
            text = "${filteredRequests.size} ${if (lang == "BN") "টি সক্রিয় রক্তের রিকোয়েস্ট রয়েছে" else "Active Blood Requests"}",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredRequests) { request ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (request.isEmergency) CrimsonRed.copy(alpha = 0.05f) else MaterialTheme.colorScheme.surface
                    ),
                    border = if (request.isEmergency) BorderStroke(1.5.dp, CrimsonRed.copy(alpha = 0.5f)) else null,
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(0.7f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = request.patientIssue.substringBefore(" ("),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                    if (request.isEmergency) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(CrimsonRed)
                                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                        ) {
                                            Text("🚨 URGENT", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        }
                                    }
                                }
                                Text(
                                    text = "🏥 ${request.hospitalName}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            // Blood Group Badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CrimsonRed)
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = request.bloodGroup,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Details grid (Area, Phone, Date, Time, Quantity, Reference)
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            DetailRow(Icons.Default.LocationOn, "${Translations.get("area", lang)}: ${request.area}")
                            DetailRow(Icons.Default.DateRange, "${Translations.get("donation_date", lang)}: ${request.donationDate} (${request.donationTime})")
                            DetailRow(Icons.Default.ShoppingBag, "${Translations.get("blood_qty", lang)}: ${request.quantity}")
                            if (request.reference != null) {
                                DetailRow(Icons.Default.Face, "${Translations.get("reference", lang)}: ${request.reference}")
                            }
                        }

                        // --- Interactive Timeline Tracker Sub-Module ---
                        Spacer(modifier = Modifier.height(12.dp))
                        Divider(color = MaterialTheme.colorScheme.outlineVariant)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (lang == "BN") "রক্তদান কাজের অগ্রগতি (Timeline Tracker)" else "Donation Progress Tracker",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        val steps = listOf("Post", "Verify", "Match", "Done")
                        val activeIndex = when(request.status) {
                            "Pending" -> 0
                            "Approved" -> 1
                            "Fulfilled" -> 3
                            else -> 0
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            steps.forEachIndexed { index, step ->
                                val completed = index <= activeIndex
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .size(18.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (completed) EmeraldGreenLight else Color.LightGray.copy(alpha = 0.5f)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.Done,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(10.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(step, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                                if (index < steps.size - 1) {
                                    Divider(
                                        modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
                                        color = if (index < activeIndex) EmeraldGreenLight else Color.LightGray.copy(alpha = 0.5f),
                                        thickness = 2.dp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Action button
                        Button(
                            onClick = { /* Simulated dial action */ },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${Translations.get("call", lang)}: ${request.contactPhone}",
                                fontSize = 12.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = CrimsonRed.copy(alpha = 0.7f), modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = text, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}
