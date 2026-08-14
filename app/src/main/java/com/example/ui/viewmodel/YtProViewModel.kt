package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.Comment
import com.example.data.model.CreatorAnalytics
import com.example.data.model.EarningsRecord
import com.example.data.model.LikeRecord
import com.example.data.model.ModerationReport
import com.example.data.model.NotificationItem
import com.example.data.model.PayoutRequest
import com.example.data.model.PayoutStatus
import com.example.data.model.Playlist
import com.example.data.model.ReportStatus
import com.example.data.model.ShortVideo
import com.example.data.model.Subscription
import com.example.data.model.User
import com.example.data.model.UserRole
import com.example.data.model.Video
import com.example.data.model.VideoCategory
import com.example.data.model.VideoResolution
import com.example.data.model.VideoVisibility
import com.example.data.model.WatchHistoryItem
import com.example.data.repository.YtProRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SearchFilter(
    val category: String = "All",
    val durationFilter: String = "Any", // Any, Under 5 min, 5-20 min, Over 20 min
    val sortBy: String = "Relevance", // Relevance, Upload date, View count, Rating
    val mediaType: String = "All" // All, Videos, Shorts, Channels
)

class YtProViewModel(private val repository: YtProRepository) : ViewModel() {

    init {
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }
    }

    val currentUser: StateFlow<User> = repository.currentUser

    // Categories and Feeds
    val categories: StateFlow<List<VideoCategory>> = repository.allCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedCategory = MutableStateFlow("All")

    val feedVideos: StateFlow<List<Video>> = selectedCategory.flatMapLatest { cat ->
        repository.getVideosByCategory(cat)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val trendingVideos: StateFlow<List<Video>> = repository.trendingVideos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val featuredVideos: StateFlow<List<Video>> = repository.featuredVideos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allShorts: StateFlow<List<ShortVideo>> = repository.allShorts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userPlaylists: StateFlow<List<Playlist>> = repository.userPlaylists
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val watchHistory: StateFlow<List<WatchHistoryItem>> = repository.userHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<NotificationItem>> = repository.notifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userSubscriptions: StateFlow<List<Subscription>> = repository.userSubscriptions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Search State
    val searchQuery = MutableStateFlow("")
    val searchFilter = MutableStateFlow(SearchFilter())

    val searchResults: StateFlow<List<Video>> = combine(
        searchQuery,
        searchFilter,
        repository.allVideos
    ) { query, filter, allVids ->
        if (query.isBlank()) return@combine emptyList()
        val queryLower = query.lowercase().trim()
        allVids.filter { v ->
            val matchesQuery = v.title.lowercase().contains(queryLower) ||
                    v.description.lowercase().contains(queryLower) ||
                    v.creatorName.lowercase().contains(queryLower) ||
                    v.tags.lowercase().contains(queryLower)

            val matchesCategory = filter.category == "All" || v.category.equals(filter.category, ignoreCase = true)
            val matchesDuration = when (filter.durationFilter) {
                "Under 5 min" -> v.durationSeconds < 300
                "5-20 min" -> v.durationSeconds in 300..1200
                "Over 20 min" -> v.durationSeconds > 1200
                else -> true
            }
            matchesQuery && matchesCategory && matchesDuration
        }.sortedWith { a, b ->
            when (filter.sortBy) {
                "Upload date" -> b.uploadTimestamp.compareTo(a.uploadTimestamp)
                "View count" -> b.views.compareTo(a.views)
                "Rating" -> b.likes.compareTo(a.likes)
                else -> 0
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Playback State
    val currentPlayingVideo = MutableStateFlow<Video?>(null)
    val activeVideo: StateFlow<Video?> = currentPlayingVideo.asStateFlow()
    val isPlaying = MutableStateFlow(true)
    val playbackPosition = MutableStateFlow(0)
    val playbackSpeed = MutableStateFlow(1.0f)
    val selectedResolution = MutableStateFlow(VideoResolution.RES_1080P)
    val isCaptionsEnabled = MutableStateFlow(true)
    val isMiniPlayerActive = MutableStateFlow(false)
    val isMiniPlayer: StateFlow<Boolean> = isMiniPlayerActive.asStateFlow()
    val isFullscreen = MutableStateFlow(false)
    val isAutoPlay = MutableStateFlow(true)

    // Current selected Channel (for viewing creator profiles)
    val selectedChannelId = MutableStateFlow<String?>(null)

    // Upload Pipeline State
    val isUploading = MutableStateFlow(false)
    val uploadProgress = MutableStateFlow(0f)
    val uploadStage = MutableStateFlow("Ready")

    // Modals & Sheets
    val showDeploymentDocs = MutableStateFlow(false)
    val showReportDialog = MutableStateFlow(false)
    data class ReportTarget(val targetType: String, val targetId: String, val targetTitle: String)
    val reportDialogState = MutableStateFlow<ReportTarget?>(null)
    val showPlaylistSelector = MutableStateFlow<String?>(null) // videoId
    val showCreatePlaylistDialog = MutableStateFlow(false)
    val showPayoutRequestDialog = MutableStateFlow(false)
    val showGoogleSignInDialog = MutableStateFlow(false)
    val showCreateBottomSheet = MutableStateFlow(false)

    // Google Sign-In & Authentication
    fun loginWithGoogle(
        email: String,
        displayName: String,
        avatarUrl: String = "",
        handle: String = ""
    ) {
        repository.loginWithGoogle(email, displayName, avatarUrl, handle)
        showGoogleSignInDialog.value = false
    }

    fun switchAccount(user: User) {
        repository.switchAccount(user)
    }

    // Admin & Studio flows
    val creatorAnalytics: StateFlow<CreatorAnalytics?> = currentUser.flatMapLatest { user ->
        repository.getCreatorAnalytics(user.id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val creatorEarnings: StateFlow<List<EarningsRecord>> = currentUser.flatMapLatest { user ->
        repository.getEarnings(user.id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adminUsers: StateFlow<List<User>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adminReports: StateFlow<List<ModerationReport>> = repository.allReports
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adminPayoutRequests: StateFlow<List<PayoutRequest>> = repository.allPayoutRequests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Player Actions
    fun playVideo(video: Video) {
        currentPlayingVideo.value = video
        playbackPosition.value = 0
        isPlaying.value = true
        isMiniPlayerActive.value = false
        viewModelScope.launch {
            repository.recordWatchProgress(video.id, 0, video.durationSeconds)
        }
    }

    fun closePlayer() {
        currentPlayingVideo.value = null
        isMiniPlayerActive.value = false
        isPlaying.value = false
    }

    fun minimizeToMiniPlayer() {
        isMiniPlayerActive.value = true
    }

    fun maximizeFromMiniPlayer() {
        isMiniPlayerActive.value = false
    }

    fun expandMiniPlayer() {
        isMiniPlayerActive.value = false
    }

    fun togglePlayPause() {
        isPlaying.value = !isPlaying.value
    }

    fun seekTo(seconds: Int) {
        playbackPosition.value = seconds
        val v = currentPlayingVideo.value ?: return
        viewModelScope.launch {
            repository.recordWatchProgress(v.id, seconds, v.durationSeconds)
        }
    }

    fun setSpeed(speed: Float) {
        playbackSpeed.value = speed
    }

    fun setResolution(res: VideoResolution) {
        selectedResolution.value = res
    }

    fun toggleCaptions() {
        isCaptionsEnabled.value = !isCaptionsEnabled.value
    }

    // Likes & Subscriptions
    fun getIsLiked(targetId: String): Flow<LikeRecord?> = repository.isLiked(targetId)
    fun getIsSubscribed(creatorId: String): Flow<Subscription?> = repository.isSubscribed(creatorId)

    fun toggleLike(targetId: String, isShort: Boolean, isLike: Boolean) {
        viewModelScope.launch {
            repository.toggleLike(targetId, isShort, isLike)
        }
    }

    fun toggleSubscription(creatorId: String) {
        viewModelScope.launch {
            repository.toggleSubscription(creatorId)
        }
    }

    // Comments
    fun getComments(targetId: String): Flow<List<Comment>> = repository.getComments(targetId)
    fun getReplies(parentId: String): Flow<List<Comment>> = repository.getReplies(parentId)

    fun addComment(targetId: String, text: String, isShort: Boolean, parentCommentId: String? = null) {
        if (text.isBlank()) return
        viewModelScope.launch {
            repository.addComment(targetId, text.trim(), isShort, parentCommentId)
        }
    }

    // Upload Pipeline
    fun startUploadPipeline(
        title: String,
        description: String,
        category: String,
        tags: String,
        visibility: VideoVisibility,
        thumbnailUrl: String,
        videoUrl: String = "",
        durationSecs: Int = 300,
        onComplete: (Video) -> Unit
    ) {
        viewModelScope.launch {
            isUploading.value = true
            uploadProgress.value = 0.08f
            uploadStage.value = "Reading video stream from gallery storage..."
            delay(400)

            uploadProgress.value = 0.28f
            uploadStage.value = "Uploading master video stream to S3 / Cloud Storage..."
            delay(450)

            uploadProgress.value = 0.55f
            uploadStage.value = "Transcoding 360p (SD), 720p (HD)..."
            delay(400)

            uploadProgress.value = 0.80f
            uploadStage.value = "Transcoding 1080p (FHD) & 4K Ultra HD master..."
            delay(400)

            uploadProgress.value = 0.95f
            uploadStage.value = "Generating AI Chapter Markers & Captions..."
            delay(300)

            uploadProgress.value = 1.0f
            uploadStage.value = "Publishing video globally to YouTube Pro feed..."
            delay(250)

            val created = repository.uploadVideo(
                title = title,
                description = description,
                category = category,
                tags = tags,
                visibility = visibility,
                thumbnailUrl = thumbnailUrl,
                videoUrl = videoUrl,
                durationSecs = durationSecs
            )
            isUploading.value = false
            uploadProgress.value = 0f
            uploadStage.value = "Completed"
            onComplete(created)
        }
    }

    fun startShortUploadPipeline(
        title: String,
        videoUrl: String = "",
        thumbnailUrl: String = "",
        soundTitle: String = "Original Sound",
        onComplete: (ShortVideo) -> Unit
    ) {
        viewModelScope.launch {
            isUploading.value = true
            uploadProgress.value = 0.15f
            uploadStage.value = "Analyzing 9:16 vertical short format..."
            delay(350)

            uploadProgress.value = 0.50f
            uploadStage.value = "Optimizing audio bitrate & master video..."
            delay(400)

            uploadProgress.value = 0.85f
            uploadStage.value = "Rendering vertical loop stream..."
            delay(350)

            uploadProgress.value = 1.0f
            uploadStage.value = "Publishing Short to YouTube Shorts feed..."
            delay(250)

            val created = repository.uploadShort(
                title = title,
                videoUrl = videoUrl,
                thumbnailUrl = thumbnailUrl,
                soundTitle = soundTitle
            )
            isUploading.value = false
            uploadProgress.value = 0f
            uploadStage.value = "Completed"
            onComplete(created)
        }
    }

    // Playlists
    fun createPlaylist(title: String, description: String = "", isPrivate: Boolean = false, initialVideoId: String? = null) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.createPlaylist(title, description, isPrivate, initialVideoId)
            showCreatePlaylistDialog.value = false
        }
    }

    fun addVideoToPlaylist(playlistId: String, videoId: String) {
        viewModelScope.launch {
            repository.addVideoToPlaylist(playlistId, videoId)
            showPlaylistSelector.value = null
        }
    }

    // Monetization Payouts
    fun requestPayout(amount: Double, method: String, destination: String) {
        viewModelScope.launch {
            repository.requestPayout(amount, method, destination)
            showPayoutRequestDialog.value = false
        }
    }

    // Reports & Moderation
    fun openReportDialog(targetType: String, targetId: String, targetTitle: String) {
        reportDialogState.value = ReportTarget(targetType, targetId, targetTitle)
        showReportDialog.value = true
    }

    fun closeReportDialog() {
        reportDialogState.value = null
        showReportDialog.value = false
    }

    fun submitReport(reason: String, details: String) {
        val target = reportDialogState.value ?: return
        viewModelScope.launch {
            repository.submitReport(target.targetType, target.targetId, target.targetTitle, reason, details)
            closeReportDialog()
        }
    }

    fun markNotificationAsRead(notifId: String) {
        viewModelScope.launch {
            repository.markNotificationsRead()
        }
    }

    // Admin Actions
    fun adminResolveReport(reportId: String) {
        viewModelScope.launch {
            repository.adminUpdateReport(reportId, ReportStatus.RESOLVED)
        }
    }

    fun adminDismissReport(reportId: String) {
        viewModelScope.launch {
            repository.adminUpdateReport(reportId, ReportStatus.DISMISSED)
        }
    }

    fun adminToggleBanUser(userId: String, banned: Boolean) {
        viewModelScope.launch {
            repository.adminToggleBan(userId, banned)
        }
    }

    fun adminToggleFlagVideo(videoId: String, flagged: Boolean) {
        viewModelScope.launch {
            repository.adminToggleFlagVideo(videoId, flagged)
        }
    }

    fun adminApprovePayout(payoutId: String) {
        viewModelScope.launch {
            repository.adminUpdatePayoutStatus(payoutId, PayoutStatus.APPROVED)
        }
    }

    fun adminRejectPayout(payoutId: String) {
        viewModelScope.launch {
            repository.adminUpdatePayoutStatus(payoutId, PayoutStatus.REJECTED)
        }
    }

    // User Role Switcher
    fun switchRole(role: UserRole) {
        repository.switchUserRole(role)
    }

    fun clearWatchHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    fun removeHistoryItem(videoId: String) {
        viewModelScope.launch {
            repository.removeHistoryItem(videoId)
        }
    }

    fun markNotificationsRead() {
        viewModelScope.launch {
            repository.markNotificationsRead()
        }
    }

    fun getCreatorVideos(creatorId: String): Flow<List<Video>> = repository.getVideosByCreator(creatorId)
    fun getCreatorShorts(creatorId: String): Flow<List<ShortVideo>> = repository.getShortsByCreator(creatorId)
    fun getCreatorUser(creatorId: String): Flow<User?> = repository.getUserById(creatorId)
}

class YtProViewModelFactory(private val repository: YtProRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(YtProViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return YtProViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
