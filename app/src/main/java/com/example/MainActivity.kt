package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AfroVibeViewModel

class MainActivity : ComponentActivity() {

    // Hoisted to the Activity so incoming share deep links (onNewIntent) can reach it.
    private val viewModel: AfroVibeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Handle the deep link that cold-started the app (if any).
        handleDeepLink(intent)
        setContent {
            MyApplicationTheme {
                val currentScreen by viewModel.currentScreen.collectAsState()
                val unreadCount by viewModel.unreadNotificationsCount.collectAsState()

                // Check if the current screen is one of our primary tab screens
                val isTabScreen = remember(currentScreen) {
                    currentScreen is Screen.HomeFeed ||
                    currentScreen is Screen.Discover ||
                    currentScreen is Screen.Camera ||
                    currentScreen is Screen.Inbox ||
                    currentScreen is Screen.Profile
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background,
                    bottomBar = {
                        if (isTabScreen) {
                            AfroBottomNavigationBar(
                                currentScreen = currentScreen,
                                unreadCount = unreadCount,
                                onTabSelected = { selectedScreen ->
                                    viewModel.navigateTo(selectedScreen)
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = if (isTabScreen) innerPadding.calculateBottomPadding() else 0.dp)
                    ) {
                        AnimatedContent(
                            targetState = currentScreen,
                            transitionSpec = {
                                fadeIn() togetherWith fadeOut()
                            },
                            label = "screen_transition"
                        ) { targetScreen ->
                            matchScreen(
                                screen = targetScreen,
                                viewModel = viewModel,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleDeepLink(intent)
    }

    /**
     * Resolves an incoming share/deep link and opens the matching video.
     * Supported forms:
     *   - afrovibe://video/{id}
     *   - https://afrovibe.app/video/{id}
     */
    private fun handleDeepLink(intent: Intent?) {
        val data: Uri = intent?.data ?: return
        val segments = data.pathSegments
        val videoId = when {
            data.host == "video" -> segments.firstOrNull()
            segments.size >= 2 && segments[segments.size - 2] == "video" -> segments.last()
            else -> null
        }
        if (!videoId.isNullOrBlank()) {
            viewModel.openVideoById(videoId)
        }
    }

    @Composable
    private fun matchScreen(
        screen: Screen,
        viewModel: AfroVibeViewModel,
        modifier: Modifier = Modifier
    ) {
        when (screen) {
            is Screen.Welcome -> {
                WelcomeScreen(
                    onNavigateToHome = { viewModel.navigateTo(Screen.HomeFeed) },
                    modifier = modifier
                )
            }
            is Screen.HomeFeed -> {
                HomeFeedScreen(
                    viewModel = viewModel,
                    onNavigateToLive = { viewModel.navigateTo(Screen.LiveSession) },
                    modifier = modifier
                )
            }
            is Screen.Discover -> {
                DiscoverScreen(
                    viewModel = viewModel,
                    modifier = modifier
                )
            }
            is Screen.Camera -> {
                CameraScreen(
                    viewModel = viewModel,
                    modifier = modifier
                )
            }
            is Screen.Inbox -> {
                InboxScreen(
                    viewModel = viewModel,
                    modifier = modifier
                )
            }
            is Screen.Profile -> {
                ProfileScreen(
                    viewModel = viewModel,
                    onNavigateToSettings = { viewModel.navigateTo(Screen.Settings) },
                    modifier = modifier
                )
            }
            is Screen.Settings -> {
                SettingsScreen(
                    viewModel = viewModel,
                    modifier = modifier
                )
            }
            is Screen.LiveSession -> {
                LiveSessionScreen(
                    viewModel = viewModel,
                    modifier = modifier
                )
            }
            is Screen.SoundDetail -> {
                SoundDetailScreen(
                    soundId = screen.soundId,
                    viewModel = viewModel,
                    modifier = modifier
                )
            }
            is Screen.CreatorProfile -> {
                // Renders custom creator profiles, simple redirection to current profile
                ProfileScreen(
                    viewModel = viewModel,
                    onNavigateToSettings = { viewModel.navigateTo(Screen.Settings) },
                    modifier = modifier
                )
            }
        }
    }
}

/**
 * Premium custom tab navigation bar designed specifically to mimic the AfroVibe layout theme.
 * Integrates active pill frames and a centralized decorated Red/Orange "+" button.
 */
@Composable
fun AfroBottomNavigationBar(
    currentScreen: Screen,
    onTabSelected: (Screen) -> Unit,
    modifier: Modifier = Modifier,
    unreadCount: Int = 0
) {
    Surface(
        color = Color(0xFF000000),
        tonalElevation = 8.dp,
        modifier = modifier
            .fillMaxWidth()
            .border(
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tab 1: Accueil (Home)
            AfroNavBarItem(
                isActive = currentScreen is Screen.HomeFeed,
                activeIcon = Icons.Filled.Home,
                inactiveIcon = Icons.Outlined.Home,
                label = "Accueil",
                testTag = "tab_accueil_btn",
                onClick = { onTabSelected(Screen.HomeFeed) }
            )

            // Tab 2: Découvrir (Discover)
            AfroNavBarItem(
                isActive = currentScreen is Screen.Discover,
                activeIcon = Icons.Filled.Search,
                inactiveIcon = Icons.Outlined.Search,
                label = "Découvrir",
                testTag = "tab_discover_btn",
                onClick = { onTabSelected(Screen.Discover) }
            )

            // Centered Glow "+" Button for camera / studio recording
            Box(
                modifier = Modifier
                    .width(54.dp)
                    .height(34.dp)
                    .clickable { onTabSelected(Screen.Camera) }
                    .testTag("tab_record_center_btn"),
                contentAlignment = Alignment.Center
            ) {
                // Cyan background offset to the left
                Box(
                    modifier = Modifier
                        .offset(x = (-4).dp)
                        .width(44.dp)
                        .height(30.dp)
                        .background(Color(0xFF32D7FF), RoundedCornerShape(8.dp))
                )
                // Pink-Red background offset to the right
                Box(
                    modifier = Modifier
                        .offset(x = 4.dp)
                        .width(44.dp)
                        .height(30.dp)
                        .background(Color(0xFFFF4B4B), RoundedCornerShape(8.dp))
                )
                // White foreground container in the center
                Box(
                    modifier = Modifier
                        .width(42.dp)
                        .height(30.dp)
                        .background(Color.White, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Studio",
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Tab 3: Boîte (Inbox)
            AfroNavBarItem(
                isActive = currentScreen is Screen.Inbox,
                activeIcon = Icons.Filled.Mail,
                inactiveIcon = Icons.Outlined.Mail,
                label = "Boîte",
                testTag = "tab_boite_btn",
                badgeCount = unreadCount,
                onClick = { onTabSelected(Screen.Inbox) }
            )

            // Tab 4: Profil (Profile)
            AfroNavBarItem(
                isActive = currentScreen is Screen.Profile,
                activeIcon = Icons.Filled.Person,
                inactiveIcon = Icons.Outlined.Person,
                label = "Profil",
                testTag = "tab_profil_btn",
                onClick = { onTabSelected(Screen.Profile) }
            )
        }
    }
}

@Composable
fun AfroNavBarItem(
    isActive: Boolean,
    activeIcon: ImageVector,
    inactiveIcon: ImageVector,
    label: String,
    testTag: String,
    onClick: () -> Unit,
    badgeCount: Int = 0
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .testTag(testTag)
            .clickable(onClick = onClick)
            .padding(vertical = 2.dp, horizontal = 8.dp)
    ) {
        BadgedBox(
            badge = {
                if (badgeCount > 0) {
                    Badge(
                        containerColor = Color(0xFFFF4B4B),
                        contentColor = Color.White,
                        modifier = Modifier.testTag("inbox_unread_badge")
                    ) {
                        Text(text = if (badgeCount > 9) "9+" else badgeCount.toString(), fontSize = 9.sp)
                    }
                }
            }
        ) {
        if (isActive) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFFFD700).copy(alpha = 0.15f))
                    .padding(horizontal = 18.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = activeIcon,
                    contentDescription = label,
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(22.dp)
                )
            }
        } else {
            Box(
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = inactiveIcon,
                    contentDescription = label,
                    tint = Color(0xFFFFFFFF).copy(alpha = 0.6f),
                    modifier = Modifier.size(22.dp)
                )
            }
        }
        }
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
            color = if (isActive) Color(0xFFFFD700) else Color(0xFFFFFFFF).copy(alpha = 0.6f),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
