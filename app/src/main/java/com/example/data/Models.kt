package com.example.data

import androidx.compose.runtime.Immutable

@Immutable
data class VideoItem(
    val id: String,
    val creatorName: String,
    val creatorUsername: String,
    val creatorAvatarUrl: Int, // local drawable or built-in avatar ID representation
    val caption: String,
    val tags: List<String>,
    val soundName: String,
    val soundArtist: String,
    val likesCount: Int,
    val commentsCount: Int,
    val bookmarksCount: Int,
    val isLiked: Boolean = false,
    val isBookmarked: Boolean = false,
    val isVerified: Boolean = false,
    val videoRes: Int, // local backdrop image resource (e.g. main_feed_dancer or generated mockups)
    val comments: List<Comment> = emptyList()
)

@Immutable
data class Comment(
    val id: String,
    val userName: String,
    val userAvatarUrl: Int,
    val content: String,
    val timeAgo: String
)

@Immutable
data class Creator(
    val id: String,
    val name: String,
    val username: String,
    val avatarUrl: Int,
    val isVerified: Boolean = false,
    val followers: String,
    val following: String,
    val totalLikes: String,
    val bio: String,
    val videos: List<VideoItem> = emptyList()
)

@Immutable
data class AfroNotification(
    val id: String,
    val type: NotificationType,
    val senderName: String,
    val senderAvatarUrl: Int,
    val detailText: String,
    val timeAgo: String,
    val isStory: Boolean = false
)

enum class NotificationType {
    LIKE, COMMENT, SYSTEM, MESSAGE, CHALLENGE
}

@Immutable
data class AfroSound(
    val id: String,
    val title: String,
    val artist: String,
    val videoCount: String,
    val coverImg: Int,
    val isFavorite: Boolean = false
)

sealed interface Screen {
    object Welcome : Screen
    object HomeFeed : Screen
    object Discover : Screen
    object Camera : Screen
    object Inbox : Screen
    object Profile : Screen
    object Settings : Screen
    data class SoundDetail(val soundId: String) : Screen
    data class CreatorProfile(val username: String) : Screen
    object LiveSession : Screen
}
