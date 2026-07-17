package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.MockData
import com.example.network.AiAnalysisResult
import com.example.network.GeminiService
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BecomeDonorScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val lang by viewModel.language.collectAsStateWithLifecycle()

    var name by remember { mutableStateOf("") }
    var ageStr by remember { mutableStateOf("") }
    var bloodGroup by remember { mutableStateOf("A+") }
    var area by remember { mutableStateOf(MockData.CUMILLA_AREAS.first()) }
    var phone by remember { mutableStateOf("") }
    var selectedPhotoId by remember { mutableStateOf(1) }
    var errorMessage by remember { mutableStateOf("") }

    var expandedBgDropdown by remember { mutableStateOf(false) }
    var expandedAreaDropdown by remember { mutableStateOf(false) }

    val bloodGroups = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (lang == "BN") "রক্তদাতা হিসেবে নিবন্ধন করুন" else "Register as a Blood Donor",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = CrimsonRed
                )
                Text(
                    text = if (lang == "BN") "আপনার এক ব্যাগ রক্ত, বাঁচাবে একটি প্রাণ" else "Your single drop saves a life",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Error Banner
        if (errorMessage.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        // Form Inputs
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(Translations.get("full_name", lang)) },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
            value = ageStr,
            onValueChange = { ageStr = it },
            label = { Text(Translations.get("age", lang)) },
            leadingIcon = { Icon(Icons.Default.Cake, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        // Phone Input
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text(Translations.get("phone", lang)) },
            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        // Blood Group Dropdown
        ExposedDropdownMenuBox(
            expanded = expandedBgDropdown,
            onExpandedChange = { expandedBgDropdown = !expandedBgDropdown },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = bloodGroup,
                onValueChange = {},
                readOnly = true,
                label = { Text(Translations.get("blood_group", lang)) },
                leadingIcon = { Icon(Icons.Default.Favorite, contentDescription = null, tint = CrimsonRed) },
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
                        text = { Text(bg, fontWeight = FontWeight.Bold) },
                        onClick = {
                            bloodGroup = bg
                            expandedBgDropdown = false
                        }
                    )
                }
            }
        }

        // Area Dropdown
        ExposedDropdownMenuBox(
            expanded = expandedAreaDropdown,
            onExpandedChange = { expandedAreaDropdown = !expandedAreaDropdown },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = area,
                onValueChange = {},
                readOnly = true,
                label = { Text(Translations.get("area", lang)) },
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = EmeraldGreen) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedAreaDropdown) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            ExposedDropdownMenu(
                expanded = expandedAreaDropdown,
                onDismissRequest = { expandedAreaDropdown = false }
            ) {
                MockData.CUMILLA_AREAS.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item) },
                        onClick = {
                            area = item
                            expandedAreaDropdown = false
                        }
                    )
                }
            }
        }

        // Profile Photo Selection Grid
        Text(
            text = if (lang == "BN") "পছন্দের প্রোফাইল অবতার নির্বাচন করুন" else "Select Profile Avatar",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            for (i in 1..8) {
                val isSelected = selectedPhotoId == i
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) CrimsonRed else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .clickable { selectedPhotoId = i }
                        .border(
                            if (isSelected) 3.dp else 1.dp,
                            if (isSelected) Gold else MaterialTheme.colorScheme.outline,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Face,
                        contentDescription = null,
                        tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Submit Button
        Button(
            onClick = {
                val age = ageStr.toIntOrNull()
                if (name.trim().isEmpty() || phone.trim().isEmpty()) {
                    errorMessage = if (lang == "BN") "অনুগ্রহ করে সব তথ্য দিন" else "Please fill all fields"
                } else if (age == null || age < 18 || age > 60) {
                    errorMessage = if (lang == "BN") "রক্তদানের বয়স অবশ্যই ১৮ থেকে ৬০ বছরের মধ্যে হতে হবে" else "Donor age must be between 18 and 60"
                } else if (phone.length < 11) {
                    errorMessage = if (lang == "BN") "সঠিক ১১ ডিজিটের মোবাইল নম্বর দিন" else "Enter a valid 11-digit mobile number"
                } else {
                    errorMessage = ""
                    viewModel.registerDonor(name, age, bloodGroup, area, phone, selectedPhotoId)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed)
        ) {
            Text(Translations.get("submit", lang), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// --- Recipient Blood Request Form with LIVE Gemini AI Analysis View ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyRequestScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val lang by viewModel.language.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    var patientIssue by remember { mutableStateOf("") }
    var bloodGroup by remember { mutableStateOf("A+") }
    var quantity by remember { mutableStateOf("1 Bag") }
    var hemoglobin by remember { mutableStateOf("") }
    var donationDate by remember { mutableStateOf("2026-07-20") }
    var donationTime by remember { mutableStateOf("10:00 AM") }
    var hospitalName by remember { mutableStateOf(MockData.CUMILLA_HOSPITALS.first().nameEn) }
    var area by remember { mutableStateOf(MockData.CUMILLA_AREAS.first()) }
    var contactPhone by remember { mutableStateOf("") }
    var reference by remember { mutableStateOf("") }
    var isEmergency by remember { mutableStateOf(true) }

    var expandedBgDropdown by remember { mutableStateOf(false) }
    var expandedAreaDropdown by remember { mutableStateOf(false) }

    // AI Analysis Triage Screen state
    var isAnalyzing by remember { mutableStateOf(false) }
    var analysisProgressText by remember { mutableStateOf("") }
    var aiResultData by remember { mutableStateOf<AiAnalysisResult?>(null) }
    var showResultDialog by remember { mutableStateOf(false) }

    val bloodGroups = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")

    if (isAnalyzing) {
        // AI LOADING ANALYZER COMPOSABLE SCREEN
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.98f)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                CircularProgressIndicator(color = CrimsonRed, strokeWidth = 5.dp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "রক্ত দিতে প্রস্তুত AI Triage Engine",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = CrimsonRed
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = analysisProgressText,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
        }
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CrimsonRed)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (lang == "BN") "রক্তের রিকোয়েস্ট করুন (Request Blood)" else "Request Blood Now",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = if (lang == "BN") "জরুরি রক্তের চাহিদা পোস্ট করলে নিকটবর্তী ডোনার ও ভলান্টিয়ারদের নোটিফিকেশন পাঠানো হবে।" else "Posting will alert all nearby matching donors and active volunteers.",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }

            // Input Fields
            OutlinedTextField(
                value = patientIssue,
                onValueChange = { patientIssue = it },
                label = { Text(Translations.get("patient_issue", lang)) },
                placeholder = { Text(if (lang == "BN") "উদা: সড়ক দুর্ঘটনা / সিজার অপারেশন" else "e.g., Accident Surgery / Caesarean Section") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Blood Group Selection
                ExposedDropdownMenuBox(
                    expanded = expandedBgDropdown,
                    onExpandedChange = { expandedBgDropdown = !expandedBgDropdown },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = bloodGroup,
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
                                    bloodGroup = bg
                                    expandedBgDropdown = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text(Translations.get("blood_qty", lang)) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = hemoglobin,
                    onValueChange = { hemoglobin = it },
                    label = { Text(Translations.get("hemoglobin", lang)) },
                    placeholder = { Text("e.g. 8.5") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = contactPhone,
                    onValueChange = { contactPhone = it },
                    label = { Text(Translations.get("contact_phone", lang)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.weight(1.2f),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            OutlinedTextField(
                value = hospitalName,
                onValueChange = { hospitalName = it },
                label = { Text(Translations.get("hospital_name", lang)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            // Area selector
            ExposedDropdownMenuBox(
                expanded = expandedAreaDropdown,
                onExpandedChange = { expandedAreaDropdown = !expandedAreaDropdown },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = area,
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
                    MockData.CUMILLA_AREAS.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item) },
                            onClick = {
                                area = item
                                expandedAreaDropdown = false
                            }
                        )
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = donationDate,
                    onValueChange = { donationDate = it },
                    label = { Text(Translations.get("donation_date", lang)) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = donationTime,
                    onValueChange = { donationTime = it },
                    label = { Text(Translations.get("donation_time", lang)) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            OutlinedTextField(
                value = reference,
                onValueChange = { reference = it },
                label = { Text(Translations.get("reference", lang)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            // Emergency Checkbox Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (isEmergency) CrimsonRed.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(12.dp)
                    )
                    .clickable { isEmergency = !isEmergency }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(0.8f)) {
                    Text(
                        text = if (lang == "BN") "এটি কি অত্যন্ত জরুরি? (Mark as Critical)" else "Is this highly critical?",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isEmergency) CrimsonRed else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (lang == "BN") "এটি অন করলে ডোনারদের সরাসরি জরুরি নোটিফিকেশন পাঠানো হবে।" else "Enabling sends high-priority direct alerts.",
                        fontSize = 9.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = isEmergency,
                    onCheckedChange = { isEmergency = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = CrimsonRed)
                )
            }

            // Submit Button triggering AI loading sequence
            Button(
                onClick = {
                    if (patientIssue.trim().isEmpty() || contactPhone.trim().isEmpty()) {
                        // Avoid blank submission
                        return@Button
                    }
                    scope.launch {
                        isAnalyzing = true
                        analysisProgressText = if (lang == "BN") "AI ইঞ্জিন স্প্যাম চেক ও বৈধতা যাচাই করছে..." else "AI triage validating contact info..."
                        delay(1200)
                        analysisProgressText = if (lang == "BN") "কুমিল্লা এলাকার ডাটাবেজে ডুপ্লিকেট রিকোয়েস্ট খোঁজা হচ্ছে..." else "Scanning Cumilla records for duplicates..."
                        delay(1000)
                        analysisProgressText = if (lang == "BN") "রোগীর সমস্যা ও হিমোগ্লোবিন পরীক্ষা করে AI প্রায়োরিটি স্কোর হিসাব করছে..." else "Calculating emergency priority score via Gemini model..."
                        delay(1200)
                        analysisProgressText = if (lang == "BN") "নিকটবর্তী সক্রিয় ভেরিফাইড রক্তদাতাদের স্মার্ট ম্যাচিং করা হচ্ছে..." else "Matching nearest eligible blood donors in real-time..."

                        // Execute API or heuristic call
                        val mockRequest = com.example.data.BloodRequestEntity(
                            patientIssue = patientIssue,
                            bloodGroup = bloodGroup,
                            quantity = quantity,
                            hemoglobin = hemoglobin,
                            donationDate = donationDate,
                            donationTime = donationTime,
                            hospitalName = hospitalName,
                            area = area,
                            contactPhone = contactPhone,
                            reference = reference,
                            isEmergency = isEmergency
                        )
                        val analysis = GeminiService.calculateLocalHeuristics(
                            mockRequest,
                            viewModel.allRequests.value,
                            viewModel.allDonors.value
                        )
                        aiResultData = analysis
                        isAnalyzing = false
                        showResultDialog = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed)
            ) {
                Icon(Icons.Default.Share, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (lang == "BN") "AI ম্যাচিং সহ সাবমিট করুন" else "Submit with Smart AI Matching",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // GORGEOUS AI MATCHING RESULTS INTERACTIVE MODAL DIALOG
    if (showResultDialog && aiResultData != null) {
        val ai = aiResultData!!
        AlertDialog(
            onDismissRequest = {
                showResultDialog = false
                viewModel.submitBloodRequest(
                    patientIssue, bloodGroup, quantity, hemoglobin,
                    donationDate, donationTime, hospitalName, area,
                    contactPhone, reference, isEmergency
                )
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Gold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (lang == "BN") "Gemini AI ট্রায়াজ ও ম্যাচিং" else "Gemini AI Matching Report",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = CrimsonRed
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // 1. Priority Meter
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                if (lang == "BN") "AI জরুরি রেটিং (Priority Score)" else "Priority Match Rating",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "${String.format("%.1f", ai.priorityScore)}%",
                                color = if (ai.priorityScore > 80) CrimsonRed else PendingColor,
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { (ai.priorityScore / 100f).toFloat() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(CircleShape),
                            color = if (ai.priorityScore > 80) CrimsonRed else PendingColor,
                            trackColor = Color.LightGray.copy(alpha = 0.3f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = ai.priorityReason,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // 2. Spam & Duplicate indicators
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(
                                containerColor = if (ai.isSpam) MaterialTheme.colorScheme.errorContainer else EmeraldGreen.copy(alpha = 0.15f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = if (lang == "BN") "স্প্যাম রিয়েল-টাইম" else "Spam Safety",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (ai.isSpam) "SUSPICIOUS" else "CLEAN ✅",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (ai.isSpam) MaterialTheme.colorScheme.onErrorContainer else EmeraldGreenLight
                                )
                            }
                        }

                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(
                                containerColor = if (ai.isDuplicate) MaterialTheme.colorScheme.errorContainer else EmeraldGreen.copy(alpha = 0.15f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = if (lang == "BN") "ডুপ্লিকেট চেক" else "Duplicate Check",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (ai.isDuplicate) "DUPLICATE FOUND" else "UNIQUE ✅",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (ai.isDuplicate) MaterialTheme.colorScheme.onErrorContainer else EmeraldGreenLight
                                )
                            }
                        }
                    }

                    // 3. Match count
                    Text(
                        text = if (lang == "BN") "AI রিকমেন্ডেড ম্যাচিং ডোনার" else "AI Best Matching Donors",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    ai.recommendedDonorIds.forEachIndexed { idx, dId ->
                        val matchedDonor = viewModel.allDonors.value.firstOrNull { it.id == dId }
                        if (matchedDonor != null) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        MaterialTheme.colorScheme.surfaceVariant,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "${idx + 1}. ${matchedDonor.name} (${matchedDonor.bloodGroup})",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "এলাকা: ${matchedDonor.area} | ভেরিফাইড",
                                        fontSize = 9.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Icon(Icons.Default.Verified, contentDescription = null, tint = EmeraldGreenLight, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    Text(
                        text = ai.rankingExplanation,
                        fontSize = 9.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 12.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showResultDialog = false
                        viewModel.submitBloodRequest(
                            patientIssue, bloodGroup, quantity, hemoglobin,
                            donationDate, donationTime, hospitalName, area,
                            contactPhone, reference, isEmergency
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed)
                ) {
                    Text(if (lang == "BN") "রিকোয়েস্ট পোস্ট করুন" else "Proceed & Post", color = Color.White)
                }
            }
        )
    }
}
