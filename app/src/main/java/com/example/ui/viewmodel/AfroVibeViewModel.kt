package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AfroVibeViewModel : ViewModel() {

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

    // Sounds list
    private val _sounds = MutableStateFlow<List<AfroSound>>(emptyList())
    val sounds: StateFlow<List<AfroSound>> = _sounds.asStateFlow()

    // Notifications and Stories list
    private val _notifications = MutableStateFlow<List<AfroNotification>>(emptyList())
    val notifications: StateFlow<List<AfroNotification>> = _notifications.asStateFlow()

    // Simulated Chat state inside inbox
    private val _selectedChatSender = MutableStateFlow<String?>(null)
    val selectedChatSender: StateFlow<String?> = _selectedChatSender.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<Pair<String, Boolean>>>(emptyList()) // text, isUserSent
    val chatMessages: StateFlow<List<Pair<String, Boolean>>> = _chatMessages.asStateFlow()

    // Camera states
    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

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
    private var nextHeartId = 0

    init {
        loadMockData()
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
            AfroNotification("nt_1", NotificationType.LIKE, "Queen_Lafo", 2, "a aimé votre vidéo.", "2m", isStory = true),
            AfroNotification("nt_2", NotificationType.MESSAGE, "Mister_K", 3, "Wesh ça vibe fort ! 🔥", "5m", isStory = true),
            AfroNotification("nt_3", NotificationType.SYSTEM, "AfroBeats_Off", 4, "Nouveau son dispo !", "15m", isStory = true),
            AfroNotification("nt_4", NotificationType.CHALLENGE, "Afro Move Challenge", 1, "Votre vidéo est dans le top 3 !", "1h"),
            AfroNotification("nt_5", NotificationType.SYSTEM, "Équipe AfroVibe", 1, "Bienvenue dans la communauté ! Faisons briller la culture locale 👋🏽🦁", "1j")
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

    // Video Feed Actions
    fun setCurrentVideoIndex(index: Int) {
        if (index in 0 until _videos.value.size) {
            _currentVideoIndex.value = index
        }
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
        _recordingProgress.value = "0s / 10s"
        
        recordJob = viewModelScope.launch {
            for (i in 1..10) {
                delay(1000)
                if (!_isRecording.value) break
                _recordingProgress.value = "${i}s / 10s"
            }
            if (_isRecording.value) {
                stopAndSaveRecording()
            }
        }
    }

    fun stopAndSaveRecording() {
        _isRecording.value = false
        recordJob?.cancel()
        _recordedVideoPath.value = "mock_video_recorded_vibe_clip.mp4"
    }

    fun cancelRecording() {
        _isRecording.value = false
        recordJob?.cancel()
        _recordingProgress.value = "0s"
        _recordedVideoPath.value = null
    }

    fun publishRecordedVideo(caption: String) {
        val sound = _selectedSoundToRecord.value
        val soundTitle = sound?.title ?: "Afro Vibe Original"
        val soundArtistName = sound?.artist ?: "Mister Vibe"

        val newVideo = VideoItem(
            id = "vid_usr_${System.currentTimeMillis()}",
            creatorName = "Mister Vibe",
            creatorUsername = "King_Moves",
            creatorAvatarUrl = 0, // Me
            caption = if (caption.trim().isEmpty()) "Ma nouvelle vibe culturelle ! 🌍🔥" else caption,
            tags = listOf("#AfroVibe", "#MonStyle", "#DanseTaCulture"),
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

        // Return to HomeFeed and reset index
        _currentVideoIndex.value = 0
        _currentScreen.value = Screen.HomeFeed
        _recordedVideoPath.value = null
        _selectedSoundToRecord.value = null
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
