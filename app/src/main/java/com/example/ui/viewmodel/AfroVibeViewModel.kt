package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AfroVibeViewModel : ViewModel() {

    companion object {
        // Simulated duration of a single feed video clip.
        private const val VIDEO_DURATION_MS = 8000L
        private const val PLAYBACK_TICK_MS = 50L
        private const val MAX_RECORDING_SECONDS = 15
    }

    // Current global screen state
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Welcome)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // Navigation backstack support
    private val backstack = mutableListOf<Screen>()

    // Current category filter in Discover ("Danse", "Musique", "Défis", "Mode", "Humour")
    private val _discoverFilter = MutableStateFlow("Tout")
    val discoverFilter: StateFlow<String> = _discoverFilter.asStateFlow()

    // Videos Flow
    private val _videos = MutableStateFlow<List<VideoItem>>(emptyList())
    val videos: StateFlow<List<VideoItem>> = _videos.asStateFlow()

    // Current active playing video index in feed
    private val _currentVideoIndex = MutableStateFlow(0)
    val currentVideoIndex: StateFlow<Int> = _currentVideoIndex.asStateFlow()

    // Whether the active feed video is currently playing (false = paused).
    private val _isVideoPlaying = MutableStateFlow(true)
    val isVideoPlaying: StateFlow<Boolean> = _isVideoPlaying.asStateFlow()

    // Playback progress of the active video in the range 0f..1f.
    private val _videoProgress = MutableStateFlow(0f)
    val videoProgress: StateFlow<Float> = _videoProgress.asStateFlow()

    // Sounds list
    private val _sounds = MutableStateFlow<List<AfroSound>>(emptyList())
    val sounds: StateFlow<List<AfroSound>> = _sounds.asStateFlow()

    // Notifications and Stories list
    private val _notifications = MutableStateFlow<List<AfroNotification>>(emptyList())
    val notifications: StateFlow<List<AfroNotification>> = _notifications.asStateFlow()

    // Count of unread notifications, used for the Inbox tab badge.
    val unreadNotificationsCount: StateFlow<Int> = _notifications
        .map { list -> list.count { !it.isRead } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    // Simulated Chat state inside inbox
    private val _selectedChatSender = MutableStateFlow<String?>(null)
    val selectedChatSender: StateFlow<String?> = _selectedChatSender.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<Pair<String, Boolean>>>(emptyList()) // text, isUserSent
    val chatMessages: StateFlow<List<Pair<String, Boolean>>> = _chatMessages.asStateFlow()

    // Camera states
    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    // Whether an in-progress recording is paused (recording can be resumed).
    private val _isRecordingPaused = MutableStateFlow(false)
    val isRecordingPaused: StateFlow<Boolean> = _isRecordingPaused.asStateFlow()

    private val _recordingProgress = MutableStateFlow("0s")
    val recordingProgress: StateFlow<String> = _recordingProgress.asStateFlow()

    private val _selectedSoundToRecord = MutableStateFlow<AfroSound?>(null)
    val selectedSoundToRecord: StateFlow<AfroSound?> = _selectedSoundToRecord.asStateFlow()

    private val _recordedVideoPath = MutableStateFlow<String?>(null) // Simulation
    val recordedVideoPath: StateFlow<String?> = _recordedVideoPath.asStateFlow()

    // LIVE Stream states
    private val _liveComments = MutableStateFlow<List<Comment>>(emptyList())
    val liveComments: StateFlow<List<Comment>> = _liveComments.asStateFlow()

    private val _liveViewerCount = MutableStateFlow(2480)
    val liveViewerCount: StateFlow<Int> = _liveViewerCount.asStateFlow()

    // Flying hearts color list for the LIVE view
    private val _flyingHearts = MutableStateFlow<List<Int>>(emptyList()) // representing colors or random IDs
    val flyingHearts: StateFlow<List<Int>> = _flyingHearts.asStateFlow()

    // User profile state
    private val _currentUserProfile = MutableStateFlow<Creator>(
        Creator(
            id = "me",
            name = "Mister Vibe",
            username = "King_Moves",
            avatarUrl = 0, // Using canvas avatar
            isVerified = true,
            followers = "24.6K",
            following = "128",
            totalLikes = "512.3K",
            bio = "Danseur | Créateur de vibes, Faisons rayonner notre culture 🔥🌍✊🏽"
        )
    )
    val currentUserProfile: StateFlow<Creator> = _currentUserProfile.asStateFlow()

    private var liveStreamJob: Job? = null
    private var recordJob: Job? = null
    private var playbackJob: Job? = null
    private var nextHeartId = 0
    private var recordedSeconds = 0

    init {
        loadMockData()
        startPlaybackLoop()
    }

    /**
     * Drives the simulated playback progress of the active feed video. The clip
     * only advances while the user is on the HomeFeed and playback is not paused,
     * which is what makes the tap-to-pause gesture meaningful. When a clip ends it
     * automatically advances to the next video, looping back to the start.
     */
    private fun startPlaybackLoop() {
        playbackJob?.cancel()
        playbackJob = viewModelScope.launch {
            while (true) {
                delay(PLAYBACK_TICK_MS)
                val canPlay = _currentScreen.value is Screen.HomeFeed &&
                    _isVideoPlaying.value &&
                    _videos.value.isNotEmpty()
                if (!canPlay) continue

                val step = PLAYBACK_TICK_MS.toFloat() / VIDEO_DURATION_MS.toFloat()
                val next = _videoProgress.value + step
                if (next >= 1f) {
                    val size = _videos.value.size
                    _currentVideoIndex.value = (_currentVideoIndex.value + 1) % size
                    _videoProgress.value = 0f
                } else {
                    _videoProgress.value = next
                }
            }
        }
    }

    private fun loadMockData() {
        // Mock videos
        val initialVideos = listOf(
            VideoItem(
                id = "vid_1",
                creatorName = "King Moves",
                creatorUsername = "King_Moves",
                creatorAvatarUrl = 1,
                caption = "La danse c'est la langue du cœur 💯🔥 Ce nouveau jeu de jambes Afrobeat va vous faire vibrer !",
                tags = listOf("#AfroVibe", "#DanseTaCulture", "#Afrique", "#Afrodance"),
                soundName = "Afro Vibe Original - King Moves",
                soundArtist = "King Moves",
                likesCount = 12500,
                commentsCount = 342,
                bookmarksCount = 1200,
                isVerified = true,
                videoRes = 1, // represents main_feed_dancer
                comments = listOf(
                    Comment("cmt_1_1", "Queen_Lafo", 2, "Wesh ça vibe trop fort !! 🔥🙌", "2m"),
                    Comment("cmt_1_2", "Mister_K", 3, "Incredible steps brother! Send the tutoriel", "5m"),
                    Comment("cmt_1_3", "AfroBeats_Off", 4, "Ce son est magique ! Bien joué 👍🏽", "15m")
                )
            ),
            VideoItem(
                id = "vid_2",
                creatorName = "Queen Queen",
                creatorUsername = "Queen_Lafo",
                creatorAvatarUrl = 2,
                caption = "Mapouka Vibes authentique de Grand-Bassam ! 🇨🇮 Célébrons la force de nos danses ancestrales !",
                tags = listOf("#Mapouka", "#CoteIvoire", "#DanseTraditionnelle"),
                soundName = "Grand Bassam Drums - AfroVibe Team",
                soundArtist = "AfroVibe Team",
                likesCount = 8700,
                commentsCount = 194,
                bookmarksCount = 830,
                isVerified = true,
                videoRes = 2, // will be depicted nicely on UI
                comments = listOf(
                    Comment("cmt_2_1", "Yao_Dancer", 5, "La Côte d'Ivoire est fière 🇨🇮✨", "30m"),
                    Comment("cmt_2_2", "King_Moves", 1, "Une reine incontestée ! Magnifique", "1h")
                )
            ),
            VideoItem(
                id = "vid_3",
                creatorName = "Kuduro Crew",
                creatorUsername = "Kuduro_Style",
                creatorAvatarUrl = 3,
                caption = "Sensation Kuduro directe de Luanda ! 🇦🇴 Prêts à relever le défi de vitesse ?",
                tags = listOf("#Kuduro", "#Angola", "#FastFeet", "#Défis"),
                soundName = "Luanda Bass - Kuduro Style",
                soundArtist = "Kuduro Style",
                likesCount = 15200,
                commentsCount = 482,
                bookmarksCount = 2050,
                isVerified = false,
                videoRes = 3,
                comments = listOf(
                    Comment("cmt_3_1", "Amara_A", 6, "Mes chevilles ont quitté le groupe ! 😂💀", "12m")
                )
            ),
            VideoItem(
                id = "vid_4",
                creatorName = "Amapiano Queen",
                creatorUsername = "Amapiano_Soweto",
                creatorAvatarUrl = 4,
                caption = "Le piano magique de Soweto! 🇿🇦 Laissez les vibrations guider votre âme.",
                tags = listOf("#Amapiano", "#Soweto", "#Musique", "#SouthAfrica"),
                soundName = "Soweto Night Groove - Amapiano",
                soundArtist = "Amapiano Soweto",
                likesCount = 22400,
                commentsCount = 890,
                bookmarksCount = 3710,
                isVerified = true,
                videoRes = 4,
                comments = emptyList()
            )
        )
        _videos.value = initialVideos

        // Mock sounds
        _sounds.value = listOf(
            AfroSound("snd_1", "Afro Vibe Original", "King Moves", "12.4K vidéos", 1),
            AfroSound("snd_2", "Grand Bassam Drums", "AfroVibe Team", "8.1K vidéos", 2),
            AfroSound("snd_3", "Luanda Bass", "Kuduro Style", "2.9K vidéos", 3),
            AfroSound("snd_4", "Soweto Night Groove", "Amapiano Soweto", "45.2K vidéos", 4),
            AfroSound("snd_5", "Kwaito Revival", "Dj Vuthela", "1.6K vidéos", 1)
        )

        // Mock notifications and stories
        _notifications.value = listOf(
            AfroNotification("nt_1", NotificationType.LIKE, "Queen_Lafo", 2, "a aimé votre vidéo.", "2m", isStory = true, relatedVideoId = "vid_1"),
            AfroNotification("nt_2", NotificationType.MESSAGE, "Mister_K", 3, "Wesh ça vibe fort ! 🔥", "5m", isStory = true),
            AfroNotification("nt_3", NotificationType.SYSTEM, "AfroBeats_Off", 4, "Nouveau son dispo !", "15m", isStory = true),
            AfroNotification("nt_4", NotificationType.CHALLENGE, "Afro Move Challenge", 1, "Votre vidéo est dans le top 3 !", "1h", relatedVideoId = "vid_3"),
            AfroNotification("nt_5", NotificationType.SYSTEM, "Équipe AfroVibe", 1, "Bienvenue dans la communauté ! Faisons briller la culture locale 👋🏽🦁", "1j", isRead = true)
        )

        // Initialize Chat messages
        _chatMessages.value = listOf(
            "Yow king, ton dernier jeu de jambes est légendaire !" to false,
            "Merci beaucoup l'ami ! Ça vient droit du coeur ✊🏽" to true,
            "Wesh ça vibe fort ! 🔥" to false
        )
    }

    // Navigation and screen switching
    fun navigateTo(screen: Screen) {
        if (_currentScreen.value != screen) {
            backstack.add(_currentScreen.value)
            _currentScreen.value = screen
            
            // Manage lifecycle actions
            if (screen == Screen.LiveSession) {
                startLiveStreamSimulation()
            } else {
                stopLiveStreamSimulation()
            }
        }
    }

    fun navigateBack() {
        if (backstack.isNotEmpty()) {
            val prev = backstack.removeAt(backstack.size - 1)
            _currentScreen.value = prev
            
            if (prev == Screen.LiveSession) {
                startLiveStreamSimulation()
            } else {
                stopLiveStreamSimulation()
            }
        } else {
            _currentScreen.value = Screen.HomeFeed
        }
    }

    // Category Filter
    fun setDiscoverFilter(category: String) {
        _discoverFilter.value = category
    }

    /** Maps a Discover category chip to the keywords used to match content. */
    private fun keywordsForCategory(category: String): List<String> = when (category) {
        "Danse" -> listOf("danse", "dance", "mapouka", "afrodance", "kuduro")
        "Musique" -> listOf("musique", "amapiano", "son", "beat", "groove", "bass", "drums")
        "Défis" -> listOf("défi", "defi", "challenge", "fastfeet")
        "Mode" -> listOf("mode", "model", "style", "fashion")
        "Humour" -> listOf("humour", "fun", "😂", "drôle", "drole")
        else -> emptyList()
    }

    private fun VideoItem.matchesCategory(category: String): Boolean {
        if (category == "Tout") return true
        val keywords = keywordsForCategory(category)
        if (keywords.isEmpty()) return true
        val haystack = (caption + " " + tags.joinToString(" ") + " " + soundName).lowercase()
        return keywords.any { haystack.contains(it) }
    }

    private fun VideoItem.matchesQuery(query: String): Boolean {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return true
        return creatorName.lowercase().contains(q) ||
            creatorUsername.lowercase().contains(q) ||
            caption.lowercase().contains(q) ||
            soundName.lowercase().contains(q) ||
            tags.any { it.lowercase().contains(q) }
    }

    /** Filters the feed by the active category chip and a free-text query. */
    fun searchVideos(query: String, category: String = _discoverFilter.value): List<VideoItem> {
        return _videos.value.filter { it.matchesCategory(category) && it.matchesQuery(query) }
    }

    /** Filters the sound library by a free-text query (title or artist). */
    fun searchSounds(query: String): List<AfroSound> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return emptyList()
        return _sounds.value.filter {
            it.title.lowercase().contains(q) || it.artist.lowercase().contains(q)
        }
    }

    // Video Feed Actions
    fun setCurrentVideoIndex(index: Int) {
        if (index in 0 until _videos.value.size) {
            _currentVideoIndex.value = index
            // Restart playback from the beginning whenever the active clip changes.
            _videoProgress.value = 0f
            _isVideoPlaying.value = true
        }
    }

    /** Toggles play/pause for the active feed video. */
    fun togglePlayPause() {
        _isVideoPlaying.update { !it }
    }

    fun setVideoPlaying(playing: Boolean) {
        _isVideoPlaying.value = playing
    }

    /**
     * Opens a specific video in the feed by its id (used by the profile grid,
     * notifications and incoming share deep links) and resumes playback.
     */
    fun openVideoById(videoId: String) {
        val index = _videos.value.indexOfFirst { it.id == videoId }
        if (index >= 0) {
            setCurrentVideoIndex(index)
        }
        navigateTo(Screen.HomeFeed)
    }

    /**
     * Builds the shareable deep link for a video. Matches the intent-filters
     * declared in the manifest so the link reopens the app on the right clip.
     */
    fun shareLinkForVideo(video: VideoItem): String {
        return "https://afrovibe.app/video/${video.id}"
    }

    fun shareMessageForVideo(video: VideoItem): String {
        return "Regarde la vibe de @${video.creatorUsername} sur AfroVibe ! 🔥🌍\n" +
            "${video.caption}\n\n${shareLinkForVideo(video)}"
    }

    fun toggleLikeVideo(videoId: String) {
        _videos.update { list ->
            list.map { item ->
                if (item.id == videoId) {
                    val newLiked = !item.isLiked
                    val diff = if (newLiked) 1 else -1
                    item.copy(isLiked = newLiked, likesCount = item.likesCount + diff)
                } else item
            }
        }
    }

    fun toggleBookmarkVideo(videoId: String) {
        _videos.update { list ->
            list.map { item ->
                if (item.id == videoId) {
                    val newBookmarked = !item.isBookmarked
                    val diff = if (newBookmarked) 1 else -1
                    item.copy(isBookmarked = newBookmarked, bookmarksCount = item.bookmarksCount + diff)
                } else item
            }
        }
    }

    fun addCommentToVideo(videoId: String, content: String) {
        if (content.trim().isEmpty()) return
        _videos.update { list ->
            list.map { item ->
                if (item.id == videoId) {
                    val newComments = item.comments + Comment(
                        id = "cmt_usr_${System.currentTimeMillis()}",
                        userName = "Mister Vibe",
                        userAvatarUrl = 0, // Current user
                        content = content,
                        timeAgo = "Maintenant"
                    )
                    item.copy(comments = newComments, commentsCount = item.commentsCount + 1)
                } else item
            }
        }
    }

    // Sound customization
    fun setSoundToRecord(sound: AfroSound) {
        _selectedSoundToRecord.value = sound
    }

    fun startRecordingSimulation() {
        if (_isRecording.value) return
        _isRecording.value = true
        _isRecordingPaused.value = false
        recordedSeconds = 0
        _recordingProgress.value = "0s / ${MAX_RECORDING_SECONDS}s"

        recordJob = viewModelScope.launch {
            while (recordedSeconds < MAX_RECORDING_SECONDS) {
                delay(1000)
                if (!_isRecording.value) break
                // Honour pause: do not increment the timer while paused.
                if (_isRecordingPaused.value) continue
                recordedSeconds += 1
                _recordingProgress.value = "${recordedSeconds}s / ${MAX_RECORDING_SECONDS}s"
            }
            if (_isRecording.value) {
                stopAndSaveRecording()
            }
        }
    }

    /** Pauses an in-progress recording without discarding the captured footage. */
    fun pauseRecording() {
        if (_isRecording.value) _isRecordingPaused.value = true
    }

    /** Resumes a paused recording. */
    fun resumeRecording() {
        if (_isRecording.value) _isRecordingPaused.value = false
    }

    fun stopAndSaveRecording() {
        _isRecording.value = false
        _isRecordingPaused.value = false
        recordJob?.cancel()
        _recordedVideoPath.value = "mock_video_recorded_vibe_clip.mp4"
    }

    fun cancelRecording() {
        _isRecording.value = false
        _isRecordingPaused.value = false
        recordJob?.cancel()
        recordedSeconds = 0
        _recordingProgress.value = "0s"
        _recordedVideoPath.value = null
    }

    /** Extracts #hashtags typed in the caption, falling back to default tags. */
    private fun parseTags(caption: String): List<String> {
        val parsed = Regex("#[\\p{L}0-9_]+").findAll(caption).map { it.value }.distinct().toList()
        return if (parsed.isEmpty()) listOf("#AfroVibe", "#MonStyle", "#DanseTaCulture") else parsed
    }

    fun publishRecordedVideo(caption: String) {
        val sound = _selectedSoundToRecord.value
        val soundTitle = sound?.title ?: "Afro Vibe Original"
        val soundArtistName = sound?.artist ?: "Mister Vibe"
        val trimmedCaption = caption.trim()

        val newVideo = VideoItem(
            id = "vid_usr_${System.currentTimeMillis()}",
            creatorName = "Mister Vibe",
            creatorUsername = "King_Moves",
            creatorAvatarUrl = 0, // Me
            caption = if (trimmedCaption.isEmpty()) "Ma nouvelle vibe culturelle ! 🌍🔥" else trimmedCaption,
            tags = parseTags(trimmedCaption),
            soundName = "$soundTitle - $soundArtistName",
            soundArtist = soundArtistName,
            likesCount = 1,
            commentsCount = 0,
            bookmarksCount = 0,
            isVerified = true,
            videoRes = 1, // uses main feed dancer representation
            comments = emptyList()
        )

        // Insert at first place in the feed list
        _videos.update { current -> listOf(newVideo) + current }
        
        // Also update local profile list
        _currentUserProfile.update { user ->
            user.copy(videos = listOf(newVideo) + user.videos)
        }

        // Return to HomeFeed and play the freshly published clip from the start.
        _currentVideoIndex.value = 0
        _videoProgress.value = 0f
        _isVideoPlaying.value = true
        _currentScreen.value = Screen.HomeFeed
        _recordedVideoPath.value = null
        _selectedSoundToRecord.value = null
    }

    // Notification Actions
    fun markNotificationRead(id: String) {
        _notifications.update { list ->
            list.map { if (it.id == id) it.copy(isRead = true) else it }
        }
    }

    fun markAllNotificationsRead() {
        _notifications.update { list -> list.map { it.copy(isRead = true) } }
    }

    /**
     * Handles a tap on a notification: marks it read, opens chat for messages and
     * jumps to the related video for like/comment/challenge activity.
     */
    fun onNotificationClicked(notification: AfroNotification) {
        markNotificationRead(notification.id)
        when (notification.type) {
            NotificationType.MESSAGE -> selectChatSender(notification.senderName)
            else -> notification.relatedVideoId?.let { openVideoById(it) }
        }
    }

    // Chat Actions
    fun selectChatSender(sender: String?) {
        _selectedChatSender.value = sender
        if (sender != null) {
            // Simulated chat reset
            _chatMessages.value = listOf(
                "Yow king, ton dernier jeu de jambes est légendaire !" to false,
                "Merci beaucoup l'ami ! Ça vient de l'Afrique ✊🏽⚡️" to true,
                "Wesh ça vibe trop fort ! 🔥" to false
            )
        }
    }

    fun sendMessage(text: String) {
        if (text.trim().isEmpty()) return
        _chatMessages.update { current -> current + (text to true) }
        
        // Simulated autogenerated response from the friend after 1.5 seconds
        viewModelScope.launch {
            delay(1500)
            val responses = listOf(
                "Grave ! Force à toi l'artiste 💪🏽",
                "Faut qu'on fasse un duo ensemble bientôt !!!",
                "Oya ! Let's record a new challenge on the street!",
                "Infréquentable ! 🚀💥"
            )
            _chatMessages.update { current -> current + (responses.random() to false) }
        }
    }

    // LIVE Stream Comments Feed Simulation
    private fun startLiveStreamSimulation() {
        liveStreamJob?.cancel()
        _liveComments.value = listOf(
            Comment("lc_1", "Yao_Dancer", 5, "Ça chauffe ! 🔥🇨🇮", "Maintenant"),
            Comment("lc_2", "Blaise_Dance", 6, "Trop fort !", "Maintenant")
        )
        _liveViewerCount.value = 2480

        liveStreamJob = viewModelScope.launch {
            val names = listOf("Aminata_K", "Kofi_Ghana", "Moussa_LeChant", "Ngozi_Y", "Fatou_Dancer", "Tunde_Vibes")
            val commentTexts = listOf(
                "Let's gooo Africa! 🌍✨",
                "Quel artiste fabuleux!",
                "La culture du djembe est inégalée 🥁",
                "Regardez-moi ce rythme incroyable 😱",
                "D'où vient ce son ?? C'est du lourd !",
                "Ambiance de folie ce soir 🔥⚡",
                "Love from Cameroun 🇨🇲✊🏽",
                "Fils du pays ! 🇨🇮✨"
            )

            while (true) {
                delay(1800)
                // Randomly add comment to list
                _liveComments.update { list ->
                    val newList = list.takeLast(15) + Comment(
                        id = "lc_${System.currentTimeMillis()}",
                        userName = names.random(),
                        userAvatarUrl = (1..6).random(),
                        content = commentTexts.random(),
                        timeAgo = "Maintenant"
                    )
                    newList
                }
                // Vary viewers count slightly
                _liveViewerCount.update { current -> current + (-5..10).random() }
            }
        }
    }

    private fun stopLiveStreamSimulation() {
        liveStreamJob?.cancel()
    }

    // Live Tap Heart Reaction
    fun tapLiveHeart() {
        val uniqueId = nextHeartId++
        _flyingHearts.update { list ->
            // Keep up to 30 elements to prevent over consumption
            list.takeLast(20) + uniqueId
        }
    }

    fun consumeLiveHeart(heartId: Int) {
        _flyingHearts.update { list -> list.filterNot { it == heartId } }
    }
}
