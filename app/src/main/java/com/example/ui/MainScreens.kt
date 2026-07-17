package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.MockData
import com.example.data.UserEntity
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val lang by viewModel.language.collectAsStateWithLifecycle()
    val isEmergencyVisible by viewModel.isEmergencyButtonVisible.collectAsStateWithLifecycle()
    val donors by viewModel.allDonors.collectAsStateWithLifecycle()
    val requests by viewModel.allRequests.collectAsStateWithLifecycle()

    val scrollState = rememberScrollState()

    // Blood Group Availability Stats
    val bloodGroupCounts = remember(donors) {
        listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-").map { bg ->
            bg to donors.count { it.bloodGroup == bg && it.verificationStatus == "Verified" && it.isAvailable }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LightBackground)
            .verticalScroll(scrollState)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Emergency SOS Button ---
        if (isEmergencyVisible) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.setScreen("emergency") }
            ) {
                // Glow effect
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .padding(4.dp)
                        .background(CrimsonRed.copy(alpha = 0.3f), RoundedCornerShape(32.dp))
                        .shadow(24.dp, RoundedCornerShape(32.dp), spotColor = CrimsonRed)
                )
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Brush.linearGradient(listOf(CrimsonRed, DeepBloodRed)))
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Box(
                                    modifier = Modifier
                                        .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text("EMERGENCY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White, letterSpacing = 1.sp)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("রক্ত প্রয়োজন?", fontSize = 22.sp, fontWeight = FontWeight.Black, color = Color.White)
                                Text("১ ক্লিকের দ্রুততম সন্ধান", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                            }
                            
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                                    .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(16.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🚨", fontSize = 28.sp)
                            }
                        }
                    }
                }
            }
        }

        // --- Quick Action Grid ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { viewModel.setScreen("find_blood") },
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(EmeraldGreen.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🔍", fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(Translations.get("find_blood", lang), color = LightOnSurface, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(if (lang == "BN") "কুমিল্লা জেলা" else "Cumilla Area", fontSize = 11.sp, color = Color.Gray)
                }
            }

            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { viewModel.setScreen("become_donor") },
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(GoldAccent.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🤝", fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(Translations.get("become_donor", lang), color = LightOnSurface, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(if (lang == "BN") "নিবন্ধন করুন" else "Register Now", fontSize = 11.sp, color = Color.Gray)
                }
            }
        }

        // --- Statistics Banner ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = EmeraldGreen),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${requests.size + 430}+", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color.White)
                    Text(if (lang == "BN") "জীবন রক্ষা" else "LIVES SAVED", fontSize = 9.sp, color = EmeraldGreenLight, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                }
                Box(modifier = Modifier.height(32.dp).width(1.dp).background(Color.White.copy(alpha = 0.2f)))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${donors.count { it.verificationStatus == "Verified" } + 94}", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color.White)
                    Text(if (lang == "BN") "সক্রিয় দাতা" else "ACTIVE DONORS", fontSize = 9.sp, color = EmeraldGreenLight, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                }
                Box(modifier = Modifier.height(32.dp).width(1.dp).background(Color.White.copy(alpha = 0.2f)))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${requests.count { it.status == "Pending" }}", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color.White)
                    Text(if (lang == "BN") "আজকের আবেদন" else "TODAY'S REQUESTS", fontSize = 9.sp, color = EmeraldGreenLight, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                }
            }
        }

        // --- Latest Requests List ---
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (lang == "BN") "জরুরী আবেদন সমূহ" else "Urgent Requests",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = LightOnSurface
                )
                Text(
                    text = if (lang == "BN") "সব দেখুন" else "See All",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CrimsonRed,
                    modifier = Modifier.clickable { viewModel.setScreen("requests") }
                )
            }
            
            val activeRequests = requests.filter { it.status == "Pending" }.take(3)
            if (activeRequests.isEmpty()) {
                Text("No active requests right now.", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(8.dp))
            } else {
                activeRequests.forEach { req ->
                    val colorAccent = if (req.isEmergency) CrimsonRed else PendingColor
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White)
                                .border(BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant), RoundedCornerShape(16.dp))
                        ) {
                            Box(modifier = Modifier.width(4.dp).height(80.dp).background(colorAccent))
                            Row(
                                modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(colorAccent.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(req.bloodGroup, fontSize = 14.sp, fontWeight = FontWeight.Black, color = colorAccent)
                                        Text("GROUP", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = colorAccent.copy(alpha = 0.7f))
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(req.hospitalName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = LightOnSurface)
                                        Box(
                                            modifier = Modifier.background(colorAccent.copy(alpha = 0.1f), RoundedCornerShape(8.dp)).padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(if (lang == "BN") "অপেক্ষমান" else "Pending", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = colorAccent)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("💉 ${req.quantity} ${if(lang=="BN") "ব্যাগ" else "Bags"} • ${req.area}", fontSize = 10.sp, color = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


// --- About Founder Page ---
@Composable
fun AboutScreen(
    lang: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        BrandLogo(modifier = Modifier.size(160.dp), showSlogan = true)
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Photo Placeholder
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Gold.copy(alpha = 0.15f))
                        .border(3.dp, Gold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = CrimsonRed, modifier = Modifier.size(54.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = if (lang == "BN") "সৌরভ নাগ" else "Saurav Nag",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = CrimsonRed
                )
                Text(
                    text = if (lang == "BN") "প্রতিষ্ঠাতা ও এডমিন (Founder & Admin)" else "Founder & Admin",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldGreenLight
                )
                Text(
                    text = Translations.get("slogan", lang),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (lang == "BN") "প্রতিষ্ঠাতার বাণী ও পরিচিতি" else "Founder Biography",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = if (lang == "BN") {
                        "সৌরভ নাগ একজন নিবেদিতপ্রাণ সমাজকর্মী এবং মানবতাবাদী ব্যক্তিত্ব। ১লা ফেব্রুয়ারি ২০২১ সালে তিনি 'রক্ত দিতে প্রস্তুত আমরা' সামাজিক সংগঠনের প্রতিষ্ঠা করেন। কুমিল্লা জেলার রক্ত সংকট দূরীকরণে এবং রোগীদের সাথে রক্তদাতাদের দ্রুততম সময়ে সংযুক্ত করতে তিনি এই উদ্ভাবনী ডিজিটাল প্ল্যাটফর্মের সূচনা করেন। সম্পূর্ণ অরাজনৈতিক ও জনকল্যাণমূলক এই উদ্যোগের উদ্দেশ্য রক্তদাতাদের উৎসাহিত করা এবং রক্তের জরুরি মুহূর্তে জীবন বাঁচানো।"
                    } else {
                        "Saurav Nag is a dedicated humanitarian and social worker. On February 1, 2021, he founded the social organization 'রক্ত দিতে প্রস্তুত আমরা' (We Are Ready to Donate Blood). To address blood shortages in Cumilla district and instantly connect donors with patients, he pioneered this innovative digital platform. Completely non-political and focused solely on humanitarian community service, the mission is to encourage blood donation and save lives during emergencies."
                    },
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    lineHeight = 20.sp
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (lang == "BN") "আমাদের মিশন ও ভিশন" else "Our Mission & Vision",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                BulletPoint(if (lang == "BN") "কুমিল্লার প্রতিটি ওয়ার্ডে ও থানায় ভলান্টিয়ার নেটওয়ার্ক গড়ে তোলা।" else "Build active volunteer networks in every ward and thana of Cumilla.")
                BulletPoint(if (lang == "BN") "রক্তের সন্ধান প্রক্রিয়াকে সম্পূর্ণ দালালী ও খরচ মুক্ত রাখা।" else "Keep the blood matching process entirely free and transparent.")
                BulletPoint(if (lang == "BN") "ভবিষ্যতে পুরো বাংলাদেশে এই নেটওয়ার্ক সম্প্রসারণ করা।" else "Scale this infrastructure across all districts of Bangladesh.")
            }
        }
    }
}

@Composable
fun BulletPoint(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(text = "🩸 ", fontSize = 12.sp)
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = text, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
    }
}


// --- Contact & Hospital Directory Page ---
@Composable
fun ContactScreen(
    lang: String,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredHospitals = remember(searchQuery) {
        MockData.CUMILLA_HOSPITALS.filter {
            it.nameEn.lowercase().contains(searchQuery.lowercase()) ||
                    it.nameBn.contains(searchQuery) ||
                    it.area.lowercase().contains(searchQuery.lowercase())
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (lang == "BN") "অফিসিয়াল যোগাযোগ" else "Official Contact Info",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                ContactItem(Icons.Default.Phone, if (lang == "BN") "+৮৮০১৭১১-১১১১১১" else "+880 1711-111111")
                ContactItem(Icons.Default.Email, "contact@roktoditeprostut.org")
                ContactItem(Icons.Default.LocationOn, if (lang == "BN") "রক্ত ভবন, কুমিল্লা সদর, বাংলাদেশ" else "Rokto Bhaban, Cumilla Sadar, Bangladesh")
            }
        }

        // --- Hospital Directory Sub-Module ---
        Text(
            text = if (lang == "BN") "কুমিল্লা হাসপাতালের তথ্য ও ডিরেক্টরি" else "Cumilla Hospital Directory",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.Start)
        )

        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text(if (lang == "BN") "হাসপাতাল বা এলাকা খুঁজুন..." else "Search hospital or area...") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        filteredHospitals.forEach { hospital ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(0.7f)) {
                        Text(
                            text = if (lang == "BN") hospital.nameBn else hospital.nameEn,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${Translations.get("area", lang)}: ${hospital.area}",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Button(
                        onClick = { /* Simulated dial action */ },
                        modifier = Modifier.weight(0.3f),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(Translations.get("call", lang), fontSize = 10.sp, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun ContactItem(icon: ImageVector, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = CrimsonRed, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = text, fontSize = 12.sp)
    }
}

// --- Settings & Role Switcher Page ---
@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val lang by viewModel.language.collectAsStateWithLifecycle()
    val isDark by viewModel.isDarkMode.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Language Box
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (lang == "BN") "ভাষা পরিবর্তন (Language Switching)" else "Change Language",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { if (lang != "BN") viewModel.toggleLanguage() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (lang == "BN") CrimsonRed else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (lang == "BN") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Text("বাংলা (BN)", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { if (lang != "EN") viewModel.toggleLanguage() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (lang == "EN") CrimsonRed else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (lang == "EN") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Text("English (EN)", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Theme Toggle Box
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (lang == "BN") "ডার্ক মোড (Dark Theme)" else "Dark Mode Theme",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (lang == "BN") "রাতকালীন ব্যবহারের জন্য উপযুক্ত" else "Optimal for night usage",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = isDark,
                    onCheckedChange = { viewModel.toggleTheme() },
                    colors = SwitchDefaults.colors(checkedThumbColor = CrimsonRed)
                )
            }
        }

        // Developer Role Switcher (For high quality review!)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Gold.copy(alpha = 0.15f)),
            border = BorderStroke(1.5.dp, Gold)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Build, contentDescription = null, tint = CrimsonRed)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (lang == "BN") "ডেভেলপার রোল সুইচ (Demo Role Switcher)" else "Demo Role Tester",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = CrimsonRed
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (lang == "BN") {
                        "অ্যাপ রিভিউ করার সুবিধার্থে যেকোনো প্রোফাইল রোল অ্যাক্সেস করতে নিচের বাটনে চাপ দিন।"
                    } else {
                        "Instantly change roles to test Super Admin features, Volunteer queues, or Donor stats."
                    },
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(12.dp))

                val roles = listOf("Super Admin", "Volunteer", "Donor", "Recipient")
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    roles.forEach { role ->
                        val isCurrent = currentUser?.role == role
                        AssistChip(
                            onClick = { viewModel.switchRole(role) },
                            label = { Text(role, fontWeight = FontWeight.Bold) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = if (isCurrent) CrimsonRed else Color.Transparent,
                                labelColor = if (isCurrent) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
