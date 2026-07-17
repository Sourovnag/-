package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import kotlinx.coroutines.launch
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.*
import com.example.ui.theme.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.border

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: MainViewModel = viewModel()
            val isDark by viewModel.isDarkMode.collectAsStateWithLifecycle()
            val lang by viewModel.language.collectAsStateWithLifecycle()
            val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
            val notifications by viewModel.allNotifications.collectAsStateWithLifecycle()

            var showNotificationDialog by remember { mutableStateOf(false) }

            MyApplicationTheme(darkTheme = isDark) {
                // Outer Sidebar/Drawer scaffold to support responsive tablet views
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet {
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                BrandLogo(showSlogan = false, modifier = Modifier.size(54.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = Translations.get("app_title", lang),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black,
                                        color = CrimsonRed
                                    )
                                    Text(
                                        text = Translations.get("slogan", lang),
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Divider()
                            Spacer(modifier = Modifier.height(8.dp))

                            // Drawer items for secondary pathways
                            NavigationDrawerItem(
                                icon = { Icon(Icons.Default.Info, contentDescription = null) },
                                label = { Text(Translations.get("about", lang)) },
                                selected = currentScreen == "about",
                                onClick = {
                                    viewModel.setScreen("about")
                                    scope.launch { drawerState.close() }
                                },
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                            )

                            NavigationDrawerItem(
                                icon = { Icon(Icons.Default.ContactSupport, contentDescription = null) },
                                label = { Text(Translations.get("contact", lang)) },
                                selected = currentScreen == "contact",
                                onClick = {
                                    viewModel.setScreen("contact")
                                    scope.launch { drawerState.close() }
                                },
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                            )

                            NavigationDrawerItem(
                                icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                                label = { Text(Translations.get("settings", lang)) },
                                selected = currentScreen == "settings",
                                onClick = {
                                    viewModel.setScreen("settings")
                                    scope.launch { drawerState.close() }
                                },
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                            )
                        }
                    }
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(LightBackground)
                                    .padding(horizontal = 20.dp, vertical = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(CrimsonRed, RoundedCornerShape(12.dp))
                                            .clickable { scope.launch { drawerState.open() } },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("🩸", fontSize = 20.sp)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(Translations.get("app_title", lang), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = LightOnSurface)
                                        Text(Translations.get("slogan", lang), fontSize = 10.sp, fontWeight = FontWeight.Medium, color = EmeraldGreen)
                                    }
                                }
                                
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(32.dp))
                                            .background(Color.White, RoundedCornerShape(32.dp))
                                            .clickable { viewModel.toggleLanguage() }
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(if (lang == "BN") "EN/বাংলা" else "BN/English", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                                    }
                                    
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(20.dp))
                                            .background(Color.White, RoundedCornerShape(20.dp))
                                            .clickable { showNotificationDialog = true },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("🔔", fontSize = 18.sp)
                                        if (notifications.isNotEmpty()) {
                                            Box(
                                                modifier = Modifier
                                                    .size(10.dp)
                                                    .align(Alignment.TopEnd)
                                                    .offset(x = (-8).dp, y = 8.dp)
                                                    .background(CrimsonRed, CircleShape)
                                                    .border(2.dp, Color.White, CircleShape)
                                            )
                                        }
                                    }
                                }
                            }
                        },
                        bottomBar = {
                            // High-contrast, easy bottom navigation
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.surface,
                                tonalElevation = 8.dp
                            ) {
                                NavigationBarItem(
                                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                                    label = { Text(Translations.get("home", lang), fontSize = 10.sp) },
                                    selected = currentScreen == "home",
                                    onClick = { viewModel.setScreen("home") }
                                )

                                NavigationBarItem(
                                    icon = { Icon(Icons.Default.Search, contentDescription = null) },
                                    label = { Text(Translations.get("find_blood", lang), fontSize = 10.sp) },
                                    selected = currentScreen == "find_blood",
                                    onClick = { viewModel.setScreen("find_blood") }
                                )

                                NavigationBarItem(
                                    icon = { Icon(Icons.Default.Favorite, contentDescription = null) },
                                    label = { Text(Translations.get("become_donor", lang), fontSize = 10.sp) },
                                    selected = currentScreen == "become_donor",
                                    onClick = { viewModel.setScreen("become_donor") }
                                )

                                NavigationBarItem(
                                    icon = { Icon(Icons.Default.ListAlt, contentDescription = null) },
                                    label = { Text(Translations.get("blood_requests", lang), fontSize = 10.sp) },
                                    selected = currentScreen == "requests",
                                    onClick = { viewModel.setScreen("requests") }
                                )

                                NavigationBarItem(
                                    icon = { Icon(Icons.Default.Dashboard, contentDescription = null) },
                                    label = { Text(Translations.get("dashboard", lang), fontSize = 10.sp) },
                                    selected = currentScreen == "dashboard" || currentScreen == "chat",
                                    onClick = { viewModel.setScreen("dashboard") }
                                )
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            AnimatedContent(
                                targetState = currentScreen,
                                transitionSpec = {
                                    fadeIn(animationSpec = tween(300)) togetherWith
                                            fadeOut(animationSpec = tween(200))
                                },
                                label = "screen_transition"
                            ) { screen ->
                                when (screen) {
                                    "home" -> HomeScreen(viewModel = viewModel)
                                    "find_blood" -> FindBloodScreen(viewModel = viewModel)
                                    "become_donor" -> BecomeDonorScreen(viewModel = viewModel)
                                    "requests" -> RequestsScreen(viewModel = viewModel)
                                    "dashboard" -> DashboardScreen(viewModel = viewModel)
                                    "chat" -> ChatScreen(viewModel = viewModel)
                                    "emergency" -> EmergencyRequestScreen(viewModel = viewModel)
                                    "about" -> AboutScreen(lang = lang)
                                    "contact" -> ContactScreen(lang = lang)
                                    "settings" -> SettingsScreen(viewModel = viewModel)
                                    else -> HomeScreen(viewModel = viewModel)
                                }
                            }
                        }
                    }
                }
            }

            // Real-Time Notification Alert List Dialog
            if (showNotificationDialog) {
                AlertDialog(
                    onDismissRequest = { showNotificationDialog = false },
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = Gold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(Translations.get("notifications", lang), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    },
                    text = {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (notifications.isEmpty()) {
                                Text(
                                    text = if (lang == "BN") "কোনো নতুন নোটিফিকেশন নেই।" else "No new notifications.",
                                    fontSize = 12.sp
                                )
                            } else {
                                notifications.forEach { item ->
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (item.type == "EMERGENCY") CrimsonRed.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant
                                        )
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Text(text = item.title, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(text = item.message, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showNotificationDialog = false }) {
                            Text(if (lang == "BN") "বন্ধ করুন" else "Close")
                        }
                    }
                )
            }
        }
    }
}
