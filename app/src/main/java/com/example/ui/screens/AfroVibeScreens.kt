package com.example.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import coil.compose.AsyncImage
import com.example.R
import com.example.data.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AfroVibeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 1. WELCOME SCREEN
 * Premium Onboarding with top/bottom tribal borders, centered brand logo and buttons
 */
@Composable
fun WelcomeScreen(
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AfroBackground)
    ) {
        // Top Tribal Pattern border
        TribalBorderPattern(
            modifier = Modifier.align(Alignment.TopCenter),
            height = 28
        )

        // Center Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Circular Logo frame
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .clip(CircleShape)
                    .border(
                        BorderStroke(
                            3.dp,
                            Brush.linearGradient(
                                colors = listOf(AfroPrimaryGold, AfroAccentPink, AfroSecondaryPurple)
                            )
                        ),
                        CircleShape
                    )
                    .background(Color(0xFF140D18)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.afrovibe_logo),
                    contentDescription = "Logo AfroVibe",
                    modifier = Modifier.size(220.dp).testTag("welcome_logo")
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Brand Text
            Text(
                text = "AFRO VIBE",
                fontSize = 44.sp,
                fontWeight = FontWeight.Black,
                color = AfroPrimaryGold,
                style = MaterialTheme.typography.headlineLarge.copy(
                    shadow = Shadow(
                        color = AfroAccentPink,
                        offset = Offset(2f, 2f),
                        blurRadius = 8f
                    )
                ),
                textAlign = TextAlign.Center
            )

            Text(
                text = "— DANSE TA CULTURE —",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = AfroAccentPink,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(top = 8.dp)
            )

            Text(
                text = "Le premier réseau social de danses, musiques et défis inspiré de l'incroyable richesse de notre culture locale.",
                fontSize = 14.sp,
                color = AfroTextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 16.dp, start = 8.dp, end = 8.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Pink-Red Gradient Connect button
            Button(
                onClick = onNavigateToHome,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("login_button"),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(),
                shape = RoundedCornerShape(28.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(AfroGradientPink, AfroGradientOrange)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Se connecter",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Outlined Custom Purple button
            OutlinedButton(
                onClick = onNavigateToHome,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("signup_button"),
                shape = RoundedCornerShape(28.dp),
                border = BorderStroke(1.5.dp, AfroSecondaryPurple),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = AfroTextLight)
            ) {
                Text(
                    text = "Créer un compte",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = AfroTextLight
                )
            }
        }

        // Bottom Tribal Pattern border
        TribalBorderPattern(
            modifier = Modifier.align(Alignment.BottomCenter),
            height = 28
        )
    }
}


/**
 * 2. HOME FEED SCREEN
 * Full Bleed Video feed carousel with swiping, interactive panels (Like, Comment, Share), spinning tracks.
 */
@Composable
fun HomeFeedScreen(
    viewModel: AfroVibeViewModel,
    onNavigateToLive: () -> Unit,
    modifier: Modifier = Modifier
) {
    val videos by viewModel.videos.collectAsState()
    val currentIndex by viewModel.currentVideoIndex.collectAsState()
    val isPlaying by viewModel.isVideoPlaying.collectAsState()
    val progress by viewModel.videoProgress.collectAsState()
    val context = LocalContext.current

    var showCommentSheet by remember { mutableStateOf(false) }

    if (videos.isNotEmpty()) {
        // Guard against transient out-of-range indices while the list mutates.
        val safeIndex = currentIndex.coerceIn(0, videos.size - 1)
        val currentVideo = videos[safeIndex]

        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            // Immersive Video Background simulation. Tapping toggles play/pause.
            Image(
                painter = painterResource(id = R.drawable.main_feed_dancer),
                contentDescription = "Video dance performance background",
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { viewModel.togglePlayPause() }
                    .testTag("video_playpause_surface"),
                contentScale = ContentScale.Crop,
                // Dye elements differently per item to simulate distinct video colors!
                colorFilter = when (safeIndex % 4) {
                    1 -> ColorFilter.tint(Color(0xFF32004F), BlendMode.Multiply)
                    2 -> ColorFilter.tint(Color(0xFF4C1800), BlendMode.Multiply)
                    3 -> ColorFilter.tint(Color(0xFF003D3D), BlendMode.Multiply)
                    else -> null
                }
            )

            // Outer dark filter gradient for readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.5f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.75f)
                            )
                        )
                    )
            )

            // Central Play indicator shown while the clip is paused
            androidx.compose.animation.AnimatedVisibility(
                visible = !isPlaying,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.Center)
            ) {
                Box(
                    modifier = Modifier
                        .size(84.dp)
                        .background(Color.Black.copy(alpha = 0.45f), CircleShape)
                        .testTag("video_paused_indicator"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = "Lecture",
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            // Top Feed Nav Bar: Live indicator Icon, Tabs (Pour toi, Abonnements), Search Icon
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Live Stream Indicator Button
                IconButton(
                    onClick = onNavigateToLive,
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.Tv,
                            contentDescription = "Accéder au LIVE",
                            tint = AfroAccentPink,
                            modifier = Modifier.size(22.dp)
                        )
                        // Tiny blinking LIVE indicator
                        var tick by remember { mutableStateOf(true) }
                        LaunchedEffect(Unit) {
                            while (true) {
                                delay(600)
                                tick = !tick
                            }
                        }
                        if (tick) {
                            Box(
                                modifier = Modifier
                                    .padding(bottom = 12.dp, start = 12.dp)
                                    .size(8.dp)
                                    .background(Color.Red, CircleShape)
                                    .align(Alignment.TopEnd)
                            )
                        }
                    }
                }

                // Suivi / Pour vous / Local tabs (Immersive UI Style)
                var selectedTab by remember { mutableStateOf(1) } // Default is 1 ("Pour vous")
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { selectedTab = 0 }
                    ) {
                        Text(
                            text = "Suivi",
                            color = if (selectedTab == 0) AfroPrimaryGold else AfroTextLight.copy(alpha = 0.6f),
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 17.sp
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Box(
                            modifier = Modifier
                                .width(20.dp)
                                .height(2.dp)
                                .background(if (selectedTab == 0) AfroPrimaryGold else Color.Transparent)
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { selectedTab = 1 }
                    ) {
                        Text(
                            text = "Pour vous",
                            color = if (selectedTab == 1) AfroPrimaryGold else AfroTextLight.copy(alpha = 0.6f),
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 17.sp
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Box(
                            modifier = Modifier
                                .width(28.dp)
                                .height(2.dp)
                                .background(if (selectedTab == 1) AfroPrimaryGold else Color.Transparent)
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { selectedTab = 2 }
                    ) {
                        Text(
                            text = "Local",
                            color = if (selectedTab == 2) AfroPrimaryGold else AfroTextLight.copy(alpha = 0.6f),
                            fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 17.sp
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Box(
                            modifier = Modifier
                                .width(20.dp)
                                .height(2.dp)
                                .background(if (selectedTab == 2) AfroPrimaryGold else Color.Transparent)
                        )
                    }
                }

                // Search Icon
                IconButton(
                    onClick = { viewModel.navigateTo(Screen.Discover) },
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search icon",
                        tint = AfroTextLight,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Carousel control indicators (Floating swiping guides on side overlay)
            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (currentIndex > 0) {
                    IconButton(
                        onClick = { viewModel.setCurrentVideoIndex(currentIndex - 1) },
                        modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(imageVector = Icons.Filled.ArrowUpward, contentDescription = "Précédent", tint = AfroPrimaryGold)
                    }
                }
                if (currentIndex < videos.size - 1) {
                    IconButton(
                        onClick = { viewModel.setCurrentVideoIndex(currentIndex + 1) },
                        modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(imageVector = Icons.Filled.ArrowDownward, contentDescription = "Suivant", tint = AfroPrimaryGold)
                    }
                }
            }

            // Right Utilities Side-Panel (Profiles, Likes, Comments, Share, Disc)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 100.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Creator Profile Bubble with "+" follow button (Immersive UI style)
                Box(contentAlignment = Alignment.BottomCenter) {
                    UserAvatarCanvas(
                        avatarId = currentVideo.creatorAvatarUrl,
                        modifier = Modifier
                            .size(54.dp)
                            .border(2.dp, Color.White, CircleShape)
                            .clickable { viewModel.navigateTo(Screen.CreatorProfile(currentVideo.creatorUsername)) }
                    )
                    Box(
                        modifier = Modifier
                            .offset(y = 8.dp)
                            .size(20.dp)
                            .background(Color(0xFFFF4B4B), CircleShape)
                            .border(1.5.dp, Color.Black, CircleShape)
                            .clickable { /* Follow Simulation */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Suivre",
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Like Button
                IconButtonWithLabel(
                    icon = if (currentVideo.isLiked) Icons.Filled.Favorite else Icons.Outlined.Favorite,
                    tint = if (currentVideo.isLiked) AfroAccentPink else Color.White,
                    label = formatCount(currentVideo.likesCount),
                    testTag = "like_click_btn",
                    onClick = { viewModel.toggleLikeVideo(currentVideo.id) }
                )

                // Comment Button
                IconButtonWithLabel(
                    icon = Icons.Outlined.Comment,
                    tint = Color.White,
                    label = formatCount(currentVideo.commentsCount),
                    testTag = "comment_sheet_btn",
                    onClick = { showCommentSheet = true }
                )

                // Bookmark / Favorite
                IconButtonWithLabel(
                    icon = if (currentVideo.isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.Bookmark,
                    tint = if (currentVideo.isBookmarked) AfroPrimaryGold else Color.White,
                    label = formatCount(currentVideo.bookmarksCount),
                    testTag = "bookmark_click_btn",
                    onClick = { viewModel.toggleBookmarkVideo(currentVideo.id) }
                )

                // Share Button (opens the native share sheet with a deep link)
                IconButtonWithLabel(
                    icon = Icons.Filled.Share,
                    tint = Color.White,
                    label = "Partager",
                    testTag = "share_click_btn",
                    onClick = {
                        shareVideoDeepLink(
                            context = context,
                            subject = "AfroVibe • @${currentVideo.creatorUsername}",
                            message = viewModel.shareMessageForVideo(currentVideo)
                        )
                    }
                )

                // Spinning vinyl record disc with concentric rings and a golden center core
                val infiniteTransition = rememberInfiniteTransition(label = "vinyl_rotate")
                val rotation by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 360f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(4000, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "vinyl_angle"
                )

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .rotate(rotation)
                        .clip(CircleShape)
                        .background(Color(0xFF1E1E24))
                        .border(4.dp, Color(0xFF16151A), CircleShape)
                        .clickable { viewModel.navigateTo(Screen.SoundDetail("snd_1")) },
                    contentAlignment = Alignment.Center
                ) {
                    // Concentric Groove elements
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .border(1.dp, Color.White.copy(alpha = 0.08f), CircleShape)
                    )
                    // Golden Center Core
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .background(Color(0xFFFFD700), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        // Spindle hole
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(Color.Black, CircleShape)
                        )
                    }
                }
            }

            // Bottom Text Info Deck (Creator Name, Verification, Description, Sound Name, Tags)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth(0.75f)
                    .padding(start = 16.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Creator Username row with TikTok style subscription button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "@${currentVideo.creatorUsername}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    if (currentVideo.isVerified) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = "Verified creator",
                            tint = Color(0xFF00FFCC),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    // Tiny high-fidelity "S'abonner" subscription button
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFFFD700), RoundedCornerShape(12.dp))
                            .clickable { /* S'abonner simulation */ }
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "S'abonner",
                            color = Color.Black,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Caption caption details
                Text(
                    text = currentVideo.caption,
                    fontSize = 14.sp,
                    color = AfroTextLight,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                // Cultured Tag list
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    currentVideo.tags.forEach { tag ->
                        Text(
                            text = tag,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = AfroPrimaryGold,
                            modifier = Modifier.clickable { viewModel.navigateTo(Screen.Discover) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Spinning sound loop track marquee
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.MusicNote,
                        contentDescription = "Icone Son",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = currentVideo.soundName,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.clickable { viewModel.navigateTo(Screen.SoundDetail("snd_1")) }
                    )
                }
            }

            // Playback progress bar pinned just above the bottom navigation
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(3.dp)
                    .testTag("video_progress_bar"),
                color = AfroPrimaryGold,
                trackColor = Color.White.copy(alpha = 0.25f)
            )

            // Real-Time Comments Bottom Sheet Overlay
            if (showCommentSheet) {
                CommentSheetContent(
                    videoItem = currentVideo,
                    onCommentSent = { text -> viewModel.addCommentToVideo(currentVideo.id, text) },
                    onDismiss = { showCommentSheet = false }
                )
            }
        }
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = AfroPrimaryGold)
        }
    }
}

@Composable
fun IconButtonWithLabel(
    icon: ImageVector,
    tint: Color,
    label: String,
    testTag: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                .testTag(testTag),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(26.dp)
            )
        }
        Text(
            text = label,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun CommentSheetContent(
    videoItem: VideoItem,
    onCommentSent: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var newCommentText by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable { onDismiss() } // Dim backdrop dismiss
            .background(Color.Black.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.65f)
                .clickable(
                    enabled = true,
                    onClick = {},
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) // consume taps
                .background(
                    color = AfroSurfaceDark,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                )
                .border(BorderStroke(1.dp, AfroPurpleBorder), RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .navigationBarsPadding()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Commentaires (${videoItem.comments.size})",
                    color = AfroTextLight,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = "Fermer", tint = AfroTextMuted)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Comments List
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (videoItem.comments.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(imageVector = Icons.Filled.ChatBubbleOutline, contentDescription = null, tint = AfroTextMuted, modifier = Modifier.size(48.dp))
                            Text(text = "Aucun commentaire pour l'instant. Soyez le premier !", color = AfroTextMuted, fontSize = 14.sp, modifier = Modifier.padding(top = 12.dp), textAlign = TextAlign.Center)
                        }
                    }
                } else {
                    items(videoItem.comments) { comment ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            UserAvatarCanvas(avatarId = comment.userAvatarUrl, modifier = Modifier.size(36.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "@${comment.userName}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AfroPrimaryGold
                                    )
                                    Text(
                                        text = comment.timeAgo,
                                        fontSize = 11.sp,
                                        color = AfroTextMuted
                                    )
                                }
                                Text(
                                    text = comment.content,
                                    fontSize = 13.sp,
                                    color = AfroTextLight,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Typing Field
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = newCommentText,
                    onValueChange = { newCommentText = it },
                    placeholder = { Text("Ajouter un commentaire sympa...", color = AfroTextMuted, fontSize = 14.sp) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("send_cmt_input"),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF1F1A24),
                        unfocusedContainerColor = Color(0xFF1F1A24),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = AfroTextLight,
                        unfocusedTextColor = AfroTextLight
                    ),
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 2,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = {
                        if (newCommentText.isNotBlank()) {
                            onCommentSent(newCommentText)
                            newCommentText = ""
                        }
                    })
                )

                IconButton(
                    onClick = {
                        if (newCommentText.isNotBlank()) {
                            onCommentSent(newCommentText)
                            newCommentText = ""
                        }
                    },
                    modifier = Modifier
                        .background(AfroAccentPink, CircleShape)
                        .size(46.dp)
                ) {
                    Icon(imageVector = Icons.Filled.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}


/**
 * 3. DISCOVER / SEARCH SCREEN
 * Structured Culture Challenges, search panel, horizontal creators to follow, and trending hashtags.
 */
@Composable
fun DiscoverScreen(
    viewModel: AfroVibeViewModel,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    val selectedFilter by viewModel.discoverFilter.collectAsState()
    val sounds by viewModel.sounds.collectAsState()
    val videos by viewModel.videos.collectAsState()

    val categories = listOf("Tout", "Danse", "Musique", "Défis", "Mode", "Humour")

    // The screen switches into "results" mode as soon as the user types a query
    // or picks a category other than "Tout".
    val isSearching = searchQuery.isNotBlank() || selectedFilter != "Tout"
    val videoResults = remember(searchQuery, selectedFilter, videos) {
        viewModel.searchVideos(searchQuery, selectedFilter)
    }
    val soundResults = remember(searchQuery, sounds) {
        viewModel.searchSounds(searchQuery)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(AfroBackground)
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Search Header
        item {
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Rechercher des vidéos, sons, défis...", color = AfroTextMuted, fontSize = 14.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .testTag("search_input_field"),
                leadingIcon = { Icon(imageVector = Icons.Filled.Search, contentDescription = null, tint = AfroTextMuted) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { searchQuery = "" },
                            modifier = Modifier.testTag("search_clear_btn")
                        ) {
                            Icon(imageVector = Icons.Filled.Close, contentDescription = "Effacer", tint = AfroTextMuted)
                        }
                    }
                },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = AfroSurfaceDark,
                    unfocusedContainerColor = AfroSurfaceDark,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = AfroTextLight,
                    unfocusedTextColor = AfroTextLight
                ),
                shape = RoundedCornerShape(24.dp)
            )
        }

        // Horizontal visual Categories tags (Danse, Musique, Défis...)
        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(categories) { category ->
                    val isSelected = selectedFilter == category
                    val categoryColor = when (category) {
                        "Danse" -> AfroAccentPink
                        "Musique" -> AfroSecondaryPurple
                        "Défis" -> AfroPrimaryGold
                        "Mode" -> Color(0xFFFF2D7A)
                        "Humour" -> Color(0xFFFFA502)
                        else -> Color(0xFF2d3436)
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) categoryColor else AfroSurfaceDark)
                            .border(
                                BorderStroke(1.dp, if (isSelected) Color.White else AfroPurpleBorder),
                                RoundedCornerShape(20.dp)
                            )
                            .clickable { viewModel.setDiscoverFilter(category) }
                            .padding(horizontal = 18.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = category,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else AfroTextLight
                        )
                    }
                }
            }
        }

        // Search / category results section (replaces the promo content when active)
        if (isSearching) {
            item {
                Text(
                    text = if (videoResults.isEmpty() && soundResults.isEmpty())
                        "Aucun résultat"
                    else
                        "${videoResults.size} vidéo(s) trouvée(s)",
                    color = AfroPrimaryGold,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.testTag("search_results_header")
                )
            }

            if (videoResults.isEmpty() && soundResults.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.SearchOff,
                            contentDescription = null,
                            tint = AfroTextMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "Essaie un autre mot-clé, un créateur ou un #hashtag.",
                            color = AfroTextMuted,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            if (soundResults.isNotEmpty()) {
                item {
                    Text(text = "Sons", color = AfroTextLight, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                items(soundResults) { sound ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(AfroSurfaceDark)
                            .border(BorderStroke(1.dp, AfroPurpleBorder), RoundedCornerShape(12.dp))
                            .clickable { viewModel.navigateTo(Screen.SoundDetail(sound.id)) }
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(AfroSecondaryPurple.copy(alpha = 0.25f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Filled.MusicNote, contentDescription = null, tint = AfroPrimaryGold, modifier = Modifier.size(20.dp))
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = sound.title, color = AfroTextLight, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(text = "${sound.artist} • ${sound.videoCount}", color = AfroTextMuted, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                        Icon(imageVector = Icons.Filled.ChevronRight, contentDescription = null, tint = AfroTextMuted, modifier = Modifier.size(18.dp))
                    }
                }
            }

            if (videoResults.isNotEmpty()) {
                item {
                    Text(text = "Vidéos", color = AfroTextLight, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                items(videoResults.chunked(3)) { rowVideos ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        rowVideos.forEach { video ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(0.7f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(AfroSurfaceDark)
                                    .clickable { viewModel.openVideoById(video.id) }
                                    .testTag("search_result_video")
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.main_feed_dancer),
                                    contentDescription = video.caption,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop,
                                    colorFilter = ColorFilter.tint(Color(0xFF2E1A3D), BlendMode.Multiply)
                                )
                                Column(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(6.dp)
                                ) {
                                    Text(
                                        text = "@${video.creatorUsername}",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Filled.Favorite, contentDescription = null, tint = AfroAccentPink, modifier = Modifier.size(11.dp))
                                        Text(text = " ${formatCount(video.likesCount)}", color = Color.White, fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                        // Pad incomplete rows so thumbnails keep their width.
                        repeat(3 - rowVideos.size) { Spacer(modifier = Modifier.weight(1f)) }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(100.dp)) }
        }

        // Beautiful Premium Banner "DÉFIE TA CULTURE"
        if (!isSearching) {
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box {
                    // Golden-pink radial neon sweep back
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFF3D164D), Color(0xFF4D3F15))
                                )
                            )
                    )

                    // Small geometric circles on backdrop
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(color = AfroAccentPink.copy(alpha = 0.15f), radius = 120.dp.toPx(), center = Offset(size.width, 0f))
                        drawCircle(color = AfroPrimaryGold.copy(alpha = 0.15f), radius = 160.dp.toPx(), center = Offset(0f, size.height))
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "DÉFIE TA CULTURE !",
                                color = AfroPrimaryGold,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Partage ton style local, Fais vibrer l'Afrique aux yeux du monde ! Rejoins le Top Challenge.",
                                color = AfroTextLight,
                                fontSize = 12.sp,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )
                            Button(
                                onClick = { viewModel.navigateTo(Screen.Camera) },
                                colors = ButtonDefaults.buttonColors(containerColor = AfroAccentPink),
                                shape = RoundedCornerShape(18.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 2.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Text("Participer", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }

                        // Right illustration graphic
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .border(2.dp, AfroPrimaryGold, CircleShape)
                                .background(Color.Black.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            // High contrast miniature logo
                            Image(
                                painter = painterResource(id = R.drawable.afrovibe_logo),
                                contentDescription = null,
                                modifier = Modifier.size(80.dp)
                            )
                        }
                    }
                }
            }
        }

        // Horizontal Creators to Follow ("Créateurs à suivre")
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Créateurs à suivre", color = AfroTextLight, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Voir tout", color = AfroPrimaryGold, fontSize = 12.sp, modifier = Modifier.clickable {})
                }
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    val mockFollows = listOf(
                        Triple("Queen_Lafo", "Danseuse Mapouka 🇨🇮", 2),
                        Triple("Coupé_King", "Abidjan Ambiance ⚡️", 3),
                        Triple("Aminata_Flow", "Dakar Model 🇸🇳", 4),
                        Triple("Soweto_Drum", "Soweto Bass 🇿🇦", 5)
                    )
                    items(mockFollows) { creator ->
                        Card(
                            modifier = Modifier
                                .width(130.dp)
                                .border(BorderStroke(1.dp, AfroPurpleBorder), RoundedCornerShape(12.dp)),
                            colors = CardDefaults.cardColors(containerColor = AfroSurfaceDark),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                UserAvatarCanvas(avatarId = creator.third, modifier = Modifier.size(48.dp))
                                Text(
                                    text = "@${creator.first}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AfroTextLight,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = creator.second,
                                    fontSize = 10.sp,
                                    color = AfroTextMuted,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Button(
                                    onClick = { },
                                    colors = ButtonDefaults.buttonColors(containerColor = AfroSecondaryPurple),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth().height(28.dp),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("Suivre", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Vertical Trend topics list ("Tendances")
        item {
            Column(modifier = Modifier.padding(bottom = 100.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Tendances du moment", color = AfroTextLight, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Tout voir", color = AfroPrimaryGold, fontSize = 12.sp, modifier = Modifier.clickable {})
                }

                // Trend item 1
                TrendItemRow(
                    hashtag = "AfroMoveChallenge",
                    views = "56.2M vues",
                    color = AfroAccentPink,
                    desc = "Rejoins le meilleur jeu de jambes Afrobeat de la quinzaine !"
                )
                
                // Trend item 2
                TrendItemRow(
                    hashtag = "MapoukaVibes",
                    views = "32.3M vues",
                    color = AfroSecondaryPurple,
                    desc = "Vibrations de tambours et danses traditionnelles de Côte d'Ivoire."
                )

                // Trend item 3
                TrendItemRow(
                    hashtag = "CoupéDécaléDance",
                    views = "18.4M vues",
                    color = AfroPrimaryGold,
                    desc = "La puissance du coupé-décalé fait chauffer l'Afrique."
                )
            }
        }
        } // end if (!isSearching)
    }
}

@Composable
fun TrendItemRow(
    hashtag: String,
    views: String,
    color: Color,
    desc: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AfroSurfaceDark)
            .border(BorderStroke(1.dp, AfroPurpleBorder), RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(color.copy(alpha = 0.2f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "#", color = color, fontSize = 20.sp, fontWeight = FontWeight.Black)
        }
        
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "#$hashtag", color = AfroTextLight, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(text = desc, color = AfroTextMuted, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(text = views, color = AfroPrimaryGold, fontSize = 10.sp, fontWeight = FontWeight.Medium)
        }

        Icon(
            imageVector = Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = AfroTextMuted,
            modifier = Modifier.size(18.dp)
        )
    }
}


/**
 * 4. CREATOR CAMERA STUDIO / CAPTUPER
 * Recording studio mechanics with active progression overlays, speed triggers, and sound attachment.
 */
@Composable
fun CameraScreen(
    viewModel: AfroVibeViewModel,
    modifier: Modifier = Modifier
) {
    val isRecording by viewModel.isRecording.collectAsState()
    val isRecordingPaused by viewModel.isRecordingPaused.collectAsState()
    val recordProgress by viewModel.recordingProgress.collectAsState()
    val selectedSound by viewModel.selectedSoundToRecord.collectAsState()
    val recordedVideo by viewModel.recordedVideoPath.collectAsState()

    var showPublishSheet by remember { mutableStateOf(false) }
    var speedSelected by remember { mutableStateOf("1x") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Viewfinder Simulation backdrop
        Box(modifier = Modifier.fillMaxSize()) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Background radial glow to look like a photographic visual space
                drawRect(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFF2E1A3D), Color(0xFF0C0A0D)),
                        center = center
                    )
                )
                // Diagonal crosshair lines for simulated viewfinder
                drawLine(
                    color = Color.White.copy(alpha = 0.15f),
                    start = Offset(0f, 0f),
                    end = Offset(size.width, size.height),
                    strokeWidth = 2f
                )
                drawLine(
                    color = Color.White.copy(alpha = 0.15f),
                    start = Offset(size.width, 0f),
                    end = Offset(0f, size.height),
                    strokeWidth = 2f
                )
            }

            // Beautiful dancing visual element inside camera to simulate recording
            if (isRecording) {
                var rotateAnim by remember { mutableStateOf(0f) }
                val transition = rememberInfiniteTransition()
                val pulseScale by transition.animateFloat(
                    initialValue = 0.95f,
                    targetValue = 1.05f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1200, easing = EaseInOutBack),
                        repeatMode = RepeatMode.Reverse
                    )
                )
                Box(
                    modifier = Modifier
                        .scale(pulseScale)
                        .align(Alignment.Center)
                        .size(160.dp)
                        .clip(CircleShape)
                        .border(BorderStroke(2.dp, AfroPrimaryGold), CircleShape)
                        .background(Color(0xFF3E164D).copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.MusicNote,
                        contentDescription = null,
                        tint = AfroAccentPink,
                        modifier = Modifier.size(54.dp)
                    )
                }
            } else {
                // Happy user graphic to record style
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(140.dp)
                        .clip(CircleShape)
                        .border(BorderStroke(1.5.dp, AfroTextMuted.copy(alpha = 0.4f)), CircleShape)
                        .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.CameraEnhance,
                        contentDescription = null,
                        tint = AfroPrimaryGold.copy(alpha = 0.8f),
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        }

        // Camera Header: Sound Select Pill
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Sound attach bar
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.Black.copy(alpha = 0.6f))
                    .border(BorderStroke(1.dp, AfroPrimaryGold), RoundedCornerShape(20.dp))
                    .clickable { viewModel.navigateTo(Screen.Discover) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(imageVector = Icons.Filled.MusicNote, contentDescription = null, tint = AfroPrimaryGold, modifier = Modifier.size(16.dp))
                    Text(
                        text = selectedSound?.let { "${it.title} - ${it.artist}" } ?: "Ajouter un son",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Camera Right sidebar menus: Retourner, Vitesse, Beauté, Filtres, Flash
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(top = 80.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CameraUtilityIconButton(icon = Icons.Filled.FlipCameraAndroid, label = "Retourner")
            CameraUtilityIconButton(icon = Icons.Filled.SlowMotionVideo, label = "Vitesse")
            CameraUtilityIconButton(icon = Icons.Filled.AutoAwesome, label = "Beauté")
            CameraUtilityIconButton(icon = Icons.Filled.FilterBAndW, label = "Filtres")
            CameraUtilityIconButton(icon = Icons.Filled.FlashOn, label = "Flash")
        }

        // Camera Bottom panel controller (Speed triggers, Red Record Button, Effets/Téléverser)
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 100.dp, start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Video record progress visual counter
            if (isRecording) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(alpha = 0.7f))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                if (isRecordingPaused) AfroTextMuted else Color.Red,
                                CircleShape
                            )
                    )
                    Text(
                        text = if (isRecordingPaused) "$recordProgress • EN PAUSE" else recordProgress,
                        color = AfroAccentPink,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            // Speed levels row: 0.3x, 0.5x, 1x, 2x, 3x (only if not recording)
            if (!isRecording) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Black.copy(alpha = 0.5f))
                        .padding(2.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    listOf("0.3x", "0.5x", "1x", "2x", "3x").forEach { speed ->
                        val isSel = speedSelected == speed
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSel) Color.White else Color.Transparent)
                                .clickable { speedSelected = speed }
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = speed,
                                color = if (isSel) Color.Black else Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Recording controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left slot: Effects when idle, Pause/Resume toggle while recording
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (isRecording) {
                        IconButton(
                            onClick = {
                                if (isRecordingPaused) viewModel.resumeRecording()
                                else viewModel.pauseRecording()
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                                .testTag("record_pause_btn")
                        ) {
                            Icon(
                                imageVector = if (isRecordingPaused) Icons.Filled.PlayArrow else Icons.Filled.Pause,
                                contentDescription = if (isRecordingPaused) "Reprendre" else "Pause",
                                tint = AfroPrimaryGold,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Text(
                            text = if (isRecordingPaused) "Reprendre" else "Pause",
                            color = Color.White,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    } else {
                        IconButton(
                            onClick = {},
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                        ) {
                            Icon(imageVector = Icons.Filled.Face, contentDescription = "Effets", tint = AfroPrimaryGold, modifier = Modifier.size(24.dp))
                        }
                        Text("Effets", color = Color.White, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
                    }
                }

                // Giant Pulse Record button (square stop icon while recording)
                IconButton(
                    onClick = {
                        if (isRecording) {
                            viewModel.stopAndSaveRecording()
                        } else {
                            viewModel.startRecordingSimulation()
                        }
                    },
                    modifier = Modifier
                        .size(86.dp)
                        .testTag("record_trigger_btn")
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .border(BorderStroke(4.dp, Color.White), CircleShape)
                            .padding(6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(if (isRecording) RoundedCornerShape(12.dp) else CircleShape)
                                .background(if (isRecording) AfroAccentPink else Color.Red)
                        )
                        if (isRecording) {
                            Icon(
                                imageVector = Icons.Filled.Stop,
                                contentDescription = "Arrêter",
                                tint = Color.White,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                }

                // Upload visual icon on right
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(
                        onClick = {
                            // Direct simulate recorded video upload
                            viewModel.stopAndSaveRecording()
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                    ) {
                        Icon(imageVector = Icons.Filled.CloudUpload, contentDescription = "Upload", tint = Color.White, modifier = Modifier.size(24.dp))
                    }
                    Text("Téléverser", color = Color.White, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
                }
            }
        }

        // Overlay Publish Sheet when a mock simulation video is ready!
        if (recordedVideo != null) {
            PublishSheetContent(
                selectedSoundTitle = selectedSound?.title ?: "Afro Vibe Original",
                onPublish = { caption ->
                    viewModel.publishRecordedVideo(caption)
                },
                onCancel = {
                    viewModel.cancelRecording()
                }
            )
        }
    }
}

@Composable
fun CameraUtilityIconButton(
    icon: ImageVector,
    label: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(
            onClick = {},
            modifier = Modifier
                .size(40.dp)
                .background(Color.Black.copy(alpha = 0.4f), CircleShape)
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = Color.White, modifier = Modifier.size(20.dp))
        }
        Text(text = label, color = Color.White, fontSize = 9.sp, modifier = Modifier.padding(top = 2.dp))
    }
}

@Composable
fun PublishSheetContent(
    selectedSoundTitle: String,
    onPublish: (String) -> Unit,
    onCancel: () -> Unit
) {
    var captionText by remember { mutableStateOf("") }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable { /* prevent background clicks */ }
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    color = AfroSurfaceDark,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                )
                .border(BorderStroke(1.dp, AfroPurpleBorder), RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .navigationBarsPadding()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "⚡️ Votre vidéo culturelle est prête !",
                color = AfroPrimaryGold,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Son attaché : $selectedSoundTitle",
                color = AfroTextMuted,
                fontSize = 14.sp
            )

            TextField(
                value = captionText,
                onValueChange = { captionText = it },
                placeholder = { Text("Ajoute de super hashtags locaux ! Ex: #AfroMoveChallenge #CotedIvoire #DanseAfro", color = AfroTextMuted, fontSize = 13.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("publish_caption_input"),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF1F1A24),
                    unfocusedContainerColor = Color(0xFF1F1A24),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = AfroTextLight,
                    unfocusedTextColor = AfroTextLight
                ),
                shape = RoundedCornerShape(12.dp),
                minLines = 3,
                maxLines = 4
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Cancel
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    border = BorderStroke(1.dp, AfroAccentPink)
                ) {
                    Text("Recommencer", color = AfroAccentPink, fontWeight = FontWeight.Bold)
                }

                // Publish
                Button(
                    onClick = { onPublish(captionText) },
                    modifier = Modifier
                        .weight(1.5f)
                        .height(52.dp)
                        .testTag("publish_finish_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = AfroPrimaryGold),
                    shape = RoundedCornerShape(26.dp)
                ) {
                    Text("Publier ma vibe !", color = AfroBackground, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}


/**
 * 5. INBOX SCREEN / NOTIFICATIONS
 * Horizontal circle Stories of friends, vertical core notifications list (Likes, messages...), 
 * plus live typing chat replies inside messages details simulation overlay.
 */
@Composable
fun InboxScreen(
    viewModel: AfroVibeViewModel,
    modifier: Modifier = Modifier
) {
    val notifications by viewModel.notifications.collectAsState()
    val unreadCount by viewModel.unreadNotificationsCount.collectAsState()
    val selectedChatSender by viewModel.selectedChatSender.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()

    var replyText by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AfroBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Boîte de réception",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = AfroTextLight
                    )
                    if (unreadCount > 0) {
                        Text(
                            text = "$unreadCount non lue(s)",
                            fontSize = 12.sp,
                            color = AfroAccentPink,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (unreadCount > 0) {
                        TextButton(
                            onClick = { viewModel.markAllNotificationsRead() },
                            modifier = Modifier.testTag("mark_all_read_btn")
                        ) {
                            Text(text = "Tout lire", color = AfroPrimaryGold, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    IconButton(onClick = {}) {
                        Icon(imageVector = Icons.Filled.Edit, contentDescription = "Nouveau Message", tint = AfroPrimaryGold)
                    }
                }
            }

            // Stories horizontal slider list
            Text(
                text = "Activités Récentes",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = AfroPrimaryGold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                val mockStories = listOf(
                    Triple("Nouvelles", 1, true),
                    Triple("Queen_Lafo", 2, false),
                    Triple("Mister_K", 3, false),
                    Triple("AfroBeats...", 4, false),
                    Triple("Soweto_D", 5, false)
                )
                items(mockStories) { story ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            if (!story.third) {
                                viewModel.selectChatSender(story.first)
                            }
                        }
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(62.dp)
                                .clip(CircleShape)
                                .border(
                                    BorderStroke(
                                        2.5.dp,
                                        Brush.sweepGradient(
                                            colors = listOf(AfroAccentPink, AfroPrimaryGold, AfroSecondaryPurple, AfroAccentPink)
                                        )
                                    ),
                                    CircleShape
                                )
                        ) {
                            UserAvatarCanvas(avatarId = story.second, modifier = Modifier.size(54.dp))
                        }
                        Text(
                            text = story.first,
                            fontSize = 11.sp,
                            color = AfroTextLight,
                            modifier = Modifier.padding(top = 4.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Divider(color = AfroPurpleBorder, thickness = 1.dp)

            // Notifications/Activity list
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(notifications) { item ->
                    NotificationRowItem(
                        notification = item,
                        onClick = { viewModel.onNotificationClicked(item) }
                    )
                }
            }
        }

        // Simulation Chat Overlay Panel when a specific chat message is clicked!
        if (selectedChatSender != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { viewModel.selectChatSender(null) } // dismiss
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .fillMaxHeight(0.8f)
                        .clickable(enabled = true, onClick = {}, interactionSource = remember { MutableInteractionSource() }, indication = null) // consume
                        .background(
                            color = AfroSurfaceDark,
                            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                        )
                        .border(BorderStroke(1.dp, AfroPurpleBorder), RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                        .navigationBarsPadding()
                ) {
                    // Chat Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            UserAvatarCanvas(avatarId = 3, modifier = Modifier.size(38.dp))
                            Column {
                                Text(text = "@$selectedChatSender", color = AfroTextLight, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                Text(text = "En ligne • Simulation", color = Color(0xFF00FFCC), fontSize = 11.sp)
                            }
                        }
                        IconButton(onClick = { viewModel.selectChatSender(null) }) {
                            Icon(imageVector = Icons.Filled.Close, contentDescription = "Close", tint = AfroTextLight)
                        }
                    }

                    Divider(color = AfroPurpleBorder)

                    // Chat messages stream
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(chatMessages) { message ->
                            val isMe = message.second
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = if (isMe) Alignment.CenterEnd else Alignment.CenterStart
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(
                                            RoundedCornerShape(
                                                topStart = 16.dp,
                                                topEnd = 16.dp,
                                                bottomStart = if (isMe) 16.dp else 4.dp,
                                                bottomEnd = if (isMe) 4.dp else 16.dp
                                            )
                                        )
                                        .background(if (isMe) AfroPrimaryGold else AfroBackground)
                                        .border(BorderStroke(1.dp, if (isMe) Color.Transparent else AfroPurpleBorder), RoundedCornerShape(16.dp))
                                        .padding(horizontal = 14.dp, vertical = 10.dp)
                                ) {
                                    Text(
                                        text = message.first,
                                        color = if (isMe) AfroBackground else AfroTextLight,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }

                    // Bottom typing panel
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .imePadding()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = replyText,
                            onValueChange = { replyText = it },
                            placeholder = { Text("Écrire ta réponse ici...", color = AfroTextMuted, fontSize = 13.sp) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("reply_input_field"),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF1F1A24),
                                unfocusedContainerColor = Color(0xFF1F1A24),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedTextColor = AfroTextLight,
                                unfocusedTextColor = AfroTextLight
                            ),
                            shape = RoundedCornerShape(24.dp),
                            maxLines = 1,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                            keyboardActions = KeyboardActions(onSend = {
                                if (replyText.isNotBlank()) {
                                    viewModel.sendMessage(replyText)
                                    replyText = ""
                                }
                            })
                        )

                        IconButton(
                            onClick = {
                                if (replyText.isNotBlank()) {
                                    viewModel.sendMessage(replyText)
                                    replyText = ""
                                }
                            },
                            modifier = Modifier
                                .size(46.dp)
                                .background(AfroPrimaryGold, CircleShape)
                        ) {
                            Icon(imageVector = Icons.Filled.Send, contentDescription = "Envoi", tint = AfroBackground, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationRowItem(
    notification: AfroNotification,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val contextColor = when (notification.type) {
        NotificationType.LIKE -> AfroAccentPink
        NotificationType.COMMENT -> AfroSecondaryPurple
        NotificationType.MESSAGE -> AfroPrimaryGold
        else -> Color(0xFF00FFCC)
    }

    // Unread notifications get a subtle gold tint and a highlighted border.
    val rowBackground = if (notification.isRead) AfroSurfaceDark else AfroPrimaryGold.copy(alpha = 0.08f)
    val rowBorder = if (notification.isRead) AfroPurpleBorder else AfroPrimaryGold.copy(alpha = 0.5f)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(rowBackground)
            .border(BorderStroke(if (notification.isRead) 0.5.dp else 1.dp, rowBorder), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(contentAlignment = Alignment.BottomEnd) {
            UserAvatarCanvas(avatarId = notification.senderAvatarUrl, modifier = Modifier.size(44.dp))
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(contextColor, CircleShape)
                    .border(BorderStroke(1.dp, Color.Black), CircleShape)
            )
        }
        
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "@${notification.senderName}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = AfroTextLight
                )
                Text(
                    text = notification.timeAgo,
                    fontSize = 11.sp,
                    color = AfroTextMuted
                )
            }
            Text(
                text = notification.detailText,
                fontSize = 13.sp,
                color = AfroTextMuted,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        if (notification.type == NotificationType.MESSAGE) {
            IconButton(onClick = onClick) {
                Icon(imageVector = Icons.Filled.Chat, contentDescription = "Chat", tint = AfroPrimaryGold, modifier = Modifier.size(18.dp))
            }
        } else if (!notification.isRead) {
            // Unread dot indicator
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(AfroAccentPink, CircleShape)
            )
        }
    }
}


/**
 * 6. CREATOR / USER PROFILE SCREEN
 * Standard Stats panels, follow counters, interactive grid categories, custom play numbers.
 */
@Composable
fun ProfileScreen(
    viewModel: AfroVibeViewModel,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUserProfile.collectAsState()
    val allVideos by viewModel.videos.collectAsState()

    // Filter videos created by current user
    val userVideos = remember(allVideos) {
        allVideos.filter { it.creatorUsername == currentUser.username }
    }

    var selectedTabIdx by remember { mutableStateOf(0) } // Grid, Likes, Favorites

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AfroBackground)
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        // App top header bar with username and parameters icon
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.width(48.dp)) // horizontal alignment space placeholder
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "@${currentUser.username}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = AfroTextLight
                )
                if (currentUser.isVerified) {
                    Icon(imageVector = Icons.Filled.CheckCircle, contentDescription = null, tint = Color(0xFF00FFCC), modifier = Modifier.size(14.dp))
                }
            }

            IconButton(onClick = onNavigateToSettings) {
                Icon(imageVector = Icons.Filled.Settings, contentDescription = "Menu Options", tint = AfroPrimaryGold)
            }
        }

        // Large user custom avatar circle
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .border(BorderStroke(2.dp, AfroPrimaryGold), CircleShape)
            ) {
                UserAvatarCanvas(avatarId = 1, modifier = Modifier.size(86.dp))
            }

            Text(
                text = currentUser.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = AfroTextLight
            )

            // Statistics panels: Abonnements, Abonnés, J'aime
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProfileStatColumn(count = currentUser.following, label = "Abonnements")
                ProfileStatColumn(count = currentUser.followers, label = "Abonnés")
                ProfileStatColumn(count = currentUser.totalLikes, label = "J'aime")
            }

            // Two core action buttons: Edit bio and save bookmark
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { /* Simulation edit bio */ },
                    modifier = Modifier.weight(1f).height(42.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AfroSurfaceDark),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, AfroPurpleBorder)
                ) {
                    Text("Éditer le profil", color = AfroTextLight, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }

                IconButton(
                    onClick = { /* Save favorites */ },
                    modifier = Modifier
                        .size(42.dp)
                        .background(AfroSurfaceDark, RoundedCornerShape(8.dp))
                        .border(BorderStroke(1.dp, AfroPurpleBorder), RoundedCornerShape(8.dp))
                ) {
                    Icon(imageVector = Icons.Outlined.BookmarkBorder, contentDescription = "Bookmarks", tint = AfroPrimaryGold)
                }
            }

            // Bio narrative text
            Text(
                text = currentUser.bio,
                fontSize = 13.sp,
                color = AfroTextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Grid selection categories (Dances grid, likes list, favorites)
        TabRow(
            selectedTabIndex = selectedTabIdx,
            containerColor = Color.Transparent,
            contentColor = AfroPrimaryGold,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIdx]),
                    color = AfroPrimaryGold
                )
            },
            divider = { Divider(color = AfroPurpleBorder) }
        ) {
            Tab(
                selected = selectedTabIdx == 0,
                onClick = { selectedTabIdx = 0 },
                icon = { Icon(imageVector = Icons.Filled.GridView, contentDescription = "Vidéos", tint = if (selectedTabIdx == 0) AfroPrimaryGold else AfroTextMuted) }
            )
            Tab(
                selected = selectedTabIdx == 1,
                onClick = { selectedTabIdx = 1 },
                icon = { Icon(imageVector = Icons.Filled.Favorite, contentDescription = "Aimés", tint = if (selectedTabIdx == 1) AfroPrimaryGold else AfroTextMuted) }
            )
            Tab(
                selected = selectedTabIdx == 2,
                onClick = { selectedTabIdx = 2 },
                icon = { Icon(imageVector = Icons.Filled.Lock, contentDescription = "Privé", tint = if (selectedTabIdx == 2) AfroPrimaryGold else AfroTextMuted) }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Responsive grid view of video clips
        if (selectedTabIdx == 0) {
            if (userVideos.isEmpty()) {
                Column(
                    modifier = Modifier.weight(1f).fillMaxWidth().padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(imageVector = Icons.Filled.VideoCameraBack, contentDescription = null, tint = AfroTextMuted, modifier = Modifier.size(54.dp))
                    Text("Aucune vidéo publiée pour l'instant.", color = AfroTextMuted, fontSize = 14.sp, modifier = Modifier.padding(top = 12.dp))
                    Button(
                        onClick = { viewModel.navigateTo(Screen.Camera) },
                        colors = ButtonDefaults.buttonColors(containerColor = AfroAccentPink),
                        modifier = Modifier.padding(top = 12.dp)
                    ) {
                        Text("Enregistrer une vibe !", color = Color.White)
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.weight(1f).padding(bottom = 90.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(userVideos) { video ->
                        Box(
                            modifier = Modifier
                                .aspectRatio(0.75f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(AfroSurfaceDark)
                                .clickable {
                                    // Make this video active in feed and go to home feed
                                    val idx = allVideos.indexOf(video)
                                    if (idx >= 0) {
                                        viewModel.setCurrentVideoIndex(idx)
                                        viewModel.navigateTo(Screen.HomeFeed)
                                    }
                                }
                        ) {
                            // Video backdrop preview placeholder
                            Image(
                                painter = painterResource(id = R.drawable.main_feed_dancer),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            // Play count label at bottom-left
                            Row(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(6.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                Text(text = "12.5K", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        } else {
            // Fake empty state for other grid tabs
            Column(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(imageVector = Icons.Filled.Lock, contentDescription = null, tint = AfroTextMuted, modifier = Modifier.size(48.dp))
                Text(text = "Contenu privé et protégé", color = AfroTextMuted, fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp))
            }
        }
    }
}

@Composable
fun ProfileStatColumn(
    count: String,
    label: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = count, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AfroTextLight)
        Text(text = label, fontSize = 12.sp, color = AfroTextMuted)
    }
}


/**
 * 7. LIVE SESSION SCREEN
 * Live streaming with active ticker comments stream, viewer counting, and flying hearts overlay.
 */
@Composable
fun LiveSessionScreen(
    viewModel: AfroVibeViewModel,
    modifier: Modifier = Modifier
) {
    val comments by viewModel.liveComments.collectAsState()
    val viewers by viewModel.liveViewerCount.collectAsState()
    val flyingHearts by viewModel.flyingHearts.collectAsState()

    var liveCommentText by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // High-Quality Live stream backdrop
        Box(modifier = Modifier.fillMaxSize()) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Energetic Stage neon gradient sweeps
                drawRect(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF3B0066), Color(0xFF140026), Color(0xFF000000))
                    )
                )
                // Draw warm spotlight beam
                drawPath(Path().apply {
                    moveTo(size.width / 2f, 0f)
                    lineTo(size.width * 0.9f, size.height)
                    lineTo(size.width * 0.1f, size.height)
                    close()
                }, brush = Brush.verticalGradient(listOf(AfroPrimaryGold.copy(alpha = 0.2f), Color.Transparent)))
            }

            // Artistic neon live singer profile drawing inside spotlight
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(CircleShape)
                        .border(BorderStroke(3.dp, AfroPrimaryGold), CircleShape)
                        .background(Color.Black.copy(alpha = 0.6f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Mic,
                        contentDescription = null,
                        tint = AfroAccentPink,
                        modifier = Modifier.size(72.dp)
                    )
                }
                
                Text(
                    text = "Ambiance LIVE direct !",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = AfroTextLight
                )
                
                Text(
                    text = "Djembe Beats Studio Abidjan",
                    fontSize = 14.sp,
                    color = AfroPrimaryGold,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Live Header: Creator avatar, viewer counter tag, close trigger
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Profile tag
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                UserAvatarCanvas(avatarId = 4, modifier = Modifier.size(32.dp))
                Column {
                    Text(text = "Ama_225", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(6.dp).background(Color.Red, CircleShape))
                        Text(text = " LIVE", fontSize = 9.sp, color = AfroAccentPink, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 2.dp))
                    }
                }
            }

            // Center Viewers tag & Close button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Red)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = "${viewers} spectateurs", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                IconButton(
                    onClick = { viewModel.navigateBack() },
                    modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = "Quitter le LIVE", tint = Color.White)
                }
            }
        }

        // Real-Time ticking Comments stream on bottom part
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(0.7f)
                .fillMaxHeight(0.35f)
                .padding(start = 16.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    reverseLayout = false
                ) {
                    items(comments) { comment ->
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.Black.copy(alpha = 0.5f))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "@${comment.userName}:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = AfroPrimaryGold
                            )
                            Text(
                                text = comment.content,
                                fontSize = 12.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // Flying hearts rendering overlay inside box
        LiveHeartRain(
            heartSeeds = flyingHearts,
            onHeartFinished = { heartId -> viewModel.consumeLiveHeart(heartId) }
        )

        // Bottom input field row + Heart pulse button on right
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp)
                .imePadding(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = liveCommentText,
                onValueChange = { liveCommentText = it },
                placeholder = { Text("Écrire au LIVE...", color = AfroTextMuted, fontSize = 13.sp) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("live_comment_input"),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Black.copy(alpha = 0.7f),
                    unfocusedContainerColor = Color.Black.copy(alpha = 0.7f),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(24.dp),
                maxLines = 1,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = {
                    if (liveCommentText.isNotBlank()) {
                        viewModel.addCommentToVideo("vid_1", liveCommentText) // Sim in feed
                        liveCommentText = ""
                    }
                })
            )

            // Pulse Heart reactor
            IconButton(
                onClick = { viewModel.tapLiveHeart() },
                modifier = Modifier
                    .size(48.dp)
                    .background(AfroAccentPink, CircleShape)
                    .testTag("live_heart_tap")
            ) {
                Icon(imageVector = Icons.Filled.Favorite, contentDescription = "Love", tint = Color.White, modifier = Modifier.size(24.dp))
            }
        }
    }
}


/**
 * 8. SOUND SELECTION PAGE
 * Visual disc headers, list of trending sound bites to attach.
 */
@Composable
fun SoundDetailScreen(
    soundId: String,
    viewModel: AfroVibeViewModel,
    modifier: Modifier = Modifier
) {
    val sounds by viewModel.sounds.collectAsState()
    val sound = sounds.firstOrNull { it.id == soundId } ?: sounds[0]

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AfroBackground)
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        // Top Back Navigation bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(onClick = { viewModel.navigateBack() }) {
                Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back", tint = AfroTextLight)
            }
            Text(
                text = "Track Info",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = AfroTextLight
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Center visual sound profile details
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rotating track icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(BorderStroke(1.dp, AfroPrimaryGold), RoundedCornerShape(12.dp))
                    .background(AfroSurfaceDark),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.MusicNote,
                    contentDescription = null,
                    tint = AfroPrimaryGold,
                    modifier = Modifier.size(36.dp)
                )
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = sound.title, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = AfroTextLight)
                Text(text = "par : ${sound.artist}", fontSize = 14.sp, color = AfroPrimaryGold, fontWeight = FontWeight.Medium)
                Text(text = "${sound.videoCount} • Sons originaux", fontSize = 12.sp, color = AfroTextMuted)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Large "Utiliser ce son" button
        Button(
            onClick = {
                viewModel.setSoundToRecord(sound)
                viewModel.navigateTo(Screen.Camera)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("use_sound_now_btn"),
            colors = ButtonDefaults.buttonColors(containerColor = AfroAccentPink),
            shape = RoundedCornerShape(27.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(imageVector = Icons.Filled.Camera, contentDescription = null, tint = Color.White)
                Text(text = "Utiliser ce son dans l'Appareil", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(text = "Défis qui utilisent ce son :", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = AfroPrimaryGold)

        // Mock videos grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.weight(1f).padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(6) { index ->
                Box(
                    modifier = Modifier
                        .aspectRatio(0.75f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(AfroSurfaceDark)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.main_feed_dancer),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        colorFilter = ColorFilter.tint(Color(0xFF2E1A3D), BlendMode.Multiply)
                    )
                    Row(
                        modifier = Modifier.align(Alignment.BottomStart).padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                        Text("12.${index}K", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}


/**
 * 9. PLUS / SETTINGS / OPTIONS SCREEN
 * Options panel, customizable dark mode selector, bottom visual AfroVibe canvas.
 */
@Composable
fun SettingsScreen(
    viewModel: AfroVibeViewModel,
    modifier: Modifier = Modifier
) {
    var isDarkModeChecked by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AfroBackground)
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Settings Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(onClick = { viewModel.navigateBack() }) {
                Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Retour", tint = AfroTextLight)
            }
            Text(
                text = "Plus d'options",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = AfroTextLight
            )
        }

        // List settings rows
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AfroSurfaceDark),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, AfroPurpleBorder)
        ) {
            Column {
                SettingsOptionRow(icon = Icons.Outlined.Lock, title = "Paramètres et confidentialité")
                Divider(color = AfroPurpleBorder)
                SettingsOptionRow(icon = Icons.Outlined.AccountCircle, title = "Gérer le compte")
                Divider(color = AfroPurpleBorder)
                SettingsOptionRow(icon = Icons.Outlined.Notifications, title = "Notifications")
                Divider(color = AfroPurpleBorder)
                SettingsOptionRow(icon = Icons.Outlined.Language, title = "Langue", value = "Français")
                Divider(color = AfroPurpleBorder)
                
                // Dark mode Switch row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Outlined.DarkMode, contentDescription = null, tint = AfroPrimaryGold)
                        Text(text = "Mode sombre", color = AfroTextLight, fontSize = 14.sp)
                    }
                    Switch(
                        checked = isDarkModeChecked,
                        onCheckedChange = { isDarkModeChecked = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = AfroPrimaryGold,
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = Color.DarkGray
                        )
                    )
                }
                
                Divider(color = AfroPurpleBorder)
                SettingsOptionRow(icon = Icons.Outlined.Help, title = "Centre d'aide")
                Divider(color = AfroPurpleBorder)
                SettingsOptionRow(icon = Icons.Outlined.Info, title = "À propos")
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Beautiful visual banner featuring the AfroVibe logo
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 100.dp)
                .height(110.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF2C1542), Color(0xFF4C3015))
                            )
                        )
                )
                
                Row(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = "AFRO VIBE", color = AfroPrimaryGold, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Danse Ta Culture v1.0", color = AfroTextMuted, fontSize = 12.sp)
                        Text(text = "Fait avec 🧡 en Afrique", color = AfroAccentPink, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Image(
                        painter = painterResource(id = R.drawable.afrovibe_logo),
                        contentDescription = null,
                        modifier = Modifier.size(76.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsOptionRow(
    icon: ImageVector,
    title: String,
    value: String? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {}
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = AfroPrimaryGold)
            Text(text = title, color = AfroTextLight, fontSize = 14.sp)
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (value != null) {
                Text(text = value, color = AfroTextMuted, fontSize = 12.sp)
            }
            Icon(imageVector = Icons.Filled.ChevronRight, contentDescription = null, tint = AfroTextMuted, modifier = Modifier.size(16.dp))
        }
    }
}


/**
 * Opens the Android share sheet so the active video can be shared with a deep link.
 * The link points at the https/afrovibe scheme handled by MainActivity's intent-filters.
 */
fun shareVideoDeepLink(context: Context, subject: String, message: String) {
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, message)
    }
    val chooser = Intent.createChooser(sendIntent, "Partager cette vibe via").apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(chooser)
}

// Helper for numbers format (e.g. 12500 -> "12.5K")
fun formatCount(count: Int): String {
    return if (count >= 1000) {
        val k = count / 1000f
        String.format("%.1fK", k).replace(",0", "")
    } else {
        count.toString()
    }
}
